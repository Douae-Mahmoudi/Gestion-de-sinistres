package com.SinistraPro.domain.exposition.dto.request;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ForgotPasswordRequest {

    @NotBlank(message = "L'email est obligatoire")
    @Pattern(regexp = "^[A-Za-z0-9+_.-]+@(.+)\\.[a-z]+$", message = "Format email invalide")
    @Email(message = "Format email invalide")
    private String email;
}
