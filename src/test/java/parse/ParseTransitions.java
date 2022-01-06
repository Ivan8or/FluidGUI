package parse;

import ivan8or.fluidgui.parse.parser.AliasParser;
import ivan8or.fluidgui.parse.parser.TransitionParser;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.junit.Assert;
import org.junit.Test;
import org.powermock.reflect.Whitebox;

import java.io.File;
import java.io.FileNotFoundException;

import static org.mockito.Mockito.*;


public class ParseTransitions {

    @Test
    public void testParseCommand() throws FileNotFoundException {
        System.out.println("* CommandManagerTest: testParseCommand()");

        Bukkit buk = mock(Bukkit.class);
        Plugin plugin = mock(Plugin.class);
        when(plugin.getName()).thenReturn("myPlugin");
        Server server = mock(Server.class);
        PluginManager pManager = mock(PluginManager.class);
        when(server.getPluginManager()).thenReturn(pManager);
        when(plugin.getServer()).thenReturn(server);

        Player p = mock(Player.class);
        when(server.getPlayer("testPlayer")).thenReturn(p);

        Material mat = mock(Material.class);
        when(mat.name()).thenReturn("honeycomb");
        //If not stubbed
        // Mockito.when(mocked.getVal()).thenCallRealMethod();

        //Set a mocked object in the element of Enum
        Whitebox.setInternalState(Material.class, "HONEYCOMB", mat);

        AliasParser ap = new AliasParser(plugin, new NamespacedKey(plugin, "key"));
        TransitionParser tp = new TransitionParser(ap, plugin, new NamespacedKey(plugin, "key"));

        ap.parseFile(new File("C:\\Users\\ivant\\IdeaProjects\\FluidGUI\\src\\test\\resources\\menu\\aliases.yml"));

        Assert.assertTrue(ap.getTransitionAliasNames().contains("cleartomain"));
    }
}
