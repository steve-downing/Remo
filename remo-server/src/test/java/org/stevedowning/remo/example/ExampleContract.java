package org.stevedowning.remo.example;

public interface ExampleContract {
    public String getExampleString();
    public String reverse(String str);
    public int getUniversalConstant();
    public void wait(int milliseconds) throws InterruptedException;
    public void fail() throws NullPointerException;
}
