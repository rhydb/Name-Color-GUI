package nijabutter.commands;

import java.io.IOException;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import nijabutter.NameColor;
import nijabutter.commands.utils.Utils;

public class color implements CommandExecutor, Listener{
	
	static NameColor plugin = NameColor.getPlugin(NameColor.class);
	//private NameColor plugin;
	private Inventory inv;
	private String target;
	public color(NameColor plugin) {
		this.plugin = plugin;
		plugin.getCommand("color").setExecutor(this);
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	
	@Override 
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		// for setting player color through console
		if (!(sender instanceof Player)) {
			if (args.length != 2) { sender.sendMessage("Usage: /color <player> <color-code>"); return true; }
			if (Bukkit.getPlayer(args[0]) == null) {sender.sendMessage("Player not found"); return true;}
			Bukkit.getPlayer(args[0]).setDisplayName(Utils.chat(args[1]) + Bukkit.getPlayer(args[0]).getDisplayName());
			Bukkit.getPlayer(args[0]).setCustomName(Utils.chat(args[1]) + Bukkit.getPlayer(args[0]).getDisplayName());
			Bukkit.getPlayer(args[0]).setPlayerListName(Utils.chat(args[1]) + Bukkit.getPlayer(args[0]).getDisplayName());
			sender.sendMessage(Utils.chat(plugin.getConfig().getString("set-message").replace("<color>", Utils.chat(args[1]).replace("<player>",  args[0]))));
			return true;
		}
		
		/*if (!(sender.hasPermission("color.use"))) { Utils.chat(plugin.getConfig().getString("invalid-permission-message")); return true; }
		if (!(sender.hasPermission("color.set"))) {
			makeInv((Player) sender, sender.getName());
			return true;
		}
		if (args.length == 1) {
			if (Bukkit.getPlayer(args[0]) == null) {sender.sendMessage(Utils.chat("&cPlayer not found")); return true;}
			makeInv((Player) sender, args[0]);
			target = args[0];
			return true;
		}*/
		makeInv((Player) sender, sender.getName());
		
		return true;
	}
	
	public void makeInv(Player p, String name) {
		String title;
		if (p.getName() == name) {
			title = "Select a new name color";
		} else {
			title = "New name color for: " + name;
		}
		inv = Bukkit.createInventory(null, 27, title);
		addItems(p);
		openInventory(p);
	}
	
	public void addItems(Player p) {
		//inv.addItem((createGuiItem(Material.BLACK_WOOL, plugin.getConfig().getString("colors.&0.name"))));
		for (String color : plugin.getConfig().getConfigurationSection("colors").getKeys(false)) 
		{
			if (!(plugin.getConfig().getBoolean("colors." + color + ".enabled"))) {continue;}
			String name = Utils.chat(plugin.getConfig().getString("colors." + color + ".name"));
			Material material = Material.matchMaterial(plugin.getConfig().getString("colors." + color + ".item"));
			//if (material == null) { material = Material.COBBLESTONE; }
			inv.addItem(createGuiItem(material, name));
		}
	}
	
	protected ItemStack createGuiItem(final Material material, final String name, final String... lore) {
		final ItemStack item = new ItemStack(material, 1);
		final ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);
		meta.setLore(Arrays.asList(lore));
		item.setItemMeta(meta);
		return item;
	}
	
	public void openInventory(final HumanEntity e) {
		e.openInventory(inv);
	}
	
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e)
	{
		if (e.getInventory() != inv) return;
		e.setCancelled(true);
		final ItemStack clickedItem = e.getCurrentItem();
		if (e.getRawSlot() > 27) { return; }
		if (clickedItem == null || clickedItem.getType() == Material.AIR) return;
		final Player p = (Player) e.getWhoClicked();
		Material mat = clickedItem.getType();
		for (String code : plugin.getConfig().getConfigurationSection("colors").getKeys(false)) {
			if (Material.matchMaterial(plugin.getConfig().getString("colors." + code + ".item")) == mat)
			{
				if (target != null) {
					Bukkit.getPlayer(target).setDisplayName(Utils.chat(code) + ChatColor.stripColor(Bukkit.getPlayer(target).getDisplayName()) + ChatColor.RESET);
					Bukkit.getPlayer(target).setCustomName(Utils.chat(code) + ChatColor.stripColor(Bukkit.getPlayer(target).getDisplayName()) + ChatColor.RESET);
					Bukkit.getPlayer(target).setPlayerListName(Utils.chat(code) + ChatColor.stripColor(Bukkit.getPlayer(target).getDisplayName()) + ChatColor.RESET);
					NameColor.colors.set(target, code);
					try {
						NameColor.colors.save(NameColor.file);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					p.sendMessage(Utils.chat(plugin.getConfig().getString("set-message").replace("<color>", code).replace("<player>", target)));
					return;
				}
				p.setDisplayName(Utils.chat(code) + ChatColor.stripColor(p.getDisplayName()) + ChatColor.RESET);
				p.setCustomName(Utils.chat(code) + ChatColor.stripColor(p.getDisplayName()) + ChatColor.RESET);
				p.setPlayerListName(Utils.chat(code) + ChatColor.stripColor(p.getDisplayName()) + ChatColor.RESET);
				p.sendMessage(Utils.chat(plugin.getConfig().getString("change-message").replace("<color>", code)));
				NameColor.colors.set(p.getName(), code);
				try {
					NameColor.colors.save(NameColor.file);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				return;
			}
		}
	}
	
	@EventHandler
    public void onInventoryClick(final InventoryDragEvent e) {
        if (e.getInventory() == inv) {
          e.setCancelled(true);
        }
    }
	

}
