package com.ace.ucv.services;

import com.ace.ucv.db.DatabaseManager;
import com.ace.ucv.model.Disease;
import com.ace.ucv.repositories.DiseaseRepository;
import com.ace.ucv.services.interfaces.IDiseaseService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DiseaseService implements IDiseaseService {
    private DiseaseRepository diseaseRepository;

    public DiseaseService() {
        this.diseaseRepository = new DiseaseRepository();
    }

    @Override
    public void addDisease(String name) {
        diseaseRepository.addDisease(name);
    }

    @Override
    public List<Disease> loadDiseasesFromDatabase() {
        return diseaseRepository.loadDiseasesFromDatabase();
    }

    @Override
    public void editDisease(Disease disease, String editedName) {
        diseaseRepository.editDisease(disease, editedName);
    }

    @Override
    public void deleteDisease(Disease disease) {
        diseaseRepository.deleteDisease(disease);
    }
}
