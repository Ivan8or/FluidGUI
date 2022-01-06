package ivan8or.fluidgui.parse.parser;

import ivan8or.fluidgui.parse.depend.DependencyID;
import ivan8or.fluidgui.parse.depend.DependencyType;
import ivan8or.fluidgui.parse.depend.ItemAlias;
import org.bukkit.Material;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ItemParser {


    // gets the item a yaml item extends (if any)
    public static Optional<DependencyID> getItemDependency(Map<String, Object> yamlItem) {

        if(!yamlItem.containsKey("extends"))
            return Optional.empty();

        DependencyID did = new DependencyID(DependencyType.ITEM, (String) yamlItem.get("extends"));
        return Optional.of(did);
    }


    public static ItemAlias parseItem(Map<String, Object> yamlItem, AliasParser items) {

        ItemAlias newItem;

        if(yamlItem.containsKey("extends")) {
            DependencyID did = new DependencyID(DependencyType.ITEM, (String) yamlItem.get("extends"));
            newItem = new ItemAlias(items.getDependency(did).asItemAlias());
        }
        else
            newItem = new ItemAlias();


        if(yamlItem.containsKey("material"))
            newItem.setMaterial(Material.valueOf((String) yamlItem.get("material")));

        if(yamlItem.containsKey("amount"))
            newItem.setAmount((int) yamlItem.get("amount"));

        if(yamlItem.containsKey("displayname"))
            newItem.setName((String) yamlItem.get("displayname"));

        if(yamlItem.containsKey("lore"))
            newItem.setLore((List<String>) yamlItem.get("lore"));

        if(yamlItem.containsKey("glowing"))
            newItem.setGlowing((boolean) yamlItem.get("glowing"));

        return newItem;
    }
}
