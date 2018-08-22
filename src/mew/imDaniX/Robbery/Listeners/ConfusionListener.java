package mew.imDaniX.Robbery.Listeners;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import mew.imDaniX.Robbery.Confusion;
import mew.imDaniX.Robbery.FactionManager;
import net.apcat.simplesit.SimpleSitPlayer;
import net.apcat.simplesit.events.PlayerSitEvent;
import net.apcat.simplesit.events.PlayerStopSittingEvent;

public class ConfusionListener implements Listener {

private FactionManager fm;
private Map<UUID,Confusion> confusions;
private JavaPlugin plugin;

public ConfusionListener(JavaPlugin plugin, FactionManager fm) {
	this.fm=fm; this.plugin=plugin;
	confusions=new HashMap<>();
}

//sit player if damage>=confDamage
@EventHandler
public void onDamageTake(EntityDamageEvent e) {
	if(e.getDamage()>=fm.getConfDamage()&&e.getEntity() instanceof Player) {
		Player p=(Player)e.getEntity();
		confusions.put(p.getUniqueId(), new Confusion(p.getVelocity(),p.getLocation()));
		new SimpleSitPlayer(p).setSitting(true);
	}
}

//if player sat down because of conf - setMessage and set player's velocity to armorstand
@EventHandler
public void onPlayerSit(PlayerSitEvent e) {
	Player p=e.getPlayer();
	if(fm.isIgnored(p)||confusions.containsKey(p.getUniqueId())==false) return;
	Confusion conf=confusions.get(p.getUniqueId());
	if(conf.forFirst()==false) {
		e.setMessage("");
		return;
	}
	e.setMessage(fm.getMessage("confusion.confused").replace("{time}", ""+fm.getConfTime()));
	if(fm.doConfEffect())
		p.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, fm.getConfTime()*20, 0, true, false));
	ArmorStand stand=e.getSeat();
	stand.setVelocity(confusions.get(p.getUniqueId()).getVelocity());
	new BukkitRunnable() {
		@Override
		public void run() {
			SimpleSitPlayer sitP=new SimpleSitPlayer(p);
			if(sitP.isSitting()) 
				sitP.setSitting(false);
		}
	}.runTaskLater(plugin, fm.getConfTime()*20+1);
}

@EventHandler
public void onPlayerStand(PlayerStopSittingEvent e) {
	UUID id=e.getPlayer().getUniqueId();
	if(confusions.containsKey(id)==false) return;
	Confusion conf=confusions.get(id);
	if(conf.isBlocked(fm.getConfTime())) {
		Player p=e.getPlayer();
		Vector eye=p.getEyeLocation().getDirection();
		new BukkitRunnable() {
			@Override
			public void run() {
				p.teleport(conf.getLocation().setDirection(eye));
				new SimpleSitPlayer(p).setSitting(true);
			}
		}.runTaskLater(plugin, 1);
		e.setMessage(fm.getMessage("confusion.cooldown").replace("{time}", ""+conf.getTimeUntil(fm.getConfTime())));
	} else {
		e.setMessage(fm.getMessage("confusion.standup"));
	}
}

@EventHandler
public void onPlayerDeath(PlayerDeathEvent e) {
	UUID id=e.getEntity().getUniqueId();
	if(confusions.containsKey(id))
		confusions.remove(id);
}

}
