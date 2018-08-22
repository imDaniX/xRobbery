package mew.imDaniX.Robbery;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import net.milkbowl.vault.economy.Economy;

public class FactionManager {
private Location mafiaLoc, policeLoc;
private String bankRg, leader;
private long delay, lastRob;
private boolean robbing, adminIgnore, leaderDmg, policeTp;
private World world;

private int mafiaCount, policeCount, timer, robTime;
private List<Double> money;
private List<String> successCmds, failCmds;

private int confTime;
private boolean confEffect;
private int confDamage;

private Economy econ;

private CustomConfig config, messages;

public FactionManager(CustomConfig config, CustomConfig messages, Economy econ) {
	this.config=config; this.messages=messages; this.econ=econ;
	robbing=false; timer=0;
	init();
}

private void init() {
	FileConfiguration cfg=config.getConfig();

	confTime=cfg.getInt("confusion.time");
	confEffect=cfg.getBoolean("confusion.effect");
	confDamage=cfg.getInt("confusion.damage");

	world=Bukkit.getWorld(cfg.getString("region.world")); 
	bankRg=cfg.getString("region.bank_rg");

	policeCount=cfg.getInt("police.count");
	policeTp=cfg.getBoolean("police.do_teleport");

	mafiaCount=cfg.getInt("mafia.count"); 
	leader=cfg.getString("mafia.leader.nick");
	leaderDmg=cfg.getBoolean("mafia.leader.damage");

	robTime=cfg.getInt("robbery.time");
	delay=cfg.getLong("robbery.delay");
	lastRob=cfg.getLong("robbery.last_rob");
	money=cfg.getDoubleList("robbery.money");
	adminIgnore=cfg.getBoolean("robbery.admin_ignore");
	
	successCmds=cfg.getStringList("robbery.commands.success");
	failCmds=cfg.getStringList("robbery.commands.fail");

	mafiaLoc=Utils.stringToLoc(cfg.getString("mafia.loc"));
	policeLoc=Utils.stringToLoc(cfg.getString("police.loc"));
}

//Robbing stuff
public void tickRobbing() {
	timer+=5;
	if(timer>=robTime)
		finishRobbing(true);
}

public boolean isRobbing() {
	return robbing;
}

public void startRobbing() {
	Bukkit.broadcastMessage(getMessage("rob.start"));
	robbing=true; timer=0;
}

public void finishRobbing(boolean type) {
	List<String> cmds=failCmds;
	if(type) {
		double sum=Utils.listRandom(money);
		Bukkit.broadcastMessage(getMessage("rob.finish.success").replace("{money}", sum+""));
		econ.depositPlayer(Bukkit.getOfflinePlayer(findLeader()), sum);
		cmds=successCmds;
	} else
		Bukkit.broadcastMessage(getMessage("rob.finish.failed"));
	ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
	for(String cmd:cmds)
		Bukkit.dispatchCommand(console, cmd);
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

//Find leader
public UUID findLeader(){
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
		this.mafiaLoc = loc;
		config.getConfig().set("mafia.loc", Utils.locToString(loc));
		config.saveConfig();
		return;
	}
	this.policeLoc = loc; 
	config.getConfig().set("police.loc", Utils.locToString(loc));
	config.saveConfig();
}

public void setRg(String bankRg) {
	this.bankRg = bankRg;
	config.getConfig().set("bank_rg", bankRg);
	config.saveConfig();
}

public void setLeader(String leader) {
	this.leader = leader;
	config.getConfig().set("mafia.leader.nick", leader);
	config.saveConfig();
}

public void setLastRob(long lastRob) {
	this.lastRob=lastRob;
	config.getConfig().set("robbery.last_rob", lastRob);
	config.saveConfig();
}

//Some getters
public int getConfDamage() {return confDamage;}
public int getConfTime() {return confTime;}
public boolean doConfEffect() {return confEffect;}
public boolean isPoliceTp() {return policeTp;}
public boolean getLeaderDmg() {return leaderDmg;}
public String getLeader() {return leader;}
public String getBankRg() {return bankRg;}
public String getMessage(String s) {return Utils.clr(messages.getConfig().getString(s));}
public List<String> getHelp(){return messages.getConfig().getStringList("command.help");}
public World getWorld() {return world;}
public Location getLoc(boolean mafia) {return mafia?mafiaLoc:policeLoc;}
}
