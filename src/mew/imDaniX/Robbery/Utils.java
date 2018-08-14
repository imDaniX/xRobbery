package mew.imDaniX.Robbery;

import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;

public class Utils {

public static String clr(String s) {
	return ChatColor.translateAlternateColorCodes('&', s);
}

public static Location stringToLoc(String s) {
	String arr[]=s.split("\\,"); Random rnd=new Random();
	return new Location(
			Bukkit.getWorld(arr[0]),
			Integer.parseInt(arr[1])+rnd.nextFloat(),
			Integer.parseInt(arr[2])+rnd.nextFloat(),
			Integer.parseInt(arr[3])+rnd.nextFloat(),
			Float.parseFloat(arr[4]),
			Float.parseFloat(arr[5]));
}

public static String locToString(Location loc) {
	return loc.getWorld().getName()+","+loc.getBlockX()+","+loc.getBlockY()+","+loc.getBlockZ()+","+loc.getYaw()+","+loc.getPitch();
}

public static long now() {
	return (System.currentTimeMillis()/1000);
}

public static <T> T listRandom(List<T> list) {
	Random rnd=new Random();
	return list.get(rnd.nextInt(list.size()));
}
}