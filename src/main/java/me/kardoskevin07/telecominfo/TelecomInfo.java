package me.kardoskevin07.telecominfo;

import me.kardoskevin07.telecominfo.commands.infoCommand;
import me.kardoskevin07.telecominfo.commands.listCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class TelecomInfo extends JavaPlugin  {

    private static TelecomInfo instance;

    @Override
    public void onEnable() {
        instance = this;

        FileConfiguration config = this.getConfig();
        saveDefaultConfig();

        getLogger().info("Loaded successfully");
        if (config.getBoolean("debug")) {
            getLogger().warning("!!! Debug mode is enabled !!!");
        }

        this.getCommand("listCarriers").setExecutor(new listCommand());
        this.getCommand("carrierInfo").setExecutor(new infoCommand());
    }

    public static TelecomInfo getInstance() {
        return instance;
    }


    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
