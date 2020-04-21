package com.podcrash.api.plugin;

import com.podcrash.api.db.TableOrganizer;
import com.podcrash.api.db.pojos.Rank;
import com.podcrash.api.db.redis.Communicator;
import com.podcrash.api.db.tables.DataTableType;
import com.podcrash.api.db.tables.MapTable;
import com.podcrash.api.db.tables.RanksTable;
import com.podcrash.api.mc.Configurator;
import com.podcrash.api.mc.commands.*;
import com.podcrash.api.mc.damage.DamageQueue;
import com.podcrash.api.mc.economy.EconomyHandler;
import com.podcrash.api.mc.listeners.*;
import com.podcrash.api.mc.tracker.CoordinateTracker;
import com.podcrash.api.mc.tracker.Tracker;
import com.podcrash.api.mc.tracker.VectorTracker;
import com.podcrash.api.mc.world.SpawnWorldSetter;
import com.podcrash.api.mc.world.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandMap;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.scheduler.BukkitTask;
import org.spigotmc.SpigotConfig;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Logger;

public class PodcrashSpigot extends PodcrashPlugin {

    // TODO make all bukkit calls synchronous

    private static PodcrashSpigot INSTANCE;
    public static PodcrashSpigot getInstance() {
        return INSTANCE;
    }

    private final Map<UUID, PermissionAttachment> playerPermissions = new HashMap<>();

    private final ExecutorService service = Executors.newCachedThreadPool();
    private int dQInt;

    private List<Tracker> trackers;
    private CoordinateTracker coordinateTracker;
    private VectorTracker vectorTracker;

    private final Map<String, Configurator> configurators = new HashMap<>();

    private EconomyHandler economyHandler;
    private SpawnWorldSetter worldSetter;

    private CommandMap commandMap;

    @Override
    public ExecutorService getExecutorService() {
        return service;
    }


    private void addTracker(Tracker tracker) {
        trackers.add(tracker);
        tracker.enable();
    }

    public void registerConfigurator(String identifier) {
        getLogger().info("Registering configurator: " + identifier);
        configurators.put(identifier, new Configurator(this, identifier));
    }

    /**
     * This method is used to start setting up the game servers
     */
    public void gameStart() {
        getLogger().info("This Server is a game lobby with code" + Communicator.getCode());
        Future<Void> future = CompletableFuture.allOf(
                setKnockback(),
                registerGameListeners()
        );

        DamageQueue.active = true;
        BukkitTask task = Bukkit.getScheduler().runTaskTimerAsynchronously(this, new DamageQueue(), 0, 0);
        dQInt = task.getTaskId();
        trackers = new ArrayList<>();
        addTracker(coordinateTracker = new CoordinateTracker(this));
        addTracker(vectorTracker = new VectorTracker(this));

        try {
            getLogger().info("Awaiting....");
            future.get();
            getLogger().info("Awaited!");
        }catch (InterruptedException|ExecutionException e) {
            e.printStackTrace();
        }

    }

