package ivan8or.fluidgui.components;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class Frame {

    private final ItemStack item;
    private final String itemID;
    private final int slot;
    private final int delay;


    public Frame(ItemStack item, int slot, String itemID, int delay, NamespacedKey key) {
        if (item == null)
            throw new NullPointerException("itemstack is null!");
        if (itemID == null)
            throw new NullPointerException("item ID is null!");
        if (key == null)
            throw new NullPointerException("key is null!");

        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta != null) {

            // set the item's id to its persistent metadata for later retrieval
            itemMeta.getPersistentDataContainer().set(key, PersistentDataType.STRING, itemID);
            item.setItemMeta(itemMeta);
        }

        this.item = item;
        this.slot = slot;
        this.itemID = itemID;
        this.delay = delay;
    }

    public Frame(Frame other) {
        this.item = other.item.clone();
        this.slot = other.slot;
        this.delay = other.delay;
        this.itemID = other.itemID;
    }

    public int getDelay() {
        return delay;
    }
    public String getID() {
        return itemID;
    }
    public void draw(Inventory inv) {
        inv.setItem(slot, item);
    }
    public String toString() {
        return item.getType().toString();
    }
}
