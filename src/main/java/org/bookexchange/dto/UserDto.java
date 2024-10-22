package org.bookexchange.dto;

import lombok.*;

import java.time.LocalDate;

@Data
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
    private String adminLevel;
    private String userType;

}