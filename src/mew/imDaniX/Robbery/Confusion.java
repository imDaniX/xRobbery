package mew.imDaniX.Robbery;

import org.bukkit.Location;
import org.bukkit.util.Vector;

public class Confusion {
private long time;
private Vector velocity;
private boolean first;
private Location loc;

public Confusion(Vector velocity, Location loc) {
	first=true;
	this.loc=loc;
	this.time=Utils.now();
	this.velocity=velocity;
}

public long getTime() {
	return time;
}
public long getTimeUntil(long cooldown) {
	return cooldown+time-Utils.now();
}
public boolean isBlocked(long cooldown) {
	return cooldown>Utils.now()-time;
}
public boolean forFirst() {
	if(first==false) return false;
	first=false; return true;
}
public Location getLocation() {
	return loc;
}
public Vector getVelocity() {
	Vector temp=velocity.clone();
	velocity.setX(0); velocity.setY(0); velocity.setZ(0);
	return temp;
}
}
