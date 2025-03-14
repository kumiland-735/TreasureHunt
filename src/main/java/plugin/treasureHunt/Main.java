package plugin.treasureHunt;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import plugin.treasureHunt.command.TreasureHuntCommand;

public final class Main extends JavaPlugin{

  public void onEnable() {
    TreasureHuntCommand treasureHuntCommand = new TreasureHuntCommand();
    Bukkit.getPluginManager().registerEvents(treasureHuntCommand, this);
    getCommand("treasureHunt").setExecutor(treasureHuntCommand);
  }
}

