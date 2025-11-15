package com.algo.inc.util;

/**
 * Terminal UI utilities with ANSI color codes and formatting
 * Developer-friendly terminal interface similar to Kali Linux tools
 */
public class TerminalUI {
    
    // ANSI Color Codes
    public static final String RESET = "\033[0m";
    public static final String BOLD = "\033[1m";
    public static final String DIM = "\033[2m";
    
    // Colors
    public static final String BLACK = "\033[30m";
    public static final String RED = "\033[31m";
    public static final String GREEN = "\033[32m";
    public static final String YELLOW = "\033[33m";
    public static final String BLUE = "\033[34m";
    public static final String MAGENTA = "\033[35m";
    public static final String CYAN = "\033[36m";
    public static final String WHITE = "\033[37m";
    
    // Bright colors
    public static final String BRIGHT_BLACK = "\033[90m";
    public static final String BRIGHT_RED = "\033[91m";
    public static final String BRIGHT_GREEN = "\033[92m";
    public static final String BRIGHT_YELLOW = "\033[93m";
    public static final String BRIGHT_BLUE = "\033[94m";
    public static final String BRIGHT_MAGENTA = "\033[95m";
    public static final String BRIGHT_CYAN = "\033[96m";
    public static final String BRIGHT_WHITE = "\033[97m";
    
    // Background colors
    public static final String BG_BLACK = "\033[40m";
    public static final String BG_RED = "\033[41m";
    public static final String BG_GREEN = "\033[42m";
    public static final String BG_YELLOW = "\033[43m";
    public static final String BG_BLUE = "\033[44m";
    public static final String BG_MAGENTA = "\033[45m";
    public static final String BG_CYAN = "\033[46m";
    public static final String BG_WHITE = "\033[47m";
    
    // Status symbols
    public static final String CHECK = "✓";
    public static final String CROSS = "✗";
    public static final String ARROW = "→";
    public static final String DOT = "•";
    public static final String INFO = "ℹ";
    public static final String WARNING = "⚠";
    public static final String ERROR = "✖";
    
    /**
     * Clear the current line
     */
    public static void clearLine() {
        System.out.print("\033[2K\r");
    }
    
    /**
     * Move cursor up N lines
     */
    public static void cursorUp(int lines) {
        System.out.print("\033[" + lines + "A");
    }
    
    /**
     * Print colored text
     */
    public static String color(String text, String color) {
        return color + text + RESET;
    }
    
    /**
     * Print bold text
     */
    public static String bold(String text) {
        return BOLD + text + RESET;
    }
    
    /**
     * Print dim text
     */
    public static String dim(String text) {
        return DIM + text + RESET;
    }
    
    /**
     * Print success message
     */
    public static void success(String message) {
        System.out.println(color(CHECK + " " + message, BRIGHT_GREEN));
    }
    
    /**
     * Print error message
     */
    public static void error(String message) {
        System.out.println(color(ERROR + " " + message, BRIGHT_RED));
    }
    
    /**
     * Print warning message
     */
    public static void warning(String message) {
        System.out.println(color(WARNING + " " + message, BRIGHT_YELLOW));
    }
    
    /**
     * Print info message
     */
    public static void info(String message) {
        System.out.println(color(INFO + " " + message, BRIGHT_CYAN));
    }
    
    /**
     * Print status with color
     */
    public static void status(String label, String value, String color) {
        System.out.println(color(label, DIM) + " " + color(value, color));
    }
    
    /**
     * Print header section
     */
    public static void sectionHeader(String title) {
        System.out.println();
        System.out.println(color("┌─ " + bold(title), BRIGHT_CYAN));
        System.out.println(color("│", BRIGHT_CYAN));
    }
    
    /**
     * Print section footer
     */
    public static void sectionFooter() {
        System.out.println(color("└", BRIGHT_CYAN));
    }
    
    /**
     * Print a progress bar
     */
    public static void progressBar(int current, int total, int width) {
        if (total == 0) return;
        
        float percentage = (float) current / total;
        int filled = (int) (width * percentage);
        int empty = width - filled;
        
        StringBuilder bar = new StringBuilder();
        bar.append(color("[", DIM));
        bar.append(color("=".repeat(filled), BRIGHT_GREEN));
        bar.append(color("-".repeat(empty), DIM));
        bar.append(color("]", DIM));
        bar.append(String.format(" %3d%%", (int)(percentage * 100)));
        
        System.out.print("\r" + bar.toString());
        if (current >= total) {
            System.out.println();
        }
    }
    
