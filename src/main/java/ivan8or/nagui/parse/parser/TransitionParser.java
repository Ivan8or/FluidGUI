package ivan8or.nagui.parse.parser;

import ivan8or.nagui.components.presentation.Presentation;
import ivan8or.nagui.components.presentation.Slide;
import ivan8or.nagui.components.transition.Frame;
import ivan8or.nagui.components.transition.Transition;
import ivan8or.nagui.parse.depend.DependencyID;
import ivan8or.nagui.parse.depend.DependencyType;
import ivan8or.nagui.parse.depend.ItemAlias;
import org.bukkit.NamespacedKey;
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
    public static Set<DependencyID> getTransitionDependencies(List<Map<String, Object>> transition) {
        Set<DependencyID> requiredAliases = new HashSet<>();

        for (Map<String, Object> yamlComponent : transition) {
            Optional<DependencyID> componentDependencies = getComponentDependency(yamlComponent);
            componentDependencies.ifPresent(requiredAliases::add);
        }
        return requiredAliases;
    }

    // gets the dependency a component uses (if any)
    public static Optional<DependencyID> getComponentDependency(Map<String, Object> yamlComponent) {

        if(yamlComponent.get("type").equals("alias")) {
            DependencyID did = new DependencyID(DependencyType.TRANSITION, (String) yamlComponent.get("alias"));
            return Optional.of(did);
        }

        if(yamlComponent.get("type").equals("item")) {
            return ItemParser.getItemDependency((Map<String, Object>) yamlComponent.get("item"));
        }
        return Optional.empty();
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
        switch ((String) yamlComponent.get("type")) {
            case "item":
                Frame nextFrame = parseSingleItemComponent(yamlComponent, aliases, key);
                singleComponent.add(nextFrame);
                break;
            case "alias":
                List<Frame> aliasFrames = parseAliasedComponent(yamlComponent, aliases);
                singleComponent.addAll(aliasFrames);
                break;
            default:
                break;
        }
        return singleComponent;
    }

    public static List<Frame> parseAliasedComponent(Map<String, Object> contents, AliasParser aliases) {
        String aliasName = (String) contents.get("alias");
        List<Frame> aliasedFrames = aliases
                .getDependency(DependencyType.TRANSITION, aliasName)
                .asTransitionAlias();
        if(aliasedFrames == null) {
            throw new IllegalStateException("Required alias '"+aliasName+"' has not been loaded!");
        }
        return aliasedFrames;
    }

    public static Frame parseSingleItemComponent(Map<String, Object> contents, AliasParser aliases, NamespacedKey key) {

        Map<String, Object> itemData = (Map<String, Object>) contents.getOrDefault("item", new HashMap<>());
        ItemAlias item = ItemParser.parseItem(itemData, aliases);

        int frameSlot = (int) contents.getOrDefault("slot", 1);
        int frameDelay = (int) contents.getOrDefault("delay", 0);
        String frameID = (String) contents.getOrDefault("id", Presentation.EMPTY_ID);

        Frame frame = new Frame(
                item.build(),
                frameSlot,
                frameID,
                frameDelay,
                key);

        return frame;
    }
}
