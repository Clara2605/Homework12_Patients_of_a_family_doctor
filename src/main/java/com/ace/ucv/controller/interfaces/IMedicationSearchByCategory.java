package com.ace.ucv.controller.interfaces;

import com.ace.ucv.model.Medication;
import javafx.collections.ObservableList;

public interface IMedicationSearchByCategory {
     ObservableList<Medication> getMedicationsByCategoryWithCount(String category);
}