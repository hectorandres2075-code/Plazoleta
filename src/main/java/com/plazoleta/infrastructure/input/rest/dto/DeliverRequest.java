package com.plazoleta.infrastructure.input.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class DeliverRequest {

    @NotBlank(message = "El PIN es obligatorio")
    @Pattern(regexp = "\\d{6}", message = "El PIN debe tener 6 dígitos")
    private String pin;

    public String getPin() { return pin; }
    public void setPin(String pin) { this.pin = pin; }
}
