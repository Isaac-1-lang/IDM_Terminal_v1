package com.algo.inc.model;

/**
 * LinkRecord - Represents a downloaded link/resource
 */
public class LinkRecord {
    private int id;
    private String linkName;
    private int websiteId;
    private long totalElapsedTime;
    private double totalDownloadedKilobytes;
    
    public LinkRecord(int id, String linkName, int websiteId,
                     long totalElapsedTime, double totalDownloadedKilobytes) {
        this.id = id;
        this.linkName = linkName;
        this.websiteId = websiteId;
        this.totalElapsedTime = totalElapsedTime;
        this.totalDownloadedKilobytes = totalDownloadedKilobytes;
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getLinkName() { return linkName; }
    public void setLinkName(String linkName) { this.linkName = linkName; }
    
    public int getWebsiteId() { return websiteId; }
    public void setWebsiteId(int websiteId) { this.websiteId = websiteId; }
    
    public long getTotalElapsedTime() { return totalElapsedTime; }
    public void setTotalElapsedTime(long totalElapsedTime) {
        this.totalElapsedTime = totalElapsedTime;
    }
    
    public double getTotalDownloadedKilobytes() { return totalDownloadedKilobytes; }
    public void setTotalDownloadedKilobytes(double totalDownloadedKilobytes) {
        this.totalDownloadedKilobytes = totalDownloadedKilobytes;
    }
}

