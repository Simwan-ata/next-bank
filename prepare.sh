#!/bin/bash
set -euo pipefail

echo "=========================================="
echo "  آماده‌سازی پروژه مدیریت مالی - اندروید"
echo "=========================================="
echo ""

if [ -f "App/src/main/assets/index.html" ]; then
    echo "✓ فایل HTML در assets موجود است"
else
    echo "✗ فایل HTML پیدا نشد!"
    exit 1
fi

echo ""
echo "پروژه آماده است."
echo ""
echo "ساختار ماژول: App/"
echo "مسیر APK: App/build/outputs/apk/debug/"
echo ""
echo "مراحل:"
echo "1. پروژه را در Android Studio باز کنید"
echo "2. Gradle Sync را اجرا کنید"
echo "3. Build → Build APK(s) را اجرا کنید"
