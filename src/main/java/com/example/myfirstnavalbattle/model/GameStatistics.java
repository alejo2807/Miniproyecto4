package com.example.myfirstnavalbattle.model;

import java.util.HashMap;

/**
 * Clase para llevar estadísticas del jugador usando HashMap.
 * Esta estructura de datos permite almacenar y consultar métricas del juego
 * de manera eficiente mediante claves (String) y valores (Integer).
 *
 * @author 4rias01
 */
public class GameStatistics {
    // Instancia única (Singleton) para compartir estadísticas entre escenas
    private static GameStatistics instance;

    // Estructura de datos: HashMap para almacenar métricas del jugador
    private HashMap<String, Integer> stats;

    /**
     * Constructor privado para patrón Singleton
     */
    private GameStatistics() {
        stats = new HashMap<>();
        initializeStats();
    }

    /**
     * Obtiene la instancia única de GameStatistics (Singleton)
     * 
     * @return La instancia compartida de GameStatistics
     */
    public static GameStatistics getInstance() {
        if (instance == null) {
            instance = new GameStatistics();
        }
        return instance;
    }

    /**
     * Resetea la instancia global (útil para empezar un nuevo juego)
     */
    public static void resetInstance() {
        instance = new GameStatistics();
    }

    /**
     * Inicializa todas las estadísticas en 0
     */
    private void initializeStats() {
        stats.put("disparosTotales", 0);
        stats.put("aciertos", 0);
        stats.put("fallos", 0);
        stats.put("barcosHundidos", 0);
    }

    /**
     * Incrementa una estadística específica en 1
     *
     * @param statName nombre de la estadística a incrementar
     */
    public void incrementStat(String statName) {
        incrementStat(statName, 1);
    }

    /**
     * Incrementa una estadística específica en la cantidad indicada
     *
     * @param statName nombre de la estadística a incrementar
     * @param amount   cantidad a incrementar
     */
    public void incrementStat(String statName, int amount) {
        if (stats.containsKey(statName)) {
            stats.put(statName, stats.get(statName) + amount);
        }
    }

    /**
     * Obtiene el valor de una estadística específica
     *
     * @param statName nombre de la estadística
     * @return valor de la estadística, o 0 si no existe
     */
    public int getStat(String statName) {
        return stats.getOrDefault(statName, 0);
    }

    /**
     * Obtiene todas las estadísticas como HashMap
     *
     * @return HashMap con todas las estadísticas
     */
    public HashMap<String, Integer> getAllStats() {
        return new HashMap<>(stats);
    }

    /**
     * Resetea todas las estadísticas a 0
     */
    public void reset() {
        initializeStats();
    }

    /**
     * Calcula la precisión del jugador como porcentaje
     *
     * @return precisión en porcentaje (0-100)
     */
    public double getPrecision() {
        int total = getStat("disparosTotales");
        if (total == 0)
            return 0.0;
        return (getStat("aciertos") * 100.0) / total;
    }

    /**
     * Genera un resumen de las estadísticas en formato String
     *
     * @return resumen de estadísticas
     */
    public String getSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("Disparos Totales: ").append(getStat("disparosTotales")).append("\n");
        sb.append("Aciertos: ").append(getStat("aciertos")).append("\n");
        sb.append("Fallos: ").append(getStat("fallos")).append("\n");
        sb.append("Barcos Hundidos: ").append(getStat("barcosHundidos")).append("\n");
        sb.append(String.format("Precisión: %.2f%%", getPrecision()));
        return sb.toString();
    }
}
