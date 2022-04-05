
package SafeFileWriter_2;

import rvm.SafeFileWriterRuntimeMonitor;

import java.io.File;
import java.io.FileWriter;

public class SafeFileWriter_2 {
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

			rvm.SafeFileWriterRuntimeMonitor.writeEvent(fw_1);
			fw_1.write("testing\n");
			rvm.SafeFileWriterRuntimeMonitor.writeEvent(fw_2);
			fw_2.write("testing\n");
			rvm.SafeFileWriterRuntimeMonitor.writeEvent(fw_3);
			fw_3.write("testing\n");
			rvm.SafeFileWriterRuntimeMonitor.writeEvent(fw_4);
			fw_4.write("testing\n");
			rvm.SafeFileWriterRuntimeMonitor.writeEvent(fw_5);
			fw_5.write("testing\n");

			rvm.SafeFileWriterRuntimeMonitor.writeEvent(fw_1);
			fw_1.write("testing\n");
			rvm.SafeFileWriterRuntimeMonitor.writeEvent(fw_2);
			fw_2.write("testing\n");
			rvm.SafeFileWriterRuntimeMonitor.writeEvent(fw_4);
			fw_4.write("testing\n");
			rvm.SafeFileWriterRuntimeMonitor.writeEvent(fw_5);
			fw_5.write("testing\n");

			rvm.SafeFileWriterRuntimeMonitor.writeEvent(fw_1);
			fw_1.write("testing\n");
			rvm.SafeFileWriterRuntimeMonitor.writeEvent(fw_3);
			fw_3.write("testing\n");
			rvm.SafeFileWriterRuntimeMonitor.writeEvent(fw_5);
			fw_5.write("testing\n");

			fw_1.close();
			rvm.SafeFileWriterRuntimeMonitor.closeEvent(fw_1);
			fw_2.close();
			rvm.SafeFileWriterRuntimeMonitor.closeEvent(fw_2);
			fw_3.close();
			rvm.SafeFileWriterRuntimeMonitor.closeEvent(fw_3);
			//fw_4.close();
			//fw_5.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}



