package me.kardoskevin07.telecominfo;

import me.kardoskevin07.telecominfo.commands.listCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class TelecomInfo extends JavaPlugin  {

    @Override
    public void onEnable() {
        // Check for telecom plugin
        /*
        if (getServer().getPluginManager().getPlugin("Telecom") == null) {
            getLogger().severe("Telecom plugin not found, disabling...");
            getServer().getPluginManager().disablePlugin(this);
        }
         */
        getLogger().info("Loaded successfully");
        this.getCommand("listCarriers").setExecutor(new listCommand());

        // System.out.println(TelecomApi.get().getCarrierByName("Saturn");
    }


    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
