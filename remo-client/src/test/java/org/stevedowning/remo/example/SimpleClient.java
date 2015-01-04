package org.stevedowning.remo.example;

import java.io.IOException;

import org.stevedowning.remo.RemoteServiceClientFactory;
import org.suporma.idyll.util.IdFactory;

public class SimpleClient {
    public static void main(String[] args) throws IOException {
        IdFactory service = new RemoteServiceClientFactory("localhost", 12345).getService(
                IdFactory.class);
        System.out.println(service.generateId());
    }
}
