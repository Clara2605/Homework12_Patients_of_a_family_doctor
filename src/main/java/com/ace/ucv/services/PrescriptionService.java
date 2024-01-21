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

    /**
     * Constructor for PrescriptionService.
     * Initializes the service with a new instance of PrescriptionRepository.
     */
    public PrescriptionService() {
        this.prescriptionRepository = new PrescriptionRepository();
    }

    /**
     * Constructor for PrescriptionService with a specific PrescriptionRepository.
     * Allows for dependency injection.
     *
     * @param prescriptionRepository The PrescriptionRepository to be used by this service.
     */
    public PrescriptionService(PrescriptionRepository prescriptionRepository) {
        this.prescriptionRepository = prescriptionRepository;
    }

    /**
     * Retrieves the ID of an item from its name in a specified table.
     *
     * @param tableName The name of the table.
     * @param itemName The name of the item.
     * @return The ID of the item.
     */
    @Override
    public int getIdFromName(String tableName, String itemName) {
        return prescriptionRepository.getIdFromName(tableName, itemName);
    }

    /**
     * Loads specific items from a table in the database.
     *
     * @param tableName The name of the table.
     * @param columnName The name of the column to retrieve items from.
     * @return A list of items.
     */
    @Override
    public List<String> loadItemsFromDatabase(String tableName, String columnName) {
        return prescriptionRepository.loadItemsFromDatabase(tableName, columnName);
    }

    /**
     * Loads all prescriptions from the database.
     *
     * @return An ObservableList of Prescriptions.
     */
    @Override
    public ObservableList<Prescription> loadPrescriptionsFromDatabase() {
        return prescriptionRepository.loadPrescriptionsFromDatabase();
    }

    /**
     * Edits an existing prescription in the database.
     *
     * @param id The ID of the prescription.
     * @param date The new date for the prescription.
     * @param patientId The ID of the patient.
     * @param diseaseId The ID of the disease.
     * @param medicationId The ID of the medication.
     * @return True if the update was successful, False otherwise.
     */
    @Override
    public boolean editPrescription(int id, String date, int patientId, int diseaseId, int medicationId) {
        return prescriptionRepository.editPrescription(id, date, patientId, diseaseId, medicationId);
    }

    /**
     * Deletes a prescription from the database.
     *
     * @param id The ID of the prescription to delete.
     * @return True if the deletion was successful, False otherwise.
     */
    @Override
    public boolean deletePrescription(int id) {
        return prescriptionRepository.deletePrescription(id);
    }

    /**
     * Saves a new prescription in the database.
     *
     * @param patient The patient associated with the prescription.
     * @param date The date of the prescription.
     * @param diseaseId The ID of the disease.
     * @param medicationId The ID of the medication.
     * @return True if the save was successful, False otherwise.
     */
    @Override
    public boolean savePrescription(Patient patient, String date, String diseaseId, String medicationId) {
        return prescriptionRepository.savePrescription(patient, date, diseaseId, medicationId);
    }

    public List<Prescription> getAllPrescriptions() {
        return prescriptionRepository.getAllPrescriptions();
    }


}
