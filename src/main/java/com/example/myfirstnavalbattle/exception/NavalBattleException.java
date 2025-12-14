package com.example.myfirstnavalbattle.exception;

/**
 * Excepción personalizada marcada (checked) para situaciones específicas del
 * juego.
 * Ejemplo de uso: Cuando se intenta realizar una acción inválida en el juego.
 */
public class NavalBattleException extends Exception {

    public NavalBattleException(String message) {
        super(message);
    }

    public NavalBattleException(String message, Throwable cause) {
        super(message, cause);
    }
}
