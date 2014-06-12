package org.stevedowning.remo.example;

import java.io.IOException;

import org.stevedowning.commons.idyll.idfactory.IdFactory;
import org.stevedowning.remo.RemoteClientFactory;

public class SimpleClient {
    public static void main(String[] args) throws IOException {
        IdFactory service = new RemoteClientFactory("localhost", 12345).getRemoteService(
                IdFactory.class);
        System.out.println(service.generateId());
    }
}
