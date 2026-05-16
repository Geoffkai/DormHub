# DormHub

DormHub is a Java desktop application for dormitory management. It organizes residents, rooms, assignments, payments, and dorm passes in one Swing-based application.

The project now uses an IDE-agnostic layout so it can be compiled and packaged without Maven or a Maven wrapper. The active application source lives under `Thumbler_sa_ref/DormHub/`.

## What DormHub Does

- Manage resident records
- Manage room information and availability
- Track room assignments
- Record payments
- Manage dorm pass records
- Load database settings from a simple properties file

## Current Project Layout

```text
MP_127/
├── Thumbler_sa_ref/
│   ├── DormHub/
│   │   ├── src/           Java source files (`com/dormhub/...`)
│   │   ├── resources/     Runtime configuration such as `db.properties`
│   │   ├── assets/img/    Images and other visual assets
│   │   ├── libs/          Third-party JAR dependencies
│   │   ├── out/           Compiled classes and copied runtime files
│   │   ├── manifest.txt   JAR manifest used by `jar -cfm`
│   │   ├── DormHub.jar    Packaged application JAR
│   │   └── build.bat      Optional Windows build helper
│   └── README.md          This file
├── documentation/         Technical report and user manual
└── installers/            Installer outputs such as Inno Setup or InstallForge builds
```

## Requirements

- JDK 23
- MySQL Server running locally or on a reachable host
- The dependency JARs placed in `Thumbler_sa_ref/DormHub/libs/`

## Dependencies

Place these libraries in `Thumbler_sa_ref/DormHub/libs/` before compiling:

- `mysql-connector-j-9.6.0.jar` or your chosen MySQL Connector/J version
- `jcalendar-1.4.jar`
- Any other supporting JARs your build requires

The repository already contains additional library files used by the project, including JGoodies and JUnit artifacts.

## Configuration

Database and app credentials are loaded from `Thumbler_sa_ref/DormHub/resources/db.properties`.

Example format:

```properties
db.url=jdbc:mysql://localhost:3306/dormhub?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
db.user=root
db.password=your_password

app.username=admin
app.password=admin123
```

## Manual Build Flow

This is the flow your professor described:

1. Compile all `.java` files into `out/`
2. Copy resources and assets into `out/`
3. Create a JAR with `jar -cfm` using `manifest.txt`
4. Optionally feed the JAR into an installer tool such as Inno Setup or InstallForge

### Compile

From `Thumbler_sa_ref/DormHub/`:

```cmd
dir /s /b src\*.java > sources.txt
javac -cp "libs/*" -d out -encoding UTF-8 @sources.txt
```

### Copy Runtime Files

```cmd
xcopy /s /e /y resources out\resources\
xcopy /s /e /y assets\img out\img\
```

The application looks for images on the classpath under `/img/...`. The runtime loader also falls back to `assets/img/...` when you run directly from the workspace, but copying into `out\img\` keeps the packaged layout clean.

### Build the JAR

`manifest.txt` must contain a blank line at the end.

```cmd
jar -cfm DormHub.jar manifest.txt -C out .
```

### Verify the JAR

```cmd
jar tf DormHub.jar | findstr /i "com/dormhub/Main.class"
```

### Run the Application

```cmd
java -cp "DormHub.jar;libs/*" com.dormhub.Main
```

Run the command from `Thumbler_sa_ref/DormHub/` so `app.env` and the image assets are found correctly.

If you want to run it with double-click later, keep the dependency JARs beside the main JAR or move them into the installer package as well.

## Installer Preparation

Once `DormHub.jar` works, the next step is to create an installer using one of these tools:

- Inno Setup
- InstallForge

Typical installer contents:

- `DormHub.jar`
- `libs/`
- `img/` or `assets/img/`
- Any runtime or configuration files the application needs at launch

## Notes

- The application uses a layered architecture: View -> Controller -> Service -> DAO -> Database.
- Keep database credentials and other sensitive values out of public repositories.
- The root README is now the canonical documentation for the project layout and build flow.

## Authors

DormHub project team
