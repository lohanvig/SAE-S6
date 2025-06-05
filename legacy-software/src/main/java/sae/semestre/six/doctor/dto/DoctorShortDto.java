package sae.semestre.six.doctor.dto;

import lombok.Getter;

@Getter
public class DoctorShortDto {

    private Long id;
    private String firstName;
    private String lastName;
    private String specialty;

    public DoctorShortDto() {}

    public DoctorShortDto(Long id, String firstName, String lastName, String specialty) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.specialty = specialty;
    }


    public void setId(Long id) {
        this.id = id;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }
}
