package com.algo.inc;

import com.algo.inc.database.DatabaseManager;
import com.algo.inc.downloader.WebsiteDownloader;
import com.algo.inc.model.DownloadReport;
import com.algo.inc.model.LinkRecord;
import com.algo.inc.model.WebsiteReport;
import com.algo.inc.util.TerminalUI;
import com.algo.inc.util.URLValidator;

import java.util.List;
import java.util.Scanner;

/**
 * Main entry point for the Web Download Manager application.
 * Algorithm Inc - Web Download Service
 */
public class WebDownloadManager {
    
    private static final Scanner scanner = new Scanner(System.in);
    private static final DatabaseManager dbManager = new DatabaseManager();
    
    public static void main(String[] args) {
        TerminalUI.printBanner();
        
        // Initialize database
        // TerminalUI.info("Initializing database connection...");
        dbManager.initializeDatabase();
        
        boolean running = true;
        while (running) {
            TerminalUI.printMenu();
            int choice = getChoice();
            
            switch (choice) {
                case 1:
                    downloadWebsite();
                    break;
                case 2:
                    viewDownloadHistory();
                    break;
                case 3:
                    viewWebsiteReport();
                    break;
                case 4:
                    running = false;
                    TerminalUI.success("Session terminated");
                    System.out.println();
                    break;
                default:
                    TerminalUI.error("Invalid option. Please try again.");
            }
        }
        
        scanner.close();
        dbManager.close();
    }
    
