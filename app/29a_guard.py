#!/usr/bin/env python3
"""
29A Anti-Spyware & Certificate Trust Engine (v2.1)
Lead Engineer: Nazmul Hossain | EMON INDUSTRY PVT LTD _29A
Support: secure@emonindustry.bd | Cyber Guardian (c) 2026
"""

import argparse
import json
import os
import subprocess
import sys
from datetime import datetime, timezone
from pathlib import Path

EXPECTED_FINGERPRINT = (
    "AA:DF:60:FC:CE:71:CA:A9:72:ED:2A:F8:6C:92:91:AE:66:"
    "03:9D:C5:53:B8:05:AF:DC:67:CD:0B:CA:20:6F:7B"
)
EXPECTED_SERIAL = "2EEF7046ECABEB9C493505A6B5002CAD818A15DD"
BASE_DIR = Path(__file__).resolve().parent.parent
CERT_PATH = BASE_DIR / "certs" / "cyber_guardian_root_ca.pem"


def run_cmd(cmd: list[str]) -> str:
    try:
        res = subprocess.run(cmd, capture_output=True, text=True, check=False)
        return res.stdout.strip()
    except FileNotFoundError:
        return ""


def verify_cyber_guardian_cert(cert_file: Path = CERT_PATH) -> dict:
    result = {"verified": False, "path": str(cert_file), "fingerprint": "", "serial": "", "message": ""}
    if not cert_file.exists():
        result["message"] = f"Certificate not found at {cert_file}"
        return result

    fp_out = run_cmd(["openssl", "x509", "-in", str(cert_file), "-noout", "-fingerprint", "-sha256"])
    serial_out = run_cmd(["openssl", "x509", "-in", str(cert_file), "-noout", "-serial"])
    actual_fp = fp_out.split("=")[-1].strip().upper()
    actual_serial = serial_out.split("=")[-1].strip().upper()
    result["fingerprint"] = actual_fp
    result["serial"] = actual_serial

    if actual_fp == EXPECTED_FINGERPRINT and actual_serial == EXPECTED_SERIAL:
        result["verified"] = True
        result["message"] = "Authentic Cyber Guardian Private Root CA verified."
    else:
        result["message"] = "Certificate fingerprint/serial mismatch! Potential tampering."
    return result


def scan_rogue_certificates() -> list[dict]:
    findings = []
    for directory in [Path("/usr/local/share/ca-certificates"), Path("/data/misc/user/0/cacerts-added"), Path.home() / ".local/share/ca-certificates"]:
        if not directory.exists():
            continue
        for item in directory.iterdir():
            if item.is_file():
                subj = run_cmd(["openssl", "x509", "-in", str(item), "-noout", "-subject"])
                fp = run_cmd(["openssl", "x509", "-in", str(item), "-noout", "-fingerprint", "-sha256"])
                if not fp:
                    subj = run_cmd(["openssl", "x509", "-inform", "DER", "-in", str(item), "-noout", "-subject"])
                    fp = run_cmd(["openssl", "x509", "-inform", "DER", "-in", str(item), "-noout", "-fingerprint", "-sha256"])
                if fp and EXPECTED_FINGERPRINT not in fp.upper():
                    findings.append({"file": str(item), "subject": subj, "fingerprint": fp})
    return findings


def scan_network_and_proxy_anomalies() -> list[str]:
    alerts = []
    for var in ("HTTP_PROXY", "HTTPS_PROXY", "ALL_PROXY", "http_proxy", "https_proxy", "all_proxy"):
        val = os.environ.get(var)
        if val:
            alerts.append(f"Active proxy environment variable detected: {var}={val}")
    suspicious_ports = {":4444", ":5555", ":6667", ":1337", ":31337", ":8081", ":8888"}
    net_out = run_cmd(["ss", "-tunap"]) or run_cmd(["netstat", "-tunap"])
    for line in net_out.splitlines():
        if "ESTAB" in line:
            for port in suspicious_ports:
                if port in line:
                    alerts.append(f"Suspicious established socket on {port}: {line.strip()}")
    return alerts


def main():
    parser = argparse.ArgumentParser(description="29A Anti-Spyware & Cyber Guardian Trust Scanner")
    parser.add_argument("--json", action="store_true", help="Output structured JSON report")
    args = parser.parse_args()

    cert_status = verify_cyber_guardian_cert()
    rogue_cas = scan_rogue_certificates()
    net_alerts = scan_network_and_proxy_anomalies()
    healthy = cert_status["verified"] and not rogue_cas and not net_alerts

    if args.json:
        print(json.dumps({
            "app": "29A Anti-Spyware & Trust Shield",
            "developer": "Nazmul Hossain",
            "roles": [
                "Computer Science Student",
                "Python Developer",
                "Linux & Cybersecurity Enthusiast",
                "Lead Engineer • System Architecture & Security Operations"
            ],
            "organization": "EMON INDUSTRY PVT LTD _29A",
            "support": "secure@emonindustry.bd",
            "timestamp_utc": datetime.now(timezone.utc).isoformat(),
            "status": "PROTECTED" if healthy else "ATTENTION_REQUIRED",
            "root_ca": cert_status,
            "rogue_user_cas": rogue_cas,
            "network_alerts": net_alerts,
        }, indent=2))
        sys.exit(0 if healthy else 1)

    print("=" * 64)
    print("  29A ANTI-SPYWARE & TRUST SHIELD (CYBER GUARDIAN v2.1)")
    print("  Lead Engineer: Nazmul Hossain | EMON INDUSTRY PVT LTD _29A")
    print("  Support Desk v2.1: secure@emonindustry.bd")
    print("=" * 64)
    print(f"\n[1/3] Root CA Integrity: {'[OK] ' + cert_status['message'] if cert_status['verified'] else '[ALERT] ' + cert_status['message']}")
    print(f"[2/3] Trust Store Audit: {'[OK] 0 rogue user CAs found' if not rogue_cas else f'[WARNING] {len(rogue_cas)} unverified CAs'}")
    print(f"[3/3] Network & Proxy:   {'[OK] Clean' if not net_alerts else f'[ALERT] {len(net_alerts)} anomalies'}")
    print("=" * 64)
    sys.exit(0 if healthy else 1)


if __name__ == "__main__":
    main()
