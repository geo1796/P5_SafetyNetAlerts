package com.safetynet.alerts.service;

import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.repository.MedicalRecordRepository;
import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * the service corresponding to the entity MedicalRecord
 */

@AllArgsConstructor
@Service
public class MedicalRecordService {

    private MedicalRecordRepository medicalRecordRepository;

    /**
     *
     * @param medicalRecord the MedicalRecord object you want to save
     * @return the medicalRecord object that has been saved
     */

    public MedicalRecord saveMedicalRecord(MedicalRecord medicalRecord) { return medicalRecordRepository.save(medicalRecord); }

    /**
     *
     * @param medicalRecords the List of MedicalRecord objects you want to save
     * @return the List of MedicalRecord objects that has been saved
     */
    public List<MedicalRecord> saveMedicalRecords(List<MedicalRecord> medicalRecords) { return (List<MedicalRecord>) medicalRecordRepository.saveAll(medicalRecords); }

    /**
     *
     * @param id the id of the MedicalRecord object you want to get
     * @return an Optional object containing the MedicalRecord corresponding to this id if there is one
     */

    public Optional<MedicalRecord> getMedicalRecord(final long id) { return medicalRecordRepository.findById(id); }

    /**
     *
     * @return an Iterable (possibly empty) of all MedicalRecord objects
     */

    public Iterable<MedicalRecord> getMedicalRecords() {
        return medicalRecordRepository.findAll();
    }

    /**
     *
     * @param lastName the last name of the MedicalRecord objects you want to get
     * @param firstName the first name of the MedicalRecord objects you want to get
     * @return an Iterable (possibly empty) of all MedicalRecord objects for these parameters
     */
    public Iterable<MedicalRecord> getMedicalRecords(final String lastName, final String firstName){
        List<MedicalRecord> medicalRecordListByFirstName = medicalRecordRepository.findAllByFirstName(firstName);
        List<MedicalRecord> medicalRecordListByLastName = medicalRecordRepository.findAllByLastName(lastName);

        ArrayList<MedicalRecord> result = new ArrayList<>();
        for(MedicalRecord medicalRecord : medicalRecordListByLastName){
            if(medicalRecordListByFirstName.contains(medicalRecord)) //if medicalRecord belongs to both list
                result.add(medicalRecord);                           // then and only then we must add it to the result
        }
        return result;
    }

    /**
     *
     * @param lastName the last name of the MedicalRecord object you want to delete
     * @param firstName the first name of the MedicalRecord object you want to delete
     * @return the deleted MedicalRecord
     * @throws IllegalArgumentException if there are many MedicalRecord objects for these param
     */

    public MedicalRecord deleteMedicalRecord(final String lastName, final String firstName) throws IllegalArgumentException {
        MedicalRecord medicalRecordToDelete = null;
        Iterable<MedicalRecord> medicalRecordIterable = getMedicalRecords(lastName, firstName);

        for(MedicalRecord medicalRecord : medicalRecordIterable){
            if (medicalRecordToDelete == null)
                medicalRecordToDelete = medicalRecord;
            else
                throw new IllegalArgumentException("plusieurs personnes portent ce nom"); //we don't want to delete the wrong object by mistake
        }

        if(medicalRecordToDelete == null)
            throw new EmptyResultDataAccessException(0); //checking if there is something to delete for this id before trying to delete it
        else {
            medicalRecordRepository.deleteById(medicalRecordToDelete.getId());
            return medicalRecordToDelete;
        }
    }
}
