package sae.semestre.six.appointment.dao;

import sae.semestre.six.appointment.model.Appointment;
import org.springframework.stereotype.Repository;
import sae.semestre.six.base.dao.AbstractHibernateDao;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class AppointmentDaoImpl extends AbstractHibernateDao<Appointment, Long> implements AppointmentDao {
    
    @Override
    @SuppressWarnings("unchecked")
    public List<Appointment> findByPatientId(Long patientId) {
        return getEntityManager()
                .createQuery("FROM Appointment WHERE patient.id = :patientId")
                .setParameter("patientId", patientId)
                .getResultList();
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public List<Appointment> findByDoctorId(Long doctorId) {
        return getEntityManager()
                .createQuery("FROM Appointment WHERE doctor.id = :doctorId")
                .setParameter("doctorId", doctorId)
                .getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Appointment> findByDoctorIdByDate(Long doctorId, LocalDateTime appointmentDate) {
        LocalDateTime start = appointmentDate.toLocalDate().atStartOfDay();
        LocalDateTime end = appointmentDate.toLocalDate().atTime(23, 59, 59, 999_999_999);

        return getEntityManager()
                .createQuery("FROM Appointment WHERE doctor.id = :doctorId AND date BETWEEN :start AND :end")
                .setParameter("doctorId", doctorId)
                .setParameter("start", start)
                .setParameter("end", end)
                .getResultList();
    }


    @Override
    @SuppressWarnings("unchecked")
    public List<Appointment> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return getEntityManager()
                .createQuery("FROM Appointment WHERE date BETWEEN :startDate AND :endDate")
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .getResultList();
    }

    @Override
    public List<Appointment> findByRoomIdByDate(Long roomId, LocalDateTime date) {
        LocalDateTime startOfDay = date.toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = date.toLocalDate().atTime(23, 59, 59, 999_999_999);

        return getEntityManager()
                .createQuery("FROM Appointment WHERE room.id = :roomId AND date BETWEEN :start AND :end", Appointment.class)
                .setParameter("roomId", roomId)
                .setParameter("start", startOfDay)
                .setParameter("end", endOfDay)
                .getResultList();
    }

} 