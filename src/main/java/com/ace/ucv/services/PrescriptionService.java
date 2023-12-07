package com.ace.ucv.services;

import com.ace.ucv.model.Patient;
import com.ace.ucv.model.Prescription;
import com.ace.ucv.repositories.PrescriptionRepository;
import com.ace.ucv.services.interfaces.IPrescriptionService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import javafx.collections.ObservableList;

import java.util.List;

@SuppressFBWarnings("EI_EXPOSE_REP2")
public class PrescriptionService implements IPrescriptionService {
    private PrescriptionRepository prescriptionRepository;

    public PrescriptionService() {
        this.prescriptionRepository = new PrescriptionRepository();
    }
    public PrescriptionService(PrescriptionRepository prescriptionRepository) {
        this.prescriptionRepository = prescriptionRepository;
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

    @Override
    public boolean savePrescription(Patient patient, String date, String diseaseId, String medicationId) {
        return prescriptionRepository.savePrescription(patient, date, diseaseId, medicationId);
    }

}
