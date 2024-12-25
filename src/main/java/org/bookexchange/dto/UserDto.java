package org.bookexchange.dto;

import lombok.*;
import org.bookexchange.model.enums.AdminLevel;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class UserDto {
    private String name;
    private String surname;
    private String username;
    private String password;
    private String address;
    private LocalDate dateOfBirth;
    private AdminLevel adminLevel;
    private String userType;
}
