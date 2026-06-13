package com.mankelfas.model.misc;

public class NotifikasiSistem implements INotifikasi {

    @Override
    public void kirimNotif(String pesan) {
        try {
            System.out.println("[NOTIF SISTEM] " + pesan);
        } catch (Exception e) {
            System.err.println("Gagal mengirim notif sistem: " + e.getMessage());
        }
    }
}
