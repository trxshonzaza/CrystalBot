package trxsh.ontop.crystalbot.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.RayTraceResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class LocationUtility {

    public static List<Location> getBestCrystalPlaces(LivingEntity e1, Entity e2) {
        List<Location> result = new ArrayList<>();

        Location location1 = e1.getLocation();
        Location location2 = e2.getLocation();

        int radius = 2;

        for (int x = location1.getBlockX() - radius; x <= location1.getBlockX() + radius; x++) {
            for (int z = location1.getBlockZ() - radius; z <= location1.getBlockZ() + radius; z++) {
                for(int y = location1.getBlockY() - radius; y <= location1.getBlockY() + radius; y++) {
                    Location location = new Location(e1.getWorld(), x, y, z);
                    Location below = new Location(e1.getWorld(), x, y - 1, z);
                    Location above = new Location(e1.getWorld(), x, y + 1, z);

                    RayTraceResult ray = e1.getWorld().rayTraceBlocks(e1.getEyeLocation(), LookUtility.getDirectionBetween(location1, location), 0.7);
                    RayTraceResult ray1 = e2.getWorld().rayTraceBlocks(e2.getLocation(), LookUtility.getDirectionBetween(location2, location), 0.7);

                    if (ray != null || ray1 != null) {
                        if (ray != null) {
                            if (Objects.requireNonNull(ray.getHitBlock()).getType() != Material.OBSIDIAN) {
                                if (ray.getHitBlock().getType().isSolid()) {
                                    continue;
                                }
                            }
                        }

                        if (ray1 != null) {
                            if (Objects.requireNonNull(ray1.getHitBlock()).getType() != Material.OBSIDIAN) {
                                if (ray1.getHitBlock().getType().isSolid()) {
                                    continue;
                                }
                            }
                        }
                    }

                    if (!below.getBlock().isLiquid())
                        if (!above.getBlock().isLiquid())
                            if (!location.getBlock().isLiquid())
                                if (location.getBlock().getType().isAir() || location.getBlock().getType() == Material.OBSIDIAN || location.getBlock().getType() == Material.BEDROCK)
                                    if (below.getBlock().getType().isSolid())
                                        if (above.getBlock().getType().isAir())
                                            if (location.distance(location2) < 3.5F && location.distance(location1) > 1.5F && location2.getY() > location1.getY() && !(location.getY() >= location2.getY()))
                                                result.add(location.getBlock().getLocation());
                }
            }
        }

        //final sort to determine wether to use existing obsidian blocks or place new one
        List<Location> obsidianBlocks = new ArrayList<>();

        for(Location l : result) {
            if(l.getBlock().getType() == Material.OBSIDIAN) {
                obsidianBlocks.add(l);
            }
        }

        List<Location> airBlocks = result.stream().filter((l) -> !obsidianBlocks.contains(l)).collect(Collectors.toList());

        double obsidianDistance = obsidianBlocks.stream()
                .mapToDouble(l -> l.distance(location2))
                .average()
                .orElse(Double.MAX_VALUE);

        double airDistance = airBlocks.stream()
                .mapToDouble(l -> l.distance(location2))
                .average()
                .orElse(Double.MAX_VALUE);

        return obsidianDistance <= airDistance ? obsidianBlocks : airBlocks;
    }

    public static List<Location> getBestAnchorPlaces(LivingEntity e1, Entity e2) {
        List<Location> result = new ArrayList<>();

        Location location1 = e1.getLocation();
        Location location2 = e2.getLocation();

        int radius = 2;

        for (int x = location1.getBlockX() - radius; x <= location1.getBlockX() + radius; x++) {
            for (int z = location1.getBlockZ() - radius; z <= location1.getBlockZ() + radius; z++) {
                for(int y = location1.getBlockY() - radius; y <= location1.getBlockY() + radius; y++) {
                    Location location = new Location(e1.getWorld(), x, y, z);
                    Location below = new Location(e1.getWorld(), x, y - 1, z);
                    Location above = new Location(e1.getWorld(), x, y + 1, z);

                    RayTraceResult ray = e1.getWorld().rayTraceBlocks(e1.getEyeLocation(), LookUtility.getDirectionBetween(location1, location), 0.7);
                    RayTraceResult ray1 = e2.getWorld().rayTraceBlocks(e2.getLocation(), LookUtility.getDirectionBetween(location2, location), 0.7);

                    if(ray != null || ray1 != null) {
                        if(ray != null) {
                            if(ray.getHitBlock().getType().isSolid()) {
                                continue;
                            }
                        }

                        if(ray1 != null) {
                            if(ray1.getHitBlock().getType().isSolid()) {
                                continue;
                            }
                        }
                    }

                    if(!below.getBlock().isLiquid())
                        if(!above.getBlock().isLiquid())
                            if(!location.getBlock().isLiquid())
                                if(!location.getBlock().getType().isSolid())
                                    if(below.getBlock().getType().isSolid())
                                        if(above.getBlock().getType().isAir())
                                            if(location.distance(location2) < 3.2F && location.distance(location2) > 1.5F && location.distance(location1) > 1.5F && location.getBlockY() == location2.getBlockY())
                                                result.add(location.getBlock().getLocation());
                }
            }
        }

        return result;
    }

    public static boolean shouldClosePearl(Entity e1) {
        Location location1 = e1.getLocation();

        World world = location1.getWorld();

        int radius = 2;

        int blocksAround = 0;

        for (int x = location1.getBlockX() - radius; x <= location1.getBlockX() + radius; x++) {
            for (int z = location1.getBlockZ() - radius; z <= location1.getBlockZ() + radius; z++) {
                for(int y = location1.getBlockY() - radius; y <= location1.getBlockY() + radius; y++) {
                    Location specified = new Location(world, x, y, z);

                    if(world.getBlockAt(specified).getType().isSolid() && !world.getBlockAt(specified).getType().isAir())
                        blocksAround++;
                }
            }
        }

        return (blocksAround > 53);
    }

    public static boolean shouldDig(Entity e1, Entity e2) {
        Location location1 = e1.getLocation();
        Location location2 = e2.getLocation();

        World world = location1.getWorld();

        int radius = 1;

        boolean isLevelOrLower = false;
        boolean isTrapped = false;
        boolean canDigDown = false;

        int blocksAround = 0;
        int blocksBelow = 0;

        for (int x = location1.getBlockX() - radius; x <= location1.getBlockX() + radius; x++) {
            for (int z = location1.getBlockZ() - radius; z <= location1.getBlockZ() + radius; z++) {
                for(int y = location1.getBlockY() - radius; y <= location1.getBlockY() + radius; y++) {
                    Location specified = new Location(world, x, y, z);

                    if(world.getBlockAt(specified).getType().isSolid())
                        blocksAround++;
                }
            }
        }

        for(int y = location1.getBlockY(); y >= location1.getBlockY() - 4; y--) {
            Location specified = new Location(world, location1.getBlockX(), y, location1.getBlockZ());

            if(world.getBlockAt(specified).getType().isSolid() && world.getBlockAt(specified).getType() != Material.BEDROCK && world.getBlockAt(specified).getType() != Material.OBSIDIAN)
                blocksBelow++;
        }

        if(blocksAround > 25)
            isTrapped = true;

        if(blocksBelow > 3)
            canDigDown = true;

        if(location1.getY() > location2.getY())
            isLevelOrLower = true;

        //Bukkit.broadcastMessage(blocksBelow + ", " + blocksAround + ", " + isLevelOrLower);

        return (isTrapped && canDigDown) || (blocksAround > 15 && isLevelOrLower && canDigDown);
    }
}
