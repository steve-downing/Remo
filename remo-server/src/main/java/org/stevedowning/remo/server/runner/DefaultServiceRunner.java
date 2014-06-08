package org.stevedowning.remo.server.runner;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.stevedowning.remo.internal.common.request.Request;
import org.stevedowning.remo.internal.common.request.RequestBatch;
import org.stevedowning.remo.internal.common.response.Response;
import org.stevedowning.remo.internal.common.response.ResponseBatch;
import org.stevedowning.remo.internal.common.serial.DefaultSerializationManager;
import org.stevedowning.remo.internal.common.serial.SerializationManager;
import org.stevedowning.remo.server.service.ServiceInterface;

public class DefaultServiceRunner implements ServiceRunner {
    private final SerializationManager serializationManager = new DefaultSerializationManager();
    private final ExecutorService executorService = Executors.newCachedThreadPool();
    
    private class ServiceLoop implements Runnable, ServiceHandle {
        private volatile boolean shutdownRequested = false;
        private volatile boolean isRunning = false;
        
        private final ServiceInterface serviceInterface;
        private final ServerSocket serverSocket;
        
        public ServiceLoop(ServiceInterface serviceInterface, ServerSocket serverSocket) {
            this.serviceInterface = serviceInterface;
            this.serverSocket = serverSocket;
        }

        public void run() {
            shutdownRequested = false;
            isRunning = true;
            while (!shutdownRequested && !Thread.interrupted()) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    handleIncomingRequestBatch(serviceInterface, clientSocket);
                } catch (IOException e) {
                    logError("Error handling request", e);
                }
            }
            isRunning = false;
        }

        // TODO: Have this return a future
        public void safeShutdown() { shutdownRequested = true; }
        public boolean isRunning() { return isRunning; }
    }
    
    private void logError(String message, Exception e) {
        // TODO: Use an actual logging framework.
        System.err.println(message);
        e.printStackTrace(System.err);
    }

    public <T> ServiceHandle runService(T handler, Class<T> serviceContract, int port)
            throws IOException {
        if (!serviceContract.isInterface()) {
            throw new IllegalArgumentException("The serviceContract should be an interface.");
        }
        ServerSocket serverSocket = new ServerSocket(port);
        ServiceInterface serviceInterface = new ServiceInterface(serviceContract, handler);
        ServiceLoop serviceLoop = new ServiceLoop(serviceInterface, serverSocket);
        new Thread(serviceLoop, serviceContract.getSimpleName()).start();
        return serviceLoop;
    }
    
    private void handleRequest(ServiceInterface service, ResponseBatch responseBatch,
            Request request) throws IOException {
        boolean success = true;
        Object result = null;
        try {
            result = getServiceRetVal(service, request);
        } catch (Exception ex) {
            success = false;
            result = ex;
            logError("Error handling request", ex);
        }

        // TODO: Optionally sanitize exceptions here.
        // TODO: Exceptions don't seem to be deserializing correctly.
        String resultStr = serializationManager.serialize(result);
        Response response = new Response(request.getId(), resultStr, success);
        responseBatch.addResponse(response);
    }

    private Object getServiceRetVal(ServiceInterface service, Request request)
            throws Exception {
        String[] serializedParams = request.getSerializedParams();
        Object[] params = new Object[serializedParams.length];
        for (int i = 0; i < serializedParams.length; ++i) {
            String serializedParam = serializedParams[i];
            params[i] = serializationManager.deserialize(serializedParam);
        }
        return service.getResult(request.getMethodId(), params);
    }
    
    private void handleIncomingRequestBatch(final ServiceInterface service,
            final Socket clientSocket) {
        executorService.submit(() -> {
            try {
                RequestBatch requestBatch =
                        serializationManager.deserialize(clientSocket.getInputStream());
                ResponseBatch responseBatch = ResponseBatch.forRequestBatch(requestBatch);
                // TODO: Delegate these requests to different threads.
                for (Request request : requestBatch) {
                    handleRequest(service, responseBatch, request);
                }
                // TODO: Serialize the individual responses out as they become available
                //       instead of sending the batch all at once.
                serializationManager.serialize(clientSocket.getOutputStream(), responseBatch);
            } catch (Exception e) {
                logError("Error handling response", e);
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    logError("Error closing socket", e);
                }
            }
        });
    }
}
