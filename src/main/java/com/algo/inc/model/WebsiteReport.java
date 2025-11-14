package com.algo.inc.model;

/**
 * WebsiteReport - Represents a website download record
 */
public class WebsiteReport {
    private int id;
    private String websiteName;
    private String downloadStartDateTime;
    private String downloadEndDateTime;
    private long totalElapsedTime;
    private double totalDownloadedKilobytes;
    
    public WebsiteReport(int id, String websiteName, String downloadStartDateTime,
                        String downloadEndDateTime, long totalElapsedTime,
                        double totalDownloadedKilobytes) {
        this.id = id;
        this.websiteName = websiteName;
        this.downloadStartDateTime = downloadStartDateTime;
        this.downloadEndDateTime = downloadEndDateTime;
        this.totalElapsedTime = totalElapsedTime;
        this.totalDownloadedKilobytes = totalDownloadedKilobytes;
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getWebsiteName() { return websiteName; }
    public void setWebsiteName(String websiteName) { this.websiteName = websiteName; }
    
    public String getDownloadStartDateTime() { return downloadStartDateTime; }
    public void setDownloadStartDateTime(String downloadStartDateTime) {
        this.downloadStartDateTime = downloadStartDateTime;
    }
    
    public String getDownloadEndDateTime() { return downloadEndDateTime; }
    public void setDownloadEndDateTime(String downloadEndDateTime) {
        this.downloadEndDateTime = downloadEndDateTime;
    }
    
    public long getTotalElapsedTime() { return totalElapsedTime; }
    public void setTotalElapsedTime(long totalElapsedTime) {
        this.totalElapsedTime = totalElapsedTime;
    }
    
    public double getTotalDownloadedKilobytes() { return totalDownloadedKilobytes; }
    public void setTotalDownloadedKilobytes(double totalDownloadedKilobytes) {
        this.totalDownloadedKilobytes = totalDownloadedKilobytes;
    }
}

