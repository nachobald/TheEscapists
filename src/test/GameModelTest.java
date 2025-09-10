package test;

import theescapists.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameModelTest {

    private GameModel model;

    @BeforeEach
    public void setup() {
        model = new GameModel();
    }

    @Test
    public void testInitialPlayerPosition() {
        assertEquals(1, model.getPlayerX(), "La posizione iniziale X del giocatore deve essere 1");
        assertEquals(1, model.getPlayerY(), "La posizione iniziale Y del giocatore deve essere 1");
    }

    @Test
    public void testPlayerMovementSimulation() {
        int startX = model.getPlayerX();
        int startY = model.getPlayerY();

        //simulo uno spostamento verso destra
        model.setPlayerPosition(startX + 1, startY);
        assertEquals(startX + 1, model.getPlayerX());
        assertEquals(startY, model.getPlayerY());

        //simulo uno spostamento verso il basso
        model.setPlayerPosition(model.getPlayerX(), startY + 1);
        assertEquals(startX + 1, model.getPlayerX());
        assertEquals(startY + 1, model.getPlayerY());
    }

    @Test
    public void testInventoryAndItemCollection() {
        assertTrue(model.getInventory().isEmpty(), "L'inventario iniziale deve essere vuoto");

        //prendo un oggetto dalla mappa
        MapItem item = model.getMapItems().get(0);
        item.collect();
        model.getInventory().add(item.getItem());

        assertEquals(1, model.getInventory().size(), "L'inventario deve contenere un oggetto dopo la raccolta");
        assertTrue(item.isCollected(), "L'oggetto deve essere segnato come raccolto");
    }

    @Test
    public void testKeyCollection() {
        Key key = new Key();
        model.getInventory().add(key);
        model.setKeyCollected(true);

        assertTrue(model.isKeyCollected(), "La chiave deve essere segnata come raccolta");
    }

    @Test
    public void testWallHealthDecrease() {
        int[][] wallHealth = model.getWallHealth();
        int initialHealth = wallHealth[0][0];
        wallHealth[0][0]--;

        assertEquals(initialHealth - 1, wallHealth[0][0], "La vita del muro deve diminuire correttamente");
    }

    @Test
    public void testMessageDisplay() throws InterruptedException {
        model.setGameMessage("Test messaggio");
        assertTrue(model.isShowMessage(), "Il messaggio deve essere mostrato");
        
        Thread.sleep(1600); //basta che sia maggiore di 1500 ms
        model.updateMessage();
        assertFalse(model.isShowMessage());
    }
}
