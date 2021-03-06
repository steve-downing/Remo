package org.suporma.remo.internal.common.serial;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;
import com.sun.org.apache.xml.internal.security.utils.Base64;

public class DefaultSerializationManager implements SerializationManager {
    @SuppressWarnings("unchecked")
    public <T> T deserialize(String str)
            throws IOException, ClassNotFoundException {
        byte[] bytes;
        try {
            bytes = Base64.decode(str.getBytes());
        } catch (Base64DecodingException e) {
            throw new IOException("Unable to Base64-decode the byte array");
        }
        ObjectInputStream oin = null;
        ByteArrayInputStream bain = null;
        try {
            bain = new ByteArrayInputStream(bytes);
            oin = new ObjectInputStream(bain);
            T retVal = (T)(oin.readObject());
            return retVal;
        } finally {
            if (oin != null) oin.close();
            if (bain != null) bain.close();
        }
    }

    public String serialize(Object obj) throws IOException {
        ObjectOutputStream oout = null;
        ByteArrayOutputStream baout = null;
        try {
            baout = new ByteArrayOutputStream();
            oout = new ObjectOutputStream(baout);
            oout.writeObject(obj);
            byte[] bytes = baout.toByteArray();
            String retVal = new String(Base64.encode(bytes));
            return retVal;
        } finally {
            if (oout != null) oout.close();
            if (baout != null) baout.close();
        }
    }

    public void serialize(OutputStream out, Object obj) throws IOException {
        ObjectOutputStream oout = new ObjectOutputStream(out);
        oout.writeObject(obj);
    }

    @SuppressWarnings("unchecked")
    public <T> T deserialize(InputStream in) throws IOException, ClassNotFoundException {
        ObjectInputStream oin = new ObjectInputStream(in);
        return (T)(oin.readObject());
    }
}
