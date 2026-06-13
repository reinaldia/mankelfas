package com.mankelfas.model.misc;

import java.util.Date;
import com.mankelfas.model.user.User;

public class Komentar {
    private String isiKomentar;
    private Date tanggal;
    private User pengirim;

    private String fotoBukti;

    public Komentar(String isiKomentar, User pengirim) {
        this.isiKomentar = isiKomentar;
        this.pengirim = pengirim;
        this.tanggal = new Date();
    }

    public Komentar(String isiKomentar, User pengirim, String fotoBukti) {
        this.isiKomentar = isiKomentar;
        this.pengirim = pengirim;
        this.tanggal = new Date();
        this.fotoBukti = fotoBukti;
    }

    public String getKomentar() {
        return "[" + tanggal + "] " + pengirim.getNama() + ": " + isiKomentar;
    }

    public String getIsiKomentar() { return isiKomentar; }
    public Date getTanggal() { return tanggal; }
    public User getPengirim() { return pengirim; }
    public String getFotoBukti() { return fotoBukti; }
    public void setFotoBukti(String fotoBukti) { this.fotoBukti = fotoBukti; }
}
