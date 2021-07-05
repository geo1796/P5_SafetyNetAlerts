package com.safetynet.alerts.service;

import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.repository.MedicalRecordRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
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

    public void deleteMedicalRecord(final Long id) throws IllegalArgumentException {
        medicalRecordRepository.deleteById(id);
    }

}
