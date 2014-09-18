package org.stevedowning.remo;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.stevedowning.remo.internal.common.future.CompletionFuture;
import org.stevedowning.remo.internal.common.future.observable.ObservableValue;
import org.stevedowning.remo.internal.common.request.CancellationRequest;
import org.stevedowning.remo.internal.common.request.InvocationRequest;
import org.stevedowning.remo.internal.common.request.Request;
import org.stevedowning.remo.internal.common.request.RequestBatch;
import org.stevedowning.remo.internal.common.request.RequestVisitor;
import org.stevedowning.remo.internal.common.response.Response;
import org.stevedowning.remo.internal.common.response.ResponseBatch;
import org.stevedowning.remo.internal.common.serial.DefaultSerializationManager;
import org.stevedowning.remo.internal.common.serial.SerializationManager;
import org.stevedowning.remo.internal.server.service.ServiceInterface;

public class NetServiceRunner implements ServiceRunner {
    private final SerializationManager serializationManager = new DefaultSerializationManager();
    private volatile Executor executor = Executors.newCachedThreadPool();
    
    public NetServiceRunner useExecutor(Executor executor) {
        this.executor = executor;
        return this;
    }
    
    private class ServiceLoop implements Runnable, ServiceHandle {
        private volatile boolean shutdownRequested = false;
        private volatile ObservableValue<Boolean> isRunning = new ObservableValue<>(false);
        
        private final ServiceInterface serviceInterface;
        private final ServerSocket serverSocket;
        
        public ServiceLoop(ServiceInterface serviceInterface, ServerSocket serverSocket) {
            this.serviceInterface = serviceInterface;
            this.serverSocket = serverSocket;
        }

        public void run() {
            shutdownRequested = false;
            isRunning.set(true);
            while (!shutdownRequested && !Thread.interrupted()) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    handleIncomingRequestBatch(serviceInterface, clientSocket);
                } catch (IOException e) {
                    logError("Error handling request", e);
                }
            }
            isRunning.set(false);
        }

        public CompletionFuture safeShutdown() {
            CompletionFuture future = CompletionFuture.getCompletionFuture(
                    isRunning, (Boolean isRunning) -> !isRunning);
            shutdownRequested = true;
            return future;
        }

        public boolean isRunning() { return isRunning.get(); }
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
        // TODO: Do a bunch more checks, like seeing if everything's Serializable.
        ServerSocket serverSocket = new ServerSocket(port);
        ServiceInterface serviceInterface = new ServiceInterface(serviceContract, handler);
        ServiceLoop serviceLoop = new ServiceLoop(serviceInterface, serverSocket);
        new Thread(serviceLoop, serviceContract.getSimpleName()).start();
        while (!serviceLoop.isRunning());
        return serviceLoop;
    }
    
    private void handleRequest(ServiceInterface service, ResponseBatch responseBatch,
            Request request) {
        request.accept(new RequestVisitor() {
            public void visit(InvocationRequest invocationRequest) {
                boolean success = true;
                Object result = null;
                try {
                    result = getServiceRetVal(service, invocationRequest);
                } catch (Exception ex) {
                    success = false;
                    result = ex;
                    logError("Error handling request", ex);
                }

                // TODO: Optionally sanitize exceptions here.
                String resultStr;
                try {
                    resultStr = serializationManager.serialize(result);
                } catch (IOException e) {
                    success = false;
                    try {
                        resultStr = serializationManager.serialize(
                                new IOException("Serialization failed"));
                    } catch (IOException e1) {
                        resultStr = "";
                        logError("Error handling response", e);
                    }
                }
                Response response = new Response(request.getId(), resultStr, success);
                responseBatch.addResponse(response);
            }

            public void visit(CancellationRequest cancellationRequest) {
                // TODO Auto-generated method stub
            }
        });
    }

    private Object getServiceRetVal(ServiceInterface service, InvocationRequest request)
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
        executor.execute(() -> {
            try {
                RequestBatch requestBatch =
                        serializationManager.deserialize(clientSocket.getInputStream());
                ResponseBatch responseBatch = ResponseBatch.forRequestBatch(requestBatch);
                // TODO: Delegate these requests to different threads.
                for (Request request : requestBatch) {
                    // TODO: Separate the Executor for deserialization and pumping requests onto
                    //       the queue vs the Executor for actually handling the requests.
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
