# TITAN NATIVE LITE — versi perbaikan awal

Aplikasi Android Java mandiri dengan papan 8×8, posisi awal 32 bidak, pemeriksaan langkah legal (termasuk rokade dan en passant), undo, reset, dan analisis **sederhana** berbasis evaluasi material dua ply.

**Penting:** Ini **belum** menggunakan Stockfish dan bukan salinan fitur penuh aplikasi referensi. Label analisis sengaja menyatakan BASIC ANALYSIS agar tidak menyesatkan. Promosi pion otomatis menjadi menteri. Belum diuji pada perangkat Android nyata.

## Build
Buka folder proyek sebagai Android Gradle project atau unggah seluruh isinya ke root repository GitHub. Jalankan GitHub Actions > Build Android APK > Run workflow, lalu unduh artifact `TitanNativeFixed-debug-apk` setelah build sukses.

Jangan unggah ZIP sebagai satu file saja ke root repository: ekstrak terlebih dahulu sehingga folder `app`, `.github` dan file Gradle ada di root.
