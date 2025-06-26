package net.omc;

import net.omc.util.Flushable;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public abstract class OMCPlugin extends JavaPlugin implements Flushable {
    /*
    TODO:
     - NearChatConfig
     - LibraryHandler
     - VersionHandler
     - MessageHandler
     - ConfigHandler
     */

    public abstract void registerListeners();

    public abstract void registerCommands();

    public void error(String message, Throwable throwable) {
        getLogger().log(Level.SEVERE, translate(message), throwable);
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

}
