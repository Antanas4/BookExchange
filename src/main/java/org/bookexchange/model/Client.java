package org.bookexchange.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@Entity
public class Client extends User{
    private String address;
    private LocalDate dateOfBirth;
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Publication> ownedPublications = new ArrayList<>();

    public Client(String name, String surname, String username, String password, String address, LocalDate dateOfBirth) {
        super(name, surname, username, password);
        this.address = address;
        this.dateOfBirth = dateOfBirth;
    }

}
