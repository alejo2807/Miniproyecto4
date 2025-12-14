package com.example.myfirstnavalbattle.model;

/**
 * Adapter class that converts GameStatistics to a specific format
 * for display in the user interface.
 * 
 * DEMONSTRATION: ADAPTER Pattern to adapt one interface to another
 * 
 * The Adapter pattern allows GameStatistics (which uses HashMap internally)
 * to be adapted to different output formats without modifying the original
 * class.
 * 
 * @author David - Brandon
 */
public class StatisticsDisplayAdapter {

        private final GameStatistics statistics;

        /**
         * Constructor that receives statistics to adapt
         * 
         * @param statistics instance of GameStatistics
         */
        public StatisticsDisplayAdapter(GameStatistics statistics) {
                this.statistics = statistics;
        }

        /**
         * Adapts statistics to console table format
         * 
         * @return String in ASCII table format
         */
        public String toConsoleTable() {
                StringBuilder table = new StringBuilder();
                table.append("╔════════════════════════════════╗\n");
                table.append("║        GAME STATISTICS         ║\n");
                table.append("╠════════════════════════════════╣\n");
                table.append(String.format("║ Total Shots:      %-12d ║\n", statistics.getStat("disparosTotales")));
                table.append(String.format("║ Hits:             %-12d ║\n", statistics.getStat("aciertos")));
                table.append(String.format("║ Misses:           %-12d ║\n", statistics.getStat("fallos")));
                table.append(String.format("║ Ships Sunk:       %-12d ║\n", statistics.getStat("barcosHundidos")));
                table.append(String.format("║ Precision:        %-11.2f%% ║\n", statistics.getPrecision()));
                table.append("╚════════════════════════════════╝\n");
                return table.toString();
        }

        /**
         * Adapts statistics to JSON format
         * 
         * @return String in JSON format
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
         * Adapts statistics to CSV format
         * 
         * @return String in CSV format
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
         * Adapts statistics to HTML format for web display
         * 
         * @return String in HTML format
         */
        public String toHTML() {
                StringBuilder html = new StringBuilder();
                html.append("<div class='statistics'>\n");
                html.append("  <h2>Game Statistics</h2>\n");
                html.append("  <ul>\n");
                html.append(String.format("    <li>Total Shots: <strong>%d</strong></li>\n",
                                statistics.getStat("disparosTotales")));
                html.append(String.format("    <li>Hits: <strong>%d</strong></li>\n",
                                statistics.getStat("aciertos")));
                html.append(String.format("    <li>Misses: <strong>%d</strong></li>\n",
                                statistics.getStat("fallos")));
                html.append(String.format("    <li>Ships Sunk: <strong>%d</strong></li>\n",
                                statistics.getStat("barcosHundidos")));
                html.append(String.format("    <li>Precision: <strong>%.2f%%</strong></li>\n",
                                statistics.getPrecision()));
                html.append("  </ul>\n");
                html.append("</div>");
                return html.toString();
        }

        /**
         * Adapts to compact format for UI (single line)
         * 
         * @return Compact format String
         */
        public String toCompactDisplay() {
                return String.format("Shots: %d | Hits: %d | Precision: %.1f%%",
                                statistics.getStat("disparosTotales"),
                                statistics.getStat("aciertos"),
                                statistics.getPrecision());
        }
}
