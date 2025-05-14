package sae.semestre.six.doctor.dao;

import sae.semestre.six.base.dao.GenericDao;
import sae.semestre.six.doctor.model.Doctor;
import java.util.List;
import java.util.Optional;

public interface DoctorDao extends GenericDao<Doctor, Long> {
    Optional<Doctor> findByDoctorNumber(String doctorNumber);
    List<Doctor> findBySpecialization(String specialization);
    List<Doctor> findByDepartment(String department);
    Optional<Long> findDoctorIdByDoctorNumber(String doctorNumber);
}