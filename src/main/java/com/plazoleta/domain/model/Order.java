package com.plazoleta.domain.model;

import java.time.LocalDateTime;
import java.util.List;

/** Un pedido: lista de platos de un mismo restaurante, hecha por un cliente. */
public class Order {

    private Long id;
    private Long idCliente;
    private LocalDateTime fecha;
    private OrderStatus estado;
    private Long idChef;          // empleado asignado (null mientras está PENDIENTE)
    private Long idRestaurante;
    private String pin;           // se genera cuando el pedido pasa a LISTO
    private List<OrderDish> platos;

    public Order() {
    }

    public Order(Long id, Long idCliente, LocalDateTime fecha, OrderStatus estado, Long idChef,
                 Long idRestaurante, String pin, List<OrderDish> platos) {
        this.id = id;
        this.idCliente = idCliente;
        this.fecha = fecha;
        this.estado = estado;
        this.idChef = idChef;
        this.idRestaurante = idRestaurante;
        this.pin = pin;
        this.platos = platos;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getIdCliente() { return idCliente; }
    public void setIdCliente(Long idCliente) { this.idCliente = idCliente; }
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
    public OrderStatus getEstado() { return estado; }
    public void setEstado(OrderStatus estado) { this.estado = estado; }
    public Long getIdChef() { return idChef; }
    public void setIdChef(Long idChef) { this.idChef = idChef; }
    public Long getIdRestaurante() { return idRestaurante; }
    public void setIdRestaurante(Long idRestaurante) { this.idRestaurante = idRestaurante; }
    public String getPin() { return pin; }
    public void setPin(String pin) { this.pin = pin; }
    public List<OrderDish> getPlatos() { return platos; }
    public void setPlatos(List<OrderDish> platos) { this.platos = platos; }
}
