package com.plazoleta.application.service;

import com.plazoleta.application.port.input.MarkOrderReadyUseCase;
import com.plazoleta.application.port.output.AuthenticatedUserPort;
import com.plazoleta.application.port.output.MessagingServicePort;
import com.plazoleta.application.port.output.OrderPersistencePort;
import com.plazoleta.application.port.output.UserServicePort;
import com.plazoleta.domain.exception.InvalidOrderException;
import com.plazoleta.domain.exception.InvalidOrderStateException;
import com.plazoleta.domain.exception.NotRestaurantEmployeeException;
import com.plazoleta.domain.exception.OrderNotFoundException;
import com.plazoleta.domain.model.Order;
import com.plazoleta.domain.model.OrderStatus;

import java.security.SecureRandom;

public class MarkOrderReadyService implements MarkOrderReadyUseCase {

    private static final SecureRandom RANDOM = new SecureRandom();

    private final OrderPersistencePort orderPersistencePort;
    private final UserServicePort userServicePort;
    private final MessagingServicePort messagingServicePort;
    private final AuthenticatedUserPort authenticatedUserPort;

    public MarkOrderReadyService(OrderPersistencePort orderPersistencePort,
                                 UserServicePort userServicePort,
                                 MessagingServicePort messagingServicePort,
                                 AuthenticatedUserPort authenticatedUserPort) {
        this.orderPersistencePort = orderPersistencePort;
        this.userServicePort = userServicePort;
        this.messagingServicePort = messagingServicePort;
        this.authenticatedUserPort = authenticatedUserPort;
    }

    @Override
    public Order markReady(Long orderId) {
        Order order = orderPersistencePort.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        Long idRestaurante = authenticatedUserPort.currentUserRestaurantId();
        if (idRestaurante == null || !idRestaurante.equals(order.getIdRestaurante())) {
            throw new NotRestaurantEmployeeException();
        }
        if (order.getEstado() != OrderStatus.EN_PREPARACION) {
            throw new InvalidOrderStateException("Solo se puede marcar como listo un pedido EN_PREPARACION");
        }

        String celular = userServicePort.findPhoneById(order.getIdCliente())
                .orElseThrow(() -> new InvalidOrderException("No se encontró el celular del cliente"));

        String pin = String.format("%06d", RANDOM.nextInt(1_000_000));

        // Primero se envía el SMS: si falla, el pedido no cambia de estado y se puede reintentar.
        messagingServicePort.sendSms(celular,
                "Tu pedido #" + order.getId() + " está listo. Tu PIN de seguridad para reclamarlo es: " + pin);

        order.setEstado(OrderStatus.LISTO);
        order.setPin(pin);
        return orderPersistencePort.save(order);
    }
}
