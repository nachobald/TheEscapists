package theescapists;

public class Player {

	private int x;
    private int y;
    private Direction dir;
    private int frame = 0;

    public Player(int startX, int startY) {
        this.x = startX;
        this.y = startY;
        this.dir = Direction.DOWN;
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
    
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Direction getDir() { 
    	return dir; 
    }      
    
    public void setDir(Direction dir) { 
    	this.dir = dir; 
    }

    public int getFrame() { 
    	return frame; 
    }
    
    public void toggleFrame() { 
    	frame = 1 - frame; 
    }
	
}
