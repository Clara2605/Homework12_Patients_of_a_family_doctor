package com.ace.ucv.services;

import com.ace.ucv.model.Disease;
import com.ace.ucv.repositories.DiseaseRepository;
import com.ace.ucv.services.interfaces.IDiseaseService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.util.List;

@SuppressFBWarnings("EI_EXPOSE_REP2")
public class DiseaseService implements IDiseaseService {
    private DiseaseRepository diseaseRepository;

    /**
     * Constructor for DiseaseService.
     * Initializes the service with a new instance of DiseaseRepository.
     */
    public DiseaseService() {
        this.diseaseRepository = new DiseaseRepository();
    }

    /**
     * Constructor for DiseaseService with a specific DiseaseRepository.
     * Allows for dependency injection.
     *
     * @param diseaseRepository The DiseaseRepository to be used by this service.
     */
    public DiseaseService(DiseaseRepository diseaseRepository) {
        this.diseaseRepository = diseaseRepository;
    }

    /**
     * Adds a new disease to the database.
     *
     * @param name The name of the disease to be added.
     */
    @Override
    public void addDisease(String name) {
        diseaseRepository.addDisease(name);
    }

    /**
     * Retrieves all diseases from the database.
     *
     * @return A list of Disease objects.
     */
    @Override
    public List<Disease> loadDiseasesFromDatabase() {
        return diseaseRepository.loadDiseasesFromDatabase();
    }

    /**
     * Edits an existing disease in the database.
     *
     * @param disease The Disease object to be updated.
     * @param editedName The new name for the disease.
     */
    @Override
    public void editDisease(Disease disease, String editedName) {
        diseaseRepository.editDisease(disease, editedName);
    }

    /**
     * Deletes a disease from the database.
     *
     * @param disease The Disease object to be deleted.
     */
    @Override
    public void deleteDisease(Disease disease) {
        diseaseRepository.deleteDisease(disease);
    }
}
