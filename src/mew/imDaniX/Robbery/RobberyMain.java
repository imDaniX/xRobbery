package mew.imDaniX.Robbery;

import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import mew.imDaniX.Robbery.Listeners.ConfusionListener;
import mew.imDaniX.Robbery.Listeners.PlayerListener;
import net.milkbowl.vault.economy.Economy;

public class RobberyMain extends JavaPlugin {
private static Economy econ;

@Override
public void onEnable() {
	if(setupEconomy()) {
		getLogger().warning("No economy plugin. Disabling xRobbery");
		getServer().getPluginManager().disablePlugin(this);
		return;
	}
	FactionManager fm=new FactionManager(new CustomConfig(this,"config"),new CustomConfig(this,"messages"), econ);
	getCommand("xrobbery").setExecutor(new Commander(fm));
	Bukkit.getPluginManager().registerEvents(new ConfusionListener(this, fm), this);
	Bukkit.getPluginManager().registerEvents(new PlayerListener(this, fm), this);
}

@Override
public void onDisable() {}

private boolean setupEconomy() {
    if (getServer().getPluginManager().getPlugin("Vault") == null) {
        return false;
    }
    RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
    if (rsp == null) {
        return false;
    }
    econ = rsp.getProvider();
    return econ == null;
}

}
