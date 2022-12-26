package SafeFileWriter_1;

import rvm.SafeFileWriterRuntimeMonitor;

import java.io.File;
import java.io.FileWriter;

public class SafeFileWriter_1 {
	public static void main(String[] args){
		FileWriter fw_1=null;
		FileWriter fw_2=null;
		FileWriter fw_3=null;
		FileWriter fw_4=null;
		FileWriter fw_5=null;
		try{
			fw_1 = new FileWriter(File.createTempFile("rvmonitortest1", ".tmp"));
			SafeFileWriterRuntimeMonitor.openEvent(fw_1);
			fw_2 = new FileWriter(File.createTempFile("rvmonitortest2", ".tmp"));
			SafeFileWriterRuntimeMonitor.openEvent(fw_2);
			fw_3 = new FileWriter(File.createTempFile("rvmonitortest3", ".tmp"));
			SafeFileWriterRuntimeMonitor.openEvent(fw_3);
			fw_4 = new FileWriter(File.createTempFile("rvmonitortest4", ".tmp"));
			SafeFileWriterRuntimeMonitor.openEvent(fw_4);
			fw_5 = new FileWriter(File.createTempFile("rvmonitortest5", ".tmp"));
			SafeFileWriterRuntimeMonitor.openEvent(fw_5);

			SafeFileWriterRuntimeMonitor.writeEvent(fw_1);
			fw_1.write("testing\n");
			SafeFileWriterRuntimeMonitor.writeEvent(fw_2);
			fw_2.write("testing\n");
			SafeFileWriterRuntimeMonitor.writeEvent(fw_3);
			fw_3.write("testing\n");
			SafeFileWriterRuntimeMonitor.writeEvent(fw_4);
			fw_4.write("testing\n");
			SafeFileWriterRuntimeMonitor.writeEvent(fw_5);
			fw_5.write("testing\n");

			SafeFileWriterRuntimeMonitor.writeEvent(fw_1);
			fw_1.write("testing\n");
			SafeFileWriterRuntimeMonitor.writeEvent(fw_2);
			fw_2.write("testing\n");
			SafeFileWriterRuntimeMonitor.writeEvent(fw_4);
			fw_4.write("testing\n");
			SafeFileWriterRuntimeMonitor.writeEvent(fw_5);
			fw_5.write("testing\n");

			SafeFileWriterRuntimeMonitor.writeEvent(fw_1);
			fw_1.write("testing\n");
			SafeFileWriterRuntimeMonitor.writeEvent(fw_3);
			fw_3.write("testing\n");
			SafeFileWriterRuntimeMonitor.writeEvent(fw_5);
			fw_5.write("testing\n");

			fw_1.close();
			SafeFileWriterRuntimeMonitor.closeEvent(fw_1);
			fw_2.close();
			SafeFileWriterRuntimeMonitor.closeEvent(fw_2);
			fw_3.close();
			SafeFileWriterRuntimeMonitor.closeEvent(fw_3);
			fw_4.close();
			SafeFileWriterRuntimeMonitor.closeEvent(fw_4);
			fw_5.close();
			SafeFileWriterRuntimeMonitor.closeEvent(fw_5);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		try{
			SafeFileWriterRuntimeMonitor.writeEvent(fw_1);
			fw_1.write("testing\n");
		} catch (Exception e) {
		}
		try{
			SafeFileWriterRuntimeMonitor.writeEvent(fw_2);
			fw_2.write("testing\n");
		} catch (Exception e) {
		}
		try{
			SafeFileWriterRuntimeMonitor.writeEvent(fw_3);
			fw_4.write("testing\n");
		} catch (Exception e) {
		}
	}
}



