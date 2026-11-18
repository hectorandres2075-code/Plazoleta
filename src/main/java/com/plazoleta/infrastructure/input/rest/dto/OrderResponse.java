package com.plazoleta.infrastructure.input.rest.dto;

import com.plazoleta.domain.model.Order;

import java.time.LocalDateTime;
import java.util.List;

/** Respuesta de un pedido. Nunca incluye el PIN de seguridad. */
public class OrderResponse {

    private final Long id;
    private final Long idCliente;
    private final LocalDateTime fecha;
    private final String estado;
    private final Long idChef;
    private final Long idRestaurante;
    private final List<Linea> platos;

    public OrderResponse(Long id, Long idCliente, LocalDateTime fecha, String estado, Long idChef,
                         Long idRestaurante, List<Linea> platos) {
        this.id = id;
        this.idCliente = idCliente;
        this.fecha = fecha;
        this.estado = estado;
        this.idChef = idChef;
        this.idRestaurante = idRestaurante;
        this.platos = platos;
    }

    public static OrderResponse from(Order o) {
        return new OrderResponse(o.getId(), o.getIdCliente(), o.getFecha(), o.getEstado().name(),
                o.getIdChef(), o.getIdRestaurante(),
                o.getPlatos().stream().map(p -> new Linea(p.getIdPlato(), p.getCantidad())).toList());
    }

    public Long getId() { return id; }
    public Long getIdCliente() { return idCliente; }
    public LocalDateTime getFecha() { return fecha; }
    public String getEstado() { return estado; }
    public Long getIdChef() { return idChef; }
    public Long getIdRestaurante() { return idRestaurante; }
    public List<Linea> getPlatos() { return platos; }

    public static class Linea {
        private final Long idPlato;
        private final Integer cantidad;

        public Linea(Long idPlato, Integer cantidad) {
            this.idPlato = idPlato;
            this.cantidad = cantidad;
        }

        public Long getIdPlato() { return idPlato; }
        public Integer getCantidad() { return cantidad; }
    }
}
