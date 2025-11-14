package com.algo.inc.model;

import java.util.List;

/**
 * DownloadReport - Complete report with website and links
 */
public class DownloadReport {
    private final WebsiteReport website;
    private final List<LinkRecord> links;
    
    public DownloadReport(WebsiteReport website, List<LinkRecord> links) {
        this.website = website;
        this.links = links;
    }
    
    public String getWebsiteName() {
        return website.getWebsiteName();
    }
    
    public String getDownloadStartDateTime() {
        return website.getDownloadStartDateTime();
    }
    
    public String getDownloadEndDateTime() {
        return website.getDownloadEndDateTime();
    }
    
    public long getTotalElapsedTime() {
        return website.getTotalElapsedTime();
    }
    
    public double getTotalDownloadedKilobytes() {
        return website.getTotalDownloadedKilobytes();
    }
    
    public List<LinkRecord> getLinks() {
        return links;
    }
}
