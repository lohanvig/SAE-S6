package sae.semestre.six.tests.regression.appointment.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import sae.semestre.six.appointment.controller.AppointmentController;
import sae.semestre.six.appointment.dao.AppointmentDao;
import sae.semestre.six.appointment.model.Appointment;
import sae.semestre.six.doctor.dao.DoctorDao;
import sae.semestre.six.doctor.model.Doctor;
import sae.semestre.six.service.EmailService;

import java.lang.reflect.Field;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Classe de test unitaire pour le contrôleur AppointmentController.
 * Vérifie le bon fonctionnement de la planification de rendez-vous
 * et l'obtention de créneaux disponibles.
 */
class AppointmentControllerTests {

    @InjectMocks
    private AppointmentController appointmentController;

    @Mock
    private AppointmentDao appointmentDao;

    @Mock
    private DoctorDao doctorDao;

    @Mock
    private EmailService emailService; // mock explicite

    @BeforeEach
    void setup() throws Exception {
        MockitoAnnotations.openMocks(this);

        // Forcer l'injection du mock EmailService dans le champ final
        Field field = AppointmentController.class.getDeclaredField("emailService");
        field.setAccessible(true);
        field.set(appointmentController, emailService);
    }

    /**
     * Test : Un rendez-vous peut être planifié avec succès si aucune collision n'est détectée
     * et si l'heure est dans les horaires autorisés (9h-17h).
     */
    @Test
    void rendezVousPlanifieAvecSucces() {
        Long doctorId = 1L;
        Long patientId = 2L;
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 10);
        cal.set(Calendar.MINUTE, 0);
        Date appointmentDate = cal.getTime();

        Doctor doctor = new Doctor();
        doctor.setEmail("doc@example.com");

        when(doctorDao.findById(doctorId)).thenReturn(doctor);
        when(appointmentDao.findByDoctorId(doctorId)).thenReturn(Collections.emptyList());

        String result = appointmentController.scheduleAppointment(doctorId, patientId, appointmentDate);

        assertEquals("Appointment scheduled successfully", result);
        verify(emailService, times(1)).sendEmail(
                eq("doc@example.com"), anyString(), contains("appointment")
        );
    }

    /**
     * Test : Si un rendez-vous existe déjà pour le même horaire, un message d’indisponibilité est renvoyé.
     */
    @Test
    void medecinIndisponiblePourLeCreneau() {
        Long doctorId = 1L;
        Long patientId = 2L;
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 10);
        cal.set(Calendar.MINUTE, 0);
        Date appointmentDate = cal.getTime();

        Doctor doctor = new Doctor();
        doctor.setEmail("doc@example.com");

        Appointment existingAppointment = new Appointment();
        existingAppointment.setAppointmentDate(appointmentDate);

        when(doctorDao.findById(doctorId)).thenReturn(doctor);
        when(appointmentDao.findByDoctorId(doctorId)).thenReturn(List.of(existingAppointment));

        String result = appointmentController.scheduleAppointment(doctorId, patientId, appointmentDate);

        assertEquals("Doctor is not available at this time", result);
        verify(emailService, never()).sendEmail(any(), any(), any());
    }

    /**
     * Test : Si l'heure du rendez-vous est en dehors des horaires autorisés (9h à 17h), une erreur est renvoyée.
     */
    @Test
    void rendezVousEnDehorsDesHorairesAutorises() {
        Long doctorId = 1L;
        Long patientId = 2L;
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 20); // après 17h
        Date appointmentDate = cal.getTime();

        Doctor doctor = new Doctor();
        doctor.setEmail("doc@example.com");

        when(doctorDao.findById(doctorId)).thenReturn(doctor);
        when(appointmentDao.findByDoctorId(doctorId)).thenReturn(Collections.emptyList());

        String result = appointmentController.scheduleAppointment(doctorId, patientId, appointmentDate);

        assertEquals("Appointments only available between 9 AM and 5 PM", result);
        verify(emailService, never()).sendEmail(any(), any(), any());
    }

    /**
     * Test : Si une exception est levée (ex. DAO), un message d'erreur générique est renvoyé.
     */
    @Test
    void exceptionLorsDeLaPlanification() {
        Long doctorId = 1L;
        Long patientId = 2L;
        Date appointmentDate = new Date();

        when(doctorDao.findById(doctorId)).thenThrow(new RuntimeException("DB error"));

        String result = appointmentController.scheduleAppointment(doctorId, patientId, appointmentDate);

        assertTrue(result.startsWith("Error:"));
    }

    /**
     * Test : Vérifie que les créneaux horaires disponibles sont retournés correctement,
     * en excluant ceux déjà pris.
     */
    @Test
    void obtenirCreneauxDisponibles() {
        Long doctorId = 1L;
        ZoneId zone = ZoneId.of("Europe/Paris");

        // Date de base : aujourd’hui à minuit
        Date date = Date.from(LocalDate.now().atStartOfDay(zone).toInstant());

        // Création d'un rendez-vous pris à 10h aujourd'hui
        Date takenDate = Date.from(LocalDate.now().atTime(10, 0).atZone(zone).toInstant());
        Appointment taken = new Appointment();
        taken.setAppointmentDate(takenDate);

        when(appointmentDao.findByDoctorId(doctorId)).thenReturn(List.of(taken));

        List<Date> slots = appointmentController.getAvailableSlots(doctorId, date);

        for (Date slot : slots) {
            int hour = slot.toInstant().atZone(ZoneId.systemDefault()).getHour();
            assertNotEquals(10, hour); // vérifie que 10h n’est pas proposé
        }
    }
}