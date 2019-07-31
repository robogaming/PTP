package io.github.robogaming.ptp;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

public final class PTP extends JavaPlugin implements Listener {
    public static boolean minigameStarted = false;
    public static int playerNum = 0;
    public static int secondsSinceStartup;
    public static Server server;
    public static Player president;
    public static int protectors;
    public static int assasins;
    public static boolean ending = false;

    @Override
    public void onEnable() {
        server = getServer();
        // Plugin startup logic
        getLogger().info("Protect the President is now enabled.");
        getServer().getPluginManager().registerEvents(this, this);

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                if (PTP.ending) {PTP.endGame();}
                if (PTP.playerNum > 0 && !PTP.minigameStarted) {
                    PTP.secondsSinceStartup+= 5;
                    if (secondsSinceStartup >= 30) {
                        server.broadcastMessage(ChatColor.GOLD + "The server was forced to start!");
                        ((PTP)Bukkit.getPluginManager().getPlugin("PTP")).forceStart();
                    } else {
                        server.broadcastMessage(ChatColor.GOLD + "" + secondsSinceStartup + "/30 seconds until forced start.");
                    }
                } else if (PTP.president.getGameMode() == GameMode.SPECTATOR) {
                    server.broadcastMessage(ChatColor.AQUA + "YAY! Assasins win!");
                    ending = true;
                } else if (PTP.assasins == 0) {
                    server.broadcastMessage(ChatColor.AQUA + "YAY! The President and his protectors win!");
                    ending = true;
                }
            }
        }, 0L, 100L);
    }

    public void forceStart() {
        minigameStarted = true;
        ArrayList<Player> players = new ArrayList<>(getServer().getOnlinePlayers());
        for (Player p : players) {
            int index = (int)Math.floor(Math.random() * (players.size()-1));

            if (president == null) {
                president = p;
                p.sendMessage(ChatColor.GOLD + "You are the president. Good luck surviving.");
                giveItem(Material.ENCHANTED_GOLDEN_APPLE, p, 6);
            } else if (protectors <= 4 && assasins >= protectors) {
                p.setCustomName("Protector");
                p.sendMessage(ChatColor.GOLD + "You are a protector. Protect the president, " + president.getName() + ", at all costs!");
                giveItem(Material.DIAMOND_CHESTPLATE, p, 1);
                giveItem(Material.DIAMOND_SWORD, p, 1);
            } else if (assasins <= 10) {
                p.setCustomName("Assasin");
                p.sendMessage(ChatColor.GOLD + "You are an assassin. Loot chests for tools and armor. Kill as many people as you can especially " + president.getName() + ", the president.");
                giveItem(Material.STONE, p, 10);
            }

            p.teleport(new Location(Bukkit.getWorld("world"), (Math.random()*100)-50, 80, (Math.random()*100)-50));

            players.remove(index);
        }
    }

    @EventHandler
    void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        event.setJoinMessage("");

        if (minigameStarted) {
            player.sendMessage(ChatColor.RED + "You were kicked because the game already started.");
            player.performCommand("server hub");
            return;
        }

        playerNum++;

        player.teleport(new Location(Bukkit.getWorld("world"), 0, 100, 0));
        player.setGameMode(GameMode.SURVIVAL);
        player.setHealth(20);
        player.setFoodLevel(20);
        PlayerInventory inv = player.getInventory();
        inv.clear();
        inv.setArmorContents(new ItemStack[4]);

        if (playerNum != 1) {
            getServer().broadcastMessage(ChatColor.GOLD + player.getName() + " joined. " + ChatColor.BLUE + "(" + playerNum + "/15)");
        } else {
            player.sendMessage(ChatColor.GOLD + "You're the only one here. The game will start in 30 seconds or when the server is full.");
        }


        if (playerNum == 15) {
            forceStart();
        }
    }

    @EventHandler
    void onChestOpen(InventoryOpenEvent event) {
        event.setCancelled(true);
        if (Bukkit.getWorld("world").getBlockAt(translate(event.getInventory().getLocation(), 0, 1, 0)).getType() != Material.BEDROCK) {
            Player player = (Player) event.getPlayer();

            double rolls = Math.round(Math.random() * 4);
            for (int i = 0; i < rolls; i++) {
                double sword = Math.round(Math.random() * 7);
                if (sword == 0) giveItem(Material.WOODEN_SWORD, player, 1);
                if (sword == 1) giveItem(Material.WOODEN_AXE, player, 1);
                if (sword == 2) giveItem(Material.IRON_AXE, player, 1);
                if (sword == 3) giveItem(Material.IRON_SWORD, player, 1);
                if (sword == 4) giveItem(Material.GOLDEN_AXE, player, 1);
                if (sword == 5) giveItem(Material.GOLDEN_SWORD, player, 1);

                double protection = Math.round(Math.random() * 12);
                if (protection == 0) giveItem(Material.LEATHER_BOOTS, player, 1);
                if (protection == 1) giveItem(Material.LEATHER_CHESTPLATE, player, 1);
                if (protection == 2) giveItem(Material.LEATHER_LEGGINGS, player, 1);
                if (protection == 3) giveItem(Material.LEATHER_HELMET, player, 1);
                if (protection == 4) giveItem(Material.IRON_BOOTS, player, 1);
                if (protection == 5) giveItem(Material.IRON_CHESTPLATE, player, 1);
                if (protection == 6) giveItem(Material.IRON_LEGGINGS, player, 1);
                if (protection == 7) giveItem(Material.IRON_HELMET, player, 1);
                if (protection == 8) giveItem(Material.GOLDEN_APPLE, player, (int) Math.round(Math.random() * 2));
                if (protection == 9) giveItem(Material.ENDER_PEARL, player, (int) Math.round(Math.random() * 5));
                if (protection == 10) giveItem(Material.BREAD, player, (int) Math.round(Math.random() * 5));
            }

            giveItem(Material.STONE, player, (int) Math.round(Math.random() * 32));

            Bukkit.getWorld("world").getBlockAt(translate(event.getInventory().getLocation(), 0, 1, 0)).setType(Material.BEDROCK);
        }
    }

    @EventHandler
    void onBlockBreak(BlockBreakEvent event) {
        if (event.getBlock().getLocation().getY() >= 80) {
            event.setCancelled(true);
        }

        if (event.getBlock().getType() != Material.STONE) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    void onLeave(PlayerQuitEvent event) {
        playerNum--;
    }

    @EventHandler
    void onHit(EntityDamageEvent event) {
        if (!minigameStarted || event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            event.setCancelled(true);
        } else if (event.getDamage() >= ((Player)event.getEntity()).getHealth()) {
            event.setCancelled(true);
            ((Player)event.getEntity()).setGameMode(GameMode.SPECTATOR);
            switch (((Player)event.getEntity()).getCustomName()) {
                case "Protector":
                    protectors--;
                    break;
                case "Assassin":
                    assasins--;
                    break;
            }
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    Location translate(Location loc, int x, int y, int z) {
        return new Location(loc.getWorld(), loc.getX() + x, loc.getY() + y, loc.getZ() + z);
    }

    void giveItem(Material mat, Player plr, int amt) {
        plr.getInventory().addItem(new ItemStack(mat, amt));
    }

    public static void endGame() {
        ArrayList<Player> players = new ArrayList<>(Bukkit.getServer().getOnlinePlayers());
        for (Player player : players) {
            player.sendMessage(ChatColor.RED + "You were kicked because the game ended.");
            player.performCommand("server hub");
            return;
        }
    }
}
