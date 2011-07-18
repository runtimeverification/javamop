package javamop.util;

import java.io.*;

public class StreamGobbler extends Thread {
	InputStream is;
	public String text = "";

	public StreamGobbler(InputStream is) {
		this.is = is;
	}

	public void run() {
		try {
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String line = null;
			while ((line = br.readLine()) != null){
				text += line + "\n";
			}
		} catch (IOException ioe) {
//			ioe.printStackTrace();
		}
	}
}
