package nijabutter.commands;

import java.io.IOException;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import nijabutter.NameColor;
import nijabutter.commands.utils.Utils;

public class color implements CommandExecutor, Listener{
	
	static NameColor plugin = NameColor.getPlugin(NameColor.class);
	private Inventory inv;
	private String target;
	private String title;
	public color(NameColor plugin) {
		color.plugin = plugin;
		plugin.getCommand("color").setExecutor(this);
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	
	public void setPlayerColor(Player p, String code) {
		p.setDisplayName(Utils.chat(code) + ChatColor.stripColor(p.getDisplayName()) + ChatColor.RESET);
		p.setCustomName(Utils.chat(code) + ChatColor.stripColor(p.getDisplayName()) + ChatColor.RESET);
		p.setPlayerListName(Utils.chat(code) + ChatColor.stripColor(p.getDisplayName()) + ChatColor.RESET);
	}
	
	@Override 
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		// for setting player color through console
		if (!(sender instanceof Player)) {
			if (args.length != 2) { sender.sendMessage("Usage: /color <player> <color-code>"); return true; }
			final Player player = Bukkit.getPlayer(args[0]);
			if (player == null) {sender.sendMessage("Player not found"); return true;}
			String code = args[1];
			setPlayerColor(player, code);
			NameColor.colors.set(target, code); // update the config
			// try save it
			try {
				NameColor.colors.save(NameColor.file);
			} catch (IOException e1) {
				e1.printStackTrace();
				sender.sendMessage(Utils.chat("&cUnable to change name color"));
			}
			sender.sendMessage(Utils.chat(plugin.getConfig().getString("set-message")).replace("<color>", Utils.chat(code)).replace("<player>",  player.getName()));
			return true;
		}
		if (!(sender.hasPermission("color.use"))) { 
			// they cant use this command at all let them know
			sender.sendMessage(Utils.chat(plugin.getConfig().getString("invalid-permission-message")));
			return true;
		}
		target = sender.getName();
		if (args.length == 1) { // msg has a player name argument
			if (!(sender.hasPermission("color.set"))) {
				// they cant set other peoples names
				sender.sendMessage(Utils.chat(plugin.getConfig().getString("invalid-permission-message")));
			}
			else {
				if (Bukkit.getPlayer(args[0]) == null) {
					// invalid player name
					sender.sendMessage(Utils.chat("&cPlayer not found"));
				}
				else {
					// player name is correct
					target = args[0];
					makeInv((Player) sender);
				}
			}
		}
		else {
			// just used /color
			makeInv((Player) sender);
		}		
		return true;
	}
	
	public void makeInv(Player p) {
		title = "New name color for: " + target;
		inv = Bukkit.createInventory(null, 27, title); // to hold all the blocks for the colors
		for (String color : plugin.getConfig().getConfigurationSection("colors").getKeys(false)) 
		{
			// go through each color, make a block for it and add it to the inventory
			if (!(plugin.getConfig().getBoolean("colors." + color + ".enabled"))) {
				// skip disabled colors
				continue;
			}
			String name = Utils.chat(plugin.getConfig().getString("colors." + color + ".name")); // get the name for the color/block
			Material material = Material.matchMaterial(plugin.getConfig().getString("colors." + color + ".item")); // make the block that is says in the config
			if (material == null) { 
				// if there was an error getting the material just make it cobblestone
				material = Material.COBBLESTONE;
			}
			inv.addItem(createGuiItem(material, name)); // create an ItemStack from the details and add it to the inventory
		}
		p.openInventory(inv); // make the player open the new inventory
	}
	
	protected ItemStack createGuiItem(final Material material, final String name, final String... lore) {
		final ItemStack item = new ItemStack(material, 1);
		final ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);
		meta.setLore(Arrays.asList(lore));
		item.setItemMeta(meta);
		return item;
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e)
	{
		if (e.getView().getTitle() != title) return;
		e.setCancelled(true);
		final ItemStack clickedItem = e.getCurrentItem();
		if (e.getRawSlot() > 27) { return; } // skip if they clicked their inventory
		if (clickedItem == null || clickedItem.getType() == Material.AIR) return; // skip if they didnt click anything
		final Player clicker = (Player) e.getWhoClicked();
		Material mat = clickedItem.getType();
		for (String code : plugin.getConfig().getConfigurationSection("colors").getKeys(false)) {
			if (Material.matchMaterial(plugin.getConfig().getString("colors." + code + ".item")) == mat)
			{
				Player p = Bukkit.getPlayer(target);
				if (p == null) {
					// player has left or something went wrong
					clicker.sendMessage(Utils.chat("&cUnable to change name color"));
				}
				else {
					setPlayerColor(p, code);
					NameColor.colors.set(target, code); // update the config
					// try save it
					try {
						NameColor.colors.save(NameColor.file);
					} catch (IOException e1) {
						e1.printStackTrace();
						clicker.sendMessage(Utils.chat("&cUnable to change name color"));
					}
					clicker.sendMessage(Utils.chat(plugin.getConfig().getString("set-message").replace("<color>", code).replace("<player>", target)));

				}
			}
		}
	}
}
