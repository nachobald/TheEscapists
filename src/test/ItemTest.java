package test;

import theescapists.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ItemTest {

	@Test
    void testPickaxe() {
        Pickaxe pickaxe = new Pickaxe(3);
        assertEquals("Piccone", pickaxe.getName());
        assertEquals(3, pickaxe.getDurability());

        pickaxe.reduceDurability();
        assertEquals(2, pickaxe.getDurability());
        assertFalse(pickaxe.isBroken());

        pickaxe.reduceDurability();
        pickaxe.reduceDurability();
        assertTrue(pickaxe.isBroken());
    }

    @Test
    void testKey() {
        Key key = new Key();
        assertEquals("Chiave", key.getName());
        assertEquals("Chiave", key.getDescription());
    }
	
}
