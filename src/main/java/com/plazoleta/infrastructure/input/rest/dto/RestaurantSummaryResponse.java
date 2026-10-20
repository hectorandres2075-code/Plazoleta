package com.plazoleta.infrastructure.input.rest.dto;

/** Lo que ve el cliente en el listado: solo nombre y logo. */
public class RestaurantSummaryResponse {

    private final String nombre;
    private final String urlLogo;

    public RestaurantSummaryResponse(String nombre, String urlLogo) {
        this.nombre = nombre;
        this.urlLogo = urlLogo;
    }

    public String getNombre() { return nombre; }
    public String getUrlLogo() { return urlLogo; }
}
