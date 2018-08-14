package mew.imDaniX.Robbery;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import net.milkbowl.vault.economy.Economy;

public class FactionManager {
private Location mafiaLoc, policeLoc;
private String bankRg, leader;
private long delay, lastRob;
private boolean permLeader, robbing, adminIgnore, leaderDmg, policeTp;
private World world;

private int mafiaCount, policeCount, timer, robTime;
private List<Integer> money;

private Economy econ;

private CustomConfig config, messages;

public FactionManager(CustomConfig config, CustomConfig messages, Economy econ) {
	this.config=config; this.messages=messages; this.econ=econ;
	robbing=false; timer=0;
	init();
}

private void init() {
	FileConfiguration cfg=config.getConfig();

	world=Bukkit.getWorld(cfg.getString("region.world")); 
	bankRg=cfg.getString("region.bank_rg");

	policeCount=cfg.getInt("police.count");
	policeTp=cfg.getBoolean("police.do_teleport");

	mafiaCount=cfg.getInt("mafia.count"); 
	leader=cfg.getString("mafia.leader.nick");
	leaderDmg=cfg.getBoolean("mafia.leader.damage");
	permLeader=cfg.getBoolean("mafia.leader.by_permission");

	robTime=cfg.getInt("robbery.time");
	delay=cfg.getLong("robbery.delay");
	lastRob=cfg.getLong("robbery.last_rob");
	money=cfg.getIntegerList("robbery.money");
	adminIgnore=cfg.getBoolean("robbery.admin_ignore");

	mafiaLoc=Utils.stringToLoc(cfg.getString("mafia.loc"));
	policeLoc=Utils.stringToLoc(cfg.getString("police.loc"));
}

//Robbing stuff
public void tickRobbing() {
	timer+=5;
	if(timer>=robTime) finishRobbing(true);
}

public boolean isRobbing() {return robbing;}

public void startRobbing() {
	Bukkit.broadcastMessage(getMessage("rob.start"));
	robbing=true; timer=0;
}

public void finishRobbing(boolean type) {
	if(type) {
		int sum=Utils.listRandom(money);
		Bukkit.broadcastMessage(getMessage("rob.finish.success").replace("{money}", sum+""));
		econ.depositPlayer(Bukkit.getOfflinePlayer(findLeader()), sum);
	} else
	Bukkit.broadcastMessage(getMessage("rob.finish.failed"));
	robbing=false; lastRob=Utils.now();
	config.getConfig().set("robbery.last_rob", lastRob);
	config.saveConfig();
}

//Is there enough people for start
public boolean isEnough(List<Player> players, boolean mafia) {
	int count=mafia?mafiaCount:policeCount;
	return players.size()>=count;
}

//Find online players(police and mafia)
public List<Player> findPlayers(boolean mafia){
	String s=mafia?"mafia":"police";
	List<Player> players=new ArrayList<>();
	for(Player p:Bukkit.getOnlinePlayers())
		if(p.hasPermission("xrob."+s)&&isIgnored(p)==false) players.add(p);
	return players;
}

//Find random leader
public UUID findLeader(){
	List<Player> players=new ArrayList<>();
	if(permLeader) {
		for(Player p:Bukkit.getOnlinePlayers())
			if(p.hasPermission("xrob.mafia.leader")&&isIgnored(p)==false) players.add(p);
		return Utils.listRandom(players).getUniqueId();} else
	return Bukkit.getPlayer(leader).getUniqueId();
}

//Is robbery available now(after delay)
public boolean isAvailable() {
	long now=Utils.now();
	return ((now-lastRob)>(delay));
}

//Time in sec until next robbery
public long untilNext() {
	long now=Utils.now();
	return (delay-now+lastRob);
}

public boolean isLeader(Player p) {
	if(permLeader) return (p.hasPermission("xrob.mafia.leader")&&isIgnored(p)==false);
	return p.getName().equals(leader);
}

public boolean isIgnored(Player p) {
	return (adminIgnore&&p.hasPermission("xrob.ignored"));
}

//Configuration stuff
public void reloadConfigs() {
	config.reloadConfig(); messages.reloadConfig();
	init();
}

public void setLoc(Location loc, boolean mafia) {
	if(mafia) {
		config.getConfig().set("mafia.loc", Utils.locToString(loc));
		this.mafiaLoc = loc; return;
	}
	config.getConfig().set("police.loc", Utils.locToString(loc));
	this.policeLoc = loc; 
	config.saveConfig();
}

public void setRg(String bankRg) {
	config.getConfig().set("bank_rg", bankRg);
	this.bankRg = bankRg;
	config.saveConfig();
}

public void setLeader(String leader) {
	config.getConfig().set("mafia.leader.nick", leader);
	this.leader = leader;
	config.saveConfig();
}

public void setLastRob(long lastRob) {
	config.getConfig().set("robbery.last_rob", lastRob);
	this.lastRob=lastRob;
	config.saveConfig();
}

//Some getters
public boolean isPoliceTp() {return policeTp;}
public boolean getPermLeader() {return permLeader;}
public boolean getLeaderDmg() {return leaderDmg;}
public String getLeader() {return leader;}
public String getBankRg() {return bankRg;}
public String getMessage(String s) {return Utils.clr(messages.getConfig().getString(s));}
public List<String> getHelp(){return messages.getConfig().getStringList("command.help");}
public World getWorld() {return world;}
public Location getLoc(boolean mafia) {return mafia?mafiaLoc:policeLoc;}

}
