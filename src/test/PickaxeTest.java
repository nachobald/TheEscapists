package test;

import theescapists.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PickaxeTest {

	@Test
    void testDurabilityReduction() {
        Pickaxe pickaxe = new Pickaxe(3);
        assertEquals(3, pickaxe.getDurability());

        pickaxe.reduceDurability();
        assertEquals(2, pickaxe.getDurability());

        pickaxe.reduceDurability();
        pickaxe.reduceDurability();
        assertTrue(pickaxe.isBroken());
    }

    @Test
    void testUseMethod() {
        GameModel model = new GameModel();
        Pickaxe pickaxe = new Pickaxe(3);
        model.getInventory().add(pickaxe);

        pickaxe.use(model); //chiama digWall nel controller
       
    }

    @Test
    void testDescription() {
        Pickaxe pickaxe = new Pickaxe(3);
        assertEquals("Piccone (3)", pickaxe.getDescription());
    }
	
}
