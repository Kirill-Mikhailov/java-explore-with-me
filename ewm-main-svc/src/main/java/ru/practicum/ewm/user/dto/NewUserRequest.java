package ru.practicum.ewm.user.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class NewUserRequest {
    @NotBlank
    @Email
    @Size(min = 6, max = 254)
    private String email;
    @Size(min = 2, max = 250)
    @NotBlank
    private String name;
}
