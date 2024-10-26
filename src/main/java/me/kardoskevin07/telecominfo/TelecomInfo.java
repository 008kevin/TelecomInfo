package me.kardoskevin07.telecominfo;

import de.bluecolored.bluemap.api.BlueMapAPI;
import me.kardoskevin07.telecominfo.addons.BlueMapAddon;
import me.kardoskevin07.telecominfo.addons.PapiAddon;
import me.kardoskevin07.telecominfo.commands.InfoCommand;
import me.kardoskevin07.telecominfo.commands.ListCommand;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Optional;

public final class TelecomInfo extends JavaPlugin  {

    private static TelecomInfo instance;

    @Override
    public void onEnable() {
        instance = this;

        FileConfiguration config = this.getConfig();
        saveDefaultConfig();

        // PlaceholderAPI check
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PapiAddon().register();
            getLogger().info("PAPI extension has been enabled");
        } else {
            getLogger().info("PAPI is not installed");
        }

        // BlueMap integration
        if (config.getBoolean("bluemap.enabled")) {
            if (Bukkit.getPluginManager().getPlugin("BlueMap") != null) {
                BlueMapAPI.onEnable(BlueMapAddon::new);
            }
        }

        getLogger().info("Loaded successfully");
        if (config.getBoolean("debug")) {
            getLogger().warning("!!! Debug mode is enabled !!!");
        }

        this.getCommand("listCarriers").setExecutor(new ListCommand());
        this.getCommand("carrierInfo").setExecutor(new InfoCommand());
    }

    public static TelecomInfo getInstance() {
        return instance;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
