package org.stevedowning.remo.internal.client.conn;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.stevedowning.remo.Future;
import org.stevedowning.remo.internal.common.future.BasicFuture;
import org.stevedowning.remo.internal.common.request.RequestBatch;
import org.stevedowning.remo.internal.common.response.ResponseBatch;
import org.stevedowning.remo.internal.common.serial.DefaultSerializationManager;
import org.stevedowning.remo.internal.common.serial.SerializationManager;

public class DefaultServerConnection implements ServerConnection {
    private final SerializationManager serializationManager = new DefaultSerializationManager();
    private final Executor executor;
    private final String hostname;
    private final int port;

    private static ExecutorService getDefaultExecutorService() {
        return Executors.newCachedThreadPool();
    }

    public DefaultServerConnection(String hostname, int port) throws IOException {
        this(hostname, port, getDefaultExecutorService());
    }

    public DefaultServerConnection(String hostname, int port, Executor executor)
            throws IOException {
        this.executor = executor;
        this.hostname = hostname;
        this.port = port;
    }

    @Override
    public Future<ResponseBatch> send(final RequestBatch requestBatch) {
        final BasicFuture<ResponseBatch> future = new BasicFuture<>();
        try {
            // TODO: Open the socket from the other thread.
            // TODO: In fact, switch this to an architecture that pulls requests off a queue from another thread.
            final Socket socket = new Socket(hostname, port);            
            future.addCancellationInterrupt(() -> {
                // Closing the socket will cause any pending serializations to throw an error.
                try {
                    socket.close();
                } catch (IOException e) {
                    // Nothing to do here.
                }
            });
            executor.execute(() -> {
                try {
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
                        socket.close();
                    } catch (IOException e) {
                        // Nothing to do here.
                    }
                }
            });
        } catch (IOException e) {
            future.setException(e);
        }
        return future;
    }
}
