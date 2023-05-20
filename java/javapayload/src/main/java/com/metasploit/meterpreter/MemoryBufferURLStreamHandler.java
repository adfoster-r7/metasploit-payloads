package com.metasploit.meterpreter;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.ArrayList;
import java.util.List;

/**
 * An {@link URLStreamHandler} for a {@link MemoryBufferURLConnection}
 *
 * @author mihi
 */
public class MemoryBufferURLStreamHandler extends URLStreamHandler {

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

    private final List files;

    public MemoryBufferURLStreamHandler(List files) {
        this.files = files;
    }

    @Override
    protected URLConnection openConnection(URL url) throws IOException {
        String file = url.getFile();
        // Example URL: 'metasploitmembuff:0/application/jar!/'
        int endOfProtocolPos = file.indexOf(':') + 1;
        int indexPos = file.indexOf('/');
        dprintf("Open a connection to url: " + url);
        dprintf("With file: " + file);
        byte[] data;
        synchronized (files) {
            data = (byte[]) files.get(Integer.parseInt(file.substring(endOfProtocolPos, indexPos)));
        }
        String contentType = file.substring(indexPos + 1);
        dprintf("got the index: " + Integer.parseInt(file.substring(endOfProtocolPos, indexPos)));
        dprintf("Got the content type:  " + contentType);

        return new MemoryBufferURLConnection(url, data, contentType);
    }
}
