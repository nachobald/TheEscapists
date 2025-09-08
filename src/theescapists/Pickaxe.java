package theescapists;

public class Pickaxe extends Item{

	private int durability;

    public Pickaxe(int durability) {
        super("Piccone");
        this.durability = durability;
    }

    public int getDurability() {
        return durability;
    }

    public void reduceDurability() {
        durability--;
    }

    public boolean isBroken() {
        return durability <= 0;
    }

	@Override
	public String getDescription() {
		return getName() + " (" + durability + ")";
	}

	@Override
	public void use(GamePanel game) {
		game.digWall(this);	
	}
	
}
