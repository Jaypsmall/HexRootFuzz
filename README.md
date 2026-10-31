# 😈 HEX ROOT FUZZ (V.1.0.2) ![Android](https://img.shields.io/badge/Android-181717?style=flat&logo=android&logoColor=3868D8) ![Kotlin](https://img.shields.io/badge/kotlin-181717?style=flat&logo=kotlin&logoColor=3868D8)

😈 **HexRootFuzz** es una aplicación para Android diseñada para simplificar el reconocimiento web y las evaluaciones de seguridad mediante una interfaz gráfica intuitiva.

En lugar de ejecutar manualmente comandos en la terminal, HexRootFuzz proporciona un entorno integral para ejecutar herramientas de seguridad populares directamente desde Android.

<a href="https://github.com/Jaypsmall/HexRootFuzz/releases/download/android-root/HexRootFuzz_v1.0.2.apk">
<img src="https://img.shields.io/badge/DOWNLOAD_HEXROOTFUZZ_v1.0.2_APK-181717?style=flat&logo=android&logoColor=3868D8" alt="Descargar versión">
</a>

---

<p align="center" width="100%">
<img src="https://github.com/Jaypsmall/HexRootFuzz/blob/master/assets/image2.png" width="30%" />
<img src="https://github.com/Jaypsmall/HexRootFuzz/blob/master/assets/image3.png" width="30%" />
<img src="https://github.com/Jaypsmall/HexRootFuzz/blob/master/assets/image4.png" width="30%" />
</p>

---

## ✨ Funcionalidades

- 🔍 Integración con FFUF
- 📂 Integración con Gobuster
- 🔑 Integración con Hydra
- 📝 Generador de listas de palabras personalizadas
- 📁 Gestor de listas de palabras
- 📊 Registros de ejecución en tiempo real
- 📋 Copiar resultados
- 📤 Exportar resultados del análisis
- 🎨 Interfaz moderna con Material Design

---

## 🛠 Herramientas incluidas

### FFUF

- Fuzzing de directorios
- API  Descubrimiento de puntos finales
- Detección de archivos ocultos
- Filtrado de respuestas
- Filtrado de códigos de estado

---

### Gobuster

- Enumeración de directorios
- Enumeración de DNS
- Descubrimiento de hosts virtuales

---

### Hydra

Admite pruebas de credenciales con servicios de autenticación compatibles para evaluaciones de seguridad autorizadas.

---

### Generador de listas de palabras

Genera listas de palabras personalizadas que incluyen:

- Directorios comunes
- Puntos finales de la API
- Listas de contraseñas
- Diccionarios personalizados

---

## 🚀 Requisitos

- Android 8.0+
- Termux
- Acceso root (opcional según la configuración)
- FFUF
- Gobuster
- Hydra

---

## 📂 Estructura del proyecto

```
HexRootFuzz/
│
├── app/
├── ui/
├── tools/
│ ├── ffuf
│ ├── gobuster
│ ├── hydra
│ └── wordlists
├──  utilidades/
└── recursos/
```

---

## ⚙ Ejemplo FFUF

```bash
ffuf \
-w wordlists/directories.txt \
-u https://target.com/FUZZ \
-mc 200
```

---

## 🎯 Funcionalidades planificadas

- Mejoras en el modo oscuro
- Historial de escaneo
- Exportación a JSON
- Exportación a CSV
- Informes HTML
- Configuración multihilo
- Encabezados personalizados
- Compatibilidad con cookies
- Compatibilidad con proxies
- Fuzzing recursivo

---

## ⚠ Aviso legal

😈 **HexRootFuzz** está destinado **únicamente para pruebas de seguridad, investigación y fines educativos autorizados**.

Los usuarios son los únicos responsables de asegurarse de tener permiso explícito antes de probar cualquier objetivo.

Las pruebas de seguridad no autorizadas pueden infringir las leyes o los términos de servicio.

---

## 💙 Contribuciones

Se aceptan solicitudes de extracción.

Si deseas mejorar el proyecto, puedes bifurcarlo y enviar tus ideas.

---

## 📄 Licencia

Licencia MIT

---

Desarrollado con 💙 usando Kotlin + Android + Jetpack Compose
