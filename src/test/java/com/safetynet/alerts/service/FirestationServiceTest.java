package com.safetynet.alerts.service;

import com.safetynet.alerts.dto.PeopleCoveredByThisFirestationDto;
import com.safetynet.alerts.dto.PersonForFirestationDto;
import com.safetynet.alerts.dto.PersonPhoneDto;
import com.safetynet.alerts.mapper.PersonForFirestationMapper;
import com.safetynet.alerts.mapper.PhoneAlertMapper;
import com.safetynet.alerts.model.Firestation;
import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.repository.FirestationRepository;
import com.safetynet.alerts.repository.MedicalRecordRepository;
import com.safetynet.alerts.repository.PersonRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FirestationServiceTest {

    @Mock
    private FirestationRepository firestationRepository;
    @Mock
    private MedicalRecordRepository medicalRecordRepository;
    @Mock
    private PersonRepository personRepository;
    @Mock
    private PersonForFirestationMapper personForFirestationMapper;
    @Mock
    private PhoneAlertMapper phoneAlertMapper;

    @InjectMocks
    private FirestationService firestationService;

    private static Firestation f1 = new Firestation();
    private static Firestation f2 = new Firestation();
    private static MedicalRecord m1 = new MedicalRecord();
    private static MedicalRecord m2 = new MedicalRecord();
    private static Person p1 = new Person();
    private static Person p2 = new Person();
    private static ArrayList<Firestation> firestations = new ArrayList<>();
    private static ArrayList<Person> people1 = new ArrayList<>();
    private static ArrayList<Person> people2 = new ArrayList<>();


    @BeforeAll
    static void init(){
        f1.setStation(7);
        f1.setAddress("address1");
        f2.setStation(7);
        f2.setAddress("address2");

        firestations.add(f1);
        firestations.add(f2);

        p1.setFirstName("John");
        p1.setLastName("Doe");
        p2.setFirstName("Son");
        p2.setLastName("Goku");
        p1.setId(1L);
        p2.setId(2L);
        p1.setPhone("0101010101");
        p2.setPhone("0202020202");
        m1.setBirthdate(("01/01/2021"));
        m2.setBirthdate("01/01/1980");

        people1.add(p1);
        people2.add(p2);
    }

    @Test
    public void testSaveFirestation() {
        when(firestationRepository.save(f1)).thenReturn(f1);

        Firestation created = firestationService.saveFirestation(f1);
        assertEquals(f1.getAddress(), created.getAddress());
    }

    @Test
    public void testGetFirestation() {
        when(firestationRepository.findById(any(Long.class))).thenReturn(Optional.of(f1));

        Optional<Firestation> optionalFirestation = firestationService.getFirestation(1L);
        assertTrue(optionalFirestation.isPresent());
        assertEquals("address1", optionalFirestation.get().getAddress());
    }

    @Test
    public void testGetFirestations() {
        when(firestationRepository.findAll()).thenReturn(firestations);
        assertThat(firestationService.getFirestations()).contains(f1, f2);

    }

    @Test
    public void testDeleteFirestation() {
        Mockito.doNothing().when(firestationRepository).deleteById(any(Long.class));
        firestationService.deleteFirestation(1L);
        verify(firestationRepository).deleteById(1L);
    }

    @Test
    public void testGetPersonByFirestation() {
        PersonForFirestationDto personForFirestationDto1 = new PersonForFirestationDto();
        PersonForFirestationDto personForFirestationDto2 = new PersonForFirestationDto();
        personForFirestationDto1.setFirstName(p1.getFirstName());
        personForFirestationDto1.setLastName(p2.getLastName());
        personForFirestationDto2.setFirstName(p2.getFirstName());
        personForFirestationDto2.setLastName(p2.getLastName());

        ArrayList<PersonForFirestationDto> personForFirestationDtoArrayList = new ArrayList<>();
        personForFirestationDtoArrayList.add(personForFirestationDto1);
        personForFirestationDtoArrayList.add(personForFirestationDto2);

        when(firestationRepository.findAllByStation(any(Integer.class))).thenReturn(firestations);
        when(personRepository.findAllByAddress(f1.getAddress())).thenReturn(people1);
        when(personRepository.findAllByAddress(f2.getAddress())).thenReturn(people2);
        when(medicalRecordRepository.findById(1L)).thenReturn(Optional.of(m1));
        when(medicalRecordRepository.findById(2L)).thenReturn(Optional.of(m2));
        when(personForFirestationMapper.toDto(p1)).thenReturn(personForFirestationDto1);
        when(personForFirestationMapper.toDto(p2)).thenReturn(personForFirestationDto2);

        PeopleCoveredByThisFirestationDto expected = new PeopleCoveredByThisFirestationDto(1, 1, personForFirestationDtoArrayList);
        PeopleCoveredByThisFirestationDto result = firestationService.getPeopleByFirestation(0);
        assertEquals(expected.getNumberOfAdults(), result.getNumberOfAdults());
        assertEquals(expected.getNumberOfChildren(), result.getNumberOfChildren());
        assertEquals(expected.getPersonCoveredByThisStation(), result.getPersonCoveredByThisStation());
    }

    @Test
    public void testGetPhoneAlert(){
        PersonPhoneDto personPhoneDto1 = new PersonPhoneDto();
        PersonPhoneDto personPhoneDto2 = new PersonPhoneDto();
        personPhoneDto1.setPhone(p1.getPhone());
        personPhoneDto2.setPhone(p2.getPhone());

        ArrayList<PersonPhoneDto> expected = new ArrayList<>();
        expected.add(personPhoneDto1);
        expected.add(personPhoneDto2);

        when(firestationRepository.findAllByStation(any(Integer.class))).thenReturn(firestations);
        when(personRepository.findAllByAddress(f1.getAddress())).thenReturn(people1);
        when(personRepository.findAllByAddress(f2.getAddress())).thenReturn(people2);
        when(phoneAlertMapper.toDto(p1)).thenReturn(personPhoneDto1);
        when(phoneAlertMapper.toDto(p2)).thenReturn(personPhoneDto2);

        assertEquals(expected, firestationService.getPhoneAlert(1));
    }
}
