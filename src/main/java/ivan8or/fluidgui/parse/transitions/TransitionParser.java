package ivan8or.fluidgui.parse.transitions;

import ivan8or.fluidgui.components.presentation.Presentation;
import ivan8or.fluidgui.components.presentation.Slide;
import ivan8or.fluidgui.components.transition.Frame;
import ivan8or.fluidgui.components.transition.Transition;
import ivan8or.fluidgui.parse.Parser;
import ivan8or.fluidgui.parse.aliases.AliasParser;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class TransitionParser extends Parser {

    final private Map<String, Slide> slides;
    final private Plugin plugin;
    final private NamespacedKey key;
    final private AliasParser aliases;

    public TransitionParser(AliasParser aliases, Plugin plugin, NamespacedKey key) {
        this.slides = new HashMap<>();
        this.aliases = aliases;
        this.plugin = plugin;
        this.key = key;
    }

    public Collection<Slide> getSlides() {
        return slides.values();
    }

    @Override
    public void parse(Map<String, Object> root) {
        if(!root.containsKey("transitions"))
            return;
        Map<String, Object> transitionsList = (Map<String, Object>) root.get("transitions");
        Set<String> entrySlides = transitionsList.keySet();

        for(String entrySlide: entrySlides) {
            Map<String, Object> currentTransitions = (Map<String, Object>) transitionsList.get(entrySlide);
            Set<String> exitSlides = currentTransitions.keySet();

            for(String exitSlide: exitSlides) {
                Map<String, Object> transitionYaml = (Map<String, Object>)currentTransitions.get(exitSlide);
                Transition nextTransition = buildTransition(transitionYaml, exitSlide);
                String transitionItemId = (String) transitionYaml.get("onItemId");

                Slide toAddTo = slides.getOrDefault(entrySlide, new Slide(entrySlide));
                toAddTo.addTransition(transitionItemId, nextTransition);
                slides.put(entrySlide, toAddTo);
            }
        }
    }

    private Transition buildTransition(Map<String, Object> transitionYaml, String goalSlide) {
        List<Map<String, Object>> components = (List<Map<String, Object>>) transitionYaml.get("frames");
        List<Frame> totaledFrames = accumulateTransition(components, aliases, key);
        Transition builtTransition = new Transition(goalSlide, totaledFrames);
        return builtTransition;
    }

    // gets all aliases a transition uses (if any)
    public static Set<String> getTransitionDependencies(List<Map<String, Object>> transition) {
        Set<String> requiredAliases = new HashSet<>();

        for (Map<String, Object> yamlComponent : transition) {
            Optional<String> componentDependencies = getComponentDependency(yamlComponent);
            componentDependencies.ifPresent(requiredAliases::add);
        }
        return requiredAliases;
    }

    // gets the dependency a component uses (if any)
    public static Optional<String> getComponentDependency(Map<String, Object> yamlComponent) {

        if(!yamlComponent.get("type").equals("alias"))
            return Optional.empty();

        Map<String, Object> contents = (Map<String, Object>) yamlComponent.get("contents");
        return Optional.of((String) contents.get("alias"));
    }


    public static List<Frame> accumulateTransition(List<Map<String, Object>> transition, AliasParser aliases, NamespacedKey key) {
        List<Frame> totaledFrames = new ArrayList<>();

        for (Map<String, Object> yamlComponent : transition) {
            List<Frame> singleComponent = parseTransitionComponent(yamlComponent, aliases, key);
            totaledFrames.addAll(singleComponent);
        }

        return totaledFrames;
    }

    public static List<Frame> parseTransitionComponent(Map<String, Object> yamlComponent, AliasParser aliases, NamespacedKey key) {
        List<Frame> singleComponent = new ArrayList<>();
        Map<String, Object> contents = (Map<String, Object>) yamlComponent.get("contents");

        switch ((String) yamlComponent.get("type")) {
            case "item":
                Frame nextFrame = parseSingleFrame(contents, key);
                singleComponent.add(nextFrame);
                break;
            case "alias":
                List<Frame> aliasFrames = parseAliasedFrames(contents, aliases);
                singleComponent.addAll(aliasFrames);
                break;
            default:
                break;
        }
        return singleComponent;
    }

    public static List<Frame> parseAliasedFrames(Map<String, Object> contents, AliasParser aliases) {
        String aliasName = (String) contents.get("alias");
        List<Frame> aliasedFrames = aliases.getAlias(aliasName);
        if(aliasedFrames == null) {
            throw new IllegalStateException("Required alias was not loaded!");
        }
        return aliasedFrames;
    }

    public static Frame parseSingleFrame(Map<String, Object> contents, NamespacedKey key) {

        Material itemMaterial = Material.valueOf(
                (String) contents.getOrDefault("material", Material.STONE.name()));

        int itemCount = Math.max(1, Math.min(64,
                (int) contents.getOrDefault("count", 1)));

        String itemName = (String) contents.getOrDefault("displayname", null);
        itemName = ChatColor.translateAlternateColorCodes('&', itemName);

        ItemStack item = new ItemStack(itemMaterial, itemCount);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(itemName);
            item.setItemMeta(meta);
        }

        int frameSlot = (int) contents.getOrDefault("slot", 1);
        int frameDelay = (int) contents.getOrDefault("delay", 0);
        String frameID = (String) contents.getOrDefault("id", Presentation.EMPTY_ID);

        Frame frame = new Frame(
                item,
                frameSlot,
                frameID,
                frameDelay,
                key);

        return frame;
    }
}
