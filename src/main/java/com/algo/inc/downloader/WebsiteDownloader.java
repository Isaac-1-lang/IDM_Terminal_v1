package com.algo.inc.downloader;

import com.algo.inc.database.DatabaseManager;
import com.algo.inc.util.TerminalUI;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class WebsiteDownloader {
    
    private final DatabaseManager dbManager;
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private Set<String> downloadedUrls;
    private String baseUrl;
    private int websiteId;
    private long totalDownloadedBytes;
    private long downloadStartTime;
    private AtomicInteger totalFiles;
    private AtomicInteger completedFiles;
    
    public WebsiteDownloader(DatabaseManager dbManager) {
        this.dbManager = dbManager;
        this.downloadedUrls = new HashSet<>();
        this.totalFiles = new AtomicInteger(0);
        this.completedFiles = new AtomicInteger(0);
    }
    
    /**
     * Download a website and all its resources
     * @param url the website URL to download
     * @param downloadDir the directory to save downloaded files
     */
    public void downloadWebsite(String url, String downloadDir) throws Exception {
        this.baseUrl = url;
        this.downloadStartTime = System.currentTimeMillis();
        this.totalDownloadedBytes = 0;
        this.downloadedUrls.clear();
        this.totalFiles.set(0);
        this.completedFiles.set(0);
        
        // Create download directory
        Path dirPath = Paths.get(downloadDir);
        Files.createDirectories(dirPath);
        
        // Extract website name from URL
        String websiteName = extractWebsiteName(url);
        
        // Record start time
        String startDateTime = LocalDateTime.now().format(DATETIME_FORMATTER);
        this.websiteId = dbManager.insertWebsite(websiteName, startDateTime);
        
        // Display download info
        System.out.println();
        TerminalUI.sectionHeader("DOWNLOAD SESSION");
        TerminalUI.status("│  Target", url, TerminalUI.BRIGHT_WHITE);
        TerminalUI.status("│  Directory", downloadDir, TerminalUI.BRIGHT_CYAN);
        TerminalUI.status("│  Session ID", String.valueOf(websiteId), TerminalUI.BRIGHT_YELLOW);
        TerminalUI.status("│  Started", startDateTime, TerminalUI.BRIGHT_GREEN);
        System.out.println(TerminalUI.color("│", TerminalUI.BRIGHT_CYAN));
        System.out.println(TerminalUI.color("├─ " + TerminalUI.bold("DOWNLOAD PROGRESS"), TerminalUI.BRIGHT_CYAN));
        System.out.println();
        
        try {
            // Download the main page
            downloadPage(url, dirPath);
            
            // Record end time and statistics
            String endDateTime = LocalDateTime.now().format(DATETIME_FORMATTER);
            long totalElapsedTime = System.currentTimeMillis() - downloadStartTime;
            double totalDownloadedKilobytes = totalDownloadedBytes / 1024.0;
            
            dbManager.updateWebsite(websiteId, endDateTime, totalElapsedTime, totalDownloadedKilobytes);
            
            // Display summary
            System.out.println();
            TerminalUI.separator();
            TerminalUI.sectionHeader("DOWNLOAD SUMMARY");
            TerminalUI.status("│  Status", "COMPLETED", TerminalUI.BRIGHT_GREEN);
            TerminalUI.status("│  Files", String.format("%d/%d", completedFiles.get(), totalFiles.get()), TerminalUI.BRIGHT_CYAN);
            TerminalUI.status("│  Size", TerminalUI.formatBytes(totalDownloadedBytes), TerminalUI.BRIGHT_GREEN);
            TerminalUI.status("│  Duration", TerminalUI.formatDuration(totalElapsedTime), TerminalUI.BRIGHT_YELLOW);
            TerminalUI.status("│  Speed", 
                             String.format("%s/s", TerminalUI.formatBytes(totalDownloadedBytes * 1000.0 / totalElapsedTime)), 
                             TerminalUI.BRIGHT_MAGENTA);
            TerminalUI.status("│  Ended", endDateTime, TerminalUI.BRIGHT_GREEN);
            TerminalUI.sectionFooter();
            
        } catch (Exception e) {
            String endDateTime = LocalDateTime.now().format(DATETIME_FORMATTER);
            long totalElapsedTime = System.currentTimeMillis() - downloadStartTime;
            double totalDownloadedKilobytes = totalDownloadedBytes / 1024.0;
            dbManager.updateWebsite(websiteId, endDateTime, totalElapsedTime, totalDownloadedKilobytes);
            
            System.out.println();
            TerminalUI.error("Download failed: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * Download a single page and its resources
     */
    private void downloadPage(String url, Path basePath) throws Exception {
        if (downloadedUrls.contains(url)) {
            return;
        }
        
        downloadedUrls.add(url);
        totalFiles.incrementAndGet();
        
        long linkStartTime = System.currentTimeMillis();
        long linkBytes = 0;
        int statusCode = 0;
        
        try {
            // Download the HTML content
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");
            connection.setConnectTimeout(30000);
            connection.setReadTimeout(30000);
            statusCode = connection.getResponseCode();
            
            Document doc = Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                .timeout(30000)
                .get();
            
            // Determine file path
            URL urlObj = new URL(url);
            String path = urlObj.getPath();
            if (path.isEmpty() || path.equals("/")) {
                path = "/index.html";
            }
            
            // Create directory structure
            Path filePath = basePath.resolve(path.substring(1));
            Files.createDirectories(filePath.getParent());
            
            // Save HTML content
            String htmlContent = doc.html();
            byte[] htmlBytes = htmlContent.getBytes("UTF-8");
            Files.write(filePath, htmlBytes);
            linkBytes += htmlBytes.length;
            
            // Show progress
            long elapsed = System.currentTimeMillis() - linkStartTime;
            completedFiles.incrementAndGet();
            printDownloadStatus(url, statusCode, linkBytes, elapsed, true);
            
            // Download linked resources (CSS, JS, images)
            downloadResources(doc, basePath, url);
            
        } catch (Exception e) {
            completedFiles.incrementAndGet();
            long elapsed = System.currentTimeMillis() - linkStartTime;
            printDownloadStatus(url, statusCode > 0 ? statusCode : 0, linkBytes, elapsed, false);
            TerminalUI.error("  Failed: " + e.getMessage());
        }
        
        long linkElapsedTime = System.currentTimeMillis() - linkStartTime;
        totalDownloadedBytes += linkBytes;
        double linkKilobytes = linkBytes / 1024.0;
        
        // Record link in database
        dbManager.insertLink(url, websiteId, linkElapsedTime, linkKilobytes);
    }
    
    /**
     * Download resources linked from the HTML document
     */
    private void downloadResources(Document doc, Path basePath, String pageUrl) throws Exception {
        // Download CSS files
        Elements cssLinks = doc.select("link[rel=stylesheet]");
        for (Element link : cssLinks) {
            String href = link.attr("href");
            if (!href.isEmpty()) {
                String absoluteUrl = resolveUrl(pageUrl, href);
                if (isSameDomain(absoluteUrl)) {
                    downloadResource(absoluteUrl, basePath, "CSS");
                }
            }
        }
        
        // Download JavaScript files
        Elements jsScripts = doc.select("script[src]");
        for (Element script : jsScripts) {
            String src = script.attr("src");
            if (!src.isEmpty()) {
                String absoluteUrl = resolveUrl(pageUrl, src);
                if (isSameDomain(absoluteUrl)) {
                    downloadResource(absoluteUrl, basePath, "JS");
                }
            }
        }
        
        // Download images
        Elements images = doc.select("img[src]");
        for (Element img : images) {
            String src = img.attr("src");
            if (!src.isEmpty()) {
                String absoluteUrl = resolveUrl(pageUrl, src);
                if (isSameDomain(absoluteUrl)) {
                    downloadResource(absoluteUrl, basePath, "IMG");
                }
            }
        }
    }
    
    /**
     * Download a resource file
     */
    private void downloadResource(String url, Path basePath, String type) throws Exception {
        if (downloadedUrls.contains(url)) {
            return;
        }
        
        downloadedUrls.add(url);
        totalFiles.incrementAndGet();
        
        String filename = extractFilename(url);
        long linkStartTime = System.currentTimeMillis();
        long linkBytes = 0;
        int statusCode = 0;
        
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");
            connection.setConnectTimeout(30000);
            connection.setReadTimeout(30000);
            statusCode = connection.getResponseCode();
            
            URL urlObj = new URL(url);
            String path = urlObj.getPath();
            if (path.isEmpty() || path.equals("/")) {
                completedFiles.incrementAndGet();
                return;
            }
            
            // Create directory structure
            Path filePath = basePath.resolve(path.substring(1));
            Files.createDirectories(filePath.getParent());
            
            // Download file with progress
            try (InputStream in = connection.getInputStream();
                 FileOutputStream out = new FileOutputStream(filePath.toFile())) {
                
                byte[] buffer = new byte[8192];
                int bytesRead;
                long lastUpdate = System.currentTimeMillis();
                
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                    linkBytes += bytesRead;
                    
                    // Update progress every 100ms
                    long now = System.currentTimeMillis();
                    if (now - lastUpdate > 100) {
                        long elapsed = now - linkStartTime;
                        printResourceProgress(type, filename, linkBytes, elapsed, statusCode);
                        lastUpdate = now;
                    }
                }
            }
            
            long elapsed = System.currentTimeMillis() - linkStartTime;
            completedFiles.incrementAndGet();
            printDownloadStatus(url, statusCode, linkBytes, elapsed, true);
            
        } catch (Exception e) {
            completedFiles.incrementAndGet();
            long elapsed = System.currentTimeMillis() - linkStartTime;
            printDownloadStatus(url, statusCode > 0 ? statusCode : 0, linkBytes, elapsed, false);
            // Don't print error for resources to avoid clutter
        }
        
        long linkElapsedTime = System.currentTimeMillis() - linkStartTime;
        totalDownloadedBytes += linkBytes;
        double linkKilobytes = linkBytes / 1024.0;
        
        // Record link in database
        dbManager.insertLink(url, websiteId, linkElapsedTime, linkKilobytes);
    }
    
    /**
     * Print download status
     */
    private void printDownloadStatus(String url, int statusCode, long bytes, long elapsedMs, boolean success) {
        String statusColor = success ? TerminalUI.BRIGHT_GREEN : TerminalUI.BRIGHT_RED;
        String statusIcon = success ? TerminalUI.CHECK : TerminalUI.CROSS;
        
        String statusCodeStr = statusCode > 0 ? 
            TerminalUI.color(String.valueOf(statusCode), statusColor) : 
            TerminalUI.dim("---");
        
        String sizeStr = TerminalUI.color(TerminalUI.formatBytes(bytes), TerminalUI.BRIGHT_CYAN);
        String timeStr = TerminalUI.color(TerminalUI.formatDuration(elapsedMs), TerminalUI.BRIGHT_YELLOW);
        String filename = TerminalUI.dim(truncate(extractFilename(url), 35));
        
        String progress = String.format("[%s/%s] %s %s %s %s %s",
            TerminalUI.color(String.valueOf(completedFiles.get()), TerminalUI.BRIGHT_CYAN),
            TerminalUI.color(String.valueOf(totalFiles.get()), TerminalUI.DIM),
            TerminalUI.color(statusIcon, statusColor),
            statusCodeStr,
            sizeStr,
            timeStr,
            filename
        );
        
        System.out.println("  " + progress);
    }
    
    /**
     * Print resource download progress
     */
    private void printResourceProgress(String type, String filename, long bytes, long elapsedMs, int statusCode) {
        TerminalUI.clearLine();
        String typeColor = type.equals("CSS") ? TerminalUI.BRIGHT_BLUE : 
                          type.equals("JS") ? TerminalUI.BRIGHT_YELLOW : 
                          TerminalUI.BRIGHT_MAGENTA;
        
        String typeLabel = TerminalUI.color("[" + type + "]", typeColor);
        String sizeStr = TerminalUI.color(TerminalUI.formatBytes(bytes), TerminalUI.BRIGHT_CYAN);
        String speed = elapsedMs > 0 ? 
            TerminalUI.color(TerminalUI.formatBytes(bytes * 1000.0 / elapsedMs) + "/s", TerminalUI.BRIGHT_GREEN) : 
            TerminalUI.dim("---");
        String fileStr = TerminalUI.dim(truncate(filename, 30));
        
        System.out.print("  " + typeLabel + " " + sizeStr + " @ " + speed + " " + fileStr);
    }
    
    /**
     * Extract filename from URL
     */
    private String extractFilename(String url) {
        try {
            URL urlObj = new URL(url);
            String path = urlObj.getPath();
            if (path.isEmpty() || path.equals("/")) {
                return "index.html";
            }
            String[] parts = path.split("/");
            return parts[parts.length - 1];
        } catch (Exception e) {
            return url;
        }
    }
    
    /**
     * Truncate string
     */
    private String truncate(String str, int maxLength) {
        if (str == null || str.length() <= maxLength) {
            return str != null ? str : "";
        }
        return str.substring(0, maxLength - 3) + "...";
    }
    
    /**
     * Resolve a relative URL to an absolute URL
     */
    private String resolveUrl(String baseUrl, String relativeUrl) throws Exception {
        URL base = new URL(baseUrl);
        URL resolved = new URL(base, relativeUrl);
        return resolved.toString();
    }
    
    /**
     * Check if URL is from the same domain
     */
    private boolean isSameDomain(String url) {
        try {
            URL urlObj = new URL(url);
            URL baseObj = new URL(baseUrl);
            return urlObj.getHost().equals(baseObj.getHost());
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Extract website name from URL
     */
    private String extractWebsiteName(String url) {
        try {
            URL urlObj = new URL(url);
            String host = urlObj.getHost();
            // Remove www. if present
            if (host.startsWith("www.")) {
                host = host.substring(4);
            }
            return host;
        } catch (Exception e) {
            return url;
        }
    }
}
