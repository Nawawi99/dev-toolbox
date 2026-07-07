param(
    [string]$Version = "1.0.0",
    [ValidateSet("app-image", "exe", "msi")]
    [string]$Type = "exe"
)

$ErrorActionPreference = "Stop"
$Root = Split-Path -Parent $PSScriptRoot
$InputDir = Join-Path $Root "target\package\input"
$IconPng = Join-Path $Root "src\main\resources\icons\dev-toolbox.png"
$IconIco = Join-Path $Root "target\package\dev-toolbox.ico"
$Jar = Join-Path $Root "target\dev-toolbox-1.0-SNAPSHOT.jar"
$MainJar = Join-Path $InputDir "dev-toolbox.jar"
$Dest = Join-Path $Root "dist"

function New-IconPngBytes {
    param(
        [string]$Source,
        [int]$Size
    )

    $sourceImage = [System.Drawing.Image]::FromFile($Source)
    $bitmap = New-Object System.Drawing.Bitmap $Size, $Size, ([System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
    $graphics = [System.Drawing.Graphics]::FromImage($bitmap)
    $graphics.SmoothingMode = [System.Drawing.Drawing2D.SmoothingMode]::AntiAlias
    $graphics.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::HighQualityBicubic
    $graphics.PixelOffsetMode = [System.Drawing.Drawing2D.PixelOffsetMode]::HighQuality
    $graphics.Clear([System.Drawing.Color]::Transparent)
    $graphics.DrawImage($sourceImage, 0, 0, $Size, $Size)

    $stream = New-Object System.IO.MemoryStream
    try {
        $bitmap.Save($stream, [System.Drawing.Imaging.ImageFormat]::Png)
        return ,$stream.ToArray()
    } finally {
        $stream.Dispose()
        $graphics.Dispose()
        $bitmap.Dispose()
        $sourceImage.Dispose()
    }
}

function Convert-PngToMultiSizeIco {
    param(
        [string]$Source,
        [string]$Destination
    )

    $sizes = @(16, 24, 32, 48, 64, 128, 256)
    $images = foreach ($size in $sizes) {
        [PSCustomObject]@{
            Size = $size
            Data = New-IconPngBytes -Source $Source -Size $size
        }
    }

    $stream = [System.IO.File]::Create($Destination)
    $writer = New-Object System.IO.BinaryWriter $stream
    try {
        $writer.Write([UInt16]0)
        $writer.Write([UInt16]1)
        $writer.Write([UInt16]$images.Count)

        $offset = 6 + (16 * $images.Count)
        foreach ($image in $images) {
            $dimension = if ($image.Size -eq 256) { 0 } else { $image.Size }
            $writer.Write([Byte]$dimension)
            $writer.Write([Byte]$dimension)
            $writer.Write([Byte]0)
            $writer.Write([Byte]0)
            $writer.Write([UInt16]1)
            $writer.Write([UInt16]32)
            $writer.Write([UInt32]$image.Data.Length)
            $writer.Write([UInt32]$offset)
            $offset += $image.Data.Length
        }

        foreach ($image in $images) {
            $writer.Write($image.Data)
        }
    } finally {
        $writer.Dispose()
        $stream.Dispose()
    }
}

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
    Convert-PngToMultiSizeIco -Source $IconPng -Destination $IconIco

    jpackage `
        --type $Type `
        --name "Dev Toolbox" `
        --app-version $Version `
        --input $InputDir `
        --main-jar "dev-toolbox.jar" `
        --main-class "dev.awn.Main" `
        --dest $Dest `
        --icon $IconIco `
        --vendor "dev.awn" `
        --win-menu `
        --win-shortcut
} finally {
    Pop-Location
}
