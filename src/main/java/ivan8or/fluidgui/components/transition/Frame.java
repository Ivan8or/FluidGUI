package ivan8or.fluidgui.components.transition;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class Frame {

    private final ItemStack item;
    private final String goalSlide;
    private final int slot;
    private final int delay;


    public Frame(ItemStack item, int slot, String goalSlide, int delay, NamespacedKey key) {
        if (item == null)
            throw new NullPointerException("itemstack is null!");
        if (goalSlide == null)
            throw new NullPointerException("goal slide is null!");
        if (key == null)
            throw new NullPointerException("key is null!");

        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta != null) {

            // set the item's id to its persistent metadata for later retrieval
            itemMeta.getPersistentDataContainer().set(key, PersistentDataType.STRING, goalSlide);
            item.setItemMeta(itemMeta);
        }

        this.item = item;
        this.slot = slot;
        this.goalSlide = goalSlide;
        this.delay = delay;
    }

    public Frame(Frame other) {
        this.item = other.item.clone();
        this.slot = other.slot;
        this.delay = other.delay;
        this.goalSlide = other.goalSlide;
    }

    public int getDelay() {
        return delay;
    }
    public String getID() {
        return goalSlide;
    }
    public void draw(Inventory inv) {
        inv.setItem(slot, item);
    }
    public String toString() {
        return item.getType().toString();
    }
}
