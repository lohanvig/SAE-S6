package sae.semestre.six.appointment.dao;

import sae.semestre.six.appointment.model.Appointment;
import sae.semestre.six.base.dao.GenericDao;

import java.util.Date;
import java.util.List;

public interface AppointmentDao extends GenericDao<Appointment, Long> {
    List<Appointment> findByPatientId(Long patientId);
    List<Appointment> findByDoctorId(Long doctorId);
    List<Appointment> findByDateRange(Date startDate, Date endDate);
} 