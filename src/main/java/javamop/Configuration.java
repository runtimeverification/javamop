package javamop;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import javamop.util.Tool;

/**
 * A class with static methods for loading configuration files.
 */
public final class Configuration {
    private static final String SERVER_SETTING= "remote_server_addr.properties";
    private static Properties serverSetting = null;
    
    /**
     * Private to prevent instantiation.
     */
    private Configuration(){
    }
    
    /**
     * Load program settings from a file at a given path.
     * @param filename The path to the file to load.
     * @return The properties described in the file.
     */
    static public Properties getSettingFile(final String filename){
        final Properties ret = new Properties();
        
        final String configPath = Tool.getConfigPath();
        final File configDir = new File(configPath);
        
        if(!configDir.exists())
            return null;
        
        final File configFile = new File(configPath + File.separator + filename);
        
        if(!configFile.exists())
            return null;
        
        try {
            ret.load(new FileInputStream(configFile.getAbsolutePath()));
        } catch (IOException e) {
            e.printStackTrace();
        } 
        
        return ret;
    }
    
    /**
     * Retrieve a property from the server settings file, which is expected to have the name
     * given in {@code SERVER_SETTING}.
     * @param key The name of the property to retrieve.
     * @return The value of the property, or {@code null} if the settings file could not be found.
     */
    static public String getServerSetting(String key){
        if(serverSetting == null){
            serverSetting = getSettingFile(SERVER_SETTING);
        }
        
        if(serverSetting != null){
            return serverSetting.getProperty(key);
        }
        return null;
    }
    
    /**
     * Retrieve the server address property from the server properties file.
     * @return The server address, or {@code null} if the settings file cannot be found.
     */
    static public String getServerAddr(){
        return getServerSetting("ServerAddr");
    }
    
}
