package org.stevedowning.remo.example;

import java.io.IOException;

import org.stevedowning.commons.idyll.idfactory.IdFactory;
import org.stevedowning.commons.idyll.idfactory.LongIdFactory;
import org.stevedowning.remo.DefaultServiceRunner;

public class SimpleServer {
    public static void main(String[] args) throws IOException {
        int port = 12345;
        new DefaultServiceRunner().runService(
                new LongIdFactory(), IdFactory.class, port);
        System.out.println("IdFactory service reporting for duty on port " + port);
    }
}
