package com.runtimeverification.rvmonitor.java.rt.util;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class TraceUtil {

    private static Map<String, Integer> locationMap = new HashMap<>();

    private static int freshID = 1;

    public static File artifactsDir = null;

    /**
     * This method reduces the size of stored traces.
     *
     * @param fullLOC E.g., org.apache.commons.fileupload2.MultipartStream$ItemInputStream.close(MultipartStream.java:950),
     * @return A short location ID, e.g., loc2.
     */
    public static Integer getShortLocation(String fullLOC) {
        Integer shortLocation = locationMap.get(fullLOC);
        if (shortLocation == null) {
            // we do not have the fullLOC in the map; add it and return the shortLocation
            shortLocation =  freshID++;
            locationMap.put(fullLOC, shortLocation);
        }
        return shortLocation;
    }

    public static Map<String, Integer> getLocationMap() {
        return locationMap;
    }

    public static String getAbsolutePath(String fileName) {
        return new File(artifactsDir + File.separator + fileName).getAbsolutePath();
    }
}
