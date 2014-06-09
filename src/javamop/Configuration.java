package javamop;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import javamop.util.Tool;

public class Configuration {
    private static final String SERVER_SETTING= "remote_server_addr.properties";
    private static Properties serverSetting = null;
    
    public Configuration(){
    }
    
    static public Properties getSettingFile(String filename){
        Properties ret = new Properties();
        
        String configPath = Tool.getConfigPath();
        File configDir = new File(configPath);
        
        if(!configDir.exists())
            return null;
        
        File configFile = new File(configPath + File.separator + filename);
        
        if(!configFile.exists())
            return null;
        
        try {
            ret.load(new FileInputStream(configFile.getAbsolutePath()));
        } catch (IOException e) {
            e.printStackTrace();
        } 
        
        return ret;
    }
    
    static public String getServerSetting(String key){
        if(serverSetting == null){
            serverSetting = getSettingFile(SERVER_SETTING);
        }
        
        if(serverSetting != null){
            return serverSetting.getProperty(key);
        }
        return null;
    }
    
    static public String getServerAddr(){
        
        return getServerSetting("ServerAddr");
    }
    
}
