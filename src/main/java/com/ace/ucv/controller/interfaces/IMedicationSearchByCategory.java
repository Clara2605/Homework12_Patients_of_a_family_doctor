package com.ace.ucv.controller.interfaces;

import com.ace.ucv.model.Medication;
import com.ace.ucv.model.Patient;
import javafx.collections.ObservableList;
import javafx.util.Pair;

import java.util.List;

public interface IMedicationSearchByCategory {
     ObservableList<Medication> getMedicationsByCategoryWithCount(String category);
}