package com.mladin.pathfinding;

import com.mladin.pathfinding.commands.PathfindCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class PathfindingPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        PathfindCommand pathfindCommand = new PathfindCommand(this);
        getCommand("pathfind").setExecutor(pathfindCommand);

        logToConsole("Plugin by Mladin Alexandru-Mihai.");
        logToConsole("GitHub: Ml0din");

        logToConsole("Plugin enabled.");
    }

    @Override
    public void onDisable() {
        logToConsole("Plugin disabled.");
    }

    public static void logToConsole(String message) {
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&2Pathfinding&8] &f" + message));
    }

    public static void logToSender(CommandSender commandSender, String message) {
       commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&2Pathfinding&8] &f" + message));
    }

    public static boolean validateInteger(String input) {
        try {
            Integer.parseInt(input);
            return true;
        }catch (Exception exception) {
            return false;
        }
    }

    public static boolean validateBoolean(String input) {
        try {
            Boolean.parseBoolean(input);
            return true;
        }catch (Exception exception) {
            return false;
        }
    }
}