    /**
     * Print download progress with speed
     */
    public static void downloadProgress(long bytes, long totalBytes, long elapsedMs, String filename) {
        clearLine();
        
        double percent = totalBytes > 0 ? (bytes * 100.0 / totalBytes) : 0;
        double speed = elapsedMs > 0 ? (bytes * 1000.0 / elapsedMs) : 0;
        String speedStr = formatBytes(speed) + "/s";
        String sizeStr = formatBytes(bytes);
        String totalStr = totalBytes > 0 ? " / " + formatBytes(totalBytes) : "";
        
        String progress = String.format("%s [%6.2f%%] %s%s @ %s %s",
            color("↓", BRIGHT_BLUE),
            percent,
            sizeStr,
            totalStr,
            speedStr,
            dim(truncate(filename, 40))
        );
        
        System.out.print(progress);
    }
    
    /**
     * Format bytes to human readable format
     */
    public static String formatBytes(double bytes) {
        if (bytes < 1024) return String.format("%.0f B", bytes);
        if (bytes < 1024 * 1024) return String.format("%.2f KB", bytes / 1024);
        if (bytes < 1024 * 1024 * 1024) return String.format("%.2f MB", bytes / (1024 * 1024));
        return String.format("%.2f GB", bytes / (1024 * 1024 * 1024));
    }
    
    /**
     * Format duration
     */
    public static String formatDuration(long milliseconds) {
        if (milliseconds < 1000) return milliseconds + "ms";
        if (milliseconds < 60000) return String.format("%.2fs", milliseconds / 1000.0);
        long seconds = milliseconds / 1000;
        long minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%dm %ds", minutes, seconds);
    }
    
    /**
     * Truncate string with ellipsis
     */
    private static String truncate(String str, int maxLength) {
        if (str == null || str.length() <= maxLength) return str;
        return str.substring(0, maxLength - 3) + "...";
    }
    
    /**
     * Print table header
     */
    public static void tableHeader(String... columns) {
        StringBuilder header = new StringBuilder();
        header.append(color("┌", DIM));
        for (int i = 0; i < columns.length; i++) {
            header.append("─".repeat(columns[i].length() + 2));
            if (i < columns.length - 1) header.append(color("┬", DIM));
        }
        header.append(color("┐", DIM));
        System.out.println(header.toString());
        
        StringBuilder row = new StringBuilder();
        row.append(color("│", DIM));
        for (String col : columns) {
            row.append(" ").append(bold(col)).append(" ");
            row.append(color("│", DIM));
        }
        System.out.println(row.toString());
        
        StringBuilder separator = new StringBuilder();
        separator.append(color("├", DIM));
        for (int i = 0; i < columns.length; i++) {
            separator.append("─".repeat(columns[i].length() + 2));
            if (i < columns.length - 1) separator.append(color("┼", DIM));
        }
        separator.append(color("┤", DIM));
        System.out.println(separator.toString());
    }
    
    /**
     * Print table row
     */
    public static void tableRow(String... columns) {
        StringBuilder row = new StringBuilder();
        row.append(color("│", DIM));
        for (String col : columns) {
            row.append(" ").append(col).append(" ");
            row.append(color("│", DIM));
        }
        System.out.println(row.toString());
    }
    
    /**
     * Print table footer
     */
    public static void tableFooter(int columnCount, int[] widths) {
        StringBuilder footer = new StringBuilder();
        footer.append(color("└", DIM));
        for (int i = 0; i < columnCount; i++) {
            footer.append("─".repeat(widths[i] + 2));
            if (i < columnCount - 1) footer.append(color("┴", DIM));
        }
        footer.append(color("┘", DIM));
        System.out.println(footer.toString());
    }
    
