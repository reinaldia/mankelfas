package com.mankelfas.model.keluhan;

import com.mankelfas.enumeration.Prioritas;
import com.mankelfas.enumeration.StatusKeluhan;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class KeluhanTest {

    @Test
    void testUpdateStatus_changesStatusAndAddsHistory() {
        Keluhan k = new Keluhan(1, "AC Netes", null, null, null);
        
        // Memastikan status awal
        assertEquals(StatusKeluhan.DILAPORKAN, k.getStatus());
        
        // Melakukan perubahan status
        k.updateStatus(StatusKeluhan.DIPROSES);
        
        // Verifikasi bahwa status berubah dan riwayat bertambah (1 riwayat baru)
        assertEquals(StatusKeluhan.DIPROSES, k.getStatus());
        assertEquals(1, k.getRiwayat().size());
        assertTrue(k.getRiwayat().get(0).getPesan().contains("mengubah status keluhan"));
    }

    @Test
    void testUpdatePrioritas_changesPrioritasAndAddsHistory() {
        Keluhan k = new Keluhan(2, "Listrik Padam", null, null, null);
        
        // Prioritas awal selalu RENDAH
        assertEquals(Prioritas.RENDAH, k.getPrioritas());
        
        // Mengubah prioritas menjadi TINGGI
        k.updatePrioritas(Prioritas.TINGGI);
        
        // Verifikasi
        assertEquals(Prioritas.TINGGI, k.getPrioritas());
        assertFalse(k.getRiwayat().isEmpty());
        assertTrue(k.getRiwayat().get(0).getPesan().contains("mengubah prioritas keluhan"));
    }

    @Test
    void testArsipkan_setsArchivedFlagToTrue() {
        Keluhan k = new Keluhan(3, "Keran Bocor", null, null, null);
        
        assertFalse(k.isArchived());
        
        k.arsipkan();
        
        assertTrue(k.isArchived());
        assertFalse(k.getRiwayat().isEmpty());
    }

    @Test
    void testBatalArsip_setsArchivedFlagToFalse() {
        Keluhan k = new Keluhan(4, "Pintu Rusak", null, null, null);
        k.arsipkan();
        assertTrue(k.isArchived());
        
        k.batalArsip();
        
        assertFalse(k.isArchived());
        // Riwayat harus bertambah menjadi 2 (1 arsip, 1 batal arsip)
        assertEquals(2, k.getRiwayat().size());
    }
}
