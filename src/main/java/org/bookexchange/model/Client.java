package org.bookexchange.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bookexchange.model.enums.UserRole;

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
    @OneToMany(mappedBy = "reviewRecipient", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> myReviews = new ArrayList<>();
    @OneToMany(mappedBy = "reviewAuthor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> authoredReviews = new ArrayList<>();

    public Client(String name, String surname, String username, String password, String address, LocalDate dateOfBirth, UserRole role) {
        super(name, surname, username, password, role);
        this.address = address;
        this.dateOfBirth = dateOfBirth;
    }
}
