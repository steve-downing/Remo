package org.suporma.remo.internal.client.conn;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.suporma.remo.Future;
import org.suporma.remo.internal.common.future.BasicFuture;
import org.suporma.remo.internal.common.request.RequestBatch;
import org.suporma.remo.internal.common.response.ResponseBatch;
import org.suporma.remo.internal.common.serial.DefaultSerializationManager;
import org.suporma.remo.internal.common.serial.SerializationManager;

public class RemoteServerConnection implements ServerConnection {
    private final SerializationManager serializationManager = new DefaultSerializationManager();
    private final Executor executor;
    private final String hostname;
    private final int port;

    private static ExecutorService getDefaultExecutorService() {
        return Executors.newCachedThreadPool();
    }

    public RemoteServerConnection(String hostname, int port) throws IOException {
        this(hostname, port, getDefaultExecutorService());
    }

    public RemoteServerConnection(String hostname, int port, Executor executor)
            throws IOException {
        this.executor = executor;
        this.hostname = hostname;
        this.port = port;
    }

    @Override
    public Future<ResponseBatch> send(final RequestBatch requestBatch) {
        final BasicFuture<ResponseBatch> future = new BasicFuture<>(executor);
        // TODO: Switch this to an architecture that pulls requests off a queue from another thread.
        executor.execute(() -> {
            try {
                final Socket socket = new Socket(hostname, port);
                try {
                    future.addCancellationInterrupt(() -> {
                        // Closing the socket will cause any pending serializations to throw an
                        // error.
                        try {
                            socket.close();
                        } catch (IOException e) {
                            // Nothing to do here.
                        }
                    });
                    serializationManager.serialize(socket.getOutputStream(), requestBatch);
                    ResponseBatch responseBatch =
                            serializationManager.deserialize(socket.getInputStream());
                    future.setVal(responseBatch);
                } catch (IOException e) {
                    future.setException(e);
                } catch (ClassNotFoundException e) {
                    future.setException(new ExecutionException(e));
                } finally {
                    try {
                        if (socket != null) socket.close();
                    } catch (IOException e) {
                        // Nothing to do here.
                    }
                }
            } catch (IOException e) {
                future.setException(e);
            }
        });
        return future;
    }
}
