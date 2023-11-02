package com.ace.ucv.services.interfaces;

import com.ace.ucv.model.Disease;

import java.util.List;

public interface IDiseaseService {
    void insertIntoDatabase(Disease disease);
    void addDisease(String name);
    void editDisease(Disease disease, String editedName);
    void deleteDisease(Disease disease);
    List<Disease> loadDiseasesFromDatabase();
}