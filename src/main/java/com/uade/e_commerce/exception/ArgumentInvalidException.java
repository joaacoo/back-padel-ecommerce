package com.uade.e_commerce.exception;

public class ArgumentInvalidException extends RuntimeException {

    public ArgumentInvalidException(String mensaje) {
        super(mensaje);
    }
}
