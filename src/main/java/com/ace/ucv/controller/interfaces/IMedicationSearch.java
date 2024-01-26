package com.ace.ucv.controller.interfaces;

import com.ace.ucv.model.Patient;
import javafx.collections.ObservableList;
import javafx.util.Pair;

import java.util.List;

public interface IMedicationSearch {
     Pair<ObservableList<Patient>, Integer> performSearch(String medicationName);
     List<Patient> getPatientsWithMedication(String medicationName);
}