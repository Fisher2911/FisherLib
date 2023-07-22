/*
 *     FisherLib
 *     Copyright (C) 2022  Fisher2911
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package io.github.fisher2911.common.timer;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;

/**
 * Represents an executor that is used for {@link Timer Timers}
 * using the Bukkit scheduler
 */
public abstract class BukkitTimerExecutor implements TimerExecutor {

    /**
     * Creates a new async {@link BukkitTimerExecutor}
     * @param plugin - the plugin
     * @param scheduler - the scheduler
     * @return the new executor
     */
    public static BukkitTimerExecutor async(JavaPlugin plugin, BukkitScheduler scheduler) {
        return new Async(plugin, scheduler);
    }

    /**
     * Creates a new sync {@link BukkitTimerExecutor}
     * @param plugin - the plugin
     * @param scheduler - the scheduler
     * @return the new executor
     */
    public static BukkitTimerExecutor sync(JavaPlugin plugin, BukkitScheduler scheduler) {
        return new Sync(plugin, scheduler);
    }

    protected final JavaPlugin plugin;
    protected final BukkitScheduler scheduler;

    private BukkitTimerExecutor(JavaPlugin plugin, BukkitScheduler scheduler) {
        this.plugin = plugin;
        this.scheduler = scheduler;
    }

    private static class Async extends BukkitTimerExecutor {

        private Async(JavaPlugin plugin, BukkitScheduler scheduler) {
            super(plugin, scheduler);
        }

        @Override
        public void execute(@NotNull Runnable runnable) {
            this.scheduler.runTaskAsynchronously(this.plugin, runnable);
        }

        @Override
        public void executeLater(Runnable runnable, int delay) {
            this.scheduler.runTaskLaterAsynchronously(this.plugin, runnable, delay);
        }
    }

    private static class Sync extends BukkitTimerExecutor {

        private Sync(JavaPlugin plugin, BukkitScheduler scheduler) {
            super(plugin, scheduler);
        }

        @Override
        public void execute(@NotNull Runnable runnable) {
            this.scheduler.runTask(this.plugin, runnable);
        }

        @Override
        public void executeLater(Runnable runnable, int delay) {
            this.scheduler.runTaskLater(this.plugin, runnable, delay);
        }
    }

}
