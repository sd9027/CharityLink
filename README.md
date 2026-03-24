# ♥ CharityLink — Donation Management System

A Java-based donation management system with Swing GUI, OOP design, file persistence, and multithreading.

---

## 📁 Project Structure

```
CharityLink/
├── src/
│   └── charitylink/
│       ├── Main.java                    ← Entry point
│       ├── model/
│       │   ├── User.java               ← Abstract base class
│       │   ├── Donor.java              ← Extends User
│       │   ├── Admin.java              ← Extends User
│       │   └── Donation.java           ← Donation record
│       ├── service/
│       │   ├── AuthService.java        ← Login, registration
│       │   ├── DonationService.java    ← Process & query donations
│       │   └── ReportService.java      ← Background report thread
│       ├── ui/
│       │   ├── UITheme.java            ← Centralized styling
│       │   ├── LoginFrame.java         ← Login / Register screen
│       │   ├── DonorDashboard.java     ← Donor panel
│       │   └── AdminDashboard.java     ← Admin panel
│       └── util/
│           ├── FileHandler.java        ← File I/O helper
│           ├── IDGenerator.java        ← Auto-increment IDs
│           └── AppConstants.java       ← Config & file paths
├── data/                               ← Auto-created on first run
│   ├── users.csv
│   └── donations.csv
├── compile_and_run.bat                 ← Windows one-click build
├── compile_and_run.sh                  ← Mac/Linux one-click build
└── README.md
```

---

## ✅ Prerequisites — Install Java JDK 17+

### Windows
1. Go to: https://adoptium.net
2. Download **Temurin 17 LTS** (Windows x64 `.msi`)
3. Run the installer → tick "Add to PATH" and "Set JAVA_HOME"
4. Verify in Command Prompt:
   ```
   java -version
   javac -version
   ```

### macOS
Option A — Homebrew:
```bash
brew install --cask temurin
```
Option B — Download `.pkg` from https://adoptium.net

Verify:
```bash
java -version
javac -version
```

### Linux (Ubuntu/Debian)
```bash
sudo apt update
sudo apt install default-jdk
java -version
javac -version
```

---

## 🚀 How to Run

### Windows
1. Double-click `compile_and_run.bat`  
   — OR —  
   Open Command Prompt in the project folder and run:
   ```
   compile_and_run.bat
   ```

### Mac / Linux
Open Terminal in the project folder:
```bash
chmod +x compile_and_run.sh
./compile_and_run.sh
```

### Manual compile (any OS)
```bash
# From project root
mkdir -p out data
find src -name "*.java" > sources.txt      # Mac/Linux
# or: dir /s /b src\*.java > sources.txt  # Windows
javac -d out @sources.txt
java -cp out charitylink.Main
```

---

## 🔑 Default Login Credentials

| Role  | Email                     | Password   |
|-------|---------------------------|------------|
| Admin | admin@charitylink.org     | admin123   |

You can register new Donor accounts from the login screen.

---

## 🖥️ Features

### Donor
- Register a new account
- Log in securely
- Make donations with an amount and cause
- View personal donation history in a table
- View personal report (generated in background thread)

### Admin
- Log in to admin dashboard
- View all donations across all donors
- View all registered donors and their totals
- Generate a full system report (runs in background thread)
- Save the report to a `.txt` file

---

## 💾 Data Storage

All data is stored as plain CSV files in the `data/` folder:

**users.csv** format:
```
id,name,email,password,role
D1000,John Doe,john@example.com,pass123,DONOR
A001,Admin,admin@charitylink.org,admin123,ADMIN
```

**donations.csv** format:
```
donationId,donorId,amount,cause,date
DON5000,D1000,500.0,Education,2025-01-15 14:30:00
```

---

## 🧩 Java Concepts Used

| Concept          | Where Used                                      |
|------------------|-------------------------------------------------|
| Inheritance      | Donor, Admin extend User                        |
| Encapsulation    | Private fields + getters/setters                |
| Polymorphism     | getRole() overridden in Donor and Admin         |
| ArrayList        | Donor list, Donation list                       |
| File I/O         | FileHandler reads/writes CSV files              |
| Multithreading   | ReportService runs report on a background thread|
| Exception Handling| try/catch in auth, donation, and file ops      |
| Swing GUI        | Full window-based UI with panels, tables, dialogs|

---

## ⚠️ Known Limitations

- Passwords stored in plain text (for educational purposes)
- No database — file-based CSV storage only
- Single machine only — no networking
- Simulated payments — no real payment gateway
