package com.mcip.smokescreenability;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.projectiles.ProjectileSource;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;

public class SmokeScreenAbility extends JavaPlugin implements Listener {

    double SMOKESHIELD_TIME = 10;
    HashMap<Location, Double> activeShields = new HashMap<Location, Double>();
    ArrayList<Player> players = new ArrayList<Player>();

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("SmokeScreenAbility enabled!");

        saveDefaultConfig();
        SMOKESHIELD_TIME = getConfig().getDouble("smokeshieldSeconds");

        Bukkit.getScheduler().runTaskTimer(this, new Runnable() {
            @Override
            public void run() {
                for (Location loc : activeShields.keySet()) {
                    loc.getWorld().createExplosion(loc, 0);
                    activeShields.put(loc, activeShields.get(loc) - 0.25);
                    if (activeShields.get(loc) <= 0){
                        activeShields.remove(loc);
                    }
                }
            }
        }, 0, 5L);
    }

    @Override
    public void onDisable() {
        getLogger().info("SmokeScreenAbility disabled!");
    }
    @EventHandler
    public void onThrow(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_AIR) {
            Player player = event.getPlayer();
            ItemStack thrown = event.getItem();
            if (thrown.getEnchantments() != null){
                players.add(player);

            }
        }
    }
    @EventHandler
    public void onPearl(ProjectileHitEvent event) {
        if (event.getEntity().getType() == EntityType.ENDER_PEARL) {
            ProjectileSource shooter = event.getEntity().getShooter();
            if (shooter instanceof Player) {
                if (players.contains(event.getEntity())){
                    Location loc = event.getEntity().getLocation();
                    activeShields.put(loc, SMOKESHIELD_TIME);
                } 
            }
        }
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event){
        if (event.getCause() == TeleportCause.ENDER_PEARL){
            if (activeShields.containsKey(event.getTo())){
                event.setCancelled(true);
            }
        }
    }
}
