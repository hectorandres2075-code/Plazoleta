package com.plazoleta.infrastructure.input.rest.dto;

import jakarta.validation.constraints.NotNull;

public class DishStatusRequest {

    @NotNull(message = "El campo activo es obligatorio")
    private Boolean activo;

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
}
