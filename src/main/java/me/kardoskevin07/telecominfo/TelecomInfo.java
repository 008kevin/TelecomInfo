package me.kardoskevin07.telecominfo;

import me.kardoskevin07.telecominfo.commands.infoCommand;
import me.kardoskevin07.telecominfo.commands.listCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class TelecomInfo extends JavaPlugin  {

    @Override
    public void onEnable() {
        getLogger().info("Loaded successfully");

        this.getCommand("listCarriers").setExecutor(new listCommand());
        this.getCommand("carrierInfo").setExecutor(new infoCommand());
    }


    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
