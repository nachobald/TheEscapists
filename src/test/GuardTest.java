package test;

import theescapists.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GuardTest {

	@Test
    void testGuardPositionAndDirection() {
        Guard guard = new Guard(3, 4);
        assertEquals(3, guard.getX());
        assertEquals(4, guard.getY());
        guard.setX(5);
        guard.setY(6);
        assertEquals(5, guard.getX());
        assertEquals(6, guard.getY());

        guard.setDirection(Direction.UP);
        assertEquals(Direction.UP, guard.getDirection());
    }

    @Test
    void testToggleFrame() {
        Guard guard = new Guard(0,0);
        int initialFrame = guard.getFrame();
        guard.toggleFrame();
        assertEquals(1 - initialFrame, guard.getFrame());
    }
	
}
