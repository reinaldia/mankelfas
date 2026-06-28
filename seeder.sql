USE db_mankelfas;

-- Seeder for users
INSERT INTO users (nama, email, password, role, nim, level, keahlian) VALUES 
('Admin Utama', 'admin@mankelfas.com', 'admin123', 'Admin', NULL, 'Super Admin', NULL),
('Budi Teknisi', 'budi@tek.com', 'pass123', 'Teknisi', NULL, NULL, 'Elektronik'),
('Andi Tukang', 'andi@tek.com', 'pass123', 'Teknisi', NULL, NULL, 'Furnitur'),
('Admin Junior', 'junior@mankelfas.com', 'admin123', 'Admin', NULL, 'Moderator', NULL),
('Akmal', 'akmal@mhs.com', 'pass123', 'Mahasiswa', 'NIM111', NULL, NULL),
('Budi', 'budi@mhs.com', 'pass123', 'Mahasiswa', 'NIM222', NULL, NULL),
('Mahasiswa 1', 'mhs@mankelfas.com', 'mhs123', 'Mahasiswa', 'NIM12345', NULL, NULL);

-- Seeder for fasilitas
INSERT INTO fasilitas (nama, kategori, lokasi, kondisi) VALUES
('Proyektor Ruang A1', 'Elektronik', 'Gedung A, Lantai 1', 'RUSAK_RINGAN'),
('AC Ruang B2', 'Elektronik', 'Gedung B, Lantai 2', 'RUSAK_PARAH'),
('Kursi Mahasiswa', 'Furnitur', 'Ruang C3', 'RUSAK_PARAH');

-- Seeder for keluhan
-- Assuming id_user: Mahasiswa 1 = 7, Budi Teknisi = 2
INSERT INTO keluhan (deskripsi, prioritas, status, id_pelapor, id_teknisi, id_fasilitas) VALUES
('Proyektor tidak menyala saat dihubungkan ke laptop', 'SEDANG', 'DILAPORKAN', 7, NULL, 1),
('AC bocor dan meneteskan air ke meja mahasiswa', 'TINGGI', 'DIPROSES', 7, 2, 2);

-- Seeder for riwayat_keluhan
INSERT INTO riwayat_keluhan (id_keluhan, pesan) VALUES
(1, 'Keluhan dilaporkan oleh Mahasiswa 1'),
(2, 'Keluhan dilaporkan oleh Mahasiswa 1'),
(2, 'Sistem mengubah status keluhan menjadi DIPROSES');
