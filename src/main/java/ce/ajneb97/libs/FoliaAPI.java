package ce.ajneb97.libs;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

public class FoliaAPI {
	private static final Map<String, Method> cachedMethods = new HashMap<>();
    private static final BukkitScheduler bS = Bukkit.getScheduler();
    private static final Object globalRegionScheduler = getGlobalRegionScheduler();
    private static final Object regionScheduler = getRegionScheduler();

    static { cacheMethods(); }
    private static Method getMethod(Class<?> clazz, String methodName, Class<?>... parameterTypes) { try { return clazz.getMethod(methodName, parameterTypes); } catch (Exception e) { return null; } }

    private static void cacheMethods() {
        if (globalRegionScheduler != null) {
            Method runAtFixedRateMethod = getMethod(globalRegionScheduler.getClass(), "runAtFixedRate", Plugin.class, Consumer.class, long.class, long.class);
            if (runAtFixedRateMethod != null) {
                cachedMethods.put("globalRegionScheduler.runAtFixedRate", runAtFixedRateMethod);
            }
    
            Method runMethod = getMethod(globalRegionScheduler.getClass(), "run", Plugin.class, Consumer.class);
            if (runMethod != null) {
                cachedMethods.put("globalRegionScheduler.run", runMethod);
            }
        }
        if (regionScheduler != null) {
            Method executeMethod = getMethod(regionScheduler.getClass(), "execute", Plugin.class, World.class, int.class, int.class, Runnable.class);
            if (executeMethod != null) {
                cachedMethods.put("regionScheduler.execute", executeMethod);
            }
    
            Method executeLocationMethod = getMethod(regionScheduler.getClass(), "execute", Plugin.class, Location.class, Runnable.class);
            if (executeLocationMethod != null) {
                cachedMethods.put("regionScheduler.executeLocation", executeLocationMethod);
            }
    
            Method runAtFixedRateMethod = getMethod(regionScheduler.getClass(), "runAtFixedRate", Plugin.class, Location.class, Consumer.class, long.class, long.class);
            if (runAtFixedRateMethod != null) {
                cachedMethods.put("regionScheduler.runAtFixedRate", runAtFixedRateMethod);
            }
    
            Method runDelayedMethod = getMethod(regionScheduler.getClass(), "runDelayed", Plugin.class, Location.class, Consumer.class, long.class);
            if (runDelayedMethod != null) {
                cachedMethods.put("regionScheduler.runDelayed", runDelayedMethod);
            }
        }

        Method getSchedulerMethod = getMethod(Entity.class, "getScheduler");
        if (getSchedulerMethod != null) {
            cachedMethods.put("entity.getScheduler", getSchedulerMethod);
        }
    
        Method executeEntityMethod = getMethod(Entity.class, "execute", Plugin.class, Runnable.class, Runnable.class, long.class);
        if (executeEntityMethod != null) {
            cachedMethods.put("entityScheduler.execute", executeEntityMethod);
        }
    
        Method runAtFixedRateEntityMethod = getMethod(Entity.class, "runAtFixedRate", Plugin.class, Consumer.class, Runnable.class, long.class, long.class);
        if (runAtFixedRateEntityMethod != null) {
            cachedMethods.put("entityScheduler.runAtFixedRate", runAtFixedRateEntityMethod);
        }

        Method teleportAsyncMethod = getMethod(Player.class, "teleportAsync", Location.class);
        if (teleportAsyncMethod != null) {
            cachedMethods.put("player.teleportAsync", teleportAsyncMethod);
        }
    }

