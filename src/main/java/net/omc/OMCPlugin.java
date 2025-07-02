package net.omc;

import net.omc.config.OMCConfig;
import net.omc.handlers.DatabaseHandler;
import net.omc.handlers.LibraryHandler;
import net.omc.handlers.OMCConfigHandler;
import net.omc.handlers.OMCMessageHandler;
import net.omc.managers.HikariManager;
import net.omc.util.Flushable;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Level;

public abstract class OMCPlugin implements Flushable {
    /*
    TODO:
     - LibraryHandler
     - VersionHandler
     */

    public abstract void registerListeners();

    public abstract void registerCommands();

    public abstract JavaPlugin getJavaPlugin();

    public abstract String getPluginName();

    public abstract String getPluginPrefix();

    public abstract File getDataFolder();

    public abstract void saveResource(String fileName, boolean b);

    public abstract OMCConfig getOMCConfig();

    public abstract DatabaseHandler getDatabaseHandler();

    public HikariManager getHikariManager() {
        return getAPI().getHikariManager(this);
    }

    public OMCApi getAPI() {
        return OMCApi.getInstance();
    }

    public LibraryHandler getLibraryHandler() {
        return getAPI().getLibraryHandler(this);
    }

    public OMCMessageHandler getDBMessageHandler() {
        return getAPI().getDBMessageHandler(this);
    }

    public OMCConfigHandler getDBConfigHandler() {
        return getAPI().getDBConfigHandler(this);
    }


    public void error(String message, Throwable throwable) {
        Bukkit.getLogger().log(Level.SEVERE, translate(message), throwable);
        sendConsole(message + " " + throwable.getMessage());
    }

    public void error(String text) {
        sendConsole("&cERROR! Something went wrong: " + text);
    }

    public void sendConsole(String text) {
        sendMessage(Bukkit.getConsoleSender(), text);
    }

    public void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(translate(message));// TODO prefix
    }

    public String translate(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }


    public abstract PluginDescriptionFile getDescription();
}
