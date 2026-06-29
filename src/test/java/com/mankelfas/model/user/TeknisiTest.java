package com.mankelfas.model.user;

import com.mankelfas.enumeration.StatusKeluhan;
import com.mankelfas.enumeration.KondisiFasilitas;
import com.mankelfas.model.keluhan.Keluhan;
import com.mankelfas.model.keluhan.Fasilitas;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TeknisiTest {

    private Teknisi teknisi;
    private Keluhan keluhan;

    @BeforeEach
    void setUp() {
        teknisi = new Teknisi(1, "Budi Teknisi", "budi@teknisi.com", "password", "Listrik");
        
        // Buat fasilitas dummy untuk keluhan
        Fasilitas fasilitas = new Fasilitas(1, "AC Ruang 101", "Elektronik", "Ruang 101", KondisiFasilitas.RUSAK_RINGAN);
        Mahasiswa pelapor = new Mahasiswa(2, "Andi", "andi@mhs.com", "pass", "12345");
        
        // Buat keluhan dummy
        keluhan = new Keluhan(1, "AC mati total", "", pelapor, fasilitas);
    }

    @Test
    void testTeknisiInitialization() {
        assertEquals("Budi Teknisi", teknisi.getNama());
        assertEquals("Listrik", teknisi.getKeahlian());
        assertEquals("Teknisi", teknisi.getRole());
        assertTrue(teknisi.getTugas().isEmpty(), "Daftar tugas harusnya kosong pada saat inisialisasi");
    }

    @Test
    void testTambahTugas() {
        teknisi.tambahTugas(keluhan);
        assertEquals(1, teknisi.getTugas().size());
        assertEquals(keluhan, teknisi.getTugas().get(0));
    }
    
    @Test
    void testTambahTugas_Null() {
        teknisi.tambahTugas(null);
        assertTrue(teknisi.getTugas().isEmpty(), "Tugas null tidak boleh ditambahkan");
    }

    @Test
    void testUpdateStatus() {
        // Harus ditambah ke tugas dulu agar bisa diupdate oleh teknisi ini
        teknisi.tambahTugas(keluhan);
        
        teknisi.updateStatus(keluhan, StatusKeluhan.DIPROSES, "Sedang mengecek kabel");
        
        assertEquals(StatusKeluhan.DIPROSES, keluhan.getStatus());
        assertEquals(1, keluhan.getKomentar().size());
        assertEquals("Sedang mengecek kabel", keluhan.getKomentar().get(0).getIsiKomentar());
    }

    @Test
    void testUpdateStatus_NotAssignedToTeknisi() {
        // Keluhan tidak ditambahkan ke daftar tugas teknisi ini
        teknisi.updateStatus(keluhan, StatusKeluhan.DIPROSES, "Test update ilegal");
        
        // Status keluhan tidak boleh berubah (biasanya DILAPORKAN secara default)
        assertNotEquals(StatusKeluhan.DIPROSES, keluhan.getStatus());
        assertTrue(keluhan.getKomentar().isEmpty());
    }
}
