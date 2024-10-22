package org.bookexchange.model;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@Entity
public class Admin extends User {
    private String adminLevel;

    public Admin(String name, String surname, String username, String password, String adminLevel) {
        super(name, surname, username, password);
        this.adminLevel = adminLevel;
    }
}
