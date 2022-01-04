package ivan8or.fluidgui.parse;

import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;

public abstract class Parser {

    final protected static Yaml yaml = new Yaml();

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

    public abstract void parse(Map<String, Object> root);

}
