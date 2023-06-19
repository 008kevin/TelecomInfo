package me.kardoskevin07.telecominfo;

import org.bukkit.plugin.java.JavaPlugin;
import com.dbteku.telecom.api.TelecomApi;

public final class TelecomInfo extends JavaPlugin  {

    @Override
    public void onEnable() {
        // Check for telecom plugin
        if (getServer().getPluginManager().getPlugin("Telecom") != null) {
            getServer().getConsoleSender().sendMessage("Telecom plugin not found, disabling...");
            getServer().getPluginManager().disablePlugin(this);
        }


    }


    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
