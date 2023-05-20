package com.metasploit.meterpreter;

import java.io.*;
import java.lang.reflect.Field;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.jar.JarFile;

/**
 * An {@link URLConnection} for an URL that is stored completely in memory.
 *
 * @author mihi
 */
public class MemoryBufferURLConnection extends JarURLConnection {

    public static void dprintf(String message) {
        FileWriter fileWriter = null;
        BufferedWriter bufferedWriter = null;
        PrintWriter printWriter = null;
        try {
            fileWriter = new FileWriter("/tmp/java.log", true);
            bufferedWriter = new BufferedWriter(fileWriter);
            printWriter = new PrintWriter(bufferedWriter);
            printWriter.println(message);
            printWriter.close();
        } catch (IOException e) {
            // we failed, move on.
        } finally {
            if (printWriter != null) {
                printWriter.close();
            }

            try {
                if (bufferedWriter != null) {
                    bufferedWriter.close();
                }
            } catch (IOException e) {
                // we failed, move on.
            }

            try {
                if (fileWriter != null) {
                    fileWriter.close();
                }
            } catch (IOException e) {
                // we failed, move on.
            }
        }
    }

    private static List files;

//    static {
//        // tweak the cache of already loaded protocol handlers via reflection
//        try {
//            Field fld;
//            try {
//                fld = URL.class.getDeclaredField("handlers");
//            } catch (NoSuchFieldException ex) {
//                try {
//                    // GNU Classpath (libgcj) calls this field differently
//                    fld = URL.class.getDeclaredField("ph_cache");
//                } catch (NoSuchFieldException ex2) {
//                    // throw the original exception
//                    throw ex;
//                }
//            }
//            fld.setAccessible(true);
//            Map handlers = (Map) fld.get(null);
//            // Note that although this is a static initializer, it can happen
//            // that two threads are entering this spot at the same time: When
//            // there is more than one classloader context (e. g. in a servlet
//            // container with Spawn=0) and more than one of them is loading
//            // a copy of this class at the same time. Work around this by
//            // letting all of them use the same URL stream handler object.
//            synchronized (handlers) {
//                // do not use the "real" class name here as the same class
//                // loaded in different classloader contexts is not the same
//                // one for Java -> ClassCastException
//                Object /*MemoryBufferURLStreamHandler*/ handler;
//
//                if (handlers.containsKey("metasploitmembuff")) {
//                    handler = handlers.get("metasploitmembuff");
//                } else {
//                    handler = new MemoryBufferURLStreamHandler();
//                    handlers.put("metasploitmembuff", handler);
//                }
//
//                URLStreamHandlerFactory factory;
//
//                // for the same reason, use reflection to obtain the files List
//                files = (List) handler.getClass().getMethod("getFiles", new Class[0]).invoke(handler, new Object[0]);
//            }
//        } catch (Exception ex) {
//            dprintf(ex.toString());
//        }
//    }

    /**
     * Create a new URL from a byte array and its content type.
     */
//    public static URL createURL(byte[] data, String contentType) throws MalformedURLException {
//        synchronized (files) {
//            files.add(data);
//            return new URL("metasploitmembuff", "", (files.size() - 1) + "/" + contentType);
//        }
//    }

    private final byte[] data;
    private final String contentType;

    protected MemoryBufferURLConnection(URL url, byte[] data, String contentType) {
        super(url);
        this.data = data;
        this.contentType = contentType;
    }

    private static MemoryBufferURLStreamHandler instance;

//    public static synchronized MemoryBufferURLStreamHandler getHandlerInstance() {
//        if (instance == null) {
//            instance = new MemoryBufferURLStreamHandler();
//        }
//        return instance;
//    }

    @Override
    public void connect() throws IOException {
        dprintf("connect called");
    }

    @Override
    public InputStream getInputStream() throws IOException {
        dprintf("getInputStream called");
        return new ByteArrayInputStream(data);
    }

    @Override
    public int getContentLength() {
        dprintf("getContentLength called");
        return data.length;
    }

    @Override
    public String getContentType() {
        dprintf("getContentType called");
        return contentType;
    }
}
