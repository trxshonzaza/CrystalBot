package trxsh.ontop.crystalbot.bot;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.BlockBreaker;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.trait.LookClose;
import net.citizensnpcs.trait.SneakTrait;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.mcmonkey.sentinel.SentinelTrait;
import trxsh.ontop.crystalbot.Main;
import trxsh.ontop.crystalbot.kit.CrystalKit;
import trxsh.ontop.crystalbot.option.CrystalOptions;
import trxsh.ontop.crystalbot.util.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class Bot {

    public String name = "CrystalBot";
    public UUID id;
    public CrystalKit kit;
    public NPC npc;
    public double health = 20;
    public boolean isEating = false;
    public boolean isCrystaling = false;
    public boolean isDigging = false;
    public boolean isAnchoring = false;
    public boolean removeRequest = false;
    public boolean pearling = false;
    public int digVerbose = 0;
    public int anchorVerbose = 0;
    public int digVerboseMax = 7;
    public int anchorVerboseMax = 10;
    public int gappleStacks = 2;
    public int totemAmount = 17;
    public int checkTotemTicks = 0;
    public int checkTotemMax = 2;
    public List<CrystalOptions> options = new ArrayList<>();

    public long CRYSTAL_PLACE_TICK_DELAY = 2;
    public long CRYSTAL_BREAK_TICK_DELAY = 2;
    public long ANCHOR_PLACE_TICK_DELAY = 2;
    public long ANCHOR_BREAK_TICK_DELAY = 3;

    public long MAIN_LOGIC_LOOP_DELAY = 1;
    public Player owner;

    ItemStack previous = null;

    public Bot(String botName, CrystalKit kit, List<CrystalOptions> options, UUID id) {
        this.name = botName;
        this.kit = kit;
        this.id = id;

        this.options = options;
    }

    public void addOption(CrystalOptions option) {
        options.add(option);
    }

    public List<CrystalOptions> getOptions() {
        return options;
    }

    public NPC getNPC() {
        return npc;
    }

    public HumanEntity getNPCAsEntity() {
        if(npc.getEntity() == null) {
            removeRequest = true;
        }

        return (HumanEntity) npc.getEntity();
    }

    public void createNPC() {
        npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, name);

        SentinelTrait sentinel = npc.getOrAddTrait(SentinelTrait.class);

        sentinel.setInvincible(false);
        sentinel.setHealth(health);
        sentinel.addTarget("players");
        sentinel.removeIgnore("players");
        sentinel.removeAvoid("players");
        sentinel.runaway = false;
        sentinel.allowKnockback = true;
        sentinel.closeChase = true;
        sentinel.rangedChase = true;
        sentinel.retainTarget = true;
        sentinel.protectFromIgnores = false;
        sentinel.range = 50;
        sentinel.reach = 4D;
        sentinel.speed = 1.4D;
        sentinel.damage = 7;
        sentinel.accuracy = 5;
        sentinel.respawnTime = 0;
        //sentinel.retainTarget = true;
        //sentinel.realistic = false;

        // use commands to set the attack speed cuz it wont let me change it through code
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "npc select " + npc.getId());
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "sentinel attackrate 1");

        npc.getOrAddTrait(LookClose.class).lookClose(true);
        npc.getOrAddTrait(LookClose.class).setRange(100);
    }

    public void loadKit() {
        KitUtility.loadKit(kit, this);
    }

    public void spawn(Location location) {
        if(npc != null)
            if(!npc.isSpawned())
                npc.spawn(location);
            else
                throw new RuntimeException("CrystalBot NPC already spawned!");
        else
            throw new NullPointerException("CrystalBot NPC cannot be null!");

        npc.getEntity().setMetadata("CrystalBot", new FixedMetadataValue(Main.Instance, "CrystalBotNPC"));
        npc.getEntity().setMetadata("CrystalBotID", new FixedMetadataValue(Main.Instance, id));
    }

    public void runLogic() {
        try {
            if(getNPC() == null)
                return;

            if(getNPCAsEntity() == null)
                return;

            PlayerInventory botInventory = getNPCAsEntity().getInventory();

            Location botLocation = getNPCAsEntity().getLocation();

            if(isEating || isAnchoring || isCrystaling || isDigging || pearling) {
                getNPC().getOrAddTrait(SentinelTrait.class).addIgnore("players");
                getNPC().getOrAddTrait(SentinelTrait.class).removeTarget("players");
                getNPC().getOrAddTrait(LookClose.class).lookClose(false);
                //Bukkit.broadcastMessage("changed values 5");
            } else {
                //Bukkit.broadcastMessage("changed values");
                getNPC().getOrAddTrait(SentinelTrait.class).runaway = false;
                getNPC().getOrAddTrait(SentinelTrait.class).removeIgnore("players");
                getNPC().getOrAddTrait(SentinelTrait.class).removeAvoid("players");
                getNPC().getOrAddTrait(SentinelTrait.class).addTarget("players");
                getNPC().getOrAddTrait(LookClose.class).lookClose(true);
            }

            if(isEating) {
                getNPC().getOrAddTrait(SentinelTrait.class).speed = .55D;
            } else if(getNPCAsEntity().getSaturation() <= 5)
                getNPC().getOrAddTrait(SentinelTrait.class).speed = 1.5D;
            else
                getNPC().getOrAddTrait(SentinelTrait.class).speed = 1.7D;

            if(botInventory.getItemInMainHand() != null) {
                if(botInventory.getItemInMainHand().getType() != Material.NETHERITE_SWORD && !isEating && !isCrystaling && !isAnchoring && !isDigging) {
                    InventoryUtility.loadSword(this);
                }
            }

            List<Entity> entities = getNPCAsEntity().getNearbyEntities(4, 4, 4);

            checkTotemTicks++;

            if(checkTotemTicks > checkTotemMax) {
                if(botInventory.getItemInOffHand().getType() != Material.TOTEM_OF_UNDYING) {
                    if(botInventory.contains(Material.TOTEM_OF_UNDYING)) {
                        for(ItemStack stack : botInventory) {
                            if(stack != null) {
                                if(stack.getType() == Material.TOTEM_OF_UNDYING) {
                                    stack.setAmount(0);
                                    botInventory.setItemInOffHand(new ItemStack(Material.TOTEM_OF_UNDYING));
                                    break;
                                }
                            }
                        }
                    }
                }

                checkTotemTicks = 0;
            }

            if(getNPCAsEntity().getHealth() < 6 && !isEating) {
                if(previous == null)
                    previous = botInventory.getItemInMainHand();

                for(ItemStack stack : botInventory.getContents()) {
                    if(stack != null && !isEating && !isCrystaling && !isAnchoring && !isDigging) {
                        if(stack.getType() == Material.GOLDEN_APPLE) {
                            isEating = true;
                            //Bukkit.broadcastMessage("begin eating");

                            stack.setAmount(stack.getAmount() - 1);
                            botInventory.setItemInMainHand(stack);

                           // getNPC().getOrAddTrait(SentinelTrait.class).runaway = true;
                            //getNPC().getOrAddTrait(SentinelTrait.class).removeTarget("players");
                            //getNPC().getOrAddTrait(SentinelTrait.class).addIgnore("players");
                            //Bukkit.broadcastMessage("changed values 4");
                            new BukkitRunnable() {
                                int ticks = 0;

                                @Override
                                public void run() {
                                    ticks++;

                                    if(ticks >= 7) {
                                        this.cancel();
                                        //Bukkit.broadcastMessage("stop eating");

                                        getNPCAsEntity().getWorld().playSound(getNPCAsEntity().getLocation(), Sound.ENTITY_PLAYER_BURP, 1f, 1f);

                                        if(previous != null) {
                                            botInventory.setItemInMainHand(previous);
                                            previous = null;
                                        }

                                        float saturation = getNPCAsEntity().getSaturation() + 6;

                                        if(saturation > 20) {
                                            saturation = 20;
                                        }

                                        getNPCAsEntity().setSaturation(saturation);

                                        getNPCAsEntity().addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20 * 4, 4));
                                        getNPCAsEntity().addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 20 * 120, 1));

                                        getNPC().getOrAddTrait(SentinelTrait.class).addTarget("players");
                                        getNPC().getOrAddTrait(SentinelTrait.class).removeIgnore("players");
                                        getNPC().getOrAddTrait(SentinelTrait.class).runaway = false;
                                        //Bukkit.broadcastMessage("changed values 1");

                                        isEating = false;
                                        return;
                                    }

                                    if(isEating && getNPCAsEntity().getHealth() > 6) {
                                        this.cancel();
                                        //Bukkit.broadcastMessage("stop eating 2");

                                        if(previous != null) {
                                            botInventory.setItemInMainHand(previous);
                                            previous = null;
                                        }

                                        getNPC().getOrAddTrait(SentinelTrait.class).addTarget("players");
                                        getNPC().getOrAddTrait(SentinelTrait.class).removeIgnore("players");
                                        getNPC().getOrAddTrait(SentinelTrait.class).runaway = false;
                                        //Bukkit.broadcastMessage("changed values 3");

                                        getNPC().getNavigator().cancelNavigation();

                                        //SentinelTrait sentinel = getNPC().getOrAddTrait(SentinelTrait.class);
                                        //Bukkit.broadcastMessage("DEBUG: Runaway: " + sentinel.runaway + ", Ignore: " + sentinel.allIgnores.targets + ", Targets: " + sentinel.allTargets.targets + ", Avoids: " + sentinel.allAvoids.targets);

                                        isEating = false;
                                        return;
                                    }

                                    getNPCAsEntity().getWorld().playSound(getNPCAsEntity().getLocation(), Sound.ENTITY_GENERIC_EAT, 1f, 1f);
                                }
                            }.runTaskTimer(Main.Instance, 4, 4);

                            break;
                        }
                    }
                }
            }

            for(Entity entity : entities) {
                if(entity instanceof EnderCrystal && !isEating && !isCrystaling && !isAnchoring) {
                    isCrystaling = true;
                    //Bukkit.broadcastMessage("break crystal");

                    Vector look = LookUtility.getDirectionBetween(getNPCAsEntity().getEyeLocation(), entity.getLocation());
                    lookAt(look);

                    CrystalUtility.handleCrystalBreak(this, (EnderCrystal) entity);
                    getNPCAsEntity().swingMainHand();

                    isCrystaling = false;

                    break;
                }

                if(entity instanceof Player) {
                    if(!isEating && !isCrystaling && !isDigging && !isAnchoring) {
                        Location playerLocation = entity.getLocation();
                        Player player = (Player)entity;

                        if(player.isInvisible() || player.getGameMode() == GameMode.SPECTATOR)
                            break;

                        if(LocationUtility.shouldDig(getNPCAsEntity(), entity)) {
                            digVerbose++;
                            if(digVerbose >= digVerboseMax) {
                                digVerbose = 0;

                                if(previous == null)
                                    previous = botInventory.getItemInMainHand();

                                for(ItemStack stack : botInventory.getContents()) {
                                    if(stack != null) {
                                        if(Tag.ITEMS_PICKAXES.isTagged(stack.getType())) {
                                            isDigging = true;
                                            //Bukkit.broadcastMessage("begin dig");

                                            getNPC().getOrAddTrait(SentinelTrait.class).removeTarget("players");
                                            getNPC().getOrAddTrait(SentinelTrait.class).addIgnore("players");
                                            getNPC().getOrAddTrait(SentinelTrait.class).runaway = true;

                                            botInventory.setItemInMainHand(stack);
                                            break;
                                        }
                                    }
                                }

                                if(!isDigging)
                                    break;

                                new BukkitRunnable() {
                                    int ticks = 0;
                                    int breakTicks = 0;
                                    final double finalY = getNPCAsEntity().getLocation().clone().subtract(0, 4, 0).getY();

                                    boolean cancelTask = false;

                                    @Override
                                    public void run() {
                                        if(cancelTask) {
                                            this.cancel();
                                            //Bukkit.broadcastMessage("stop digging");
                                            Bot.this.isDigging = false;

                                            if(previous != null) {
                                                botInventory.setItemInMainHand(previous);
                                                previous = null;
                                            }

                                            return;
                                        }

                                        getNPCAsEntity().swingMainHand();

                                        if(breakTicks >= 3) {
                                            getNPCAsEntity().getWorld().playSound(getNPCAsEntity().getLocation().clone().subtract(0 ,1 ,0), getNPCAsEntity().getLocation().clone().subtract(0 ,1 ,0).getBlock().getBlockData().getSoundGroup().getHitSound(), 1f, 1f);
                                            breakTicks = 0;
                                        }

                                        if(ticks >= 6) {
                                            final Location under = new Location(getNPCAsEntity().getWorld(), getNPCAsEntity().getLocation().getBlockX() + 0.5, getNPCAsEntity().getLocation().getBlockY() - 1, getNPCAsEntity().getLocation().getBlockZ() + 0.5);
                                            getNPCAsEntity().getWorld().playSound(under, under.getBlock().getBlockData().getSoundGroup().getBreakSound(), 1f, 1f);
                                            under.getBlock().breakNaturally();
                                            getNPCAsEntity().teleport(under);
                                            ticks = 0;
                                        }

                                        if(getNPCAsEntity().getLocation().getY() <= finalY) {
                                            new BukkitRunnable() {
                                                Location toPlace = new Location(getNPCAsEntity().getWorld(), getNPCAsEntity().getLocation().getBlockX() + 1, getNPCAsEntity().getLocation().getBlockY() - 1, getNPCAsEntity().getLocation().getBlockZ());
                                                Location stay = getNPCAsEntity().getLocation();

                                                @Override
                                                public void run() {
                                                    if(!toPlace.getWorld().getBlockAt(toPlace).getType().isSolid()) {
                                                        new BukkitRunnable() {
                                                            int ticks1 = 0;

                                                            @Override
                                                            public void run() {
                                                                if(ticks1 >= 5) {
                                                                    this.cancel();
                                                                    //Bukkit.broadcastMessage("stop digging");
                                                                    npc.getOrAddTrait(SneakTrait.class).setSneaking(false);
                                                                    return;
                                                                }

                                                                CrystalUtility.placeObsidianAndCrystal(Bot.this, toPlace);
                                                                getNPCAsEntity().teleport(stay);
                                                                ticks1++;
                                                            }
                                                        }.runTaskTimer(Main.Instance, 7, 7);
                                                    } else {
                                                        toPlace.getBlock().setType(Material.AIR);
                                                        toPlace.add(0, 1, 0).getBlock().setType(Material.AIR);

                                                        new BukkitRunnable() {
                                                            @Override
                                                            public void run() {
                                                                new BukkitRunnable() {
                                                                    int ticks1 = 0;

                                                                    @Override
                                                                    public void run() {
                                                                        if(ticks1 >= 5) {
                                                                            this.cancel();
                                                                            npc.getOrAddTrait(SneakTrait.class).setSneaking(false);
                                                                            return;
                                                                        }

                                                                        CrystalUtility.placeObsidianAndCrystal(Bot.this, toPlace);
                                                                        getNPCAsEntity().teleport(stay);
                                                                        ticks1++;
                                                                    }
                                                                }.runTaskTimer(Main.Instance, 7, 7);
                                                            }
                                                        }.runTaskLater(Main.Instance, CRYSTAL_BREAK_TICK_DELAY + CRYSTAL_PLACE_TICK_DELAY);
                                                    }
                                                }
                                            }.runTaskLater(Main.Instance, CRYSTAL_BREAK_TICK_DELAY + CRYSTAL_PLACE_TICK_DELAY);

                                            getNPC().getOrAddTrait(SentinelTrait.class).addTarget("players");
                                            getNPC().getOrAddTrait(SentinelTrait.class).removeIgnore("players");
                                            getNPC().getOrAddTrait(SentinelTrait.class).runaway = false;

                                            cancelTask = true;
                                        }

                                        ticks++;
                                        breakTicks++;
                                    }
                                }.runTaskTimer(Main.Instance, 1, 1);

                                break;
                            }
                        }

                        if(playerLocation.distance(botLocation) < 4.7f) {
                            List<Location> anchorPlaceable = LocationUtility.getBestAnchorPlaces(getNPCAsEntity(), entity);
                            Location bestPlaceAnchor = null;

                            if(anchorPlaceable.isEmpty()) {
                                //Bukkit.broadcastMessage("CrystalBot with Citizens NPC ID " + getNPC().getId() + " cannot place any anchors!");
                                return;
                            }

                            for(Location loc : anchorPlaceable) {
                                if(bestPlaceAnchor != null) {
                                    if(bestPlaceAnchor.distance(playerLocation) > loc.distance(playerLocation)) {
                                        bestPlaceAnchor = loc;
                                    }
                                } else {
                                    bestPlaceAnchor = loc;
                                }
                            }

                            anchorVerbose++;

                            if(anchorVerbose >= anchorVerboseMax) {
                                isAnchoring = true;
                                //sage("place anchor");
                                CrystalUtility.placeAnchorAndExplode(this, bestPlaceAnchor);

                                anchorVerbose = 0;
                                break;
                            }

                            if(playerLocation.getY() - botLocation.getY() > .3f && playerLocation.getY() - botLocation.getY() < 5f) {
                                List<Location> placeable = LocationUtility.getBestCrystalPlaces(getNPCAsEntity(), entity);
                                Location bestPlaceCrystal = null;

                                if(placeable.isEmpty()) {
                                    //Bukkit.broadcastMessage("CrystalBot with Citizens NPC ID " + getNPC().getId() + " cannot place any crystals!");
                                    return;
                                }

                                for(Location loc : placeable) {
                                    if(bestPlaceCrystal != null) {
                                        if(bestPlaceCrystal.distance(playerLocation) > loc.distance(playerLocation)) {
                                            bestPlaceCrystal = loc;
                                        }
                                    }else {
                                        bestPlaceCrystal = loc;
                                    }
                                }

                                isCrystaling = true;
                                //Bukkit.broadcastMessage("place crystal");
                                CrystalUtility.placeObsidianAndCrystal(this, bestPlaceCrystal);

                                break;
                            }
                        }
                    }
                }
            }

            Entity target = null;
            Location tLoc = npc.getNavigator().getTargetAsLocation();

            if(tLoc != null) {
                for(Entity e : tLoc.getWorld().getNearbyEntities(tLoc, 3, 3, 3)) {
                    if(e instanceof Player) {
                        target = e;
                    }
                }
            }

            if(target == null) {
                Player lookTarget = npc.getOrAddTrait(LookClose.class).getTarget();

                if(lookTarget != null) {
                    target = lookTarget;
                }
            }

            for(ItemStack stack : botInventory.getContents()) {
                if(stack != null) {
                    if(stack.getType() == Material.ENDER_PEARL) {
                        if(target != null && !pearling && !isCrystaling && !isEating && !isAnchoring) {
                            double distance = target.getLocation().distance(getNPCAsEntity().getEyeLocation());

                            if(distance >= 15 || (LocationUtility.shouldClosePearl(getNPCAsEntity()) && distance >= 6)) {
                                Location i = getNPCAsEntity().getEyeLocation();
                                Location f = target.getLocation();

                                RayTraceResult result = getNPCAsEntity().getWorld().rayTraceBlocks(i, LookUtility.getDirectionBetween(i, f), distance);

                                if(result == null) {
                                    pearling = true;
                                    //Bukkit.broadcastMessage("begin pearl");

                                    stack.setAmount(stack.getAmount() - 1);

                                    double[] angles = ProjectileUtility.calculateAngles(i, f.clone().add(0, 5, 0));

                                    Location location = getNPCAsEntity().getLocation();
                                    location.setYaw((float) angles[0]);
                                    location.setPitch((float) angles[1]);

                                    getNPCAsEntity().teleport(location);

                                    EnderPearl pearl = getNPCAsEntity().launchProjectile(EnderPearl.class);

                                    getNPCAsEntity().getWorld().playSound(getNPCAsEntity().getLocation(), Sound.ENTITY_ENDER_PEARL_THROW, 1f, 1f);
                                    getNPCAsEntity().swingMainHand();

                                    new BukkitRunnable() {
                                        @Override
                                        public void run() {
                                            if(pearl.isDead() || !pearl.isValid()) {
                                                pearling = false;
                                                this.cancel();
                                                return;
                                            }

                                            // waiting...
                                        }
                                    }.runTaskTimer(Main.Instance, 1, 1);
                                } else {
                                    //Bukkit.broadcastMessage("there is a obstruction");
                                }
                            }
                        } else {
                            //.broadcastMessage("target is null");
                        }

                        break;
                    }
                }
            }

        }catch(Exception e) {
            Bukkit.broadcastMessage("CrystalBot with Citizens NPC ID " + getNPC().getId() + " could not run logic. Check console for stacktrace. " + e.getMessage());
            e.printStackTrace();
            isCrystaling = false;
            isEating = false;
            isAnchoring = false;
            isDigging = false;
            removeRequest = true;
        }
    }

    public void lookAt(Vector look) {
        Vector direction = look.normalize();

        Location npcLoc = getNPCAsEntity().getEyeLocation();
        Location targetLoc = npcLoc.clone().add(direction).subtract(0, 1, 0);

        //spawnParticleCircle(targetLoc, 0.5, 20, Particle.FLAME); for debug purposes

        npc.faceLocation(targetLoc);
    }

    /*public static void spawnParticleCircle(Location center, double radius, int points, Particle particle) {
        World world = center.getWorld();
        for (int i = 0; i < points; i++) {
            double angle = 2 * Math.PI * i / points;
            double x = center.getX() + radius * Math.cos(angle);
            double z = center.getZ() + radius * Math.sin(angle);
            double y = center.getY();

            world.spawnParticle(particle, new Location(world, x, y, z), 1, 0, 0, 0, 0);
        }
    }*/
}
