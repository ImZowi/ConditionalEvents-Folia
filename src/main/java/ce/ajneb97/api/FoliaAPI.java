package ce.ajneb97.api;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

public class FoliaAPI {
	private static final Map<String, Method> cachedMethods = new ConcurrentHashMap<>();
	
    private static BukkitScheduler bS = Bukkit.getScheduler();
    private static Object globalRegionScheduler = getGlobalRegionScheduler();
    private static Object regionScheduler = getRegionScheduler();
    private static Object asyncScheduler = getAsyncScheduler();
    
    protected static final boolean IS_FOLIA;

    static {
    	cacheMethods();
        boolean folia = false;
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            folia = globalRegionScheduler != null && regionScheduler != null;
        } catch (Exception ig) {}
        IS_FOLIA = folia;
    }

    private static Method getMethod(Class<?> clazz, String methodName, Class<?>... parameterTypes) {
    	if (clazz == null) return null;
    	try {
    		Method method = clazz.getMethod(methodName, parameterTypes);
    		method.setAccessible(true);
    		return method;
    	} catch (NoSuchMethodException ig) { return null; }
    }

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
            Method runDelayedMethod = getMethod(globalRegionScheduler.getClass(), "runDelayed", Plugin.class, Consumer.class, long.class);
            if (runDelayedMethod != null) {
                cachedMethods.put("globalRegionScheduler.runDelayed", runDelayedMethod);
            }
            Method cancelTasksMethod = getMethod(globalRegionScheduler.getClass(), "cancelTasks", Plugin.class);
            if (cancelTasksMethod != null) {
                cachedMethods.put("globalRegionScheduler.cancelTasks", cancelTasksMethod);
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
        Method teleportAsyncWithCause = getMethod(Player.class, "teleportAsync", Location.class, TeleportCause.class);
        if (teleportAsyncWithCause != null) {
            cachedMethods.put("player.teleportAsyncCause", teleportAsyncWithCause);
        }
        if (asyncScheduler != null) {
            Method cancelTasksMethod = getMethod(asyncScheduler.getClass(), "cancelTasks", Plugin.class);
            if (cancelTasksMethod != null) {
                cachedMethods.put("asyncScheduler.cancelTasks", cancelTasksMethod);
            }
        }
    }

    private static Object invokeMethod(Method method, Object object, Object... args) {
        try {
            if (method != null && object != null) {
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
    
    private static Object getAsyncScheduler() {
        Method method = getMethod(Server.class, "getAsyncScheduler");
        return invokeMethod(method, Bukkit.getServer());
    }

    public static void runTaskAsync(Plugin pl, Runnable run, long delay) {
        if (!IS_FOLIA) {
            bS.runTaskLaterAsynchronously(pl, run, delay);
            return;
        }
        Executors.defaultThreadFactory().newThread(run).start();
    }

    public static void runTaskAsync(Plugin pl, Runnable run) {
        runTaskAsync(pl, run, 1L);
    }
    
    public static void runTaskAsynchronously(Plugin pl, Runnable run) {
        if (!IS_FOLIA) {
            bS.runTaskAsynchronously(pl, run);
            return;
        }
        Executors.defaultThreadFactory().newThread(run).start();
    }

    public static void runTaskTimerAsync(Plugin pl, Consumer<Object> run, long delay, long period) {
        if (!IS_FOLIA) {
            Object[] taskHolder = new Object[1];
            taskHolder[0] = bS.runTaskTimerAsynchronously(pl, () -> run.accept(taskHolder[0]), delay, period);
            return;
        }
        Method method = cachedMethods.get("globalRegionScheduler.runAtFixedRate");
        invokeMethod(method, globalRegionScheduler, pl, run, delay, period);
    }

    public static void runTaskTimerAsync(Plugin pl, Runnable runnable, long delay, long period) {
        runTaskTimerAsync(pl, obj -> runnable.run(), delay, period);
    }

    public static void cancelTask(Object task) {
        if (task == null) return;
        try {
            Method cancelMethod = task.getClass().getMethod("cancel");
            cancelMethod.invoke(task);
        } catch (Exception e) {}
    }

    public static void runTaskTimer(Plugin pl, Consumer<Object> run, long delay, long period) {
        if (!IS_FOLIA) {
            bS.runTaskTimer(pl, () -> run.accept(null), delay, period);
            return;
        }
        Method method = cachedMethods.get("globalRegionScheduler.runAtFixedRate");
        invokeMethod(method, globalRegionScheduler, pl, run, delay, period);
    }

    public static void runTask(Plugin pl, Runnable run) {
        if (!IS_FOLIA) {
            bS.runTask(pl, run);
            return;
        }
        Method method = cachedMethods.get("globalRegionScheduler.run");
        invokeMethod(method, globalRegionScheduler, pl, (Consumer<Object>) ignored -> run.run());
    }

    public static void runTask(Plugin pl, Runnable run, long delay) {
        if (!IS_FOLIA) {
            bS.runTaskLater(pl, run, delay);
            return;
        }
        Method method = cachedMethods.get("globalRegionScheduler.run");
        invokeMethod(method, globalRegionScheduler, pl, (Consumer<Object>) ignored -> run.run());
    }

    public static void runTask(Plugin pl, Consumer<Object> run) {
        if (!IS_FOLIA) {
            bS.runTask(pl, () -> run.accept(null));
            return;
        }
        Method method = cachedMethods.get("globalRegionScheduler.run");
        invokeMethod(method, globalRegionScheduler, pl, run);
    }
    
    public static void runTaskLater(Plugin pl, Runnable run, long delay) {
        if (!IS_FOLIA) {
            bS.runTaskLater(pl, run, delay);
            return;
        }
        Method method = cachedMethods.get("globalRegionScheduler.runDelayed");
        invokeMethod(method, globalRegionScheduler, pl, (Consumer<Object>) ignored -> run.run(), delay);
    }

    public static Object runTaskLater(Plugin pl, Consumer<Object> run, long delay) {
        if (!IS_FOLIA) {
            return bS.runTaskLater(pl, () -> run.accept(null), delay);
        }
        Method method = cachedMethods.get("globalRegionScheduler.runDelayed");
        return invokeMethod(method, globalRegionScheduler, pl, run, delay);
    }
    
    public static void runEntityTask(Plugin pl, Entity entity, Runnable task) {
        if (entity == null || !entity.isValid()) { return; }
        if (!IS_FOLIA) {
            try {
                bS.runTask(pl, task);
            } catch (Exception e) {}        	
        }
        try {
        	Method getSchedulerMethod = entity.getClass().getMethod("getScheduler");
        	Object entityScheduler = getSchedulerMethod.invoke(entity);
        	Method runMethod = entityScheduler.getClass().getMethod("run", Plugin.class, Runnable.class, Runnable.class, Long.class);
        	runMethod.invoke(entityScheduler, pl, task, null, 0L);
        } catch (Exception e) {}
    }
    
    public static void runTaskForEntity(Plugin pl, Entity entity, Runnable run, Runnable retired, long delay) {
    	if (entity == null || !entity.isValid()) { return; }
        if (!IS_FOLIA) {
            bS.runTaskLater(pl, run, delay);
            return;
        }
        try {
	        Method getSchedulerMethod = cachedMethods.get("entity.getScheduler");
	        Object entityScheduler = invokeMethod(getSchedulerMethod, entity);
	        Method executeMethod = cachedMethods.get("entityScheduler.execute");
	        invokeMethod(executeMethod, entityScheduler, pl, run, retired, delay);
        } catch (Exception e) {}
    }

    public static void runTaskForEntityRepeating(Plugin pl, Entity entity, Consumer<Object> task, Runnable retired, long initialDelay, long period) {
        if (!IS_FOLIA) {
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
        if (!IS_FOLIA) {
            bS.runTask(pl, run);
            return;
        }
        if (world == null) return;
        Method executeMethod = cachedMethods.get("regionScheduler.execute");
        invokeMethod(executeMethod, regionScheduler, pl, world, chunkX, chunkZ, run);
    }

    public static void runTaskForRegion(Plugin pl, Location location, Runnable run) {
        if (!IS_FOLIA) {
            bS.runTask(pl, run);
            return;
        }
        if (location == null) return;
        Method executeMethod = cachedMethods.get("regionScheduler.executeLocation");
        invokeMethod(executeMethod, regionScheduler, pl, location, run);
    }

    public static void runTaskForRegionRepeating(Plugin pl, Location location, Consumer<Object> task, long initialDelay, long period) {
        if (!IS_FOLIA) {
            bS.runTaskTimer(pl, () -> task.accept(null), initialDelay, period);
            return;
        }
        if (location == null) return;
        Method runAtFixedRateMethod = cachedMethods.get("regionScheduler.runAtFixedRate");
        invokeMethod(runAtFixedRateMethod, regionScheduler, pl, location, task, initialDelay, period);
    }

    public static void runTaskForRegionDelayed(Plugin pl, Location location, Consumer<Object> task, long delay) {
        if (!IS_FOLIA) {
            bS.runTaskLater(pl, () -> task.accept(null), delay);
            return;
        }
        if (location == null) return;
        Method runDelayedMethod = cachedMethods.get("regionScheduler.runDelayed");
        invokeMethod(runDelayedMethod, regionScheduler, pl, location, task, delay);
    }

    public static CompletableFuture<Boolean> teleportPlayer(Plugin pl, Player e, Location location, Boolean async) {
    	return teleportPlayer(pl, e, location, (TeleportCause) null);
    }
    
    public static CompletableFuture<Boolean> teleportPlayer(Plugin pl, Player e, Location location, TeleportCause cause) {
        if (IS_FOLIA) {
            CompletableFuture<Boolean> out = new CompletableFuture<>();
            runTaskForEntity(pl, e, () -> {
                Method teleportAsyncWithCause = cachedMethods.get("player.teleportAsyncCause");
                if (teleportAsyncWithCause != null) {
                    Object res = invokeMethod(teleportAsyncWithCause, e, location, cause);
                    if (res instanceof CompletableFuture) {
                        @SuppressWarnings("unchecked")
                        CompletableFuture<Boolean> f = (CompletableFuture<Boolean>) res;
                        f.whenComplete((ok, ex) -> {
                            if (ex != null) {
                                out.complete(false);
                            } else {
                                out.complete(Boolean.TRUE.equals(ok));
                            }
                        });
                        return;
                    }
                    out.complete(res != null);
                    return;
                }
                Method teleportAsyncMethod = cachedMethods.get("player.teleportAsync");
                if (teleportAsyncMethod != null) {
                    Object res = invokeMethod(teleportAsyncMethod, e, location);
                    if (res instanceof CompletableFuture) {
                        @SuppressWarnings("unchecked")
                        CompletableFuture<Boolean> f = (CompletableFuture<Boolean>) res;
                        f.whenComplete((ok, ex) -> {
                            if (ex != null) {
                                out.complete(false);
                            } else {
                                out.complete(Boolean.TRUE.equals(ok));
                            }
                        });
                        return;
                    }
                    out.complete(res != null);
                    return;
                }
                if (cause != null) {
                    out.complete(e.teleport(location, cause));
                    return;
                }
                out.complete(e.teleport(location));
            }, () -> {}, 1L);
            return out;
        }
        Method teleportAsyncWithCause = cachedMethods.get("player.teleportAsyncCause");
        if (teleportAsyncWithCause != null) {
            Object res = invokeMethod(teleportAsyncWithCause, e, location, cause);
            if (res instanceof CompletableFuture) {
                @SuppressWarnings("unchecked")
                CompletableFuture<Boolean> f = (CompletableFuture<Boolean>) res;
                return f;
            }
            return CompletableFuture.completedFuture(res != null);
        }
        Method teleportAsyncMethod = cachedMethods.get("player.teleportAsync");
        if (teleportAsyncMethod != null) {
            Object res = invokeMethod(teleportAsyncMethod, e, location);
            if (res instanceof CompletableFuture) {
                @SuppressWarnings("unchecked")
                CompletableFuture<Boolean> f = (CompletableFuture<Boolean>) res;
                return f;
            }
            return CompletableFuture.completedFuture(res != null);
        }
        if (cause != null) {
            return CompletableFuture.completedFuture(e.teleport(location, cause));
        }
        e.teleport(location);
        return CompletableFuture.completedFuture(true);
    }
    
    public static void cancelAllTasks(Plugin pl) {
        if (!IS_FOLIA) {
            bS.cancelTasks(pl);
            return;
        }
        Method cancelGlobalMethod = cachedMethods.get("globalRegionScheduler.cancelTasks");
        invokeMethod(cancelGlobalMethod, globalRegionScheduler, pl);
        Method cancelAsyncMethod = cachedMethods.get("asyncScheduler.cancelTasks");
        invokeMethod(cancelAsyncMethod, asyncScheduler, pl);
    }
}