package sae.semestre.six.appointment.dao;

import sae.semestre.six.appointment.model.Appointment;
import org.springframework.stereotype.Repository;
import sae.semestre.six.base.dao.AbstractHibernateDao;

import java.util.Calendar;
import java.util.Date;
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
    public List<Appointment> findByDoctorIdByDate(Long doctorId, Date appointmentDate) {
        Calendar start = Calendar.getInstance();
        start.setTime(appointmentDate);
        start.set(Calendar.HOUR_OF_DAY, 0);
        start.set(Calendar.MINUTE, 0);
        start.set(Calendar.SECOND, 0);
        start.set(Calendar.MILLISECOND, 0);

        Calendar end = (Calendar) start.clone();
        end.set(Calendar.HOUR_OF_DAY, 23);
        end.set(Calendar.MINUTE, 59);
        end.set(Calendar.SECOND, 59);
        end.set(Calendar.MILLISECOND, 999);

        return getEntityManager()
                .createQuery("FROM Appointment WHERE doctor.id = :doctorId AND date BETWEEN :start AND :end")
                .setParameter("doctorId", doctorId)
                .setParameter("start", start.getTime())
                .setParameter("end", end.getTime())
                .getResultList();
    }


    @Override
    @SuppressWarnings("unchecked")
    public List<Appointment> findByDateRange(Date startDate, Date endDate) {
        return getEntityManager()
                .createQuery("FROM Appointment WHERE date BETWEEN :startDate AND :endDate")
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Appointment> findByRoomIdByDate(Long roomId, Date date) {
        Calendar startOfDay = Calendar.getInstance();
        startOfDay.setTime(date);
        startOfDay.set(Calendar.HOUR_OF_DAY, 0);
        startOfDay.set(Calendar.MINUTE, 0);
        startOfDay.set(Calendar.SECOND, 0);
        startOfDay.set(Calendar.MILLISECOND, 0);

        Calendar endOfDay = (Calendar) startOfDay.clone();
        endOfDay.set(Calendar.HOUR_OF_DAY, 23);
        endOfDay.set(Calendar.MINUTE, 59);
        endOfDay.set(Calendar.SECOND, 59);
        endOfDay.set(Calendar.MILLISECOND, 999);

        return getEntityManager()
                .createQuery("FROM Appointment WHERE room.id = :roomId AND date BETWEEN :start AND :end")
                .setParameter("roomId", roomId)
                .setParameter("start", startOfDay.getTime())
                .setParameter("end", endOfDay.getTime())
                .getResultList();
    }

} 