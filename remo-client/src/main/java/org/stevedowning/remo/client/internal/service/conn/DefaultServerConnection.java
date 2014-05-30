package org.stevedowning.remo.client.internal.service.conn;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.stevedowning.remo.common.future.BasicFuture;
import org.stevedowning.remo.common.future.Future;
import org.stevedowning.remo.common.request.ConnectionRequestBatch;
import org.stevedowning.remo.common.response.ConnectionResponseBatch;
import org.stevedowning.remo.common.serial.DefaultSerializationManager;
import org.stevedowning.remo.common.serial.SerializationManager;

public class DefaultServerConnection implements ServerConnection {
    private final SerializationManager serializationManager = new DefaultSerializationManager();
    private final ExecutorService executorService;
    private final String hostname;
    private final int port;

    private static ExecutorService getDefaultExecutorService() {
        return Executors.newCachedThreadPool();
    }

    public DefaultServerConnection(String hostname, int port) throws IOException {
        this(hostname, port, getDefaultExecutorService());
    }

    public DefaultServerConnection(String hostname, int port,
            ExecutorService executorService) throws IOException {
        this.executorService = executorService;
        this.hostname = hostname;
        this.port = port;
    }

    @Override
    public Future<ConnectionResponseBatch> send(final ConnectionRequestBatch requestBatch) {
        final BasicFuture<ConnectionResponseBatch> future = new BasicFuture<>();
        try {
            final Socket socket = new Socket(hostname, port);
            
            future.addCancellationAction(new Runnable() {
                public void run() {
                    // Closing the socket will cause any pending serializations to throw an error.
                    try {
                        socket.close();
                    } catch (IOException e) {
                        // Nothing to do here.
                    }
                }
            });
            executorService.submit(new Runnable() {
                public void run() {
                    try {
                        serializationManager.serialize(socket.getOutputStream(), requestBatch);
                        ConnectionResponseBatch responseBatch =
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
                }
            });
        } catch (IOException e) {
            future.setException(e);
        }
        return future;
    }
}
