package theescapists;

public class Spoon extends Item{

	private int durability;

    public Spoon(int durability) {
        super("Cucchiaio");
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