    private static Object invokeMethod(Method method, Object object, Object... args) {
        try {
            if (method != null && object != null) {
                method.setAccessible(true);
                return method.invoke(object, args);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Object getGlobalRegionScheduler() {
        Method method = getMethod(Server.class, "getGlobalRegionScheduler");
        return invokeMethod(method, Bukkit.getServer());
    }
    
    private static Object getRegionScheduler() {
        Method method = getMethod(Server.class, "getRegionScheduler");
        return invokeMethod(method, Bukkit.getServer());
    }

    public static boolean isFolia() {
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            return globalRegionScheduler != null && regionScheduler != null;
        } catch (Exception ig) {
            return false;
        }
    }

    public static void runTaskAsync(Plugin pl, Runnable run, long delay) {
        if (!isFolia()) {
            bS.runTaskLaterAsynchronously(pl, run, delay);
            return;
        }
        Executors.defaultThreadFactory().newThread(run).start();
    }

    public static void runTaskAsync(Plugin pl, Runnable run) { runTaskAsync(pl, run, 1L); }

    public static void runTaskTimerAsync(Plugin pl, Consumer<Object> run, long delay, long period) {
        if (!isFolia()) {
            bS.runTaskTimerAsynchronously(pl, () -> run.accept(null), delay, period);
            return;
        }
        Method method = cachedMethods.get("globalRegionScheduler.runAtFixedRate");
        invokeMethod(method, globalRegionScheduler, pl, run, delay, period);
    }

    public static void runTaskTimer(Plugin pl, Consumer<Object> run, long delay, long period) {
        if (!isFolia()) {
            bS.runTaskTimer(pl, () -> run.accept(null), delay, period);
            return;
        }
        Method method = cachedMethods.get("globalRegionScheduler.runAtFixedRate");
        invokeMethod(method, globalRegionScheduler, pl, run, delay, period);
    }

    public static void runTask(Plugin pl, Runnable run) {
        if (!isFolia()) {
            bS.runTask(pl, run);
            return;
        }
        Method method = cachedMethods.get("globalRegionScheduler.run");
        invokeMethod(method, globalRegionScheduler, pl, (Consumer<Object>) ignored -> run.run());
    }

    public static void runTask(Plugin pl, Runnable run, long delay) {
        if (!isFolia()) {
            bS.runTaskLater(pl, run, delay);
            return;
        }
        Method method = cachedMethods.get("globalRegionScheduler.run");
        invokeMethod(method, globalRegionScheduler, pl, (Consumer<Object>) ignored -> run.run());
    }

    public static void runTask(Plugin pl, Consumer<Object> run) {
        if (!isFolia()) {
            bS.runTask(pl, () -> run.accept(null));
            return;
        }
        Method method = cachedMethods.get("globalRegionScheduler.run");
        invokeMethod(method, globalRegionScheduler, pl, run);
    }

    public static void runEntityTask(Plugin plugin, Entity entity, Runnable task) {
        if (entity == null || !entity.isValid()) {
            return;
        }
        if (isFolia()) {
            try {
                entity.getScheduler().run(plugin, scheduledTask -> task.run(), null);
            } catch (Exception e) {}
        } else {
            try {
                bS.runTask(plugin, task);
            } catch (Exception e) {}
        }
    }

    public static void runTaskForEntity(Plugin pl, Entity entity, Runnable run, Runnable retired, long delay) {
        if (!isFolia()) {
            bS.runTaskLater(pl, run, delay);
            return;
        }
		if (entity == null) return;
        Method getSchedulerMethod = cachedMethods.get("entity.getScheduler");
        Object entityScheduler = invokeMethod(getSchedulerMethod, entity);
        Method executeMethod = cachedMethods.get("entityScheduler.execute");
        invokeMethod(executeMethod, entityScheduler, pl, run, retired, delay);
    }

    public static void runTaskForEntityRepeating(Plugin pl, Entity entity, Consumer<Object> task, Runnable retired,
            long initialDelay, long period) {
        if (!isFolia()) {
            bS.runTaskTimer(pl, () -> task.accept(null), initialDelay, period);
            return;
        }
        if (entity == null) return;
        Method getSchedulerMethod = cachedMethods.get("entity.getScheduler");
        Object entityScheduler = invokeMethod(getSchedulerMethod, entity);
        Method runAtFixedRateMethod = cachedMethods.get("entityScheduler.runAtFixedRate");
        invokeMethod(runAtFixedRateMethod, entityScheduler, pl, task, retired, initialDelay, period);
    }

    public static void runTaskForRegion(Plugin pl, World world, int chunkX, int chunkZ, Runnable run) {
        if (!isFolia()) {
            bS.runTask(pl, run);
            return;
        }
        if (world == null) return;
        Method executeMethod = cachedMethods.get("regionScheduler.execute");
        invokeMethod(executeMethod, regionScheduler, pl, world, chunkX, chunkZ, run);
    }

    public static void runTaskForRegion(Plugin pl, Location location, Runnable run) {
        if (!isFolia()) {
            bS.runTask(pl, run);
            return;
        }
        if (location == null) return;
        Method executeMethod = cachedMethods.get("regionScheduler.executeLocation");
        invokeMethod(executeMethod, regionScheduler, pl, location, run);
    }

    public static void runTaskForRegionRepeating(Plugin pl, Location location, Consumer<Object> task, long initialDelay,
            long period) {
        if (!isFolia()) {
            bS.runTaskTimer(pl, () -> task.accept(null), initialDelay, period);
            return;
        }
        if (location == null) return;
        Method runAtFixedRateMethod = cachedMethods.get("regionScheduler.runAtFixedRate");
        invokeMethod(runAtFixedRateMethod, regionScheduler, pl, location, task, initialDelay, period);
    }

    public static void runTaskForRegionDelayed(Plugin pl, Location location, Consumer<Object> task, long delay) {
        if (!isFolia()) {
            bS.runTaskLater(pl, () -> task.accept(null), delay);
            return;
        }
        if (location == null) return;
        Method runDelayedMethod = cachedMethods.get("regionScheduler.runDelayed");
        invokeMethod(runDelayedMethod, regionScheduler, pl, location, task, delay);
    }

    public static CompletableFuture<Boolean> teleportPlayer(Player e, Location location, Boolean async) { return teleportAsync(e, location, async); }
	public static CompletableFuture<Boolean> teleportAsync(LivingEntity entity, Location location, Boolean async) { return teleportAsync((Entity) entity, location, async); }
    public static CompletableFuture<Boolean> teleportAsync(Entity entity, Location location, Boolean async) {
        if (!isFolia()) {
            entity.teleport(location);
            return CompletableFuture.completedFuture(true);
        } else if (async) {
            Method teleportMethod = cachedMethods.get("entity.teleportAsync");
            if (teleportMethod != null) {
                Object result = invokeMethod(teleportMethod, entity, location);
                if (result instanceof CompletableFuture) {
                    return ((CompletableFuture<Boolean>) result).thenApply(Boolean.TRUE::equals);
                }
                return CompletableFuture.completedFuture(result != null);
            }
            if (entity instanceof Player) {
                Method playerTeleportMethod = cachedMethods.get("player.teleportAsync");
                Object result = invokeMethod(playerTeleportMethod, entity, location);
                if (result instanceof CompletableFuture) {
                    return ((CompletableFuture<Boolean>) result).thenApply(Boolean.TRUE::equals);
                }
                return CompletableFuture.completedFuture(result != null);
            }
            entity.teleport(location);
            return CompletableFuture.completedFuture(true);
        } else {
            entity.teleport(location);
            return CompletableFuture.completedFuture(true);
        }
    }
}