# DormHub

A dormitory management system built with Java Swing and MySQL.

## Project Structure

```
MP_127/
├── DormHub/
│   ├── src/              ← Java source files (com/dormhub/...)
│   ├── libs/             ← Dependency JARs (e.g. mysql-connector)
│   ├── assets/           ← Images and icons
│   ├── resources/        ← db.properties and config files
│   ├── out/              ← Compiled .class files (auto-generated)
│   ├── manifest.txt      ← JAR manifest
│   └── build.bat         ← Build script (Windows)
├── documentation/        ← Technical report and user manual
├── installers/           ← Final installer files
└── README.md
```

## Prerequisites

- JDK 23
- MySQL Server running
- MySQL Connector/J JAR placed in `DormHub/libs/`

## Building the JAR

1. Place your MySQL Connector JAR in `DormHub/libs/`
2. Copy your source files into `DormHub/src/com/dormhub/`
3. Copy `db.properties` into `DormHub/resources/`
4. Copy images/icons into `DormHub/assets/`
5. Double-click `DormHub/build.bat` (or run it from Command Prompt)
6. The output `DormHub.jar` will appear in `DormHub/`

## Running the App

```cmd
java -jar DormHub\DormHub.jar
```
