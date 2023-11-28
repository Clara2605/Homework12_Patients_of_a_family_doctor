package com.ace.ucv.services;

import com.ace.ucv.model.Prescription;
import com.ace.ucv.repositories.PrescriptionRepository;
import com.ace.ucv.services.interfaces.IPrescriptionService;
import javafx.collections.ObservableList;

import java.util.List;

public class PrescriptionService implements IPrescriptionService {
    private PrescriptionRepository prescriptionRepository;

    public PrescriptionService() {
        this.prescriptionRepository = new PrescriptionRepository();
    }

    @Override
    public int getIdFromName(String tableName, String itemName) {
        return prescriptionRepository.getIdFromName(tableName, itemName);
    }

    @Override
    public List<String> loadItemsFromDatabase(String tableName, String columnName) {
        return prescriptionRepository.loadItemsFromDatabase(tableName, columnName);
    }

    @Override
    public ObservableList<Prescription> loadPrescriptionsFromDatabase() {
        return prescriptionRepository.loadPrescriptionsFromDatabase();
    }

    @Override
    public boolean editPrescription(int id, String date, int patientId, int diseaseId, int medicationId) {
        return prescriptionRepository.editPrescription(id, date, patientId, diseaseId, medicationId);
    }

    @Override
    public boolean deletePrescription(int id) {
        return prescriptionRepository.deletePrescription(id);
    }
}
