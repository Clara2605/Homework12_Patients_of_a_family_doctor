package com.ace.ucv.services;

import com.ace.ucv.model.Patient;
import com.ace.ucv.repositories.PatientRepository;
import com.ace.ucv.services.interfaces.IPatientService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.util.List;

@SuppressFBWarnings("EI_EXPOSE_REP2")
public class PatientService implements IPatientService {
    private PatientRepository patientRepository;

    /**
     * Constructor for PatientService.
     * Initializes the service with a new instance of PatientRepository.
     */
    public PatientService() {
        this.patientRepository = new PatientRepository();
    }

    /**
     * Constructor for PatientService with a specific PatientRepository.
     * Allows for dependency injection.
     *
     * @param patientRepository The PatientRepository to be used by this service.
     */
    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    /**
     * Adds a new patient to the database.
     *
     * @param patient The Patient object to be added.
     */
    @Override
    public void addPatient(Patient patient) {
        patientRepository.addPatient(patient);
    }

    /**
     * Retrieves all patients from the database.
     *
     * @return A list of Patient objects.
     */
    @Override
    public List<Patient> loadPatientsFromDatabase() {
        return patientRepository.loadPatientsFromDatabase();
    }

    /**
     * Edits an existing patient in the database.
     *
     * @param patient The Patient object to be updated.
     * @param newName The new name for the patient.
     * @param newAge The new age for the patient.
     * @param newFieldOfWork The new field of work for the patient.
     */
    @Override
    public void editPatient(Patient patient, String newName, int newAge, String newFieldOfWork) {
        patientRepository.editPatient(patient, newName, newAge, newFieldOfWork);
    }

    /**
     * Deletes a patient from the database.
     *
     * @param patient The Patient object to be deleted.
     */
    @Override
    public void deletePatient(Patient patient) {
        patientRepository.deletePatient(patient);
    }
}
