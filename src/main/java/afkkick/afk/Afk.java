package afkkick.afk;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public final class Afk extends JavaPlugin implements Listener {
    public List<Player> players;
    public LocalDateTime time;
    public int increase = 1;//เช็คทุกๆ 1 ชั่วโมง ปรับเปลี่ยนระยะเวลาตรงนี้ **เลขจำนวนเต็มเท่านั้น
    @Override
    public void onEnable() {
        new Kicktime().runTaskTimer(this, 0, 1200);
        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "AFK Kick : Enable");
        getServer().getPluginManager().registerEvents(this, this);
        this.players = new ArrayList<>();
        this.time = LocalDateTime.now();
        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "" + time);
    }

    @Override
    public void onDisable() {

    }

    public Inventory invenCreate(int n){
        Inventory inventory = Bukkit.createInventory(null,9,ChatColor.RED + "AFK Check");
        ItemStack Red = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemStack Green = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
        ItemMeta rmeta = Red.getItemMeta();
        rmeta.setDisplayName(ChatColor.RED + "✘");
        ItemMeta gmeta = Green.getItemMeta();
        gmeta.setDisplayName(ChatColor.GREEN + "✔");
        Red.setItemMeta(rmeta);
        Green.setItemMeta(gmeta);
        for(int i = 0; i <= 8;i++){
            if(i==n){
                inventory.setItem(n,Green);
            }else {
                inventory.setItem(i,Red);
            }

        }
        return inventory;
    }
    @EventHandler
    public void onInvopen(InventoryOpenEvent event){
        Player player = (Player) event.getPlayer();
        if (event.getView().getTitle().equalsIgnoreCase(ChatColor.RED + "AFK Check")) {
            BukkitRunnable rtask = new BukkitRunnable() {
                @Override
                public void run() {
                    if(event.getView().getTitle().equalsIgnoreCase(ChatColor.RED + "AFK Check")) {
                        if(players.contains(player)){

                            player.closeInventory();

                        }
                    }
                }
            };
            rtask.runTaskLater(this,100);
        }
        else {

        }
    }
    @EventHandler
    public void onClickinven(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (event.getCurrentItem() == null) {
            return;
        }
        if (event.getView().getTitle().equalsIgnoreCase(ChatColor.RED + "AFK Check")) {
            if (!(event.getClickedInventory().getHolder() instanceof Player)) {
                if (event.getCurrentItem().getType().equals(Material.LIME_STAINED_GLASS_PANE)) {
                    players.remove(player);
                    event.setCancelled(true);
                    player.closeInventory();
                }
                else if(event.getCurrentItem().getType().equals(Material.RED_STAINED_GLASS_PANE)){

                    event.setCancelled(true);
                    player.closeInventory();

                }
            }
            else {

                event.setCancelled(true);

            }
            player.updateInventory();
        }


    }

    @EventHandler
    public void onCloseinven(InventoryCloseEvent event){
        Player player = (Player) event.getPlayer();
        if(event.getView().getTitle().equals(ChatColor.RED + "AFK Check")){
            if(players.contains(player)){
                players.remove(player);
                SimpleDateFormat formatter = new SimpleDateFormat(" HH:mm:ss dd-MM-yyyy ");
                Date date = new Date(System.currentTimeMillis());
                player.kickPlayer(ChatColor.RED + "คุณถูกเตะเนื่องจาก AFK " + ChatColor.WHITE + "[" + formatter.format(date) + "]" );
            }
            else if (!(players.contains(player))){
                player.sendMessage(ChatColor.AQUA + "Anti AFK" + ChatColor.WHITE + " : " + ChatColor.GREEN + "Success");
            }
        }
    }

    public class Kicktime extends BukkitRunnable {
        @Override
        public void run() {
            LocalDateTime localDateTime = LocalDateTime.now();
            if (time.compareTo(localDateTime) < 0) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player != null) {
                        if (!(players.contains(player))) {
                            Random rng = new Random();
                            int n = rng.nextInt(9);
                            player.openInventory(invenCreate(n));
                            players.add(player);
                        }
                    }
                }
                time = LocalDateTime.now().plusHours(increase);

            }
        }
    }

}
