package com.runtimeverification.rvmonitor.logicrepository;

import java.util.*;
import java.io.*;
import java.nio.channels.*;
import java.text.*;

public class Log {
	static boolean errState = false; // logging state

	static String prefix = "com.runtimeverification.rvmonitor.logicrepository.";
	static String suffix = ".log";

	static String logDir;
	static String input;
	static ArrayList<String> result;
	static String filename;
	static String errfilename;
	
	static String logContents = "";
	
	static public final int NOT_REPORTED = 0;
	static public final int SUCCESS = 1;
	static public final int ERROR = 2;
	static int status = NOT_REPORTED;
	
	static String errorMsg = "";
	static long time = 0;
	
	static public void init(String logDir) {
		Log.logDir = logDir;
		
		File logDirFile = new File(Log.logDir);

		if(!logDirFile.exists()){
			boolean status = logDirFile.mkdir();
			if(!status){
				errState = true;
				return;
			}
		}
		
		if(!logDirFile.exists() || !logDirFile.isDirectory()){
			errState = true;
			return;
		}

		Date date = new Date();
		DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd");

		Log.filename = Log.prefix + dateFormat.format(date) + Log.suffix;
		Log.errfilename = Log.prefix + dateFormat.format(date) + Log.suffix + ".err";
		
		File logFile = new File(Log.logDir + "/" + Log.filename);
		File errlogFile = new File(Log.logDir + "/" + Log.errfilename);

		if(!logFile.exists()){
			try{
				logFile.createNewFile();
			} catch (Exception e){
				errState = true;
				return;
			}
		}

		if(!errlogFile.exists()){
			try{
				errlogFile.createNewFile();
			} catch (Exception e){
				errState = true;
				return;
			}
		}
	}

	static public void write(String section, String contents){
		if(Log.errState)
			return;
		
		logContents += "//// " + section + "\n";
		logContents += contents + "\n";
	}
	
	static public void setStatus(int status){
		Log.status = status;
	}
	
	static public void setErrorMsg(String msg){
		Log.errorMsg = msg;
	}
	
	static public void setExecTime(long time){
		Log.time = time;
	}
	
	static public void flush(){
		if(Log.errState){
			return;
		}
		try{
			File logFile;
			
			if(Log.status == Log.SUCCESS)
				logFile = new File(Log.logDir + "/" + Log.filename);
			else
				logFile = new File(Log.logDir + "/" + Log.errfilename);
			
			FileChannel channel = new RandomAccessFile(logFile, "rw").getChannel();
			FileLock lock = channel.lock();

			FileWriter fw = new FileWriter(logFile, true);

			Date date = new Date();
			DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss");

			fw.write("///////////// A request at " + dateFormat.format(date) + " /////////////\n");
			
			switch(Log.status){
			case Log.NOT_REPORTED:
				fw.write("//// Result Status\n");
				fw.write("Uknown Status\n");
				break;
			case Log.SUCCESS:
				fw.write("//// Result Status\n");
				fw.write("Success\n");
				break;
			case Log.ERROR:
				fw.write("//// Result Status\n");
				if(Log.errorMsg != null)
					fw.write("[Error]" + Log.errorMsg + "\n");
				else
					fw.write("[Error] No error message from Logic Repository\n");
				break;
			}
			
			if(time != 0){
				fw.write("//// Execution Time\n");
				fw.write(Log.time + "ms\n");
			}
			
			fw.write(logContents);
			fw.flush();
			fw.close();
			
			lock.release();
			channel.close();
		} catch (Exception e){
			return;
		}
	}
	
}
