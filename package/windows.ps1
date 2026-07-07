param(
    [string]$Version = "1.0.0",
    [ValidateSet("app-image", "exe", "msi")]
    [string]$Type = "exe"
)

$ErrorActionPreference = "Stop"
$Root = Split-Path -Parent $PSScriptRoot
$InputDir = Join-Path $Root "target\package\input"
$IconPng = Join-Path $Root "src\main\resources\icons\backend-toolbox.png"
$IconIco = Join-Path $Root "target\package\backend-toolbox.ico"
$Jar = Join-Path $Root "target\toolbox-1.0-SNAPSHOT.jar"
$MainJar = Join-Path $InputDir "backend-toolbox.jar"
$Dest = Join-Path $Root "dist"

Push-Location $Root
try {
    if (Test-Path $InputDir) {
        Remove-Item -LiteralPath $InputDir -Recurse -Force
    }
    New-Item -ItemType Directory -Force -Path $InputDir | Out-Null
    mvn -DskipTests package dependency:copy-dependencies "-DoutputDirectory=$InputDir" "-DincludeScope=runtime"

    New-Item -ItemType Directory -Force -Path (Split-Path -Parent $IconIco) | Out-Null
    New-Item -ItemType Directory -Force -Path $Dest | Out-Null
    Copy-Item -LiteralPath $Jar -Destination $MainJar -Force

    Add-Type -AssemblyName System.Drawing
    $bitmap = [System.Drawing.Bitmap]::FromFile($IconPng)
    $icon = [System.Drawing.Icon]::FromHandle($bitmap.GetHicon())
    $stream = [System.IO.File]::Create($IconIco)
    try {
        $icon.Save($stream)
    } finally {
        $stream.Dispose()
        $icon.Dispose()
        $bitmap.Dispose()
    }

    jpackage `
        --type $Type `
        --name "Backend Toolbox" `
        --app-version $Version `
        --input $InputDir `
        --main-jar "backend-toolbox.jar" `
        --main-class "dev.awn.Main" `
        --dest $Dest `
        --icon $IconIco `
        --vendor "dev.awn" `
        --win-menu `
        --win-shortcut
} finally {
    Pop-Location
}
