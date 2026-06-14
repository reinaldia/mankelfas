# ManKelFas (Manajemen Keluhan Fasilitas)

ManKelFas adalah aplikasi desktop berbasis JavaFX untuk memanajemen pelaporan, pemantauan, dan perbaikan keluhan fasilitas. Aplikasi ini memiliki multi-role dashboard untuk **Admin**, **Teknisi**, dan **Mahasiswa**.

## Prasyarat
- Java Development Kit (JDK) 11 atau lebih baru
- Apache Maven

## Cara Menjalankan Aplikasi

Berikut adalah panduan _command line_ untuk melakukan kompilasi, menjalankan, dan membersihkan *build* proyek ini melalui terminal/Command Prompt di *root folder* proyek.

### 1. Membersihkan Build (Clean)
Jika Anda ingin membersihkan file *build* lama (folder `target`) sebelum melakukan kompilasi ulang, jalankan:
```bash
mvn clean
```

### 2. Kompilasi (Compile)
Untuk mengompilasi *source code* Java menjadi _bytecode_, jalankan perintah berikut:
```bash
mvn compile
```

### 3. Menjalankan Aplikasi (Run)
Aplikasi ini menggunakan JavaFX plugin. Untuk menjalankan aplikasi secara langsung, gunakan perintah:
```bash
mvn javafx:run
```

---
*Catatan:*
- Pastikan Anda sudah menjalankan _database_ MySQL dan menyesuaikan kredensial di kelas konfigurasi database jika ada.
- *Default file* untuk inisialisasi basis data dapat ditemukan pada `schema.sql` dan `seeder.sql`.
