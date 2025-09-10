package theescapists;

public class Key extends Item {

	public Key() {
        super("Chiave");
    }

    @Override
    public String getDescription() {
       return getName();
    }
    
    @Override
    public void use(GameModel game) {
        System.out.println("Hai la chiave: puoi aprire l’uscita principale!");
    }
	
}
