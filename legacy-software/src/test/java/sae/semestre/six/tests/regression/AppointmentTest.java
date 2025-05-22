package sae.semestre.six.tests.regression;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sae.semestre.six.appointment.exception.UnvailableException;
import sae.semestre.six.appointment.model.Appointment;
import sae.semestre.six.doctor.model.Doctor;
import sae.semestre.six.room.model.Room;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AppointmentTest {

    private Doctor doctor;
    private Room room;
    private Appointment appointment;

    @BeforeEach
    void setUp() {
        doctor = new Doctor();
        doctor.setWorkStartHour(LocalTime.of(9, 0));
        doctor.setWorkEndHour(LocalTime.of(17, 0)); // de 9h à 17h
        room = new Room();
        appointment = new Appointment();
        appointment.setDoctor(doctor);
        appointment.setRoom(room);
        appointment.setDuration(30); // 30 minutes
    }

    @Test
    void testNullValuesThrowsException() {
        appointment.setDoctor(null);
        assertThrows(IllegalArgumentException.class,
                () -> appointment.validateSlot(new ArrayList<>(), new ArrayList<>()));
    }

    @Test
    void testOverlappingAppointments() {
        // Créneau demandé : 10h00 - 10h30
        LocalDateTime requestedStart = LocalDateTime.of(2025, 5, 22, 10, 0);
        appointment.setDate(requestedStart);

        // Créneau existant : 10h15 - 10h45 (chevauchement)
        Appointment existing = new Appointment();
        existing.setDate(LocalDateTime.of(2025, 5, 22, 10, 15));
        existing.setDuration(30);

        List<Appointment> doctorAppointments = List.of(existing);
        List<Appointment> roomAppointments = new ArrayList<>();

        assertThrows(UnvailableException.class, () -> {
            appointment.validateSlot(doctorAppointments, roomAppointments);
        });
    }

    @Test
    void testAppointmentOutsideDoctorHoursThrowsException() {
        appointment.setDate(LocalDateTime.of(2025, 5, 22, 8, 0)); // avant 9h
        assertThrows(UnvailableException.class,
                () -> appointment.validateSlot(new ArrayList<>(), new ArrayList<>()));
    }

    @Test
    void testConflictingDoctorAppointmentThrowsException() {
        LocalDateTime date = LocalDateTime.of(2025, 5, 22, 10, 0);
        appointment.setDate(date);

        Appointment existing = new Appointment();
        existing.setDate(date);
        existing.setDuration(30);

        List<Appointment> doctorAppointments = Collections.singletonList(existing);
        List<Appointment> roomAppointments = new ArrayList<>();

        assertThrows(UnvailableException.class,
                () -> appointment.validateSlot(doctorAppointments, roomAppointments));
    }

    @Test
    void testConflictingRoomAppointmentThrowsException() {
        LocalDateTime date = LocalDateTime.of(2025, 5, 22, 11, 0);
        appointment.setDate(date);

        Appointment existing = new Appointment();
        existing.setDate(date);
        existing.setDuration(30);

        List<Appointment> doctorAppointments = new ArrayList<>();
        List<Appointment> roomAppointments = Collections.singletonList(existing);

        assertThrows(UnvailableException.class,
                () -> appointment.validateSlot(doctorAppointments, roomAppointments));
    }

    @Test
    void testValidAppointmentDoesNotThrow() {
        appointment.setDate(LocalDateTime.of(2025, 5, 22, 14, 0));

        List<Appointment> doctorAppointments = new ArrayList<>();
        List<Appointment> roomAppointments = new ArrayList<>();

        assertDoesNotThrow(() -> appointment.validateSlot(doctorAppointments, roomAppointments));
    }
}