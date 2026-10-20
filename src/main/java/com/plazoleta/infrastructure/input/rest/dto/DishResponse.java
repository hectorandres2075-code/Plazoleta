package com.plazoleta.infrastructure.input.rest.dto;

public class DishResponse {

    private final Long id;
    private final String nombre;
    private final Integer precio;
    private final String descripcion;
    private final String urlImagen;
    private final String categoria;
    private final boolean activo;
    private final Long idRestaurante;

    public DishResponse(Long id, String nombre, Integer precio, String descripcion, String urlImagen,
                        String categoria, boolean activo, Long idRestaurante) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.descripcion = descripcion;
        this.urlImagen = urlImagen;
        this.categoria = categoria;
        this.activo = activo;
        this.idRestaurante = idRestaurante;
    }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public Integer getPrecio() { return precio; }
    public String getDescripcion() { return descripcion; }
    public String getUrlImagen() { return urlImagen; }
    public String getCategoria() { return categoria; }
    public boolean isActivo() { return activo; }
    public Long getIdRestaurante() { return idRestaurante; }
}
