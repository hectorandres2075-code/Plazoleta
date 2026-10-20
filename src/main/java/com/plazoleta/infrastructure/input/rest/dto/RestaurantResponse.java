package com.plazoleta.infrastructure.input.rest.dto;

public class RestaurantResponse {

    private final Long id;
    private final String nombre;
    private final String nit;
    private final String direccion;
    private final String telefono;
    private final String urlLogo;
    private final Long idPropietario;

    public RestaurantResponse(Long id, String nombre, String nit, String direccion, String telefono,
                              String urlLogo, Long idPropietario) {
        this.id = id;
        this.nombre = nombre;
        this.nit = nit;
        this.direccion = direccion;
        this.telefono = telefono;
        this.urlLogo = urlLogo;
        this.idPropietario = idPropietario;
    }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getNit() { return nit; }
    public String getDireccion() { return direccion; }
    public String getTelefono() { return telefono; }
    public String getUrlLogo() { return urlLogo; }
    public Long getIdPropietario() { return idPropietario; }
}
