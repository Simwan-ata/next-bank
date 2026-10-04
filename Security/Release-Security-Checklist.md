# Release Security Gate

## Required before release

- [ ] No Critical or High security finding remains unresolved.
- [ ] WebView is restricted to trusted local assets.
- [ ] No external JavaScript/CDN is required for financial processing.
- [ ] READ_SMS is not requested at startup.
- [ ] Raw SMS is not exposed through the JavaScript bridge.
- [ ] Local financial data is encrypted with Android Keystore-backed AES/GCM.
- [ ] Android backup is disabled.
- [ ] Parser rejects invalid and overflowing amounts.
- [ ] Duplicate imports are deterministic.
- [ ] XSS payloads are escaped before rendering.
- [ ] No financial data appears in logs.
- [ ] Release APK is reviewed for exported components and permissions.
- [ ] Manual test completed on at least one Android 13+ device and one older supported API level.
