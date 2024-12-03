package org.bookexchange.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bookexchange.model.enums.AdminLevel;
import org.bookexchange.model.enums.UserRole;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@Entity
public class Admin extends User {
    @Enumerated(EnumType.STRING)
    private AdminLevel adminLevel;

    public Admin(String name, String surname, String username, String password, AdminLevel adminLevel, UserRole role) {
        super(name, surname, username, password, role);
        this.adminLevel = adminLevel;
    }
}
