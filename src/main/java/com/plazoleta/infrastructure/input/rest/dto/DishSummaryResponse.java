package com.plazoleta.infrastructure.input.rest.dto;

/** Lo que ve el cliente en el menú: sin id de restaurante ni estado. */
public class DishSummaryResponse {

    private final Long id;
    private final String nombre;
    private final Integer precio;
    private final String descripcion;
    private final String urlImagen;
    private final String categoria;

    public DishSummaryResponse(Long id, String nombre, Integer precio, String descripcion,
                               String urlImagen, String categoria) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.descripcion = descripcion;
        this.urlImagen = urlImagen;
        this.categoria = categoria;
    }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public Integer getPrecio() { return precio; }
    public String getDescripcion() { return descripcion; }
    public String getUrlImagen() { return urlImagen; }
    public String getCategoria() { return categoria; }
}
