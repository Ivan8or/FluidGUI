package ivan8or.fluidgui.parse;

import ivan8or.fluidgui.components.presentation.Presentation;
import ivan8or.fluidgui.components.transition.Frame;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;

public class TransitionAliasParser {

    final private Map<String, List<Frame>> aliases;
    final private static Yaml yaml = new Yaml();

    final private Plugin plugin;

    public TransitionAliasParser(Plugin plugin) {
        this.aliases = new HashMap<>();
        this.plugin = plugin;
    }


    public void parseFile(File f) throws FileNotFoundException {
        Map<String, Object> root = yaml.load(new FileInputStream(f));
        parse(root);
    }

    public void parseStream(InputStream is) {
        Map<String, Object> root = yaml.load(is);
        parse(root);
    }

    public void parseString(String yamlString) {
        Map<String, Object> root = yaml.load(yamlString);
        parse(root);
    }

    public void parse(Map<String, Object> root) {
        if(!root.containsKey("aliases"))
            return;
        Map<String, Object> aliasList = (Map<String, Object>) root.get("aliases");

        if(!aliasList.containsKey("transitions"))
            return;
        Map<String, Object> transitionsList = (Map<String, Object>) aliasList.get("transitions");
        Set<String> aliasNames = transitionsList.keySet();

        for(String aliasName: aliasNames) {
            List<Map<String, Object>> transition = (List<Map<String, Object>>) transitionsList.get(aliasName);
            aliases.put(aliasName, parseTransitionAlias(transition));
        }
    }

    private List<Frame> parseTransitionAlias(List<Map<String, Object>> transition) {
        List<Frame> toReturn = new ArrayList<>();

        for(Map<String, Object> yamlFrame: transition) {
            switch((String) yamlFrame.get("type")) {
                case "item":
                    Map<String, Object> contents = (Map<String, Object>) yamlFrame.get("contents");
                    Frame nextFrame = parseFrame(contents);
                    toReturn.add(nextFrame);
                    break;
                case "alias":
                    break;
                default:
                    break;
            }
        }
        return toReturn;
    }

    private Frame parseFrame(Map<String, Object> contents) {

        Material itemMaterial = Material.valueOf(
                (String) contents.getOrDefault("item", Material.STONE.name()));

        int itemCount = Math.max(1, Math.min(64,
                (int) contents.getOrDefault("count", 1)));

        String itemName = (String) contents.getOrDefault("item-name", null);

        ItemStack item = new ItemStack(itemMaterial, itemCount);
        ItemMeta meta = item.getItemMeta();
        if(meta != null) {
            meta.setDisplayName(itemName);
            item.setItemMeta(meta);
        }

        int frameSlot = (int) contents.getOrDefault("slot", 1);
        int frameDelay = (int) contents.getOrDefault("delay", 0);
        String frameTarget = (String) contents.getOrDefault("target", Presentation.IGNORE_NAME);

        Frame frame = new Frame(
                item,
                frameSlot,
                frameTarget,
                frameDelay,
                new NamespacedKey(plugin,frameTarget));

        return frame;
    }
}
