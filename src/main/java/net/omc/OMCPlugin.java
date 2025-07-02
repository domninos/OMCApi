package net.omc;

import net.omc.config.OMCConfig;
import net.omc.util.Flushable;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginDescriptionFile;

import java.io.File;
import java.util.logging.Level;

public abstract class OMCPlugin implements Flushable {
    /*
    TODO:
     - LibraryHandler
     - VersionHandler
     - MessageHandler
     - ConfigHandler
     */

    public OMCPlugin() {
        OMCLicense.getInstance().loadLicenseManager(this);
    }

    public abstract void registerListeners();

    public abstract void registerCommands();

    public abstract String getPluginName();

    public abstract String getPluginPrefix();

    public abstract File getDataFolder();
    public abstract void saveResource(String fileName, boolean b);

    public abstract OMCConfig getOMCConfig();

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
