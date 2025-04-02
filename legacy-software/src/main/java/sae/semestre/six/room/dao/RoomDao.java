package sae.semestre.six.room.dao;

import sae.semestre.six.base.dao.GenericDao;
import sae.semestre.six.room.model.Room;

public interface RoomDao extends GenericDao<Room, Long> {
    Room findByRoomNumber(String roomNumber);
} 