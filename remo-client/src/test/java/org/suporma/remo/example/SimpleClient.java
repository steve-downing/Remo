package org.suporma.remo.example;

import java.io.IOException;

import org.suporma.idyll.util.IdFactory;
import org.suporma.remo.RemoteServiceClientFactory;

public class SimpleClient {
    public static void main(String[] args) throws IOException {
        IdFactory service = new RemoteServiceClientFactory("localhost", 12345).getService(
                IdFactory.class);
        System.out.println(service.generateId());
    }
}
