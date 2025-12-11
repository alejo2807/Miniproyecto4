package com.example.myfirstnavalbattle.model;

/**
 * Clase adaptadora que convierte GameStatistics a un formato
 * específico para mostrar en la interfaz de usuario.
 * 
 * DEMOSTRACIÓN: Patrón ADAPTER para adaptar una interfaz a otra
 * 
 * El patrón Adapter permite que GameStatistics (que usa HashMap internamente)
 * se adapte a diferentes formatos de salida sin modificar la clase original.
 * 
 * @author 4rias01
 */
public class StatisticsDisplayAdapter {

    private final GameStatistics statistics;

    /**
     * Constructor que recibe las estadísticas a adaptar
     * 
     * @param statistics instancia de GameStatistics
     */
    public StatisticsDisplayAdapter(GameStatistics statistics) {
        this.statistics = statistics;
    }

    /**
     * Adapta las estadísticas a formato de tabla para consola
     * 
     * @return String con formato de tabla ASCII
     */
    public String toConsoleTable() {
        StringBuilder table = new StringBuilder();
        table.append("╔════════════════════════════════╗\n");
        table.append("║     ESTADÍSTICAS DEL JUEGO     ║\n");
        table.append("╠════════════════════════════════╣\n");
        table.append(String.format("║ Disparos Totales: %-12d ║\n", statistics.getStat("disparosTotales")));
        table.append(String.format("║ Aciertos:         %-12d ║\n", statistics.getStat("aciertos")));
        table.append(String.format("║ Fallos:           %-12d ║\n", statistics.getStat("fallos")));
        table.append(String.format("║ Barcos Hundidos:  %-12d ║\n", statistics.getStat("barcosHundidos")));
        table.append(String.format("║ Precisión:        %-11.2f%% ║\n", statistics.getPrecision()));
        table.append("╚════════════════════════════════╝\n");
        return table.toString();
    }

    /**
     * Adapta las estadísticas a formato JSON
     * 
     * @return String en formato JSON
     */
    public String toJSON() {
        return String.format(
                "{\"disparosTotales\":%d,\"aciertos\":%d,\"fallos\":%d,\"barcosHundidos\":%d,\"precision\":%.2f}",
                statistics.getStat("disparosTotales"),
                statistics.getStat("aciertos"),
                statistics.getStat("fallos"),
                statistics.getStat("barcosHundidos"),
                statistics.getPrecision());
    }

    /**
     * Adapta las estadísticas a formato CSV
     * 
     * @return String en formato CSV
     */
    public String toCSV() {
        return String.format("%d,%d,%d,%d,%.2f",
                statistics.getStat("disparosTotales"),
                statistics.getStat("aciertos"),
                statistics.getStat("fallos"),
                statistics.getStat("barcosHundidos"),
                statistics.getPrecision());
    }

    /**
     * Adapta las estadísticas a formato HTML para mostrar en web
     * 
     * @return String en formato HTML
     */
    public String toHTML() {
        StringBuilder html = new StringBuilder();
        html.append("<div class='statistics'>\n");
        html.append("  <h2>Estadísticas del Juego</h2>\n");
        html.append("  <ul>\n");
        html.append(String.format("    <li>Disparos Totales: <strong>%d</strong></li>\n",
                statistics.getStat("disparosTotales")));
        html.append(String.format("    <li>Aciertos: <strong>%d</strong></li>\n",
                statistics.getStat("aciertos")));
        html.append(String.format("    <li>Fallos: <strong>%d</strong></li>\n",
                statistics.getStat("fallos")));
        html.append(String.format("    <li>Barcos Hundidos: <strong>%d</strong></li>\n",
                statistics.getStat("barcosHundidos")));
        html.append(String.format("    <li>Precisión: <strong>%.2f%%</strong></li>\n",
                statistics.getPrecision()));
        html.append("  </ul>\n");
        html.append("</div>");
        return html.toString();
    }

    /**
     * Adapta a formato compacto para UI (una sola línea)
     * 
     * @return String compacto
     */
    public String toCompactDisplay() {
        return String.format("Disparos: %d | Aciertos: %d | Precisión: %.1f%%",
                statistics.getStat("disparosTotales"),
                statistics.getStat("aciertos"),
                statistics.getPrecision());
    }
}
