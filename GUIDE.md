# Quick Start Guide - Web Download Manager

## 🎯 Complete Setup in 5 Minutes

### Step 1: Create Project Structure
```bash
cd C:\Users\user\IdeaProjects\link-local

# Create all directories
mkdir src\main\java\com\algo\inc\database
mkdir src\main\java\com\algo\inc\downloader
mkdir src\main\java\com\algo\inc\model
mkdir src\main\java\com\algo\inc\util
```

### Step 2: Save All Files

Save these files in their respective locations:

#### Root Directory:
- `pom.xml` → `C:\Users\user\IdeaProjects\link-local\pom.xml`

#### Main Package (`src/main/java/com/algo/inc/`):
- `WebDownloadManager.java`

#### Database Package (`src/main/java/com/algo/inc/database/`):
- `DatabaseManager.java`

#### Downloader Package (`src/main/java/com/algo/inc/downloader/`):
- `WebsiteDownloader.java`

#### Model Package (`src/main/java/com/algo/inc/model/`):
- `WebsiteRecord.java`
- `LinkRecord.java`
- `DownloadReport.java`

#### Util Package (`src/main/java/com/algo/inc/util/`):
- `URLValidator.java`

### Step 3: Set Up PostgreSQL Database

Before running the application, you need to set up PostgreSQL:

1. **Install PostgreSQL** (if not already installed)
2. **Create the database:**
   ```sql
   CREATE DATABASE webdownloads;
   ```

3. **Configure database connection** (optional):
   - Default settings: host=localhost, port=5432, database=webdownloads, user=postgres, password=postgres
   - You can override these using system properties:
     ```bash
     java -Ddb.host=localhost -Ddb.port=5432 -Ddb.name=webdownloads -Ddb.user=postgres -Ddb.password=yourpassword -jar target\link-local-1.0-SNAPSHOT-jar-with-dependencies.jar
     ```

### Step 4: Build the Project
```bash
mvn clean package
```

This will:
- Download all dependencies (Jsoup, PostgreSQL JDBC, etc.)
- Compile all Java files
- Create an executable JAR file

### Step 5: Run the Application

**With default PostgreSQL settings:**
```bash
java -jar target\link-local-1.0-SNAPSHOT-jar-with-dependencies.jar
```

**With custom PostgreSQL settings:**
```bash
java -Ddb.host=localhost -Ddb.port=5432 -Ddb.name=webdownloads -Ddb.user=postgres -Ddb.password=yourpassword -jar target\link-local-1.0-SNAPSHOT-jar-with-dependencies.jar
```

**Or with Maven:**
```bash
mvn exec:java -Dexec.mainClass="com.algo.inc.WebDownloadManager" -Dexec.args="-Ddb.host=localhost -Ddb.port=5432 -Ddb.name=webdownloads -Ddb.user=postgres -Ddb.password=yourpassword"
```

## 📝 File Checklist

Make sure you have all these files:

```
link-local/
├── pom.xml                                              ✓
├── src/
│   └── main/
│       └── java/
│           └── com/
│               └── algo/
│                   └── inc/
│                       ├── WebDownloadManager.java      ✓
│                       ├── database/
│                       │   └── DatabaseManager.java     ✓
│                       ├── downloader/
│                       │   └── WebsiteDownloader.java   ✓
│                       ├── model/
│                       │   ├── WebsiteRecord.java       ✓
│                       │   ├── LinkRecord.java          ✓
│                       │   └── DownloadReport.java      ✓
│                       └── util/
│                           └── URLValidator.java        ✓
```

## 🧪 Test Your Installation

1. Run the application
2. Choose option 1 (Download a Website)
3. Enter: `http://example.com`
4. Press Enter for default directory
5. Watch the download progress!

## 🎓 First Download Example

```
╔════════════════════════════════════════════════════════╗
║        WEB DOWNLOAD MANAGER - Algorithm Inc.          ║
║              Complete Website Downloader              ║
╚════════════════════════════════════════════════════════╝

═══════════════ MAIN MENU ═══════════════
1. Download a Website
2. View Download History
3. View Website Report
4. Exit
═══════════════════════════════════════════
Enter your choice: 1

📥 WEBSITE DOWNLOAD
─────────────────────────────────────────
Enter website URL: http://example.com
✓ URL validated: http://example.com
Enter download directory (or press Enter for current directory): 
✓ Download directory: C:\Users\user\IdeaProjects\downloads

🚀 Starting download...

📁 Created directory: C:\Users\user\IdeaProjects\downloads\example.com
🌐 Downloading from: http://example.com

📄 Downloading homepage: index.html
✓ Homepage downloaded: 1.23 KB in 145 ms
```

## ⚡ Quick Commands Reference

### Build Only:
```bash
mvn clean compile
```

### Build and Package:
```bash
mvn clean package
```

### Run (after building):
```bash
java -jar target\link-local-1.0-SNAPSHOT-jar-with-dependencies.jar
```

### Clean Everything:
```bash
mvn clean
```

### View Database:
The application uses PostgreSQL database `webdownloads` (default name).
You can view it using:
- **psql command line:** `psql -U postgres -d webdownloads`
- **pgAdmin** (GUI tool)
- **DBeaver** (Universal database tool)
- **Any PostgreSQL client**

**Tables created:**
- `websites` - Stores website download records
- `links` - Stores individual link/resource download records

## 🔍 Verification Steps

After setup, verify everything works:

1. ✅ PostgreSQL is installed and running
2. ✅ Database `webdownloads` is created
3. ✅ Project builds without errors: `mvn clean package`
4. ✅ Application connects to PostgreSQL: Check console for "Connected to PostgreSQL database"
5. ✅ Tables are created: Check console for "Database tables created successfully"
6. ✅ Downloads work: Try downloading example.com
7. ✅ Reports display: View download history

## ❓ Common Issues

### Issue 1: "No main manifest attribute"
**Solution:** Rebuild with `mvn clean package`

### Issue 2: "Class not found"
**Solution:** Make sure all files are in correct packages

### Issue 3: Build fails on pom.xml
**Solution:** Check Java and Maven versions:
```bash
java -version  # Should be 21+
mvn -version   # Should be 3.6+
```

### Issue 4: Cannot connect to PostgreSQL
**Solution:** 
- Make sure PostgreSQL is running: `pg_ctl status` or check services
- Verify database exists: `psql -U postgres -l`
- Check connection parameters (host, port, user, password)
- Verify PostgreSQL allows connections from localhost (check pg_hba.conf)

### Issue 5: Database authentication failed
**Solution:**
- Verify username and password are correct
- Check PostgreSQL user permissions
- Try connecting manually: `psql -U postgres -d webdownloads`

### Issue 6: Cannot create directories
**Solution:** Run as administrator or check permissions

## 🎉 Success!

If you see the main menu, congratulations! Your Web Download Manager is ready to use.

## 📚 Next Steps

1. Try downloading different websites
2. View the download reports
3. Examine the database structure
4. Modify the code to add features
5. Test error handling with invalid URLs

## 🆘 Need Help?

Check the README.md for detailed documentation or contact your instructor.

---

**Happy Downloading! 🚀**