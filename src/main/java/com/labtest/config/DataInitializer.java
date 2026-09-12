package com.labtest.config;

import com.labtest.entity.*;
import com.labtest.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LabTestRepository labTestRepository;

    @Autowired
    private LaboratoryRepository laboratoryRepository;

    @Autowired
    private TimeSlotRepository timeSlotRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${app.seed.default-users:true}")
    private boolean seedDefaultUsers;


    @Override
    public void run(String... args) throws Exception {
        log.info("Starting database initialization...");

        if (userRepository.count() == 0 && seedDefaultUsers) {
            createUsers();
        }

        if (labTestRepository.count() == 0) {
            createLabTests();
        }

        if (laboratoryRepository.count() == 0) {
            createLaboratories();
        }

        if (timeSlotRepository.count() == 0) {
            createTimeSlots();
        }

        log.info("Database initialization completed!");
    }

    private void createUsers() {

        User patient = new User();
        patient.setUsername("patient1");
        patient.setPassword(passwordEncoder.encode("password123"));
        patient.setRole(UserRole.PATIENT);
        userRepository.save(patient);

        User technician = new User();
        technician.setUsername("tech1");
        technician.setPassword(passwordEncoder.encode("password123"));
        technician.setRole(UserRole.LAB_TECHNICIAN);
        userRepository.save(technician);

        User admin = new User();
        admin.setUsername("admin1");
        admin.setPassword(passwordEncoder.encode("password123"));
        admin.setRole(UserRole.ADMIN);
        userRepository.save(admin);

        log.info("Created default users");
    }
    private void createLabTests() {

        LabTest bloodTest = new LabTest();
        bloodTest.setName("Blood Test");
        bloodTest.setDescription("Complete blood count test");
        bloodTest.setAverageProcessingMinutes(5);
        labTestRepository.save(bloodTest);

        LabTest xray = new LabTest();
        xray.setName("X-Ray");
        xray.setDescription("Digital X-Ray examination");
        xray.setAverageProcessingMinutes(10);
        labTestRepository.save(xray);

        LabTest mri = new LabTest();
        mri.setName("MRI Scan");
        mri.setDescription("Magnetic Resonance Imaging");
        mri.setAverageProcessingMinutes(15);
        labTestRepository.save(mri);

        LabTest ct = new LabTest();
        ct.setName("CT Scan");
        ct.setDescription("Computed Tomography Scan");
        ct.setAverageProcessingMinutes(15);
        labTestRepository.save(ct);

        log.info("Created default lab tests");
    }
    private void createLaboratories() {

        Laboratory laboratory = new Laboratory();

        laboratory.setName("City Diagnostic Lab");
        laboratory.setAddress("123 Main Street");
        laboratory.setCity("Indore");
        laboratory.setState("Madhya Pradesh");
        laboratory.setPincode("452001");
        laboratory.setPhone("9876543210");
        laboratory.setEmail("citylab@example.com");
        laboratory.setDescription("Full-service diagnostic laboratory");
        laboratory.setActive(true);

        laboratoryRepository.save(laboratory);

        log.info("Created laboratory: City Diagnostic Lab");
    }

    private void createTimeSlots() {

        Laboratory laboratory = laboratoryRepository.findAll()
                .stream()
                .findFirst()
                .orElseThrow(() ->
                        new RuntimeException("No laboratory found"));

        LocalDateTime now = LocalDateTime.now();

        LocalDateTime tomorrow = now.plusDays(1)
                .withHour(9)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);

        for (int day = 0; day < 7; day++) {

            LocalDateTime slotDate = tomorrow.plusDays(day);

            // Morning slots: 9 AM - 11 AM
            for (int hour = 9; hour < 12; hour++) {

                TimeSlot slot = new TimeSlot();

                slot.setLaboratory(laboratory);
                slot.setSlotTime(slotDate.withHour(hour));
                slot.setTotalCapacity(10);
                slot.setRemainingCapacity(10);

                timeSlotRepository.save(slot);
            }

            // Afternoon slots: 2 PM - 4 PM
            for (int hour = 14; hour < 17; hour++) {

                TimeSlot slot = new TimeSlot();

                slot.setLaboratory(laboratory);
                slot.setSlotTime(slotDate.withHour(hour));
                slot.setTotalCapacity(10);
                slot.setRemainingCapacity(10);

                timeSlotRepository.save(slot);
            }
        }

        log.info("Created time slots for next 7 days (9 AM - 5 PM, excluding 12-2 PM)");
    }
}