package com.ace.ucv.services.interfaces;

import com.ace.ucv.model.Medication;

import java.util.List;

public interface IMedicationService {
    void addMedication(Medication medication);
    void editMedication(Medication medication, String editedName, String editedCategory);
    void deleteMedication(Medication medication);
    List<Medication> loadMedicationsFromDatabase();
}