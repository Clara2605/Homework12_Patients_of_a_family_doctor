package com.ace.ucv.services;

import com.ace.ucv.model.Medication;
import com.ace.ucv.repositories.MedicationRepository;
import com.ace.ucv.services.interfaces.IMedicationService;

import java.util.List;

public class MedicationService implements IMedicationService {
    private MedicationRepository medicationRepository;

    /**
     * Constructor for MedicationService.
     * Initializes the service with a new instance of MedicationRepository.
     */
    public MedicationService() {
        this.medicationRepository = new MedicationRepository();
    }

    /**
     * Adds a new medication to the database.
     *
     * @param medication The Medication object to be added.
     */
    @Override
    public void addMedication(Medication medication) {
        medicationRepository.addMedication(medication);
    }

    /**
     * Edits an existing medication in the database.
     *
     * @param medication The Medication object to be updated.
     * @param editedName The new name for the medication.
     * @param editedCategory The new category for the medication.
     */
    @Override
    public void editMedication(Medication medication, String editedName, String editedCategory) {
        medicationRepository.editMedication(medication, editedName, editedCategory);
    }

    /**
     * Deletes a medication from the database.
     *
     * @param medication The Medication object to be deleted.
     */
    @Override
    public void deleteMedication(Medication medication) {
        medicationRepository.deleteMedication(medication);
    }

    /**
     * Retrieves all medications from the database.
     *
     * @return A list of Medication objects.
     */
    @Override
    public List<Medication> loadMedicationsFromDatabase() {
        return medicationRepository.loadMedicationsFromDatabase();
    }
}
