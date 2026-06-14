package com.mankelfas.model.misc;

/**
 * Bertugas menyebarkan pesan pemberitahuan otomatis ke dalam sistem.
 * Memberikan peringatan segera apabila terdapat kejadian atau pembaruan status yang penting.
 */
public class NotifikasiSistem implements INotifikasi {

    /**
     * Melakukan pelepasan informasi pemberitahuan khusus yang ditandai dengan awalan label [NOTIF SISTEM].
     * 
     * @param pesan Kalimat berita atau pengingat yang akan dipancarkan
     */
    @Override
    public void kirimNotif(String pesan) {
        try {
            System.out.println("[NOTIF SISTEM] " + pesan);
        } catch (Exception e) {
            // Memberikan rekam log apabila pemancaran pemberitahuan tersebut terhambat
            System.err.println("Gagal mengirim notif sistem: " + e.getMessage());
        }
    }
}
