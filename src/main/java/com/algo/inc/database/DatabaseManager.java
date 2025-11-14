package com.algo.inc.database;

import com.algo.inc.model.DownloadReport;
import com.algo.inc.model.LinkRecord;
import com.algo.inc.model.WebsiteReport;
import com.algo.inc.util.TerminalUI;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    
    // PostgreSQL connection parameters - can be overridden by system properties
    private static final String DB_HOST = System.getProperty("db.host", "localhost");
    private static final String DB_PORT = System.getProperty("db.port", "5432");
    private static final String DB_NAME = System.getProperty("db.name", "regex");
    private static final String DB_USER = System.getProperty("db.user", "postgres");
    private static final String DB_PASSWORD = System.getProperty("db.password", "121402pr0732021");
    private static final String DB_URL = String.format("jdbc:postgresql://%s:%s/%s", DB_HOST, DB_PORT, DB_NAME);
    
    private Connection connection;
    
    /**
     * Initialize the database and create tables if they don't exist
     */
    public void initializeDatabase() {
        try {
            TerminalUI.status("  Connecting", DB_HOST + ":" + DB_PORT + "/" + DB_NAME, TerminalUI.BRIGHT_CYAN);
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            createTables();
            TerminalUI.success("Database connection established");
        } catch (SQLException e) {
            TerminalUI.error("Database connection failed: " + e.getMessage());
            TerminalUI.warning("Connection URL: " + DB_URL);
            e.printStackTrace();
        }
    }
    
    /**
     * Create database tables if they don't exist
     */
    private void createTables() throws SQLException {
        // PostgreSQL uses SERIAL for auto-increment
        String createWebsitesTable = "CREATE TABLE IF NOT EXISTS websites (" +
            "id SERIAL PRIMARY KEY, " +
            "website_name VARCHAR(255) NOT NULL, " +
            "download_start_datetime VARCHAR(50) NOT NULL, " +
            "download_end_datetime VARCHAR(50), " +
            "total_elapsed_time BIGINT, " +
            "total_downloaded_kilobytes DOUBLE PRECISION" +
            ")";
        
        String createLinksTable = "CREATE TABLE IF NOT EXISTS links (" +
            "id SERIAL PRIMARY KEY, " +
            "link_name TEXT NOT NULL, " +
            "website_id INTEGER NOT NULL, " +
            "total_elapsed_time BIGINT, " +
            "total_downloaded_kilobytes DOUBLE PRECISION, " +
            "FOREIGN KEY (website_id) REFERENCES websites(id) ON DELETE CASCADE" +
            ")";
        
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createWebsitesTable);
            stmt.execute(createLinksTable);
            TerminalUI.status("  Tables", "websites, links", TerminalUI.BRIGHT_GREEN);
        }
    }
    
    /**
     * Insert a website record into the database
     * @return the generated ID of the inserted website
     */
    public int insertWebsite(String websiteName, String downloadStartDateTime) throws SQLException {
        String sql = "INSERT INTO websites (website_name, download_start_datetime) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, websiteName);
            pstmt.setString(2, downloadStartDateTime);
            pstmt.executeUpdate();
            
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
            throw new SQLException("Failed to get generated website ID");
        }
    }
    
    /**
     * Update website record with end time and statistics
     */
    public void updateWebsite(int websiteId, String downloadEndDateTime, 
                             long totalElapsedTime, double totalDownloadedKilobytes) throws SQLException {
        String sql = "UPDATE websites SET download_end_datetime = ?, total_elapsed_time = ?, total_downloaded_kilobytes = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, downloadEndDateTime);
            pstmt.setLong(2, totalElapsedTime);
            pstmt.setDouble(3, totalDownloadedKilobytes);
            pstmt.setInt(4, websiteId);
            pstmt.executeUpdate();
        }
    }
    
    /**
     * Insert a link record into the database
     */
    public void insertLink(String linkName, int websiteId, long totalElapsedTime, 
                          double totalDownloadedKilobytes) throws SQLException {
        String sql = "INSERT INTO links (link_name, website_id, total_elapsed_time, total_downloaded_kilobytes) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, linkName);
            pstmt.setInt(2, websiteId);
            pstmt.setLong(3, totalElapsedTime);
            pstmt.setDouble(4, totalDownloadedKilobytes);
            pstmt.executeUpdate();
        }
    }
    
    /**
     * Get all websites from the database
     * @return list of WebsiteReport objects
     */
    public List<WebsiteReport> getAllWebsites() {
        List<WebsiteReport> websites = new ArrayList<>();
        String sql = "SELECT * FROM websites ORDER BY id DESC";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                WebsiteReport website = new WebsiteReport(
                    rs.getInt("id"),
                    rs.getString("website_name"),
                    rs.getString("download_start_datetime"),
                    rs.getString("download_end_datetime"),
                    rs.getLong("total_elapsed_time"),
                    rs.getDouble("total_downloaded_kilobytes")
                );
                websites.add(website);
            }
        } catch (SQLException e) {
            System.err.println("Error getting all websites: " + e.getMessage());
            e.printStackTrace();
        }
        
        return websites;
    }
    
    /**
     * Get download report by website ID
     * @param websiteId the website ID
     * @return DownloadReport object or null if not found
     */
    public DownloadReport getDownloadReport(int websiteId) {
        String websiteSql = "SELECT * FROM websites WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(websiteSql)) {
            pstmt.setInt(1, websiteId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                WebsiteReport website = new WebsiteReport(
                    rs.getInt("id"),
                    rs.getString("website_name"),
                    rs.getString("download_start_datetime"),
                    rs.getString("download_end_datetime"),
                    rs.getLong("total_elapsed_time"),
                    rs.getDouble("total_downloaded_kilobytes")
                );
                
                List<LinkRecord> links = getLinksForWebsite(websiteId);
                return new DownloadReport(website, links);
            }
        } catch (SQLException e) {
            System.err.println("Error getting download report: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Get download report by website name
     * @param websiteName the website name
     * @return DownloadReport object or null if not found
     */
    public DownloadReport getDownloadReportByName(String websiteName) {
        String websiteSql = "SELECT * FROM websites WHERE website_name = ? ORDER BY id DESC LIMIT 1";
        
        try (PreparedStatement pstmt = connection.prepareStatement(websiteSql)) {
            pstmt.setString(1, websiteName);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                int websiteId = rs.getInt("id");
                WebsiteReport website = new WebsiteReport(
                    websiteId,
                    rs.getString("website_name"),
                    rs.getString("download_start_datetime"),
                    rs.getString("download_end_datetime"),
                    rs.getLong("total_elapsed_time"),
                    rs.getDouble("total_downloaded_kilobytes")
                );
                
                List<LinkRecord> links = getLinksForWebsite(websiteId);
                return new DownloadReport(website, links);
            }
        } catch (SQLException e) {
            System.err.println("Error getting download report by name: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Get all links for a specific website
     * @param websiteId the website ID
     * @return list of LinkRecord objects
     */
    private List<LinkRecord> getLinksForWebsite(int websiteId) throws SQLException {
        List<LinkRecord> links = new ArrayList<>();
        String sql = "SELECT * FROM links WHERE website_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, websiteId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                LinkRecord link = new LinkRecord(
                    rs.getInt("id"),
                    rs.getString("link_name"),
                    rs.getInt("website_id"),
                    rs.getLong("total_elapsed_time"),
                    rs.getDouble("total_downloaded_kilobytes")
                );
                links.add(link);
            }
        }
        
        return links;
    }
    
    /**
     * Close the database connection
     */
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Error closing database connection: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
