package com.plazoleta.application.port.output;

/** Lo que Plazoleta necesita del microservicio de Mensajería. */
public interface MessagingServicePort {

    void sendSms(String celular, String mensaje);
}
