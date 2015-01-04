package org.suporma.remo.internal.common.serial;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface SerializationManager {
    public void serialize(OutputStream out, Object obj) throws IOException;
    public String serialize(Object obj) throws IOException;
    public <T> T deserialize(InputStream in) throws IOException, ClassNotFoundException;
    public <T> T deserialize(String str) throws IOException, ClassNotFoundException;
}
