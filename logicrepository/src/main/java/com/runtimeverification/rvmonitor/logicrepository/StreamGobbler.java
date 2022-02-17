package com.runtimeverification.rvmonitor.logicrepository;

import java.io.*;

public class StreamGobbler extends Thread {
    private final InputStream is;
    private final StringBuilder text;
    
    public StreamGobbler(final InputStream is) {
        this.is = is;
        text = new StringBuilder();
    }
    
    public void run() {
        try {
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            while ((line = br.readLine()) != null){
                text.append(line).append("\n");
            }
        } catch (IOException ioe) {
            //          ioe.printStackTrace();
        }
    }
    
    public String getText() {
        return text.toString();
    }
}
