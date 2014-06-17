package org.stevedowning.remo.example;

import java.io.IOException;

import org.stevedowning.commons.idyll.idfactory.IdFactory;
import org.stevedowning.remo.RemoteServiceClientFactory;

public class SimpleClient {
    public static void main(String[] args) throws IOException {
        IdFactory service = new RemoteServiceClientFactory("localhost", 12345).getService(
                IdFactory.class);
        System.out.println(service.generateId());
    }
}
