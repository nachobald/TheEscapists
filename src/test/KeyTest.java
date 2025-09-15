package test;

import theescapists.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class KeyTest {

	@Test
    void testKeyCreation() {
        Key key = new Key();
        assertEquals("Chiave", key.getName());
        assertEquals("Chiave", key.getDescription());
    }

    @Test
    void testUseMethod() {
        GameModel model = new GameModel();
        Key key = new Key();
        key.use(model);
    }
	
}
