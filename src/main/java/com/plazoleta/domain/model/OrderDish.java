package com.plazoleta.domain.model;

/** Una línea del pedido: qué plato y cuántas unidades. */
public class OrderDish {

    private Long idPlato;
    private Integer cantidad;

    public OrderDish() {
    }

    public OrderDish(Long idPlato, Integer cantidad) {
        this.idPlato = idPlato;
        this.cantidad = cantidad;
    }

    public Long getIdPlato() { return idPlato; }
    public void setIdPlato(Long idPlato) { this.idPlato = idPlato; }
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
}
