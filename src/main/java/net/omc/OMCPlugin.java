package net.omc;

import net.omc.config.OMCConfig;
import net.omc.handlers.LibraryHandler;
import net.omc.handlers.OMCConfigHandler;
import net.omc.handlers.OMCDatabaseHandler;
import net.omc.handlers.OMCMessageHandler;
import net.omc.managers.GitManager;
import net.omc.managers.HikariManager;
import net.omc.managers.LicenseManager;
import net.omc.managers.VersionManager;
import net.omc.util.Flushable;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public abstract class OMCPlugin extends JavaPlugin implements Flushable {
    /*
    TODO:
     - VersionHandler
     */

    public abstract void registerListeners();

    public abstract void registerCommands();

    public abstract String getPrefix();

    public abstract String getNetworkPrefix();

    public abstract OMCConfig getOMCConfig();

    public abstract OMCDatabaseHandler getDatabaseHandler();

    public abstract void stopLibraryExecutor();

    public VersionManager getVersionManager() {
        return getAPI().getVersionManager(this);
    }

    public LicenseManager getLicenseManager() {
        return getAPI().loadLicense(this);
    }

    public GitManager getGitManager() {
        return getAPI().getGitManager(this);
    }

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
        sender.sendMessage(translate(getPrefix() + " " + message));
    }

    public String translate(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}