    private static int getChoice() {
        try {
            String input = scanner.nextLine().trim();
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
    
    private static void downloadWebsite() {
        TerminalUI.sectionHeader("DOWNLOAD WEBSITE");
        
        // Get URL from user
        System.out.print(TerminalUI.color("│  URL", TerminalUI.DIM) + " " + 
                         TerminalUI.color("→", TerminalUI.BRIGHT_YELLOW) + " ");
        String url = scanner.nextLine().trim();
        
        // Validate URL
        if (!URLValidator.isValid(url)) {
            TerminalUI.error("Oops! Invalid URL format!");
            TerminalUI.info("Example: https://gmail@isaac.com");
            TerminalUI.sectionFooter();
            return;
        }
        
        // Normalize URL
        url = URLValidator.normalize(url);
        TerminalUI.status("  Validated", url, TerminalUI.BRIGHT_GREEN);
        
        // Get download directory
        System.out.print(TerminalUI.color("│  Directory", TerminalUI.DIM) + " " + 
                         TerminalUI.color("→", TerminalUI.BRIGHT_YELLOW) + " ");
        String downloadDir = scanner.nextLine().trim();
        if (downloadDir.isEmpty()) {
            downloadDir = System.getProperty("user.dir") + "/downloads";
        }
        TerminalUI.status("  Target", downloadDir, TerminalUI.BRIGHT_CYAN);
        TerminalUI.sectionFooter();
        
        System.out.println();
        
        // Start download
        WebsiteDownloader downloader = new WebsiteDownloader(dbManager);
        try {
            downloader.downloadWebsite(url, downloadDir);
            System.out.println();
            TerminalUI.success("Download completed successfully");
        } catch (Exception e) {
            System.out.println();
            TerminalUI.error("Download failed: " + e.getMessage());
            if (e.getCause() != null) {
                TerminalUI.warning("Cause: " + e.getCause().getMessage());
            }
        }
        
        System.out.println();
        TerminalUI.separator();
    }
    
    private static void viewDownloadHistory() {
        TerminalUI.sectionHeader("DOWNLOAD HISTORY");
        
        List<WebsiteReport> websites = dbManager.getAllWebsites();
        
        if (websites.isEmpty()) {
            TerminalUI.warning("No download history found");
            TerminalUI.sectionFooter();
            return;
        }
        
        System.out.println();
        TerminalUI.tableHeader("ID", "Website", "Start Time", "Duration", "Size");
        
        for (WebsiteReport website : websites) {
            String startDateTime = website.getDownloadStartDateTime();
            String displayDateTime = (startDateTime != null && startDateTime.length() >= 19) 
                ? startDateTime.substring(0, 19).replace(" ", " ") 
                : (startDateTime != null ? startDateTime : "N/A");
            
            String id = TerminalUI.color(String.valueOf(website.getId()), TerminalUI.BRIGHT_CYAN);
            String name = TerminalUI.color(truncate(website.getWebsiteName(), 25), TerminalUI.WHITE);
            String time = TerminalUI.dim(displayDateTime);
            String duration = TerminalUI.color(
                String.format("%.2fs", website.getTotalElapsedTime() / 1000.0), 
                TerminalUI.BRIGHT_YELLOW
            );
            String size = TerminalUI.color(
                String.format("%.2f KB", website.getTotalDownloadedKilobytes()), 
                TerminalUI.BRIGHT_GREEN
            );
            
            TerminalUI.tableRow(id, name, time, duration, size);
        }
        
        System.out.println();
        TerminalUI.tableFooter(5, new int[]{4, 25, 19, 10, 10});
        TerminalUI.sectionFooter();
    }
    
    private static void viewWebsiteReport() {
        TerminalUI.sectionHeader("WEBSITE REPORT");
        
        System.out.print(TerminalUI.color("│  Website ID/Name", TerminalUI.DIM) + " " + 
                         TerminalUI.color("→", TerminalUI.BRIGHT_YELLOW) + " ");
        String input = scanner.nextLine().trim();
        
        DownloadReport report = null;
        
        // Try to parse as ID first
        try {
            int id = Integer.parseInt(input);
            report = dbManager.getDownloadReport(id);
        } catch (NumberFormatException e) {
            // Try as website name
            report = dbManager.getDownloadReportByName(input);
        }
        
        if (report == null) {
            TerminalUI.error("Website not found in database");
            TerminalUI.sectionFooter();
            return;
        }
        
        // Display report
        System.out.println();
        System.out.println(TerminalUI.color("┌─ " + TerminalUI.bold("DOWNLOAD REPORT"), TerminalUI.BRIGHT_CYAN));
        System.out.println(TerminalUI.color("│", TerminalUI.BRIGHT_CYAN));
        
        TerminalUI.status("│  Website", report.getWebsiteName(), TerminalUI.BRIGHT_WHITE);
        TerminalUI.status("│  Start", report.getDownloadStartDateTime(), TerminalUI.BRIGHT_CYAN);
        TerminalUI.status("│  End", report.getDownloadEndDateTime() != null ? 
                         report.getDownloadEndDateTime() : "N/A", TerminalUI.BRIGHT_CYAN);
        TerminalUI.status("│  Duration", 
                         String.format("%.2fs", report.getTotalElapsedTime() / 1000.0), 
                         TerminalUI.BRIGHT_YELLOW);
        TerminalUI.status("│  Size", 
                         String.format("%.2f KB", report.getTotalDownloadedKilobytes()), 
                         TerminalUI.BRIGHT_GREEN);
        TerminalUI.status("│  Links", 
                         String.valueOf(report.getLinks().size()), 
                         TerminalUI.BRIGHT_MAGENTA);
        
        System.out.println(TerminalUI.color("│", TerminalUI.BRIGHT_CYAN));
        System.out.println(TerminalUI.color("├─ " + TerminalUI.bold("DOWNLOADED RESOURCES"), TerminalUI.BRIGHT_CYAN));
        System.out.println();
        
        if (report.getLinks().isEmpty()) {
            TerminalUI.warning("  No resources downloaded");
        } else {
            TerminalUI.tableHeader("Resource", "Duration", "Size");
            
            for (LinkRecord link : report.getLinks()) {
                String resource = TerminalUI.color(
                    truncate(link.getLinkName(), 45), 
                    TerminalUI.WHITE
                );
                String duration = TerminalUI.color(
                    TerminalUI.formatDuration(link.getTotalElapsedTime()), 
                    TerminalUI.BRIGHT_YELLOW
                );
                String size = TerminalUI.color(
                    String.format("%.2f KB", link.getTotalDownloadedKilobytes()), 
                    TerminalUI.BRIGHT_GREEN
                );
                
                TerminalUI.tableRow(resource, duration, size);
            }
            
            System.out.println();
            TerminalUI.tableFooter(3, new int[]{45, 10, 10});
        }
        
        System.out.println();
        TerminalUI.sectionFooter();
    }
    
    private static String truncate(String str, int maxLength) {
        if (str == null || str.length() <= maxLength) {
            return str != null ? str : "";
        }
        return str.substring(0, maxLength - 3) + "...";
    }
}
