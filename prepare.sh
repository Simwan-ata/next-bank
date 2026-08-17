#!/bin/bash
# اسکریپت کمکی برای آماده‌سازی پروژه اندروید

echo "=========================================="
echo "  آماده‌سازی پروژه مدیریت مالی - اندروید"
echo "=========================================="
echo ""

# بررسی وجود index.html در assets
if [ -f "app/src/main/assets/index.html" ]; then
    echo "✓ فایل HTML در assets موجود است"
else
    echo "✗ فایل HTML پیدا نشد!"
    exit 1
fi

echo ""
echo "پروژه آماده است."
echo ""
echo "مراحل بعدی:"
echo "1. پوشه را در Android Studio باز کنید"
echo "2. صبر کنید تا Gradle Sync تمام شود"
echo "3. Build → Build APK(s) را بزنید"
echo ""
echo "مسیر APK پس از ساخت:"
echo "  app/build/outputs/apk/debug/app-debug.apk"
echo ""
