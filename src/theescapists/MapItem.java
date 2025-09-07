package theescapists;

public class MapItem {

	private int x, y;
	private Item item;  
	private boolean collected = false;

	public MapItem(int x, int y, Item item) {
		this.x = x;
	    this.y = y;
	    this.item = item;
	}

	public int getX() {
		return x; 
	}
	 
	public int getY() {
		return y;
    }
	 
	public Item getItem() {
		return item; 
	}
	 
	public boolean isCollected() { 
		return collected; 
	}
	
	public void collect() {
		collected = true;
	}
	
}
