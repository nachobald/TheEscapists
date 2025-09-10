package test;

import theescapists.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MapItemTest {
	
	 @Test
	    void testMapItemCreationAndCollect() {
	        Pickaxe pickaxe = new Pickaxe(3);
	        MapItem item = new MapItem(5, 5, pickaxe);

	        assertEquals(5, item.getX());
	        assertEquals(5, item.getY());
	        assertEquals(pickaxe, item.getItem());
	        assertFalse(item.isCollected());

	        item.collect();
	        assertTrue(item.isCollected());
	    }
	
}
