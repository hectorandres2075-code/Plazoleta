package com.plazoleta.infrastructure.input.rest.dto;

public class OwnerIdResponse {

    private final Long idPropietario;

    public OwnerIdResponse(Long idPropietario) {
        this.idPropietario = idPropietario;
    }

    public Long getIdPropietario() { return idPropietario; }
}
