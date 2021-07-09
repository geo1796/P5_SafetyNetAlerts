package com.safetynet.alerts.service;

import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.repository.MedicalRecordRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Data
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

    public void deleteMedicalRecord(final String lastName, final String firstName) throws IllegalArgumentException {
        List<MedicalRecord> medicalRecordListByLastName = medicalRecordRepository.findAllByLastName(lastName);
        List<MedicalRecord> medicalRecordListByFirstName = medicalRecordRepository.findAllByFirstName(firstName);
        MedicalRecord medicalRecordToDelete = null;

        for(MedicalRecord medicalRecordByLastName : medicalRecordListByLastName){
            if(medicalRecordListByFirstName.contains(medicalRecordByLastName))
                medicalRecordToDelete = medicalRecordByLastName;
        }
        if(medicalRecordToDelete == null)
            throw new EmptyResultDataAccessException(0);
        else
            medicalRecordRepository.deleteById(medicalRecordToDelete.getId());
    }
}