    /**
     * Print ASCII art banner
     */
    public static void printBanner() {
        System.out.println();
        printFaceArt();
        System.out.println();
        printAsciiArt();
        System.out.println();
        System.out.println(color("  " + "Algorithm Inc.", BRIGHT_BLUE) + 
                         color(" • ", DIM) + 
                         color("Link-local Downloader", BRIGHT_CYAN) + 
                         color("  │  ", DIM) + 
                         color("v1.0.0", BRIGHT_BLACK));
        System.out.println();
    }
    
    /**
     * Print the face ASCII art
     */
    private static void printFaceArt() {
        String[] faceLines = {
            "       _____       ",
            "     .-'     '-.     ",
            "    /           \\    ",
            "   |  .--. .--.  |   ",
            "   | (    Y    ) |   ",
            "   |  '--' '--'  |   ",
            "    \\           /    ",
            "     '-._____.-'     ",
            "      / /| |\\ \\      ",
            "     /_/ |_| \\_\\     "
        };
        
        // Print face with gradient colors
        for (int i = 0; i < faceLines.length; i++) {
            String colorToUse;
            if (i < 3) {
                colorToUse = BRIGHT_CYAN;
            } else if (i < 6) {
                colorToUse = BRIGHT_BLUE;
            } else if (i < 9) {
                colorToUse = BRIGHT_MAGENTA;
            } else {
                colorToUse = BRIGHT_YELLOW;
            }
            System.out.println(color(faceLines[i], colorToUse));
        }
    }
    
    /**
     * Generate ASCII art for "LINK LOCAL"
     * Modern, stylish design using standard characters
     */
    private static void printAsciiArt() {
        String[] lines = {
            "██╗     ██╗███╗   ██╗██╗  ██╗    ██╗      ██████╗  ██████╗ █████╗ ██╗     ",
            "██║     ██║████╗  ██║██║ ██╔╝    ██║     ██╔═══██╗██╔════╝██╔══██╗██║     ",
            "██║     ██║██╔██╗ ██║█████╔╝     ██║     ██║   ██║██║     ███████║██║     ",
            "██║     ██║██║╚██╗██║██╔═██╗     ██║     ██║   ██║██║     ██╔══██║██║     ",
            "███████╗██║██║ ╚████║██║  ██╗    ███████╗╚██████╔╝╚██████╗██║  ██║███████╗",
            "╚══════╝╚═╝╚═╝  ╚═══╝╚═╝  ╚═╝    ╚══════╝ ╚═════╝  ╚═════╝╚═╝  ╚═╝╚══════╝"
        };
        
        // Print with vibrant colors
        for (int i = 0; i < lines.length; i++) {
            if (lines[i].isEmpty()) {
                System.out.println();
                continue;
            }
            String colorToUse;
            if (i < 3) {
                colorToUse = BRIGHT_CYAN;
            } else if (i < 6) {
                colorToUse = BRIGHT_BLUE;
            } else if (i < 9) {
                colorToUse = BRIGHT_MAGENTA;
            } else {
                colorToUse = BRIGHT_YELLOW;
            }
            System.out.println(color(lines[i], colorToUse));
        }
    }
    
    /**
     * Print menu
     */
    public static void printMenu() {
        System.out.println();
        System.out.println(color("┌─ " + bold("MAIN MENU"), BRIGHT_CYAN));
        System.out.println(color("│", BRIGHT_CYAN));
        System.out.println(color("│", BRIGHT_CYAN) + "  " + 
            color("1", BRIGHT_GREEN) + ". " + 
            color("Download Website", WHITE));
        System.out.println(color("│", BRIGHT_CYAN) + "  " + 
            color("2", BRIGHT_GREEN) + ". " + 
            color("View Download History", WHITE));
        System.out.println(color("│", BRIGHT_CYAN) + "  " + 
            color("3", BRIGHT_GREEN) + ". " + 
            color("View Website Report", WHITE));
        System.out.println(color("│", BRIGHT_CYAN) + "  " + 
            color("4", BRIGHT_GREEN) + ". " + 
            color("Exit", WHITE));
        System.out.println(color("│", BRIGHT_CYAN));
        System.out.print(color("└─ ", BRIGHT_CYAN) + 
            color("Select option", DIM) + " " + 
            color("→", BRIGHT_YELLOW) + " ");
    }
    
    /**
     * Print separator line
     */
    public static void separator() {
        System.out.println(color("────────────────────────────────────────────────────────────────", DIM));
    }
}

