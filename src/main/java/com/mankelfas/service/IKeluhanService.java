package com.mankelfas.service;

import com.mankelfas.model.keluhan.Fasilitas;
import com.mankelfas.model.keluhan.Keluhan;
import java.util.List;

public interface IKeluhanService {
    List<Keluhan> getAllKeluhan();
    void addKeluhan(Keluhan k);
    void updateKeluhan(Keluhan k);
    void deleteKeluhan(int idKeluhan);

    List<Fasilitas> getAllFasilitas();
    void addFasilitas(Fasilitas f);
    void updateFasilitas(Fasilitas f);
    void deleteFasilitas(int idFasilitas);
}
