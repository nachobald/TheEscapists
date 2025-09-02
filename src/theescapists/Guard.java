package theescapists;

import java.util.Random;

public class Guard {
	
	private int x, y;
    private Random rand = new Random();

    public Guard(int startX, int startY) {
        this.x = startX;
        this.y = startY;
    }

    public int getX() { return x; }
    public int getY() { return y; }

    // Muove la guardia casualmente di una casella (evita muri)
    public void move(char[][] map) {
        int newX = x;
        int newY = y;
        int dir = rand.nextInt(4); // 0=w, 1=s, 2=a, 3=d

        switch (dir) {
            case 0 -> newY--;
            case 1 -> newY++;
            case 2 -> newX--;
            case 3 -> newX++;
        }

        if (map[newY][newX] != '#') { // controlla muro
            x = newX;
            y = newY;
        }
    }
    
}
