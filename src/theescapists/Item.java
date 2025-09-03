package theescapists;

public class Item {

	private final String name;
	private int durability;
	
	public Item(String name, int durability) {
		this.name = name;
		this.durability = durability;
	}
	
	public String getName() {
		return name;
	}
	
	public int getDurability() {
		return durability;
	}
	
	public void use() {
		if(durability > 0) {
			durability--;
		}
	}
	
	public boolean isBroken() {
		return durability == 0;
	}
}
