package sae.semestre.six.tests.regression;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sae.semestre.six.appointment.exception.UnvailableException;
import sae.semestre.six.appointment.model.Appointment;
import sae.semestre.six.doctor.model.Doctor;
import sae.semestre.six.room.model.Room;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class AppointmentTest {

    private Doctor doctor;
    private Room room;
    private Appointment appointment;

    @BeforeEach
    void setUp() {
        doctor = new Doctor();
        doctor.setWorkStartHour(9);
        doctor.setWorkEndHour(17); // de 9h à 17h
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
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 10);
        cal.set(Calendar.MINUTE, 0);
        appointment.setDate(cal.getTime());

        // Créneau existant : 10h15 - 10h45 (chevauchement avec celui demandé)
        Calendar existingCal = Calendar.getInstance();
        existingCal.set(Calendar.HOUR_OF_DAY, 10);
        existingCal.set(Calendar.MINUTE, 15);
        Appointment existing = new Appointment();
        existing.setDate(existingCal.getTime());
        existing.setDuration(30);

        List<Appointment> doctorAppointments = List.of(existing);
        List<Appointment> roomAppointments = new ArrayList<>();

        assertThrows(UnvailableException.class, () -> {
            appointment.validateSlot(doctorAppointments, roomAppointments);
        });
    }

    @Test
    void testAppointmentOutsideDoctorHoursThrowsException() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 8); // avant 9h
        cal.set(Calendar.MINUTE, 0);
        appointment.setDate(cal.getTime());

        assertThrows(UnvailableException.class,
                () -> appointment.validateSlot(new ArrayList<>(), new ArrayList<>()));
    }

    @Test
    void testConflictingDoctorAppointmentThrowsException() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 10);
        cal.set(Calendar.MINUTE, 0);
        appointment.setDate(cal.getTime());

        Appointment existing = new Appointment();
        existing.setDate(cal.getTime());
        existing.setDuration(30);

        List<Appointment> doctorAppointments = Collections.singletonList(existing);
        List<Appointment> roomAppointments = new ArrayList<>();

        assertThrows(UnvailableException.class,
                () -> appointment.validateSlot(doctorAppointments, roomAppointments));
    }

    @Test
    void testConflictingRoomAppointmentThrowsException() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 11);
        cal.set(Calendar.MINUTE, 0);
        appointment.setDate(cal.getTime());

        Appointment existing = new Appointment();
        existing.setDate(cal.getTime());
        existing.setDuration(30);

        List<Appointment> doctorAppointments = new ArrayList<>();
        List<Appointment> roomAppointments = Collections.singletonList(existing);

        assertThrows(UnvailableException.class,
                () -> appointment.validateSlot(doctorAppointments, roomAppointments));
    }

    @Test
    void testValidAppointmentDoesNotThrow() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 14);
        cal.set(Calendar.MINUTE, 0);
        appointment.setDate(cal.getTime());

        List<Appointment> doctorAppointments = new ArrayList<>();
        List<Appointment> roomAppointments = new ArrayList<>();

        assertDoesNotThrow(() -> appointment.validateSlot(doctorAppointments, roomAppointments));
    }
}
