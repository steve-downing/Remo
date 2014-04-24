package org.stevedowning.remo.common.serial;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public interface SerializationManager {
    public void serialize(PrintWriter out, Object obj) throws IOException;
    public String serialize(Object obj) throws IOException;
    public <T> T deserialize(BufferedReader in) throws IOException, ClassNotFoundException;
    public <T> T deserialize(String str) throws IOException, ClassNotFoundException;
}
