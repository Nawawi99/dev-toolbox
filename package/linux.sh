#!/usr/bin/env bash
set -euo pipefail

VERSION="${1:-1.0.0}"
TYPE="${2:-deb}"
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
INPUT_DIR="$ROOT/target/package/input"
ICON_PNG="$ROOT/src/main/resources/icons/backend-toolbox.png"
DEST="$ROOT/dist"

cd "$ROOT"
rm -rf "$INPUT_DIR"
mkdir -p "$INPUT_DIR" "$DEST"
mvn -DskipTests package dependency:copy-dependencies "-DoutputDirectory=$INPUT_DIR" -DincludeScope=runtime

cp "$ROOT/target/toolbox-1.0-SNAPSHOT.jar" "$INPUT_DIR/backend-toolbox.jar"

jpackage \
  --type "$TYPE" \
  --name "backend-toolbox" \
  --app-version "$VERSION" \
  --input "$INPUT_DIR" \
  --main-jar "backend-toolbox.jar" \
  --main-class "dev.awn.Main" \
  --dest "$DEST" \
  --icon "$ICON_PNG" \
  --vendor "dev.awn"
