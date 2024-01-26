package com.ace.ucv.controller.interfaces;

import com.ace.ucv.model.Patient;
import javafx.collections.ObservableList;

public interface IPrescriptionSearch {
     ObservableList<Patient> getPatientsWithPrescriptionCount(int minPrescriptions);
}