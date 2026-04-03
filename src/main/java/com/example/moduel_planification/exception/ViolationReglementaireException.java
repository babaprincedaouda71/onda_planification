package com.example.moduel_planification.exception;

public class ViolationReglementaireException extends RuntimeException {

    private final String code;

    public ViolationReglementaireException(String code, String message) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}