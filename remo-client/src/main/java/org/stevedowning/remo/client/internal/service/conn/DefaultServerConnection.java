package org.stevedowning.remo.client.internal.service.conn;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.stevedowning.remo.client.internal.future.DefaultClientSideFuture;
import org.stevedowning.remo.common.request.RequestBatch;
import org.stevedowning.remo.common.response.ResponseBatch;
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
    public CancellableFuture<ResponseBatch> send(final RequestBatch requestBatch) {
        final DefaultClientSideFuture<String> future = new DefaultClientSideFuture<String>();
        try {
            final Socket socket = new Socket(hostname, port);
            future.addCancellationAction(new Runnable() {
                public void run() {
                    socket.close();
                }
            });
            executorService.submit(new Runnable() {
                public void run() {
                    try {
                        serializationManager.serialize(socket.getOutputStream(), requestBatch);
                        ResponseBatch responseBatch =
                                serializationManager.deserialize(socket.getInputStream());
                        future.setVal(responseBatch);
                    } catch (IOException e) {
                        future.setException(e);
                    } finally {
                        socket.close();
                    }
                }
            });
        } catch (IOException e) {
            future.setException(e);
        }
        return future;
    }
}
