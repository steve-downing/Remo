package org.stevedowning.remo.example;

import java.io.IOException;

import org.stevedowning.remo.DefaultServiceRunner;

public class ExampleServer {
    private static class ExampleServiceImpl implements ExampleContract {
        public String getExampleString() { return "Derp!"; }
        public String reverse(String str) {
            StringBuilder sb = new StringBuilder();
            for (int i = str.length() - 1; i >= 0; --i) {
                sb.append(str.charAt(i));
            }
            return sb.toString();
        }
        public int getUniversalConstant() { return 42; }
        public void wait(int milliseconds) throws InterruptedException {
            Thread.sleep(milliseconds);
        }
        @SuppressWarnings("null")
        public void fail() throws NullPointerException {
            String nil = null;
            nil.charAt(0);
        }
        
    }
    
    public static void main(String[] args) throws IOException {
        int port = 12345;
        new DefaultServiceRunner().runService(
                new ExampleServiceImpl(), ExampleContract.class, port);
        System.out.println("Example service reporting for duty on port " + port);
    }
}
