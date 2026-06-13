package com.mankelfas.repository;

import com.mankelfas.model.keluhan.Fasilitas;
import java.util.List;

public interface IFasilitasRepository {
    List<Fasilitas> getAllFasilitas();
    boolean addFasilitas(Fasilitas fasilitas);
    boolean updateFasilitas(Fasilitas fasilitas);
    boolean deleteFasilitas(int idFasilitas);
}
