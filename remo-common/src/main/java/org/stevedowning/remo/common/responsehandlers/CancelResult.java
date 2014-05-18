package org.stevedowning.remo.common.responsehandlers;

public class CancelResult {
    private final boolean cancelSucceededOnServer, cancelRequestedOnServer, cancelRequestedOnClient;
    
    private CancelResult(boolean cancelRequestedOnClient, boolean cancelRequestedOnServer,
            boolean cancelSucceededOnServer) {
        this.cancelRequestedOnServer = cancelRequestedOnServer;
        this.cancelSucceededOnServer = cancelSucceededOnServer;
        this.cancelRequestedOnClient = cancelRequestedOnClient;
    }

    public static final CancelResult CANCEL_NOT_REQUESTED = new CancelResult(false, false, false);
    public static final CancelResult CANCEL_ON_CLIENT_ONLY = new CancelResult(true, false, false);
    public static final CancelResult CANCEL_ON_SERVER_REQUESTED_AND_FAILED =
            new CancelResult(true, true, false);
    public static final CancelResult CANCEL_ON_SERVER_SUCCEEDED =
            new CancelResult(true, true, true);
    
    public boolean cancelOnClientRequested() { return cancelRequestedOnClient; }
    public boolean cancelOnServerRequested() { return cancelRequestedOnServer; }
    public boolean cancelOnServerSucceeded() { return cancelSucceededOnServer; }
}
