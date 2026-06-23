# Amaliy Fizika — Android ilova (APK)

7-, 8- va 9-sinf **amaliy fizika** platformasining Android ilovasi. Butunlay **oflayn** ishlaydi:
barcha darslar va tajribalar ilova ichida joylashgan, ma'lumotlar telefon xotirasida saqlanadi.

| | |
|---|---|
| **Ilova nomi** | Amaliy Fizika |
| **Paket (Application ID)** | `uz.muhandisd.amaliyfizika` |
| **Minimal Android** | 7.0 (API 24) |
| **Internet** | **kerak emas** (INTERNET ruxsati umuman yo'q) |

---

## ⚠️ Nega tayyor APK emas, loyiha?

APK faylni qurish uchun **Android SDK** (Google serverlaridan) kerak bo'ladi, lekin men ishlayotgan
muhitda Google/Gradle serverlariga ulanish yopiq. Shu sababli men sizga **to'liq, qurishga tayyor
loyihani** tayyorladim — logotip ikonka qilingan, HTML ichiga joylangan, xavfsiz sozlangan.
APK faylni quyidagi 2 usuldan biri bilan olasiz. **1-usul hech narsa o'rnatishni talab qilmaydi.**

---

## ✅ 1-usul — GitHub orqali (tavsiya etiladi, hech narsa o'rnatmaysiz)

GitHub serverlari APK ni siz uchun avtomatik quradi.

1. [github.com](https://github.com) da bepul hisob oching (agar yo'q bo'lsa).
2. Yangi **repository** yarating (masalan, `amaliy-fizika`), "Private" qilsangiz ham bo'ladi.
3. Ushbu papkadagi **barcha fayllarni** repozitoriyga yuklang
   (eng oson yo'li: GitHub sahifasida **Add file → Upload files** → hamma narsani sudrab tashlang → Commit).
4. Yuklash tugagach, **Actions** bo'limiga o'ting. "Build APK" ish jarayoni avtomatik boshlanadi
   (yoki **Run workflow** tugmasini bosing). 3–5 daqiqa kuting — yashil ✓ paydo bo'ladi.
5. O'sha jarayonni oching → pastdagi **Artifacts** bo'limidan **`AmaliyFizika-debug-apk`** ni yuklab oling.
   Ichida `app-debug.apk` bo'ladi.

Bu APK debug-kalit bilan imzolangan — to'g'ridan-to'g'ri telefonga o'rnatsa bo'ladi.

---

## 🛠 2-usul — Android Studio orqali (kompyuterda)

1. [Android Studio](https://developer.android.com/studio) ni o'rnating (bepul).
2. **Open** → ushbu `AmaliyFizika` papkasini tanlang. Gradle o'zi kerakli narsalarni yuklab oladi.
3. Yuqori menyudan **Build → Build Bundle(s) / APK(s) → Build APK(s)**.
4. Tayyor bo'lgach "locate" havolasini bossangiz, APK shu yerda bo'ladi:
   `app/build/outputs/apk/debug/app-debug.apk`

---

## 📱 APK ni telefonga o'rnatish

1. `app-debug.apk` faylni telefonga ko'chiring (Telegram, USB, Google Drive va h.k.).
2. Faylni oching. Android "Noma'lum manbalar"dan o'rnatishni so'rasa, ruxsat bering
   (Sozlamalar → Ilovalarni o'rnatish ruxsati).
3. **O'rnatish** → tugadi. Bosh ekranda logotipingiz bilan "Amaliy Fizika" paydo bo'ladi.

---

## 🔒 Xavfsizlik

- **Internetga chiqmaydi.** `AndroidManifest.xml` da INTERNET ruxsati ataylab yo'q —
  ilova texnik jihatdan tarmoqqa ulana olmaydi.
- **Hech qanday ruxsat so'ramaydi** (kamera, joylashuv, kontaktlar — hech biri).
- Barcha kontent ilova ichida; tashqi server, reklama yoki kuzatuvchi (tracker) yo'q.
- Kod ochiq — `MainActivity.java` da har bir sozlama izohlangan.
- APK debug-kalit bilan imzolanadi (shaxsiy foydalanish uchun yetarli).

---

## 🏪 (Ixtiyoriy) Play Store uchun imzolangan APK/AAB

Do'konga qo'yish uchun o'z **release** kalitingiz kerak:

```bash
keytool -genkey -v -keystore amaliy-fizika.jks -keyalg RSA -keysize 2048 -validity 10000 -alias afkey
```

So'ng `app/build.gradle` ga `signingConfigs` qo'shib, `./gradlew assembleRelease` yoki
`./gradlew bundleRelease` (AAB) ni ishga tushiring. Batafsil:
[developer.android.com/studio/publish/app-signing](https://developer.android.com/studio/publish/app-signing).

---

## 📂 Loyiha tuzilishi

```
AmaliyFizika/
├── app/
│   ├── build.gradle
│   ├── proguard-rules.pro
│   └── src/main/
│       ├── AndroidManifest.xml          ← ruxsatlar (INTERNET yo'q)
│       ├── assets/index.html            ← platformangiz (75 dars)
│       ├── java/.../MainActivity.java   ← xavfsiz WebView
│       └── res/                         ← ikonkalar (barcha o'lchamlar)
├── .github/workflows/build-apk.yml      ← GitHub avtomatik qurilishi
├── gradlew / gradlew.bat / gradle/      ← Gradle wrapper
├── build.gradle / settings.gradle
└── store/ic_launcher-playstore.png      ← 512px Play Store ikonkasi
```
