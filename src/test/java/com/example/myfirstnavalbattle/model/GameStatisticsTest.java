package com.example.myfirstnavalbattle.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the GameStatistics class.
 */
public class GameStatisticsTest {

    private GameStatistics stats;

    @BeforeEach
    public void setUp() {
        GameStatistics.resetInstance();
        stats = GameStatistics.getInstance();
    }

    /**
     * Test assignment 1: Verify Singleton pattern.
     */
    @Test
    public void testSingleton() {
        GameStatistics anotherInstance = GameStatistics.getInstance();
        assertSame(stats, anotherInstance, "Instances should be exactly the same object");
    }

    /**
     * Test assignment 2: Verify incrementStat behavior.
     */
    @Test
    public void testIncrementStat() {
        assertEquals(0, stats.getStat("disparosTotales"));
        stats.incrementStat("disparosTotales");
        assertEquals(1, stats.getStat("disparosTotales"));
        stats.incrementStat("disparosTotales", 5);
        assertEquals(6, stats.getStat("disparosTotales"));
    }

    /**
     * Test assignment 3: Verify precision calculation.
     */
    @Test
    public void testGetPrecision() {
        // 0 shots, should be 0.0
        assertEquals(0.0, stats.getPrecision(), 0.001);

        // 10 shots, 5 hits = 50%
        stats.incrementStat("disparosTotales", 10);
        stats.incrementStat("aciertos", 5);
        assertEquals(50.0, stats.getPrecision(), 0.001);

        // 20 total shots (10+10), 5 total hits (5+0) = 25%
        stats.incrementStat("disparosTotales", 10);
        assertEquals(25.0, stats.getPrecision(), 0.001);
    }
}
