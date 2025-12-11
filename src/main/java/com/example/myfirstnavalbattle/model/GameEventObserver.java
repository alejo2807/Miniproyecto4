package com.example.myfirstnavalbattle.model;

/**
 * Interfaz para observar eventos importantes del juego.
 * Implementa el patrón Observer para desacoplar la lógica del juego
 * de la lógica de notificación.
 * 
 * DEMOSTRACIÓN: Uso de INTERFACES para definir contratos
 * 
 * @author 4rias01
 */
public interface GameEventObserver {

    /**
     * Se llama cuando un barco es destruido
     * 
     * @param shipSize          tamaño del barco destruido
     * @param destroyedByPlayer true si fue destruido por el jugador, false si fue
     *                          por la IA
     */
    void onShipDestroyed(int shipSize, boolean destroyedByPlayer);

    /**
     * Se llama cuando un disparo impacta pero no destruye
     * 
     * @param row      fila del impacto
     * @param col      columna del impacto
     * @param byPlayer true si el disparo fue del jugador
     */
    void onHit(int row, int col, boolean byPlayer);

    /**
     * Se llama cuando un disparo falla
     * 
     * @param row      fila del disparo
     * @param col      columna del disparo
     * @param byPlayer true si el disparo fue del jugador
     */
    void onMiss(int row, int col, boolean byPlayer);

    /**
     * Se llama cuando el juego termina
     * 
     * @param playerWon true si el jugador ganó, false si la IA ganó
     */
    void onGameEnd(boolean playerWon);
}
