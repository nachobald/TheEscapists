package theescapists;

import java.util.Random;

public class Guard {
	
	private int x, y;
    private Random rand = new Random();
    private Direction dir = Direction.DOWN;
    private int frame = 0;

    public Guard(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() { 
    	return x; 
    }
    
    public int getY() { 
    	return y; 
    }
    
    public void setX(int x) {
    	this.x = x;
    }
    
    public void setY(int y) {
    	this.y = y;
    }

    //muove la guardia casualmente di una casella (evita muri)
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

        if (map[newY][newX] != '#') { //controlla muro
            x = newX;
            y = newY;
        }
    }
    
    public Direction getDirection() { 
    	return dir; 
    }
    
    public void setDirection(Direction d) { 
    	this.dir = d; 
    }

    public int getFrame() {
    	return frame; 
    }
    
    public void toggleFrame() { 
    	frame = 1 - frame; 
    }
    
}
