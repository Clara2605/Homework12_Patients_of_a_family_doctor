package com.ace.ucv.services.interfaces;
import com.ace.ucv.model.Patient;
import java.util.List;

public interface IPatientService {
    void addPatient(Patient patient);
    void editPatient(Patient patient, String newName, int newAge, String newFieldOfWork);
    void deletePatient(Patient patient);
    List<Patient> loadPatientsFromDatabase();
}