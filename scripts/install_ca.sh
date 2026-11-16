#!/usr/bin/env bash
# 29A / Cyber Guardian CA Installer & Verifier v2.1
# EMON INDUSTRY PVT LTD _29A
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
CERT_PEM="$SCRIPT_DIR/../certs/cyber_guardian_root_ca.pem"
CERT_DER="$SCRIPT_DIR/../certs/cyber_guardian_root_ca.der"
EXPECTED_FP="AA:DF:60:FC:CE:71:CA:A9:72:ED:2A:F8:6C:92:91:AE:66:03:9D:C5:53:B8:05:AF:DC:67:CD:0B:CA:20:6F:7B"

if [[ ! -s "$CERT_PEM" ]]; then
    echo "[FAIL] Missing certificate: $CERT_PEM"
    exit 1
fi

echo "[*] Verifying SHA-256 fingerprint before installation..."
ACTUAL_FP=$(openssl x509 -in "$CERT_PEM" -noout -fingerprint -sha256 | cut -d'=' -f2 | tr '[:lower:]' '[:upper:]')

if [[ "$ACTUAL_FP" != "$EXPECTED_FP" ]]; then
    echo "[CRITICAL] Fingerprint mismatch! Aborting installation."
    exit 1
fi
echo "[OK] Fingerprint verified."

if [[ -d "/usr/local/share/ca-certificates" ]] && command -v update-ca-certificates >/dev/null 2>&1; then
    sudo cp "$CERT_PEM" /usr/local/share/ca-certificates/cyber_guardian_root_ca.crt
    sudo update-ca-certificates
    echo "[+] Installed to Linux system trust store."
elif [[ -n "${PREFIX:-}" ]] && [[ "$PREFIX" == *"com.termux"* ]]; then
    mkdir -p "$HOME/.29a/certs"
    cp "$CERT_PEM" "$CERT_DER" "$HOME/.29a/certs/"
    echo "[+] Copied certificates to $HOME/.29a/certs/."
    echo "[!] Install on Android: Settings -> Security -> Encryption & credentials -> Install a certificate -> CA certificate"
else
    echo "[!] Import PEM/CRT: $CERT_PEM | Android DER: $CERT_DER"
fi
