# مدیریت مالی پیامک‌های بانکی - نسخه اندروید + GitHub Action

این پروژه شامل:
- موکاپ HTML داخل WebView
- ساختار کامل پروژه اندروید
- **GitHub Action** برای ساخت خودکار APK

---

## ساخت APK روی GitHub (پیشنهادی)

### مراحل:

1. یک مخزن جدید در GitHub بسازید.
2. محتویات این پوشه را داخل مخزن قرار دهید و push کنید (شاخه `main`).
3. به تب **Actions** بروید.
4. workflow به نام **Build APK** را انتخاب کنید.
5. روی **Run workflow** کلیک کنید (یا صبر کنید تا بعد از push به صورت خودکار اجرا شود).
6. پس از اتمام موفق بیلد:
   - به پایین صفحه بروید
   - در بخش **Artifacts** فایل `bank-sms-manager-v1-debug` را دانلود کنید
   - داخل آن فایل `.apk` وجود دارد

### نکته مهم
- برای اولین بار ممکن است بیلد چند دقیقه طول بکشد (دانلود SDK و وابستگی‌ها).
- APK ساخته‌شده از نوع **Debug** است و می‌توانید مستقیم روی گوشی نصب کنید (با اجازه نصب از منابع ناشناخته).

---

## ساخت APK به صورت محلی با Android Studio

1. پوشه را در Android Studio باز کنید.
2. صبر کنید تا Gradle Sync تمام شود.
3. از منو: `Build` → `Build Bundle(s) / APK(s)` → `Build APK(s)`
4. مسیر خروجی:
   ```
   app/build/outputs/apk/debug/app-debug.apk
   ```

---

## ساختار پروژه

```
bank-sms-manager-android/
├── .github/workflows/build-apk.yml   ← GitHub Action ساخت APK
├── app/
│   ├── build.gradle.kts
│   └── src/main/
│       ├── assets/index.html         ← موکاپ
│       ├── java/.../MainActivity.kt  ← WebView
│       ├── res/
│       └── AndroidManifest.xml
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
├── prepare.sh
└── README.md
```

---

## نسخه

- **v1.0** – WebView + GitHub Action برای ساخت خودکار APK
