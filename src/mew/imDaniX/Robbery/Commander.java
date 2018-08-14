package mew.imDaniX.Robbery;

import static mew.imDaniX.Robbery.Utils.clr;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Commander implements CommandExecutor {

FactionManager fm;
	
public Commander(FactionManager fm) {
	this.fm=fm;
}

public boolean onCommand(CommandSender s, Command cmd, String label, String args[]){
	if(cmd.getName().equals("xrobbery")){
		if(permError(s,"")) return true;

		if(args.length==0) {
			if(fm.isRobbing()) {
				s.sendMessage(fm.getMessage("rob.already_on"));
				return true;}
			if(fm.isAvailable()==false) {
				s.sendMessage(fm.getMessage("rob.delay_fail").replace("{time}", fm.untilNext()+""));
				return true;}
			if(Bukkit.getConsoleSender()!=s&&fm.isLeader((Player)s)==false) {
				s.sendMessage(fm.getMessage("rob.not_leader"));
				return true;}

			List<Player> mafia=fm.findPlayers(true);
			List<Player> police=fm.findPlayers(false);

			if(fm.isEnough(mafia, true) &&fm.isEnough(police, false)) {
				Location loc=fm.getLoc(true);
				for(Player m:mafia) 
					m.teleport(loc);
				loc=fm.getLoc(false);
				if(fm.isPoliceTp())
					for(Player p:police)
						p.teleport(loc);
				fm.startRobbing();
				return true;
			}
			s.sendMessage(fm.getMessage("rob.count_fail"));
			return true;
		}

		if (args.length==1) {
			if(args[0].equals("help")) {
				if(permError(s,".help")) return true;
				for(String line:fm.getHelp())
					s.sendMessage(clr(line.replace("{label}", label)));
				return true;
			}
			if(args[0].equals("reload")) {
				if(permError(s,".reload")) return true;
				fm.reloadConfigs();
				s.sendMessage(fm.getMessage("command.reload"));
				return true;
			}
			s.sendMessage(fm.getMessage("command.arg_error").replace("{label}", label));
			return true;
		}

		if (args.length==2) {
			if(args[0].equals("setloc")) {
				if(permError(s,".setloc")) return true;
				if(args[1].startsWith("m")) {
					Location loc=((Player)s).getLocation();
					fm.setLoc(loc, true); 
					s.sendMessage(fm.getMessage("command.setloc.mafia").replace("{location}", Utils.locToString(loc)));
				}else
				if(args[1].startsWith("p")) {
					Location loc=((Player)s).getLocation();
					fm.setLoc(loc, false);
					s.sendMessage(fm.getMessage("command.setloc.police").replace("{location}", Utils.locToString(loc)));
				}
				return true;
			}
			if(args[0].equals("setrg")) {
				if(permError(s,".setrg")) return true;
				fm.setRg(args[1]);
				s.sendMessage(fm.getMessage("command.setrg").replace("{region}", args[1]));
				return true;
			}
			if(args[0].equals("leader")) {
				if(permError(s,".leader")) return true;
				fm.setLeader(args[1]);
				s.sendMessage(fm.getMessage("command.leader").replace("{nick}", args[1]));
				return true;
			}
		}
		s.sendMessage(fm.getMessage("command.arg_error").replace("{label}", label));
		return true;
	}
	return false;
}

private boolean permError(CommandSender s, String cmd) {
	if(s.hasPermission("xrob.command"+cmd))
		return false;
	s.sendMessage(fm.getMessage("command.perm_error"));
	return true;
}
}
