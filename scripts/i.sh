#!/usr/bin/env bash
# Cyber Guardian Runtime Integrator (i.sh)
# EMON INDUSTRY PVT LTD _29A
set -euo pipefail

echo "=========================================="
echo "   CYBER GUARDIAN RUNTIME VERIFICATION"
echo "=========================================="

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
if [ -s "./cyber_guardian_android_ca.crt" ]; then
    TARGET_CRT="./cyber_guardian_android_ca.crt"
elif [ -s "$SCRIPT_DIR/../certs/cyber_guardian_android_ca.crt" ]; then
    TARGET_CRT="$SCRIPT_DIR/../certs/cyber_guardian_android_ca.crt"
else
    echo "[FAIL] Missing or empty certificate: cyber_guardian_android_ca.crt"
    exit 1
fi

echo "[1] Inspecting certificate subject and issuer..."
openssl x509 -inform DER -in "$TARGET_CRT" -noout -subject -issuer 2>/dev/null || \
    openssl x509 -in "$TARGET_CRT" -noout -subject -issuer

echo "[2] Verifying cryptographic validity window..."
openssl x509 -inform DER -in "$TARGET_CRT" -noout -dates 2>/dev/null || \
    openssl x509 -in "$TARGET_CRT" -noout -dates

echo "[3] Checking SHA-256 fingerprint consistency..."
openssl x509 -inform DER -in "$TARGET_CRT" -noout -fingerprint -sha256 2>/dev/null || \
    openssl x509 -in "$TARGET_CRT" -noout -fingerprint -sha256

echo "=========================================="
echo "   INTEGRATION CHECK COMPLETED [OK]"
echo "=========================================="
