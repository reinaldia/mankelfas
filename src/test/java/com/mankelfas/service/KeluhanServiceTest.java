package com.mankelfas.service;

import com.mankelfas.model.keluhan.Fasilitas;
import com.mankelfas.model.keluhan.Keluhan;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class KeluhanServiceTest {

    private final KeluhanService keluhanService = KeluhanService.getInstance();

    @Test
    void testAddKeluhan_withShortDescription_throwsException() {
        Keluhan k = new Keluhan(1, "Singkat", null, null, null);
        
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            keluhanService.addKeluhan(k);
        });
        assertEquals("Deskripsi keluhan harus detail (minimal 10 karakter).", exception.getMessage());
    }

    @Test
    void testAddKeluhan_withNullFasilitas_throwsException() {
        Keluhan k = new Keluhan(2, "Lampu di ruangan mati dan perlu diganti segera.", null, null, null);
        // Fasilitas sengaja dibiarkan null (parameter ke-5)
        
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            keluhanService.addKeluhan(k);
        });
        assertEquals("Harus memilih fasilitas yang dikeluhkan.", exception.getMessage());
    }

    @Test
    void testAddFasilitas_withEmptyName_throwsException() {
        Fasilitas f = new Fasilitas(1, "", "Umum", "Gedung A", com.mankelfas.enumeration.KondisiFasilitas.BERFUNGSI_BAIK);
        
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            keluhanService.addFasilitas(f);
        });
        assertEquals("Nama fasilitas harus diisi.", exception.getMessage());
    }

    @Test
    void testDeleteKeluhan_withInvalidId_throwsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            keluhanService.deleteKeluhan(0);
        });
        assertEquals("ID keluhan tidak valid.", exception.getMessage());
    }

    @Test
    void testUpdateKeluhan_withInvalidId_throwsException() {
        Keluhan k = new Keluhan(0, "Lampu Rusak", null, null, null);
        
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            keluhanService.updateKeluhan(k);
        });
        assertEquals("Data keluhan tidak valid untuk diupdate.", exception.getMessage());
    }

    @Test
    void testUpdateFasilitas_withInvalidId_throwsException() {
        Fasilitas f = new Fasilitas(0, "Proyektor", "Elektronik", "Kelas 101", com.mankelfas.enumeration.KondisiFasilitas.BERFUNGSI_BAIK);
        
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            keluhanService.updateFasilitas(f);
        });
        assertEquals("Data fasilitas tidak valid untuk diupdate.", exception.getMessage());
    }
}
