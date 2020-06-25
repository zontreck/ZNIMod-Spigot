package zeenai.server.colormagic;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import zeenai.server.Main;

public class Colors implements Listener
{
    private final Inventory colorMenu;
    private HashMap<UUID, ChatColor> playerColors = new HashMap<>();
    public Colors(Inventory I){
        colorMenu=I;
    }
    
    @EventHandler
    public void onColorSelect(InventoryClickEvent e){
        Player player = (Player)e.getWhoClicked();
        String dest = "";
        if(e.getView().getTitle().compareToIgnoreCase("Chat Color Menu")==0){
            // in color menu
            e.setCancelled(true);
            dest = "chat";
            player.closeInventory();

        } else if(e.getView().getTitle().compareToIgnoreCase("Prefix Color Menu")==0){
            e.setCancelled(true);
            dest="prefix";
            player.closeInventory();
        } else if(e.getView().getTitle().compareToIgnoreCase("Name Color Menu")==0){
            e.setCancelled(true);
            dest="name";
            player.closeInventory();
        }
        
        if(dest=="")return;
        switch(e.getSlot()){
            case 1:
                updateColor(dest, player, ChatColor.RED, "red");
                break;
            case 2:
                updateColor(dest, player, ChatColor.GOLD, "gold");
                break;
            case 3:
                updateColor(dest, player, ChatColor.YELLOW, "yellow");
                break;
            case 4:
                updateColor(dest, player, ChatColor.DARK_GREEN, "green");
                break;
            case 5:
                updateColor(dest, player, ChatColor.GREEN, "lime");
                break;
            case 6:
                updateColor(dest, player, ChatColor.DARK_AQUA, "cyan");
                break;
            case 7:
                updateColor(dest, player, ChatColor.AQUA, "aqua");
                break;

            case 10:
                updateColor(dest, player, ChatColor.BLUE, "blue");
                break;
            case 11:
                updateColor(dest, player, ChatColor.DARK_PURPLE, "purple");
                break;
            case 12:
                updateColor(dest, player, ChatColor.LIGHT_PURPLE, "pink");
                break;

            case 14:
                updateColor(dest, player, ChatColor.WHITE, "white");
                break;
            case 15:
                updateColor(dest, player, ChatColor.GRAY, "gray");
                break;
            case 16:
                updateColor(dest, player, ChatColor.DARK_GRAY, "dark-gray");
                break;
            case 17:
                updateColor(dest, player, ChatColor.ITALIC, "italic");
                break;
            case 18:
                updateColor(dest, player, ChatColor.MAGIC, "magic");
                break;
            case 19:
                updateColor(dest, player, ChatColor.STRIKETHROUGH, "strikethrough");
                break;
        }
    }


    private void updateColor(String dest, Player player, ChatColor color, String desc) {
        FileConfiguration fc = Main.GetMainInstance().getConfig();
        fc.set(player.getName()+"."+dest+"Color", color.getChar());
        Main.GetMainInstance().saveConfig();
//		playerColors.put(player.getUniqueId(), color);
		player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 20.0F, 20.0F);
		player.sendMessage("§7[§2Colors!§7]§a Your "+dest+" color has been set to " + color+desc + ChatColor.WHITE+ ".");
    }
    

    @EventHandler
	public void onChat(AsyncPlayerChatEvent event) {
		UUID uuid = event.getPlayer().getUniqueId();
        Player p = event.getPlayer();

        
        FileConfiguration fc = Main.GetMainInstance().getConfig();
		if (fc.contains(event.getPlayer().getName()+".prefix") || fc.contains(event.getPlayer().getName()+".chatColor")) {
            //event.setMessage(this.playerColors.get(uuid) + event.getMessage());
            event.setCancelled(true);
            String prefixCode = fc.getString(p.getName()+".prefixColor");
            ChatColor prefixColors = null;
            if(prefixCode!=null)prefixColors = ChatColor.getByChar(prefixCode);
            String prefixText = fc.getString(p.getName()+".prefix");
            if(prefixText != null && prefixColors!=null){
                prefixText = ChatColor.RED+"["+prefixColors+prefixText+ChatColor.RED+"] ";
            } else prefixText = "";

            if(prefixText == "[]") prefixText="";
            
            String chatColorCode = fc.getString(p.getName()+".chatColor");
            if(chatColorCode!=null){
                prefixColors = ChatColor.getByChar(chatColorCode);
                chatColorCode = prefixColors + event.getMessage();
            } else chatColorCode = event.getMessage();
            
            String nameStr;
            ChatColor nameColors;
            String nameColorCode = fc.getString(p.getName()+".nameColor");
            if(nameColorCode != null){
                nameColors = ChatColor.getByChar(nameColorCode);
                nameStr = ChatColor.WHITE+""+nameColors+p.getName()+ChatColor.WHITE+": ";
            }else nameStr = ChatColor.WHITE+p.getName()+": ";

            Bukkit.broadcastMessage(prefixText+nameStr+chatColorCode);
            
		}
    }
    
    
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event) {
		ItemStack[] items = event.getPlayer().getInventory().getContents();
		for (int i = 0; i < items.length; i++) {
			if ((items[i] != null) && (colorMenu.contains(items[i]))) {
				event.getPlayer().getInventory().remove(items[i]);
			}
		}

		items = event.getPlayer().getInventory().getArmorContents();
		for (int i = 0; i < items.length; i++) {
			if ((items[i] != null) && (colorMenu.contains(items[i]))) {
				event.getPlayer().getInventory().remove(items[i]);
			}
		}
    }

}