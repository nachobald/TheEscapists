package theescapists;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Inventory {

	private final List<Item> items;
    private final int capacity;
    
    public Inventory() {
        this(10); //capacità di default
    }
    
    public Inventory(int capacity) {
        this.items = new ArrayList<>();
        this.capacity = capacity;
    }
    
    public boolean add(Item item) {
        if (isFull()) return false;
        items.add(item);
        return true;
    }
    
    public boolean remove(Item item) {
        return items.remove(item);
    }
    
    public boolean contains(Class<? extends Item> itemType) {
        return items.stream().anyMatch(itemType::isInstance);
    }
    
    public <T extends Item> Optional<T> getFirst(Class<T> itemType) {
        return items.stream()
                   .filter(itemType::isInstance)
                   .map(itemType::cast)
                   .findFirst();
    }
    
    public boolean isEmpty() { 
    	return items.isEmpty(); 
    }
    
    public List<Item> getItems() {
        return new ArrayList<>(items); //return copy
    }
    
    public boolean isFull() {
        return items.size() >= capacity;
    }
    
    public int size() {
        return items.size();
    }
    
    public void clear() {
        items.clear();
    }
    
    //per l'UI
    public List<Item> getTools() {
        return getItemsByType(Pickaxe.class);
    }
    
    public List<Item> getKeys() {
        return getItemsByType(Key.class);
    }
    
    private List<Item> getItemsByType(Class<? extends Item> type) {
        return items.stream()
                   .filter(type::isInstance)
                   .collect(Collectors.toList());
    }
	
}
