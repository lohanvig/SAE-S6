package sae.semestre.six.room.dao;

import jakarta.persistence.NoResultException;
import sae.semestre.six.base.dao.AbstractHibernateDao;
import sae.semestre.six.room.model.Room;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RoomDaoImpl extends AbstractHibernateDao<Room, Long> implements RoomDao {

    @Override
    public Room findByRoomNumber(String roomNumber) {
        try {
            return (Room) getEntityManager()
                    .createQuery("FROM Room WHERE roomNumber = :roomNumber")
                    .setParameter("roomNumber", roomNumber)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public List<Room> findAll() {
        return getEntityManager()
                .createQuery("FROM Room", Room.class)
                .getResultList();
    }
} 