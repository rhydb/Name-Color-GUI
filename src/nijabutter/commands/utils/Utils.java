package nijabutter.commands.utils;

import org.bukkit.ChatColor;

public class Utils {
	public static String chat (String s) {
		return ChatColor.translateAlternateColorCodes('&', s);
		// convert &7Colored to ChatColor.GRAY + "Colored"
		// for use when using the colored string in minecraft itself
	}
}
