// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.specfiltering;

import java.util.Properties;

/**
 * Interface for all the configurations
 */
interface Configuration {

    /**
     * Get all the property settings for the given configuration
     *
     * @return all the property settings
     */
    public Properties getAllSettings();

    /**
     * Get one specific setting from the configuration
     *
     * @param key the key of the setting
     * @return the value of the setting
     */
    public String getServerSetting(String key);
}
