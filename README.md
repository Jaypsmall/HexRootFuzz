# 😈 HEX ROOT FUZZ (V.1.0.0)   ![Android](https://img.shields.io/badge/Android-181717?style=flat&logo=android&logoColor=yellow) ![Kotlin](https://img.shields.io/badge/kotlin-181717?style=flat&logo=kotlin&logoColor=yellow)

 Android application for web security testing and reconnaissance from mobile devices.

![Platform](https://img.shields.io/badge/Platform-Android-green)
![Language](https://img.shields.io/badge/Kotlin-Jetpack%20Compose-blue)
![License](https://img.shields.io/badge/License-MIT-orange)

---

## 📖 Overview

😈 **HexRootFuzz** is an Android application designed to simplify web reconnaissance and security assessments through an intuitive graphical interface.

Instead of manually executing terminal commands, HexRootFuzz provides an all-in-one environment for running popular security tools directly from Android.

---

## ✨ Features

- 🔍 FFUF integration
- 📂 Gobuster integration
- 🔑 Hydra integration
- 📝 Custom wordlist generator
- 📁 Wordlist manager
- 📊 Real-time execution logs
- 📋 Copy results
- 📤 Export scan results
- 🎨 Modern Material Design interface

---

## 🛠 Included Tools

### FFUF

- Directory fuzzing
- API endpoint discovery
- Hidden files detection
- Response filtering
- Status code filtering

---

### Gobuster

- Directory enumeration
- DNS enumeration
- Virtual host discovery

---

### Hydra

Supports credential testing against compatible authentication services for authorized security assessments.

---

### Wordlist Generator

Generate custom wordlists including:

- Common directories
- API endpoints
- Password lists
- Custom dictionaries

---

## 🚀 Requirements

- Android 8.0+
- Termux
- Root access (optional depending on configuration)
- FFUF
- Gobuster
- Hydra

---

## 📂 Project Structure

```
HexRootFuzz/
│
├── app/
├── ui/
├── tools/
│   ├── ffuf
│   ├── gobuster
│   ├── hydra
│   └── wordlists
├── utils/
└── assets/
```

---

## 📦 Installation

https://github.com/Jaypsmall/HexRootFuzz/releases/download/android-root/HexRootFuzz_v1.0.2.apk

---

## ⚙ Example FFUF

```bash
ffuf \
-w wordlists/directories.txt \
-u https://target.com/FUZZ \
-mc 200
```

---

## 🎯 Planned Features

- Dark mode improvements
- Scan history
- JSON export
- CSV export
- HTML reports
- Multi-thread configuration
- Custom headers
- Cookie support
- Proxy support
- Recursive fuzzing

---

## ⚠ Disclaimer

😈 **HexRootFuzz** is intended **only for authorized security testing, research, and educational purposes**.

Users are solely responsible for ensuring they have explicit permission before testing any target.

Unauthorized security testing may violate laws or terms of service.

---

## ❤️ Contributing

Pull requests are welcome.

If you'd like to improve the project, feel free to fork it and submit your ideas.

---

## 📄 License

MIT License

---

Made with ❤️ using Kotlin + Android + Jetpack Compose
