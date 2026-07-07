#!/usr/bin/env bash
set -euo pipefail

VERSION="${1:-1.0.0}"
TYPE="${2:-dmg}"
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
INPUT_DIR="$ROOT/target/package/input"
ICON_PNG="$ROOT/src/main/resources/icons/dev-toolbox.png"
ICONSET="$ROOT/target/package/DevToolbox.iconset"
ICON_ICNS="$ROOT/target/package/dev-toolbox.icns"
DEST="$ROOT/dist"

cd "$ROOT"
rm -rf "$INPUT_DIR"
mkdir -p "$INPUT_DIR" "$ICONSET" "$DEST"
mvn -DskipTests package dependency:copy-dependencies "-DoutputDirectory=$INPUT_DIR" -DincludeScope=runtime

cp "$ROOT/target/dev-toolbox-1.0-SNAPSHOT.jar" "$INPUT_DIR/dev-toolbox.jar"

sips -z 16 16 "$ICON_PNG" --out "$ICONSET/icon_16x16.png" >/dev/null
sips -z 32 32 "$ICON_PNG" --out "$ICONSET/icon_16x16@2x.png" >/dev/null
sips -z 32 32 "$ICON_PNG" --out "$ICONSET/icon_32x32.png" >/dev/null
sips -z 64 64 "$ICON_PNG" --out "$ICONSET/icon_32x32@2x.png" >/dev/null
sips -z 128 128 "$ICON_PNG" --out "$ICONSET/icon_128x128.png" >/dev/null
sips -z 256 256 "$ICON_PNG" --out "$ICONSET/icon_128x128@2x.png" >/dev/null
sips -z 256 256 "$ICON_PNG" --out "$ICONSET/icon_256x256.png" >/dev/null
sips -z 512 512 "$ICON_PNG" --out "$ICONSET/icon_256x256@2x.png" >/dev/null
sips -z 512 512 "$ICON_PNG" --out "$ICONSET/icon_512x512.png" >/dev/null
cp "$ICON_PNG" "$ICONSET/icon_512x512@2x.png"
iconutil -c icns "$ICONSET" -o "$ICON_ICNS"

jpackage \
  --type "$TYPE" \
  --name "Dev Toolbox" \
  --app-version "$VERSION" \
  --input "$INPUT_DIR" \
  --main-jar "dev-toolbox.jar" \
  --main-class "dev.awn.Main" \
  --dest "$DEST" \
  --icon "$ICON_ICNS" \
  --vendor "dev.awn"
