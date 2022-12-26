package FileClose_2;

import java.io.File;
import java.io.FileWriter;

public class FileClose_2 {
	public static void main(String[] args){
		FileWriter fw_1=null;
		FileWriter fw_2=null;
		FileWriter fw_3=null;
		FileWriter fw_4=null;
		FileWriter fw_5=null;

		try{
			fw_1 = new FileWriter(File.createTempFile("rvmonitortest1", ".tmp"));
			fw_2 = new FileWriter(File.createTempFile("rvmonitortest2", ".tmp"));
			fw_3 = new FileWriter(File.createTempFile("rvmonitortest3", ".tmp"));
			fw_4 = new FileWriter(File.createTempFile("rvmonitortest4", ".tmp"));
			fw_5 = new FileWriter(File.createTempFile("rvmonitortest5", ".tmp"));

			fw_1.write("testing\n");
			rvm.FileCloseRuntimeMonitor.writeEvent(fw_1);
			fw_2.write("testing\n");
			rvm.FileCloseRuntimeMonitor.writeEvent(fw_2);
			fw_3.write("testing\n");
			rvm.FileCloseRuntimeMonitor.writeEvent(fw_3);
			fw_4.write("testing\n");
			rvm.FileCloseRuntimeMonitor.writeEvent(fw_4);
			fw_5.write("testing\n");
			rvm.FileCloseRuntimeMonitor.writeEvent(fw_5);

			fw_1.write("testing\n");
			rvm.FileCloseRuntimeMonitor.writeEvent(fw_1);
			fw_2.write("testing\n");
			rvm.FileCloseRuntimeMonitor.writeEvent(fw_2);
			fw_4.write("testing\n");
			rvm.FileCloseRuntimeMonitor.writeEvent(fw_4);
			fw_5.write("testing\n");
			rvm.FileCloseRuntimeMonitor.writeEvent(fw_5);

			fw_1.write("testing\n");
			rvm.FileCloseRuntimeMonitor.writeEvent(fw_1);
			fw_3.write("testing\n");
			rvm.FileCloseRuntimeMonitor.writeEvent(fw_3);
			fw_5.write("testing\n");
			rvm.FileCloseRuntimeMonitor.writeEvent(fw_5);

			fw_1.close();
			rvm.FileCloseRuntimeMonitor.closeEvent(fw_1);
			fw_2.close();
			rvm.FileCloseRuntimeMonitor.closeEvent(fw_2);
			fw_3.close();
			rvm.FileCloseRuntimeMonitor.closeEvent(fw_3);
			//fw_4.close();
			//fw_5.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		rvm.FileCloseRuntimeMonitor.endProgEvent();
	}
}



