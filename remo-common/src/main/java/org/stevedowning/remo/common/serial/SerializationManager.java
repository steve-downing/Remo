package org.stevedowning.remo.common.serial;

import java.io.BufferedReader;
import java.io.PrintWriter;

public interface SerializationManager {
    public void serialize(PrintWriter out, Object obj);
    public String serialize(Object obj);
    public <T> T deserialize(BufferedReader in);
}
