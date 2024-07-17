package com.mladin.pathfinding.commands;

import com.mladin.pathfinding.PathfindingPlugin;
import com.mladin.pathfinding.pathfind.PathGenerator;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PathfindCommand implements CommandExecutor {
    protected PathfindingPlugin pathfindingPlugin;
    public PathfindCommand(PathfindingPlugin pathfindingPlugin) {
        this.pathfindingPlugin = pathfindingPlugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player) {
            Player player = (Player) sender;
            Location location = player.getLocation();

            int start_x = location.getBlockX();
            int start_z = location.getBlockZ();

            if(args.length == 4) {
                if(PathfindingPlugin.validateInteger(args[0]) && PathfindingPlugin.validateInteger(args[1])) {
                    int end_x = Integer.parseInt(args[0]);
                    int end_z = Integer.parseInt(args[1]);

                    if(PathfindingPlugin.validateInteger(args[2]) && PathfindingPlugin.validateBoolean(args[3])) {
                        int speed = Integer.parseInt(args[2]);
                        boolean debug = Boolean.parseBoolean(args[3]);

                        PathfindingPlugin.logToSender(sender, "Generating path from " + start_x + ", " + start_z + " to " + end_x + ", " + end_z + ".");

                        PathGenerator pathGenerator = new PathGenerator(this.pathfindingPlugin, location.getWorld(), location.getWorld().getHighestBlockYAt(location.getBlockX(), location.getBlockZ()), start_x, start_z, end_x, end_z, player, speed, debug);
                        pathGenerator.start();
                    }else {
                        PathfindingPlugin.logToSender(sender, "Invalid settings.");
                    }
                }else {
                    PathfindingPlugin.logToSender(sender, "Invalid coordinates.");
                }
            }else {
                PathfindingPlugin.logToSender(sender, "Invalid arguments.");
            }
        }else {
            PathfindingPlugin.logToSender(sender, "You can't use this command.");
        }


        return true;
    }
}
