package org.stevedowning.remo.example;

import java.io.IOException;

import org.stevedowning.commons.idyll.idfactory.IdFactory;
import org.stevedowning.remo.DefaultRemoClientFactory;

public class SimpleClient {
    public static void main(String[] args) throws IOException {
        IdFactory service = new DefaultRemoClientFactory().getRemoteService(
                IdFactory.class, "localhost", 12345);
        System.out.println(service.generateId());
    }
}
