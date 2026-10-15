package com.ispc.todostock.network;

public class ContactoRequest {

    private final String email;
    private final String asunto;
    private final String mensaje;

    public ContactoRequest(String email, String asunto, String mensaje) {
        this.email = email;
        this.asunto = asunto;
        this.mensaje = mensaje;
    }
}