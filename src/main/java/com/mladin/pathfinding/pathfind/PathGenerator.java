package com.mladin.pathfinding.pathfind;

import com.mladin.pathfinding.PathfindingPlugin;
import org.apache.commons.lang.time.StopWatch;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class PathGenerator {
    protected PathfindingPlugin pathfindingPlugin;

    protected World world;
    protected int y;

    protected int start_x;
    protected int start_z;

    protected int current_x;
    protected int current_z;

    protected int end_x;
    protected int end_z;

    protected HashMap<String, PathPoint> mapped = new HashMap<>();

    protected int step = 0;
    protected HashMap<Integer, PathPoint> steps = new HashMap<>();

    protected Player player;

    protected BukkitTask task;

    protected boolean active = false;

    protected int speed;
    protected boolean debug;

    public PathGenerator(PathfindingPlugin pathfindingPlugin, World world, int y, int start_x, int start_z, int end_x, int end_z, Player player, int speed, boolean debug) {
        this.pathfindingPlugin = pathfindingPlugin;

        this.world = world;
        this.y = y;

        this.start_x = start_x;
        this.start_z = start_z;

        this.end_x = end_x;
        this.end_z = end_z;

        this.player = player;

        this.speed = speed;
        this.debug = debug;
    }

    public void start() {
        this.current_x = start_x;
        this.current_z = start_z;

        PathPoint pathPoint = new PathPoint(current_x, current_z);
        mapLocation(pathPoint);
        steps.put(step, pathPoint);

        active = true;

        if(speed != 0) {
            this.task = Bukkit.getScheduler().runTaskTimerAsynchronously(this.pathfindingPlugin, new Runnable() {
                @Override
                public void run() {
                    if(active) {
                        getNextLocation();
                    }else {
                        task.cancel();
                    }
                }
            }, 0L, speed);

        }else {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();

            while (active) {
                getNextLocation();
            }

            stopWatch.stop();

            PathfindingPlugin.logToSender(player, "Time elapsed: " + stopWatch.getTime() + "ms.");
            PathfindingPlugin.logToConsole("Time elapsed: " + stopWatch.getTime() + "ms.");
        }
    }

    public void getNextLocation() {
        if(current_x != end_x || current_z != end_z) {
            PathPoint nextLocation = getNextOptimalLocation(current_x, current_z);

            if(nextLocation != null) {
                updatePath(nextLocation);
            }else {
                PathPoint alternative = null;
                while (step != 0 && alternative == null) {
                    PathPoint current = steps.get(step);
                    removePathPointClient(current.getX(), current.getZ());

                    steps.remove(step);
                    step -= 1;

                    PathPoint previous = steps.get(step);
                    alternative = getNextOptimalLocation(previous.getX(), previous.getZ());
                }

                if(alternative != null) {
                    updatePath(alternative);
                }else {
                    this.current_x = start_x;
                    this.current_z = start_z;

                    active = false;

                    PathfindingPlugin.logToSender(player, "Can't find the path.");
                    PathfindingPlugin.logToConsole("Can't find the path.");
                }
            }
        }else {
            active = false;

            PathfindingPlugin.logToSender(player, "Found the path.");
            PathfindingPlugin.logToConsole("Found the path.");

            if(!debug) {
                steps.values().forEach(step -> {
                    updatePathPointClient(step.getX(), step.getZ());
                });
            }
        }
    }

    public PathPoint getNextOptimalLocation(int x, int z) {
        double distance = Double.MAX_VALUE;
        PathPoint optimalLocation = null;

        for (modifier value : modifier.values()) {
            PathPoint pathPoint = value.applyModifier(x, z, world, y);
            if(pathPoint != null && !isMapped(pathPoint)) {
                double pathPointDistance = getDistance(pathPoint.getX(), pathPoint.getZ());
                if(pathPointDistance < distance) {
                    distance = pathPointDistance;
                    optimalLocation = pathPoint;
                }
            }
        }

        return optimalLocation;
    }

    public void mapLocation(PathPoint pathPoint) {
        mapped.put(pathPoint.getX() + "" + pathPoint.getZ(), pathPoint);
    }

    public boolean isMapped(PathPoint pathPoint) {
        return mapped.containsKey(pathPoint.getX() + "" + pathPoint.getZ());
    }

    public enum modifier {
        UP(1,0),
        DOWN(-1,0),

        LEFT(0, -1),
        RIGHT(0, 1),

        DIAGONAL_LEFT_UP(1, -1),
        DIAGONAL_LEFT_DOWN(-1, 1),

        DIAGONAL_RIGHT_UP(1, 1),
        DIAGONAL_RIGHT_DOWN(-1,-1);


        private int modifier_x;
        private int modifier_z;

        modifier(int modifier_x, int modifier_z) {
            this.modifier_x = modifier_x;
            this.modifier_z = modifier_z;
        }

        public PathPoint applyModifier(int x, int z, World world, int y) {
            int modified_x = x + modifier_x;
            int modified_z = z + modifier_z;

            if(world.getBlockAt(modified_x, y, modified_z).getType() != Material.BEDROCK && world.getBlockAt(modified_x, y, modified_z).getType() != Material.AIR) {
                return new PathPoint(modified_x, modified_z);
            }else {
                return null;
            }
        }
    }

    public void updatePath(PathPoint nextLocation) {
        mapLocation(nextLocation);
        steps.put(++step, nextLocation);

        current_x = nextLocation.getX();
        current_z = nextLocation.getZ();

        if(debug) {
            PathfindingPlugin.logToConsole("Current path location is X: " + current_x + ", Z: " + current_z + ".");
            updatePathPointClient(current_x, current_z);
        }
    }

    public void removePathPointClient(int x, int z) {
        player.sendBlockChange(new Location(world, x, y, z, 0,0), Material.GRASS_BLOCK.createBlockData());
    }

    public void updatePathPointClient(int x, int z) {
        player.sendBlockChange(new Location(world, x, y, z, 0,0), Material.EMERALD_BLOCK.createBlockData());
    }

    public double getDistance(int x, int z) {
        return Math.sqrt(Math.pow(end_x - x, 2) + Math.pow(end_z - z, 2));
    }

    public BukkitTask getTask() {
        return this.task;
    }
}
