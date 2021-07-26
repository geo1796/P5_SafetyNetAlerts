package com.safetynet.alerts.service;

import com.safetynet.alerts.dto.*;
import com.safetynet.alerts.mapper.*;
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
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PersonServiceTest {

    @Mock
    private PersonRepository personRepository;
    @Mock
    private FirestationRepository firestationRepository;
    @Mock
    private MedicalRecordRepository medicalRecordRepository;
    @Mock
    private PersonEmailMapper personEmailMapper;
    @Mock
    private ChildMapper childMapper;
    @Mock
    private HomeMemberMapper homeMemberMapper;
    @Mock
    private PersonForPersonInfoMapper personForPersonInfoMapper;
    @Mock
    private PersonForFloodAndFireMapper personForFloodAndFireMapper;

    @InjectMocks
    private PersonService personService;

    private static final Firestation f1 = new Firestation();
    private static final Firestation f2 = new Firestation();
    private static final Person p1 = new Person();
    private static final Person p2 = new Person();
    private static final ArrayList<Person> people = new ArrayList<>();
    private static final MedicalRecord m1 = new MedicalRecord();
    private static final MedicalRecord m2 = new MedicalRecord();
    private static final ChildDto childDto = new ChildDto();
    private static final PersonForPersonInfoDto personForPersonInfoDto1 = new PersonForPersonInfoDto();
    private static final PersonForPersonInfoDto personForPersonInfoDto2 = new PersonForPersonInfoDto();
    private static final PersonForFloodAndFireDto personForFloodAndFireDto1 = new PersonForFloodAndFireDto();
    private static final PersonForFloodAndFireDto personForFloodAndFireDto2 = new PersonForFloodAndFireDto();
    private static final ArrayList<Person> people1 = new ArrayList<>();
    private static final ArrayList<Person> people2 = new ArrayList<>();
    private static final ArrayList<Firestation> firestations1 = new ArrayList<>();
    private static final ArrayList<Firestation> firestations2 = new ArrayList<>();
    private static final ArrayList<PersonForFloodAndFireDto> peopleForFloodAndFireDto1 = new ArrayList<>();
    private static final ArrayList<PersonForFloodAndFireDto> peopleForFloodAndFireDto2 = new ArrayList<>();

    static void initPersonObjects(){
        p1.setFirstName("John");
        p1.setId(1L);
        p2.setFirstName("Son");
        p2.setId(2L);
        people.add(p1);
        people.add(p2);
        people1.add(p1);
        people2.add(p2);
    }

    static void initMedicalRecordObjects(){
        m1.setBirthdate("01/01/2000");
        m2.setBirthdate("01/01/2021");
    }

    static void initFirestationObjects(){
        f1.setStation(1);
        f1.setAddress("address1");
        f2.setStation(2);
        f2.setAddress("address2");

        firestations1.add(f1);
        firestations2.add(f2);
    }

    static void initDtos(){
        childDto.setFirstName(p1.getFirstName());
        childDto.setAge(0);

        personForPersonInfoDto1.setFirstName(p1.getFirstName());
        personForPersonInfoDto1.setAge(21);
        personForPersonInfoDto2.setFirstName(p2.getFirstName());
        personForPersonInfoDto2.setAge(0);

        personForFloodAndFireDto1.setAge(21);
        personForFloodAndFireDto1.setFirstName(p1.getFirstName());
        personForFloodAndFireDto2.setAge(0);
        personForFloodAndFireDto2.setFirstName(p2.getFirstName());

        peopleForFloodAndFireDto1.add(personForFloodAndFireDto1);
        peopleForFloodAndFireDto2.add(personForFloodAndFireDto2);
    }

    @BeforeAll
    static void init(){
        initPersonObjects();
        initMedicalRecordObjects();
        initFirestationObjects();
        initDtos();
    }

    @Test
    public void testGetDtoForFloodAndFire(){
        when(personForFloodAndFireMapper.toDto(p1, m1)).thenReturn(personForFloodAndFireDto1);
        when(personForFloodAndFireMapper.toDto(p2, m2)).thenReturn(personForFloodAndFireDto2);

        when(medicalRecordRepository.findById(1L)).thenReturn(Optional.of(m1));
        when(medicalRecordRepository.findById(2L)).thenReturn(Optional.of(m2));

        assertEquals(personForFloodAndFireDto1, personService.getDtoForFloodAndFire(p1));
        assertEquals(personForFloodAndFireDto2, personService.getDtoForFloodAndFire(p2));
    }

    @Test
    public void testGetDtoForPersonInfo(){
        when(medicalRecordRepository.findById(1L)).thenReturn(Optional.of(m1));
        when(medicalRecordRepository.findById(2L)).thenReturn(Optional.of(m2));
        when(personForPersonInfoMapper.toDto(p1, m1)).thenReturn(personForPersonInfoDto1);
        when(personForPersonInfoMapper.toDto(p2, m2)).thenReturn(personForPersonInfoDto2);

        assertEquals(personForPersonInfoDto1, personService.getDtoForPersonInfo(p1));
        assertEquals(personForPersonInfoDto2, personService.getDtoForPersonInfo(p2));
    }

    @Test
    public void testGetChildDto(){
        when(medicalRecordRepository.findById(1L)).thenReturn(Optional.of(m1));
        when(childMapper.toDto(any(Person.class))).thenReturn(childDto);

        assertNull(personService.getChildDto(p1));

        when(medicalRecordRepository.findById(2L)).thenReturn(Optional.of(m2));

        assertEquals(childDto, personService.getChildDto(p2));
    }

    @Test
    public void testGetPeople(){
        when(personRepository.findAllByFirstName(any(String.class))).thenReturn(people);
        when(personRepository.findAllByLastName(any(String.class))).thenReturn(people);

        assertEquals(people, personService.getPeople("", ""));
    }

    @Test
    public void testDeletePerson(){
        testGetPeople();

        assertThrows(IllegalArgumentException.class, () -> personService.deletePerson("", ""));

        ArrayList<Person> personArrayList = new ArrayList<>();
        personArrayList.add(p1);
        when(personRepository.findAllByLastName(any(String.class))).thenReturn(personArrayList);

        assertEquals(p1, personService.deletePerson("", ""));
    }

    @Test
    public void testGetCommunityEmail(){
        PersonEmailDto personEmailDto1 = new PersonEmailDto();
        personEmailDto1.setEmail("John@");
        PersonEmailDto personEmailDto2 = new PersonEmailDto();
        personEmailDto2.setEmail("Son@");

        ArrayList<PersonEmailDto> emails = new ArrayList<>();
        emails.add(personEmailDto1);
        emails.add(personEmailDto2);

        when(personRepository.findAllByCity(any(String.class))).thenReturn(people);
        when(personEmailMapper.toDto(p1)).thenReturn(personEmailDto1);
        when(personEmailMapper.toDto(p2)).thenReturn(personEmailDto2);

        assertEquals(emails, personService.getCommunityEmail(""));
    }

    @Test
    public void testGetChildAlert(){
        testGetChildDto();

        HomeMemberDto homeMemberDto = new HomeMemberDto();
        homeMemberDto.setFirstName(p1.getFirstName());
        ArrayList<HomeMemberDto> homeMembers = new ArrayList<>();
        homeMembers.add(homeMemberDto);
        childDto.setHomeMemberDtoList(homeMembers);
        ArrayList<ChildDto> expected = new ArrayList<>();
        expected.add(childDto);

        when(personRepository.findAllByAddress(any(String.class))).thenReturn(people);
        when(homeMemberMapper.toDto(p1)).thenReturn(homeMemberDto);

        assertEquals(expected, personService.getChildAlert(""));
    }

    @Test
    public void testGetPersonInfo(){
        testGetPeople();
        testGetDtoForPersonInfo();

        ArrayList<PersonForPersonInfoDto> expected = new ArrayList<>();
        expected.add(personForPersonInfoDto1);
        expected.add(personForPersonInfoDto2);
        assertEquals(expected, personService.getPersonInfo("",""));
    }

    @Test
    public void testGetFlood(){
        testGetDtoForFloodAndFire();

        when(personRepository.findAllByAddress(f1.getAddress())).thenReturn(people1);
        when(personRepository.findAllByAddress(f2.getAddress())).thenReturn(people2);
        when(firestationRepository.findAllByStation(1)).thenReturn(firestations1);
        when(firestationRepository.findAllByStation(2)).thenReturn(firestations2);

        FloodDto floodDto1 = new FloodDto();
        floodDto1.setAddress(f1.getAddress());
        floodDto1.setPersonForFloodAndFireDtoList(peopleForFloodAndFireDto1);
        FloodDto floodDto2 = new FloodDto();
        floodDto2.setAddress(f2.getAddress());
        floodDto2.setPersonForFloodAndFireDtoList(peopleForFloodAndFireDto2);
        ArrayList<FloodDto> expected = new ArrayList<>();
        expected.add(floodDto1);
        expected.add(floodDto2);

        ArrayList<FloodDto> result = (ArrayList<FloodDto>) personService.getFlood(new int[] {1, 2});

        for(int i = 0 ; i < 2 ; i++){
            assertEquals(expected.get(i).getAddress(), result.get(i).getAddress());
            assertEquals(expected.get(i).getPersonForFloodAndFireDtoList().get(0).getFirstName(),
                        result.get(i).getPersonForFloodAndFireDtoList().get(0).getFirstName());
        }

    }

    @Test
    public void testGetFire() {
        testGetDtoForFloodAndFire();

        when(personRepository.findAllByAddress(f1.getAddress())).thenReturn(people1);
        when(firestationRepository.findAllByAddress(f1.getAddress())).thenReturn(firestations1);

        FireAddressDto fireAddressDto1 = new FireAddressDto();
        fireAddressDto1.setStationNumber(f1.getStation());
        fireAddressDto1.setPersonForFloodAndFireDtoList(peopleForFloodAndFireDto1);

        ArrayList<FireAddressDto> expected = new ArrayList<>();
        expected.add(fireAddressDto1);

        ArrayList<FireAddressDto> result = (ArrayList<FireAddressDto>) personService.getFire("address1");

        assertEquals(expected.get(0).getStationNumber(), result.get(0).getStationNumber());
        assertEquals(expected.get(0).getPersonForFloodAndFireDtoList().get(0).getFirstName(),
                     result.get(0).getPersonForFloodAndFireDtoList().get(0).getFirstName());
    }
}
