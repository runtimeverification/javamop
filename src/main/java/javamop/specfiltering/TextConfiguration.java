// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.specfiltering;

import javamop.util.Tool;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Class for loading plain text configuration file.
 */
class TextConfiguration implements Configuration {

    private final Properties properties;

    public TextConfiguration(String fileName) {
        final Properties ret = new Properties();

        final String configPath = Tool.getConfigPath();
        final File configDir = new File(configPath);

        if(!configDir.exists()) {
            properties = null;
            return;
        }

        final File configFile = new File(configPath + File.separator + fileName);

        if(!configFile.exists()) {
            properties = null;
            return;
        }

        try {
            ret.load(new FileInputStream(configFile.getAbsolutePath()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        properties = ret;
    }

    /**
     * Retrieve a property from the server settings file, which is expected to have the name
     * given in {@code SERVER_SETTING}.
     * @param key The name of the property to retrieve.
     * @return The value of the property, or {@code null} if the settings file could not be found.
     */
    public String getServerSetting(String key) {
        if (properties == null) {
            return null;
        } else {
            return properties.getProperty(key);
        }
    }

    /**
     * Get all property settings
     *
     * @return all the property settings
     */
    public Properties getAllSettings() {
        return properties;
    }
}