    public void gameDisable() {
        for(World world : Bukkit.getWorlds()) {
            if (world.getName().equalsIgnoreCase("world")) continue;
            Bukkit.unloadWorld(world, false);
        }
        DamageQueue.active = false;
        Bukkit.getScheduler().cancelTask(dQInt);
        HandlerList.unregisterAll(this);
        for(Tracker tracker : trackers)
            tracker.disable();

    }
    @Override
    public void onEnable() {
        INSTANCE = this;
        getLogger().info("Starting PodcrashSpigot!");
        Pluginizer.setInstance(this);

        Future<Void> future = CompletableFuture.allOf(
                enableWrap(),
                registerCommands(),
                registerListeners());
        //WorldManager.getInstance().loadWorlds();
        try {
            future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        MapTable table = TableOrganizer.getTable(DataTableType.MAPS);
        table.setPlugin(this); //this is required

        economyHandler = new EconomyHandler();
        worldSetter = new SpawnWorldSetter(); // this is a special cookie
        // Fetch private bukkit commandmap by reflections
        try {
            Field commandMapField = getServer().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
            commandMap = (CommandMap) commandMapField.get(getServer());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            getLogger().severe("Failed to load Bukkit commandmap. Disabling plugin.");
            getServer().getPluginManager().disablePlugin(this);
        }
        Communicator.readyGameLobby();
        if (Communicator.isGameLobby())
            gameStart();
    }

    @Override
    public void onDisable() {
        if (Communicator.isGameLobby())
            gameDisable();
        disable();
        WorldManager.getInstance().unloadWorlds();
    }

    @Override
    public void onLoad() {
    }

    public <K extends Tracker> K getTracker(Class<? extends K> trackerClasz) {
        for(Tracker tracker : trackers)
            if (tracker.getClass().equals(trackerClasz))
                return trackerClasz.cast(tracker);
        throw new RuntimeException("tracker is null, shouldn't happen");
    }
    public CoordinateTracker getCoordinateTracker() {
        return coordinateTracker;
    }
    public VectorTracker getVectorTracker() {
        return vectorTracker;
    }


    /**
     * Customized Knockback
     * @return
     */
    private CompletableFuture<Void> setKnockback() {
        return CompletableFuture.runAsync(() -> {
            Logger log = this.getLogger();
            log.info("Kb Numbers: ");
        /*

        getDouble("settings.knockback.friction", knockbackFriction);
        getDouble("settings.knockback.horizontal", knockbackHorizontal);
        getDouble("settings.knockback.vertical", knockbackVertical);
        getDouble("settings.knockback.verticallimit", knockbackVerticalLimit);
        getDouble("settings.knockback.extrahorizontal", knockbackExtraHorizontal);
        getDouble("settings.knockback.extravertical", knockbackExtraVertical);
         */

            SpigotConfig.knockbackFriction = SpigotConfig.config.getDouble("settings.knockback.friction");
            SpigotConfig.knockbackHorizontal = SpigotConfig.config.getDouble("settings.knockback.horizontal");
            SpigotConfig.knockbackVertical = SpigotConfig.config.getDouble("settings.knockback.vertical");
            SpigotConfig.knockbackVerticalLimit = SpigotConfig.config.getDouble("settings.knockback.verticallimit");
            SpigotConfig.knockbackExtraHorizontal = SpigotConfig.config.getDouble("settings.knockback.extrahorizontal");
            SpigotConfig.knockbackExtraVertical = SpigotConfig.config.getDouble("settings.knockback.extravertical");


            log.info("Friction: " + SpigotConfig.knockbackFriction);
            log.info("Horizontal: " + SpigotConfig.knockbackHorizontal);
            log.info("Veritcal: " + SpigotConfig.knockbackVertical);
            log.info("Vertical Limit: " + SpigotConfig.knockbackVerticalLimit);
            log.info("Extra Horizontal: " + SpigotConfig.knockbackExtraHorizontal);
            log.info("Extra Vertical: " + SpigotConfig.knockbackExtraVertical);

        }, getExecutorService());
    }
    private CompletableFuture<Void> registerListeners() {
        new BaseChatListener(this);
        return CompletableFuture.runAsync(() -> {
            new MapMaintainListener(this);
            new PlayerInventoryListener(this);
            new SpigotJoinListener(this);
            new StatusListener(this);
            new MobListeners(this);
            new ActionBlockListener(this);
            new FallDamageHandler(this);
            new MOTDHandler(this);
            new CmdPreprocessHandler(this);
            new GeneralLobbyListener(this);

            // TODO: Add more listeners here..
        });
    }

    private CompletableFuture<Void> registerGameListeners() {
        return CompletableFuture.runAsync(() -> {
            new GameListener(this);
            new GameDamagerConverterListener(this);
            new TrapListener(this);

            // TODO: Add more listeners here..
        });
    }
    private CompletableFuture<Void> registerCommands() {
        return CompletableFuture.runAsync(() -> {
            registerCommand(new SetRoleCommand());
            registerCommand(new AddRoleCommand());
            registerCommand(new BalanceCommand());
            registerCommand(new BuyCommand());
            registerCommand(new ConfirmCommand());
            registerCommand(new TellCommand());
            registerCommand(new EndCommand());
            registerCommand(new PingCommand());
            registerCommand(new StartCommand());
            registerCommand(new ViewCommand());
            registerCommand(new SpecCommand());
            registerCommand(new SetMapCommand());
            registerCommand(new TeamCommand());
            registerCommand(new KillCommand());
            registerCommand(new KnockbackCommand());
            registerCommand(new HitRegCommand());
            registerCommand(new MuteCommand());
        });
}

    public Configurator getConfigurator(String identifier) {
        return configurators.get(identifier);
    }
    public void reloadConfigurators() {
        CompletableFuture.runAsync(() ->
                configurators.values().forEach(Configurator::reloadConfig));
    }

    public EconomyHandler getEconomyHandler() {
        return economyHandler;
    }
    public SpawnWorldSetter getWorldSetter() {
        return worldSetter;
    }

    public void setupPermissions(Player player) {
        PermissionAttachment attachment = player.addAttachment(this);
        this.playerPermissions.put(player.getUniqueId(), attachment);
        permissionsSetter(player);
    }
    private void permissionsSetter(Player player) {
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {

            PermissionAttachment attachment = this.playerPermissions.get(player.getUniqueId());
            String[] disallowedPerms = new String[] {
                    "bukkit.command.reload",
                    "bukkit.command.timings",
                    "bukkit.command.plugins",
                    "bukkit.command.help",
                    "bukkit.command.ban-ip",
                    "bukkit.command.stop",
                    "invicta.map",
                    "invicta.host",
                    "invicta.developer",
                    "invicta.testing",
                    "invicta.mute",
                    "invicta.mod"
            };
            getInstance().getLogger().info("Disabling bad permissions");
            for(String disallowed : disallowedPerms)
                attachment.setPermission(disallowed, false);

            RanksTable table = TableOrganizer.getTable(DataTableType.PERMISSIONS);
            Set<Rank> ranks =  table.getRanksSync(player.getUniqueId());
            for(Rank r : ranks) {
                player.sendMessage(String.format("%s%sYou have been assigned the %s role!", ChatColor.GREEN, ChatColor.BOLD, r.getName()));
                for(String permission : r.getPermissions()) {
                    attachment.setPermission(permission, true);
                }
            }
        });
    }

    public void registerCommand(BukkitCommand command) {
        commandMap.register(command.getLabel(), command);
    }
}
