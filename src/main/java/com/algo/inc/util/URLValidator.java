package com.algo.inc.util;

import java.net.URL;
import java.net.MalformedURLException;

public class URLValidator {
    
    /**
     * Validates if a string is a valid URL
     * @param urlString the URL string to validate
     * @return true if valid URL, false otherwise
     */
    public static boolean isValid(String urlString) {
        if (urlString == null || urlString.trim().isEmpty()) {
            return false;
        }
        
        try {
            URL url = new URL(urlString);
            String protocol = url.getProtocol();
            return protocol != null && (protocol.equals("http") || protocol.equals("https"));
        } catch (MalformedURLException e) {
            return false;
        }
    }
    
    /**
     * Normalizes a URL by adding protocol if missing and ensuring proper format
     * @param urlString the URL string to normalize
     * @return normalized URL string
     */
    public static String normalize(String urlString) {
        if (urlString == null || urlString.trim().isEmpty()) {
            return urlString;
        }
        
        urlString = urlString.trim();
        
        // Remove trailing slash
        if (urlString.endsWith("/")) {
            urlString = urlString.substring(0, urlString.length() - 1);
        }
        
        // Add protocol if missing
        if (!urlString.startsWith("http://") && !urlString.startsWith("https://")) {
            urlString = "https://" + urlString;
        }
        
        return urlString;
    }
}
