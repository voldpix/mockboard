#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$(cd "$SCRIPT_DIR/../.." && pwd)"

"$SCRIPT_DIR/build_frontend.sh"

cd "$ROOT_DIR"

echo "Rebuilding Java package..."
./mvnw clean package -DskipTests

