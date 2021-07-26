package com.safetynet.alerts.service;

import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.repository.MedicalRecordRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MedicalRecordServiceTest {


    @Mock
    private MedicalRecordRepository medicalRecordRepository;

    @InjectMocks
    MedicalRecordService medicalRecordService;

    private static final MedicalRecord m1 = new MedicalRecord();
    private static final MedicalRecord m2 = new MedicalRecord();
    private static final ArrayList<MedicalRecord> medicalRecords = new ArrayList<>();

    @BeforeAll
    static void init(){
        medicalRecords.add(m1);
        medicalRecords.add(m2);
    }

    @Test
    public void testGetMedicalRecords(){
        when(medicalRecordRepository.findAllByFirstName(any(String.class))).thenReturn(medicalRecords);
        when(medicalRecordRepository.findAllByLastName(any(String.class))).thenReturn(medicalRecords);

        assertEquals(medicalRecords, medicalRecordService.getMedicalRecords("", ""));
    }

    @Test
    public void testDeletePerson(){
        testGetMedicalRecords();

        assertThrows(IllegalArgumentException.class, () -> medicalRecordService.deleteMedicalRecord("", ""));

        ArrayList<MedicalRecord> medicalRecordArrayList = new ArrayList<>();
        medicalRecordArrayList.add(m1);
        when(medicalRecordRepository.findAllByLastName(any(String.class))).thenReturn(medicalRecordArrayList);

        assertEquals(m1, medicalRecordService.deleteMedicalRecord("", ""));
    }

}
