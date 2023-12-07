package com.ace.ucv.services;

import com.ace.ucv.model.Patient;
import com.ace.ucv.repositories.PatientRepository;
import com.ace.ucv.services.interfaces.IPatientService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.util.List;

@SuppressFBWarnings("EI_EXPOSE_REP2")
public class PatientService implements IPatientService {
    private PatientRepository patientRepository;

    public PatientService() {
        this.patientRepository = new PatientRepository();
    }
    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @Override
    public void addPatient(Patient patient) {
        patientRepository.addPatient(patient);
    }

    @Override
    public List<Patient> loadPatientsFromDatabase() {
        return patientRepository.loadPatientsFromDatabase();
    }

    @Override
    public void editPatient(Patient patient, String newName, int newAge, String newFieldOfWork) {
        patientRepository.editPatient(patient, newName, newAge, newFieldOfWork);
    }

    @Override
    public void deletePatient(Patient patient) {
        patientRepository.deletePatient(patient);
    }
}
