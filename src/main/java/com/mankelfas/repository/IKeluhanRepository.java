package com.mankelfas.repository;

import com.mankelfas.model.keluhan.Keluhan;
import java.util.List;

public interface IKeluhanRepository {
    List<Keluhan> getAllKeluhan();
    boolean addKeluhan(Keluhan keluhan);
    boolean updateKeluhan(Keluhan keluhan);
    boolean deleteKeluhan(int idKeluhan);
}
