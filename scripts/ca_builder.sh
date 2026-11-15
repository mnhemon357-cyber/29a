#!/usr/bin/env bash
# Cyber Guardian CA Builder & Packer v2.1
# EMON INDUSTRY PVT LTD _29A
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
CERT_NAME="cyber_guardian_android_ca.crt"
OUTPUT_DIR="./output_pkg"

echo "[*] Initializing Cyber Guardian CA build..."
mkdir -p "$OUTPUT_DIR"

if [ -f "$CERT_NAME" ]; then
    cp "$CERT_NAME" "$OUTPUT_DIR/"
    echo "[+] Packaging completed successfully into $OUTPUT_DIR/"
elif [ -f "$SCRIPT_DIR/../certs/$CERT_NAME" ]; then
    cp "$SCRIPT_DIR/../certs/$CERT_NAME" "$OUTPUT_DIR/"
    echo "[+] Packaging completed successfully into $OUTPUT_DIR/"
else
    echo "[!] Error: $CERT_NAME not found."
    exit 1
fi
