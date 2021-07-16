package com.safetynet.alerts.service;

import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.repository.MedicalRecordRepository;
import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class MedicalRecordService {

    private MedicalRecordRepository medicalRecordRepository;

    public MedicalRecord saveMedicalRecord(MedicalRecord medicalRecord) { return medicalRecordRepository.save(medicalRecord); }

    public List<MedicalRecord> saveMedicalRecords(List<MedicalRecord> medicalRecords) { return (List<MedicalRecord>) medicalRecordRepository.saveAll(medicalRecords); }

    public Optional<MedicalRecord> getMedicalRecord(final long id) { return medicalRecordRepository.findById(id); }

    public Iterable<MedicalRecord> getMedicalRecords() {
        return medicalRecordRepository.findAll();
    }

    public Iterable<MedicalRecord> getMedicalRecords(final String lastName, final String firstName){
        List<MedicalRecord> medicalRecordListByFirstName = medicalRecordRepository.findAllByFirstName(firstName);
        List<MedicalRecord> medicalRecordListByLastName = medicalRecordRepository.findAllByLastName(lastName);

        ArrayList<MedicalRecord> result = new ArrayList<>();
        for(MedicalRecord medicalRecord : medicalRecordListByLastName){
            if(medicalRecordListByFirstName.contains(medicalRecord))
                result.add(medicalRecord);
        }
        return result;
    }

    public MedicalRecord deleteMedicalRecord(final String lastName, final String firstName) throws IllegalArgumentException {
        MedicalRecord medicalRecordToDelete = null;
        Iterable<MedicalRecord> medicalRecordIterable = getMedicalRecords(lastName, firstName);

        for(MedicalRecord medicalRecord : medicalRecordIterable){
            if (medicalRecordToDelete == null)
                medicalRecordToDelete = medicalRecord;
            else
                throw new IllegalArgumentException("plusieurs personnes portent ce nom");
        }

        if(medicalRecordToDelete == null)
            throw new EmptyResultDataAccessException(0);
        else {
            medicalRecordRepository.deleteById(medicalRecordToDelete.getId());
            return medicalRecordToDelete;
        }
    }
}
