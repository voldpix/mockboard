#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$(cd "$SCRIPT_DIR/../.." && pwd)"
JAR_PATH="$ROOT_DIR/target/mockboard-0.4-beta.jar"

"$SCRIPT_DIR/rebuild_java.sh"

if [ ! -f "$JAR_PATH" ]; then
  echo "Jar not found: $JAR_PATH" >&2
  exit 1
fi

echo "Running Mockboard from $JAR_PATH"
exec java -jar "$JAR_PATH" "$@"
