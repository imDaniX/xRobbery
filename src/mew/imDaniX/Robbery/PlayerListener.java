package mew.imDaniX.Robbery;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class PlayerListener implements Listener {
private FactionManager fm;
private RegionManager rgManager;

public PlayerListener(JavaPlugin plugin, FactionManager fm) {
	this.fm=fm;
	WorldGuardPlugin worldGuard = (WorldGuardPlugin) plugin.getServer().getPluginManager().getPlugin("WorldGuard");
    this.rgManager=worldGuard.getRegionManager(fm.getWorld());
    start(plugin);
}

@EventHandler
public void onEntityFight(EntityDamageByEntityEvent e) {
	if(fm.getLeaderDmg()) return;
	if(e.getEntity() instanceof Player&&e.getDamager() instanceof Player) {
		Player ent=(Player)e.getEntity(); Entity dmg=e.getDamager();
		if(fm.isLeader(ent)&&fm.isIgnored((Player)ent)==false&&
		   dmg.hasPermission("xrob.mafia")&&fm.isIgnored((Player)dmg)==false)
			e.setCancelled(true);}
}

@EventHandler
public void onPlayerDeath(PlayerDeathEvent e) {
	if(fm.isRobbing()&&fm.isLeader(e.getEntity()))
		fm.finishRobbing(false);
}

@EventHandler
public void onPlayerLeave(PlayerQuitEvent e) {
	if(fm.isRobbing()&&fm.isLeader(e.getPlayer()))
		fm.finishRobbing(false);
}

private void start(JavaPlugin plugin) {
Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() { public void run() { if(fm.isRobbing()) {
		Player p=Bukkit.getPlayer(fm.getLeader());
		ApplicableRegionSet ar=rgManager.getApplicableRegions(p.getLocation());
		boolean notFound=true;
		for(ProtectedRegion pr:ar) {
			if(pr.getId().equals(fm.getBankRg())){
				notFound=false;
				break;
			}
		}
		if(notFound) fm.finishRobbing(false);
	}
	if(fm.isRobbing()) fm.tickRobbing();
}}, 0L, 100L);}
}
