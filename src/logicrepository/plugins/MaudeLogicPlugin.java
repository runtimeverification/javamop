package logicrepository.plugins;

import java.io.File;

public abstract class MaudeLogicPlugin extends LogicPlugin{

	public String executeMaude(String input){
		return null;
	}
	
	public static String getMaudeProgramPath(String maudePath){
		File maude = null;
		
		if (!checkMaudeProgramPath(maudePath))
			return null;
		
		maude = new File(maudePath);
		
		if(maude.isDirectory()){
			if(checkMaudeProgramPath(maudePath + "/maude"))
				return maudePath + "/maude";
			if(checkMaudeProgramPath(maudePath + "/maude.linux"))
				return maudePath + "/maude.linux";
			if(checkMaudeProgramPath(maudePath + "/maude.intelDarwin"))
				return maudePath + "/maude.intelDarwin";
			if(checkMaudeProgramPath(maudePath + "\\maude.exe"))
				return maudePath + "\\maude.exe";
			return null;
		}
		
		return maudePath;		
	}
	
	public static boolean checkMaudeProgramPath(String maudePath){
		File maude = null;
		
		if (maudePath == null || maudePath.length() == 0)
			return false;
		
		maude = new File(maudePath);
		if(!maude.exists())
			return false;
		
		if(maude.isDirectory()){
			if(checkMaudeProgramPath(maudePath + "/maude"))
				return true;
			if(checkMaudeProgramPath(maudePath + "/maude.linux"))
				return true;
			if(checkMaudeProgramPath(maudePath + "\\maude.exe"))
				return true;
			return false;
		}
		
		return maude.canExecute();
	}
	
	public static String getMaudePath(){
		// Get Maude Path
		String maudePath = System.getenv("MAUDEPATH");
		String[] maudePaths = {System.getenv("MAUDE"), "/usr/local/maude-linux", "/usr/local/maude", "/usr/local/bin/maude-intelDarwin", "/usr/local/bin/maude-linux", "/usr/local/bin/maude", 
				"/home/software/maude24", "C:\\Program Files\\MaudeFW", "C:\\MaudeFW"};
		int i = 0;
		while (!checkMaudeProgramPath(maudePath) && i < maudePaths.length) {
			maudePath = maudePaths[i++];
		}
		if( i == maudePaths.length)
			maudePath = null;
		maudePath = getMaudeProgramPath(maudePath);

		return maudePath;
	}

	
	
}
