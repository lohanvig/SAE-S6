package sae.semestre.six.appointment.dao;

import sae.semestre.six.appointment.model.Appointment;
import sae.semestre.six.base.dao.GenericDao;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public interface AppointmentDao extends GenericDao<Appointment, Long> {
    List<Appointment> findByPatientId(Long patientId);
    List<Appointment> findByDoctorId(Long doctorId);
    List<Appointment> findByDoctorIdByDate(Long doctorId, LocalDateTime appointmentDate);
    List<Appointment> findByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    List<Appointment> findByRoomIdByDate(Long roomId, LocalDateTime date);
} 