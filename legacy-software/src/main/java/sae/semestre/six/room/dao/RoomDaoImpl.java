package sae.semestre.six.room.dao;

import sae.semestre.six.base.dao.AbstractHibernateDao;
import sae.semestre.six.room.model.Room;
import org.springframework.stereotype.Repository;

@Repository
public class RoomDaoImpl extends AbstractHibernateDao<Room, Long> implements RoomDao {
    
    @Override
    public Room findByRoomNumber(String roomNumber) {
        return (Room) getEntityManager()
                .createQuery("FROM Room WHERE roomNumber = :roomNumber")
                .setParameter("roomNumber", roomNumber)
                .getSingleResult();
    }
} 