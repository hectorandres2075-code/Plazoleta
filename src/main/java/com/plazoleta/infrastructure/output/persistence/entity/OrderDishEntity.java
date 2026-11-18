package com.plazoleta.infrastructure.output.persistence.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "pedido_platos")
public class OrderDishEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pedido_id")
    private OrderEntity pedido;

    @Column(nullable = false)
    private Long idPlato;

    @Column(nullable = false)
    private Integer cantidad;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public OrderEntity getPedido() { return pedido; }
    public void setPedido(OrderEntity pedido) { this.pedido = pedido; }
    public Long getIdPlato() { return idPlato; }
    public void setIdPlato(Long idPlato) { this.idPlato = idPlato; }
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
}
