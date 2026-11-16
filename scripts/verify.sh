#!/usr/bin/env bash
# Cyber Guardian Package Integrity & Fingerprint Verifier v2.1
# EMON INDUSTRY PVT LTD _29A
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
CERT_DIR="$SCRIPT_DIR/../certs"
EXPECTED_FP="AA:DF:60:FC:CE:71:CA:A9:72:ED:2A:F8:6C:92:91:AE:66:03:9D:C5:53:B8:05:AF:DC:67:CD:0B:CA:20:6F:7B"

echo "=== CYBER GUARDIAN® v2.1 PACKAGE VERIFICATION ==="
cd "$CERT_DIR"
sha256sum -c SHA256SUMS

ACTUAL_FP=$(openssl x509 -in cyber_guardian_root_ca.pem -noout -fingerprint -sha256 | cut -d'=' -f2 | tr '[:lower:]' '[:upper:]')
if [[ "$ACTUAL_FP" != "$EXPECTED_FP" ]]; then
    echo "[FAIL] Fingerprint mismatch!"
    echo "Expected: $EXPECTED_FP"
    echo "Actual:   $ACTUAL_FP"
    exit 1
fi

echo "[OK] Root CA SHA-256 Fingerprint verified: $ACTUAL_FP"
openssl x509 -in cyber_guardian_root_ca.pem -noout -subject -issuer -dates
