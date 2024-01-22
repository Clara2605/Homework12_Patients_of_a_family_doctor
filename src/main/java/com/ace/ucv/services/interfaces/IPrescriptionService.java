package com.ace.ucv.services.interfaces;

import com.ace.ucv.model.Patient;
import com.ace.ucv.model.Prescription;
import javafx.collections.ObservableList;

import java.util.List;

public interface IPrescriptionService {
    int getIdFromName(String tableName, String itemName);
    List<String> loadItemsFromDatabase(String tableName, String columnName);
    ObservableList<Prescription> loadPrescriptionsFromDatabase();
    boolean editPrescription(int id, String date, int patientId, int diseaseId, int medicationId);
    boolean deletePrescription(int id);
    boolean savePrescription(Patient patient, String date, String diseaseId, String medicationId);
}