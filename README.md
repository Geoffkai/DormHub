# DormHub

DormHub is a Java desktop application for dormitory management. It helps organize residents, rooms, assignments, payments, and dorm passes in one place through a Swing-based GUI.

The project uses a layered architecture (View -> Controller -> Service -> DAO -> Database) to keep business logic clean and maintainable.

## What This Project Does

- Manage resident records
- Manage room information and availability
- Track room assignments
- Record payments
- Manage dorm pass records
- Initialize database schema automatically on first run

## Tech Stack

- Java 23
- Maven Wrapper
- Java Swing (GUI)
- MySQL + MySQL Connector/J
- JUnit 5 (testing)

## Prerequisites

Before running DormHub, make sure you have:

- JDK 23 installed
- MySQL server running
- Git (optional, for cloning)

## Configuration

Database and app credentials are loaded from:

- `src/main/resources/db.properties`

Example format:

```properties
db.url=jdbc:mysql://localhost:3306/dormhub?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
db.user=root
db.password=your_password

app.username=
app.password=
```

You can also override database values with:

- System properties: `-Ddb.url`, `-Ddb.user`, `-Ddb.password`
- Environment variables: `DB_URL`, `DB_USER`, `DB_PASSWORD`

## Run The Application

DormHub now starts directly in GUI mode by default.

### Windows (PowerShell / Command Prompt)

```powershell
.\mvnw.cmd exec:java
```

### macOS / Linux

```bash
./mvnw exec:java
```

## Build A Runnable JAR

To create a packaged executable JAR with all dependencies bundled:

### Windows

```powershell
.\mvnw.cmd clean package
java -jar target\dormhub-1.0-SNAPSHOT-jar-with-dependencies.jar
```

### macOS / Linux

```bash
./mvnw clean package
java -jar target/dormhub-1.0-SNAPSHOT-jar-with-dependencies.jar
```

This JAR includes all application dependencies, so only Java needs to be installed to run it.

## Build A Standalone Application (Windows)

To create a fully standalone app that includes its own Java runtime (no JDK installation required):

### Build the Standalone App

```powershell
.\mvnw.cmd clean package jpackage:jpackage
```

This creates a portable application at `target/installer/DormHub/` containing:
- **DormHub.exe** — Double-click to launch the application
- **runtime/** — Bundled Java Runtime Environment
- **app/** — Application files and JAR

### How to Use

1. Copy the entire `target/installer/DormHub/` folder to any location
2. Double-click `DormHub.exe` to run the application
3. No Java installation needed on the end-user's machine

### Distributing the App

You can zip the `target/installer/DormHub/` folder for distribution to other Windows machines.

## Run Tests

### Windows

```powershell
.\mvnw.cmd test
```

### macOS / Linux

```bash
./mvnw test
```

## Project Structure

```text
MP_127/
|- pom.xml
|- mvnw / mvnw.cmd
|- README.md
|- sql/
|  |- dormhub.sql
|- src/
|  |- main/
|  |  |- java/com/dormhub/
|  |  |  |- Main.java
|  |  |  |- controller/
|  |  |  |- dao/
|  |  |  |  |- impl/
|  |  |  |- db/
|  |  |  |- model/
|  |  |  |- service/
|  |  |  |  |- Impl/
|  |  |  |- util/
|  |  |  |- view/
|  |  |- resources/
|  |     |- db.properties
|  |     |- com/dormhub/db/
|  |     |- img/
|  |- test/
|     |- java/com/dormhub/
|- GUI/
|- target/
```

## Notes

- The schema can be initialized automatically when the app connects for the first time.
- Keep sensitive credentials out of shared/public repositories.

## Authors

Built and maintained by the DormHub project team.
