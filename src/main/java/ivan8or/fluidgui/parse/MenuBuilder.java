package ivan8or.fluidgui.parse;

import ivan8or.fluidgui.components.presentation.Presentation;
import ivan8or.fluidgui.components.presentation.Slide;
import ivan8or.fluidgui.parse.parser.AliasParser;
import ivan8or.fluidgui.parse.parser.Parser;
import ivan8or.fluidgui.parse.parser.TransitionParser;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;


public class MenuBuilder extends Parser {

    final private AliasParser aliasParser;
    final private TransitionParser transitionParser;

    final private Plugin plugin;
    final private NamespacedKey key;

    final private List<String> yamlsToParse;

    private Presentation presentation;

    public MenuBuilder(Plugin plugin, NamespacedKey key) {
        this.plugin = plugin;
        this.key = key;

        aliasParser = new AliasParser(plugin, key);
        transitionParser = new TransitionParser(aliasParser, plugin, key);

        yamlsToParse = new ArrayList<>();
    }

    @Override
    public void parse(Map<String, Object> root) {
        String yamlText = yaml.dump(root);
        yamlsToParse.add(yamlText);
    }

    public Presentation getMenu(String title) {
        if(presentation == null)
            presentation = buildPresentation(title);

        return presentation.clone();
    }

    public Presentation buildPresentation(String title) {
        for(String yamlText: yamlsToParse) {
            aliasParser.parseString(yamlText);
        }
        for(String yamlText: yamlsToParse) {
            transitionParser.parseString(yamlText);
        }
        Collection<Slide> allSlides = transitionParser.getSlides();

        Presentation newPresentation = new Presentation(plugin, title);
        for(Slide s: allSlides) {
            newPresentation.addSlide(s);
        }

        return newPresentation;
    }
}
