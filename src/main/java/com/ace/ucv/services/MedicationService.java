package com.ace.ucv.services;

import com.ace.ucv.model.Medication;
import com.ace.ucv.repositories.MedicationRepository;
import com.ace.ucv.services.interfaces.IMedicationService;

import java.util.List;

public class MedicationService implements IMedicationService {
    private MedicationRepository medicationRepository;

    public MedicationService() {
        this.medicationRepository = new MedicationRepository();
    }

    @Override
    public void addMedication(Medication medication) {
        medicationRepository.addMedication(medication);
    }

    @Override
    public void editMedication(Medication medication, String editedName, String editedCategory) {
        medicationRepository.editMedication(medication, editedName, editedCategory);
    }

    @Override
    public void deleteMedication(Medication medication) {
        medicationRepository.deleteMedication(medication);
    }

    @Override
    public List<Medication> loadMedicationsFromDatabase() {
        return medicationRepository.loadMedicationsFromDatabase();
    }
}
