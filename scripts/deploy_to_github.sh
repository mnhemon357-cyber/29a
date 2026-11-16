#!/usr/bin/env bash
# One-Command GitHub Publisher for 29A
# Usage: ./scripts/deploy_to_github.sh <your-github-username> [repo-name]
set -euo pipefail

USER_NAME="${1:-}"
REPO_NAME="${2:-29a}"

if [[ -z "$USER_NAME" ]]; then
    echo "Usage: $0 <your-github-username> [repo-name]"
    exit 1
fi

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR"

chmod +x scripts/*.sh app/29a_guard.py
./scripts/verify.sh

if [ ! -d ".git" ]; then
    git init
    git branch -M main
fi

git config user.name "${GIT_AUTHOR_NAME:-Nazmul Hossain}"
git config user.email "${GIT_AUTHOR_EMAIL:-secure@emonindustry.bd}"

git add .
if git diff --cached --name-only | grep -E "private_key|\.key$|\.p12$|\.pfx$"; then
    echo "[CRITICAL] Private key detected in staging! Aborting."
    exit 1
fi

git commit -m "Release v2.1: 29A Anti-Spyware APK, Cyber Guardian Root CA & Web Portal" || true

REMOTE_URL="https://github.com/${USER_NAME}/${REPO_NAME}.git"
if git remote get-url origin >/dev/null 2>&1; then
    git remote set-url origin "$REMOTE_URL"
else
    git remote add origin "$REMOTE_URL"
fi

echo "[*] Pushing to $REMOTE_URL ..."
git push -u origin main

echo ""
echo "=========================================================="
echo "  PUSH COMPLETE!"
echo "  1. Enable Website: https://github.com/${USER_NAME}/${REPO_NAME}/settings/pages"
echo "     Set Branch: 'main' and Folder: '/docs' -> Save"
echo "  2. Live Website:   https://${USER_NAME}.github.io/${REPO_NAME}}/"
echo "  3. APK Auto-Build: https://github.com/${USER_NAME}/${REPO_NAME}}/actions"
echo "=========================================================="
