package test;

import theescapists.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.awt.Point;
import java.util.List;

public class GameViewTest {

	@Test
    void testInventoryOverlay() {
        GameModel model = new GameModel();

        assertFalse(model.isShowInventoryOverlay());
        model.toggleInventoryOverlay();
        assertTrue(model.isShowInventoryOverlay());
        model.toggleInventoryOverlay();
        assertFalse(model.isShowInventoryOverlay());
    }

    @Test
    void testGameMessage() {
        GameModel model = new GameModel();

        model.setGameMessage("Test messaggio");
        assertTrue(model.isShowMessage());
        assertEquals("Test messaggio", model.getGameMessage());

        //simula il passare del tempo per nascondere il messaggio
        model.updateMessage();
        
    }

    @Test
    void testMapElementsPositions() {
        GameModel model = new GameModel();

        List<Point> trees = model.getTreePositions();
        List<Point> lanterns = model.getLanternPositions();
        List<Point> chests = model.getChestPositions();

        assertNotNull(trees);
        assertNotNull(lanterns);
        assertNotNull(chests);
        assertTrue(trees.size() > 0);
        assertTrue(lanterns.size() > 0);
        assertTrue(chests.size() > 0);
    }
	
}

