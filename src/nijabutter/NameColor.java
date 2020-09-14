package nijabutter;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import nijabutter.commands.color;
import nijabutter.listeners.Join;

public class NameColor extends JavaPlugin implements Listener{
	
	public static FileConfiguration colors;
	public static File file;
	@Override
	public void onEnable() {
		this.saveDefaultConfig();
		if (!(new File(this.getDataFolder() + File.separator + "colors.yml").exists())) {
			this.saveResource("colors.yml", false);
		}
		file = new File(this.getDataFolder(), "colors.yml");
		colors = YamlConfiguration.loadConfiguration(new File(this.getDataFolder() + File.separator + "colors.yml"));		
		getServer().getPluginManager().registerEvents(this, this);
		new color(this);
		new Join(this);
	}
	
	
	
}
