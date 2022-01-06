package ivan8or.nagui.parse.depend;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ItemAlias {

    final static private Material defaultMaterial = Material.STONE;
    final static private List<String> defaultLore = null;
    final static private int defaultAmount = 1;
    final static private boolean defaultGlowing = false;

    private Optional<Material> material;
    private Optional<String> name;
    private Optional<List<String>> lore;
    private Optional<Integer> amount;
    private Optional<Boolean> glowing;

    public ItemAlias() {
        this.material = Optional.empty();
        this.name = Optional.empty();
        this.lore = Optional.empty();
        this.amount = Optional.empty();
        this.glowing = Optional.empty();
    }

    public ItemAlias(ItemAlias other) {
        this.material = other.material;
        this.name = other.name;
        this.lore = other.lore;
        this.amount = other.amount;
        this.glowing = other.glowing;
    }

    public void setMaterial(Material newMaterial) {
        this.material = Optional.of(newMaterial);
    }

    public void setName(String newName) {
        this.name = Optional.of(newName);
    }

    public void setLore(List<String> newLore) {
        this.lore = Optional.of(newLore);
    }

    public void setAmount(int newAmount) {
        this.amount = Optional.of(newAmount);
    }

    public void setGlowing(boolean newGlowing) {
        this.glowing = Optional.of(newGlowing);
    }

    public String toString() {
        return "mat: "+material.orElse(defaultMaterial).name()+
                ", name: "+name.orElse("default")+
                ", amount: "+amount.orElse(1);
    }

    public ItemStack build() {

        Material finalMaterial = material.orElse(defaultMaterial);
        String finalName = ChatColor.translateAlternateColorCodes(
                '&', "&f" + name.orElse(finalMaterial.name()));

        List<String> finalLore = lore.orElse(defaultLore);
        if(finalLore != null)
            finalLore = finalLore.stream()
                    .map(i -> ChatColor.translateAlternateColorCodes('&',"&7"+i))
                    .collect(Collectors.toList());

        int finalAmount = Math.max(1, Math.min(64, amount.orElse(defaultAmount)));
        boolean finalGlowing = glowing.orElse(defaultGlowing);

        ItemStack newItem = new ItemStack(finalMaterial, finalAmount);
        ItemMeta meta = newItem.getItemMeta();
        if(meta == null)
            return newItem;

        meta.setDisplayName(finalName);
        meta.setLore(finalLore);

        if(finalGlowing) {
            meta.addEnchant(Enchantment.LURE, 1, false);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        newItem.setItemMeta(meta);
        return newItem;
    }
}