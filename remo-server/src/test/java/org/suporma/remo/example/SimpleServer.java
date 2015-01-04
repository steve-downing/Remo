package org.suporma.remo.example;

import java.io.IOException;

import org.suporma.idyll.util.IdFactory;
import org.suporma.idyll.util.LongIdFactory;
import org.suporma.remo.NetServiceRunner;

public class SimpleServer {
    public static void main(String[] args) throws IOException {
        int port = 12345;
        new NetServiceRunner().runService(
                new LongIdFactory(), IdFactory.class, port);
        System.out.println("IdFactory service reporting for duty on port " + port);
    }
}
