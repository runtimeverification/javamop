package javamop.commandline;

import javamop.Configuration;

import java.util.List;

/**
 * This class handles the filtering of properties that are used for building a javamop agent
 */
public class SpecFilter {

    private final String url;
    private final String vcs;
    private final String filterConfig;
    private final String omitFile;

    public SpecFilter() {
        url = Configuration.getServerSetting("PropertyDBURL");
        vcs = Configuration.getServerSetting("PropertyDBVCS");
        filterConfig = Configuration.getServerSetting("FilterConf");
        omitFile = Configuration.getServerSetting("OmitFile");

        boolean success = downloadAllSpecs();
        if (!success){
            //throw some exception to show that download was not successful
        }
    }

    private boolean downloadAllSpecs() {
        // use vcs and url to get the properties
        return false;  //To change body of created methods use File | Settings | File Templates.
    }


    public List<String> filterProperties() {
        //1. use the downloaded specs and filterConfig to filter out unwanted properties
        //2. omit listed files from those that survive the filter

        return null;  //To change body of created methods use File | Settings | File Templates.
    }
}
