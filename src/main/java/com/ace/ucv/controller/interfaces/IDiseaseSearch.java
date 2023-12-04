package com.ace.ucv.controller.interfaces;

import com.ace.ucv.model.Disease;
import com.ace.ucv.model.Patient;
import javafx.collections.ObservableList;
import javafx.util.Pair;

import java.util.List;

public interface IDiseaseSearch {
     Pair<ObservableList<Patient>, Integer> performSearch(String diseaseName);
     List<Patient> getPatientsWithDisease(String diseaseName);
}