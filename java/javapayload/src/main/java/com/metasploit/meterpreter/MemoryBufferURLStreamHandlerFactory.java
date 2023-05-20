package com.metasploit.meterpreter;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;
import java.util.ArrayList;
import java.util.List;

public class MemoryBufferURLStreamHandlerFactory implements URLStreamHandlerFactory {
    private static List files = new ArrayList();
    private static MemoryBufferURLStreamHandlerFactory instance;

    public static synchronized MemoryBufferURLStreamHandlerFactory getInstance() {
        if (instance == null) {
            MemoryBufferURLStreamHandlerFactory.instance = new MemoryBufferURLStreamHandlerFactory();
        }
        return instance;
    }

    public synchronized static URL createURL(byte[] data, String contentType) throws MalformedURLException {
        synchronized (files) {
            files.add(data);
            return new URL("metasploitmembuff", "", -1, (files.size() - 1) + "/" + contentType, getInstance().createURLStreamHandler("metasploitmembuff"));
        }
    }

    @Override
    public URLStreamHandler createURLStreamHandler(String protocol) {
        return new MemoryBufferURLStreamHandler(files);
    }
}