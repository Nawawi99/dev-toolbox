# Backend Toolbox

Backend Toolbox is a fast, offline desktop app for backend developers. It keeps common daily tools in one local app, with no accounts, cloud services, paid APIs, or telemetry.

It includes tools for JSON, JWT, YAML, SQL, Regex, Time conversion, Encoding/Hashing, Log filtering, and Docker Compose inspection.

## Install

Download the installer for your operating system from the GitHub release:

- Windows: download the `.exe` installer and run it.
- macOS: download the `.dmg`, open it, and drag Backend Toolbox into Applications.
- Linux: download the `.deb` and install it with your package manager.

## Run From Source

For development:

```bash
mvn javafx:run
```

## Build Installers Locally

Installers are built with `jpackage`, so each installer must be built on its target OS.

Windows:

```powershell
.\package\windows.ps1 -Version 1.0.0 -Type exe
```

macOS:

```bash
./package/macos.sh 1.0.0 dmg
```

Linux:

```bash
./package/linux.sh 1.0.0 deb
```
