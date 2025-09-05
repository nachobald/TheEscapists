package theescapists;

public abstract class Item {

	protected final String name;
	
	public Item(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public abstract String getDescription();
	
	public abstract void use(GamePanel game);
	
}
