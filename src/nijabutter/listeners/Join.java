package nijabutter.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import nijabutter.NameColor;
import nijabutter.commands.utils.Utils;

public class Join implements Listener{
	static NameColor plugin = NameColor.getPlugin(NameColor.class);
	
	public Join(NameColor plugin) {
		Join.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		if (NameColor.colors.contains(p.getName())) {
			String code = NameColor.colors.getString(p.getName());
			p.setDisplayName(Utils.chat(code + p.getDisplayName() + ChatColor.RESET));
			p.setCustomName(Utils.chat(code + p.getDisplayName() + ChatColor.RESET));
			p.setPlayerListName(Utils.chat(code + p.getDisplayName() + ChatColor.RESET));
		}
		
	}
}
