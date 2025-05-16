package sae.semestre.six.room.dto;

public class RoomDto {

    private Long id;
    private String roomNumber;
    private int floor;
    private String type;

    // Constructors
    public RoomDto() {}

    public RoomDto(Long id, String roomNumber, int floor, String type) {
        this.id = id;
        this.roomNumber = roomNumber;
        this.floor = floor;
        this.type = type;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
