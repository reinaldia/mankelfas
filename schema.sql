CREATE DATABASE IF NOT EXISTS db_mankelfas;
USE db_mankelfas;

CREATE TABLE users (
    id_user INT AUTO_INCREMENT PRIMARY KEY,
    nama VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role ENUM('Admin', 'Mahasiswa', 'Teknisi') NOT NULL,
    nim VARCHAR(50),
    level VARCHAR(50),
    keahlian VARCHAR(255)
);

CREATE TABLE fasilitas (
    id_fasilitas INT AUTO_INCREMENT PRIMARY KEY,
    nama VARCHAR(255) NOT NULL,
    kategori VARCHAR(100),
    lokasi VARCHAR(255),
    kondisi ENUM('BERFUNGSI_BAIK', 'RUSAK_RINGAN', 'RUSAK_PARAH', 'DALAM_PERBAIKAN', 'SEDANG_DIPERIKSA')
);

CREATE TABLE keluhan (
    id_keluhan INT AUTO_INCREMENT PRIMARY KEY,
    deskripsi TEXT NOT NULL,
    tanggal DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    prioritas ENUM('RENDAH', 'SEDANG', 'TINGGI', 'URGENT') DEFAULT 'RENDAH',
    foto_bukti VARCHAR(255),
    progress VARCHAR(255),
    estimasi_waktu VARCHAR(100),
    archived BOOLEAN DEFAULT FALSE,
    status ENUM('DILAPORKAN', 'DITUGASKAN', 'DIPROSES', 'SELESAI', 'DITOLAK', 'DIBATALKAN') DEFAULT 'DILAPORKAN',
    id_pelapor INT NOT NULL,
    id_teknisi INT,
    id_fasilitas INT NOT NULL,
    waktu_diproses DATETIME,
    target_selesai DATETIME,
    FOREIGN KEY (id_pelapor) REFERENCES users(id_user) ON DELETE CASCADE,
    FOREIGN KEY (id_teknisi) REFERENCES users(id_user) ON DELETE SET NULL,
    FOREIGN KEY (id_fasilitas) REFERENCES fasilitas(id_fasilitas) ON DELETE CASCADE
);

CREATE TABLE kendala_teknisi (
    id_kendala INT AUTO_INCREMENT PRIMARY KEY,
    id_keluhan INT NOT NULL,
    id_teknisi INT NOT NULL,
    alasan_kendala TEXT NOT NULL,
    waktu_dilaporkan DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_keluhan) REFERENCES keluhan(id_keluhan) ON DELETE CASCADE,
    FOREIGN KEY (id_teknisi) REFERENCES users(id_user) ON DELETE CASCADE
);

CREATE TABLE riwayat_keluhan (
    id_riwayat INT AUTO_INCREMENT PRIMARY KEY,
    id_keluhan INT NOT NULL,
    pesan TEXT NOT NULL,
    waktu DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_keluhan) REFERENCES keluhan(id_keluhan) ON DELETE CASCADE
);

CREATE TABLE komentar (
    id_komentar INT AUTO_INCREMENT PRIMARY KEY,
    id_keluhan INT NOT NULL,
    id_pengirim INT NOT NULL,
    isi_komentar TEXT NOT NULL,
    foto_bukti VARCHAR(255),
    tanggal DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_keluhan) REFERENCES keluhan(id_keluhan) ON DELETE CASCADE,
    FOREIGN KEY (id_pengirim) REFERENCES users(id_user) ON DELETE CASCADE
);

CREATE TABLE notifikasi (
    id_notif INT AUTO_INCREMENT PRIMARY KEY,
    id_user INT NOT NULL,
    pesan TEXT NOT NULL,
    tanggal DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    dibaca BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (id_user) REFERENCES users(id_user) ON DELETE CASCADE
);
