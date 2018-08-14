package mew.imDaniX.Robbery;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class CustomConfig {

private YamlConfiguration yml;
private File file;

CustomConfig(JavaPlugin plg, String name) {
	file = new File(plg.getDataFolder(), name + ".yml");
	file.getParentFile().mkdirs();
	if (!file.exists())
		plg.saveResource(name + ".yml", false);
	yml = YamlConfiguration.loadConfiguration(file);
}

public FileConfiguration getConfig() {
	return yml;
}

public void saveConfig() {
	try {
		yml.save(file);
	} catch (IOException e) {
		e.printStackTrace();
	}
}

public void reloadConfig() {
	try {
		yml = YamlConfiguration.loadConfiguration(file);
	} catch (Exception e) {
		e.printStackTrace();
	}
}

}
