package com.mankelfas.enumeration;

/**
 * Enumerasi yang merepresentasikan kondisi dari suatu fasilitas.
 */
public enum KondisiFasilitas {
    BERFUNGSI_BAIK("Berfungsi Baik"),
    RUSAK_RINGAN("Rusak Ringan"),
    RUSAK_PARAH("Rusak Parah (Perlu Ganti)"),
    RUSAK_RINGAN_SEDANG_DIPERIKSA("Rusak Ringan (Sedang Diperiksa)"),
    RUSAK_PARAH_SEDANG_DIPERIKSA("Rusak Parah (Sedang Diperiksa)"),
    RUSAK_RINGAN_DALAM_PERBAIKAN("Rusak Ringan (Dalam Perbaikan)"),
    RUSAK_PARAH_DALAM_PERBAIKAN("Rusak Parah (Dalam Perbaikan)");

    private final String deskripsi;

    KondisiFasilitas(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    @Override
    public String toString() {
        return deskripsi;
    }

    public KondisiFasilitas toSedangDiperiksa() {
        if (this == RUSAK_RINGAN) return RUSAK_RINGAN_SEDANG_DIPERIKSA;
        if (this == RUSAK_PARAH) return RUSAK_PARAH_SEDANG_DIPERIKSA;
        return this;
    }

    public KondisiFasilitas toDalamPerbaikan() {
        if (this == RUSAK_RINGAN || this == RUSAK_RINGAN_SEDANG_DIPERIKSA) return RUSAK_RINGAN_DALAM_PERBAIKAN;
        if (this == RUSAK_PARAH || this == RUSAK_PARAH_SEDANG_DIPERIKSA) return RUSAK_PARAH_DALAM_PERBAIKAN;
        return this;
    }

    public static KondisiFasilitas fromDeskripsi(String deskripsi) {
        for (KondisiFasilitas k : values()) {
            if (k.getDeskripsi().equalsIgnoreCase(deskripsi)) {
                return k;
            }
        }
        return null;
    }
}
