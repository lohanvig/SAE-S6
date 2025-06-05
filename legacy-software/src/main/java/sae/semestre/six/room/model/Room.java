package sae.semestre.six.room.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import sae.semestre.six.appointment.model.Appointment;

import java.util.Set;
import java.util.HashSet;

@Setter
@Getter
@Entity
@Table(name = "rooms")
public class Room {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "room_number", unique = true)
    private String roomNumber;
    
    @Column(name = "floor")
    private Integer floor;
    
    @Column(name = "type")
    private String type; 
    
    @Column(name = "capacity")
    private Integer capacity;

    @OneToMany(mappedBy = "room")
    private Set<Appointment> appointments = new HashSet<>();
    
    
    @Column(name = "current_patient_count")
    private Integer currentPatientCount = 0;

    public boolean canAcceptPatient() {
        return currentPatientCount < capacity;
    }

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

    public Integer getFloor() {
        return floor;
    }

    public void setFloor(Integer floor) {
        this.floor = floor;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public Set<Appointment> getAppointments() {
        return appointments;
    }

    public void setAppointments(Set<Appointment> appointments) {
        this.appointments = appointments;
    }

    public Integer getCurrentPatientCount() {
        return currentPatientCount;
    }

    public void setCurrentPatientCount(Integer currentPatientCount) {
        this.currentPatientCount = currentPatientCount;
    }
}