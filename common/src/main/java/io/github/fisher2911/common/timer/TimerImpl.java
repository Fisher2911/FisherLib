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

import org.bukkit.Bukkit;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Predicate;

public abstract class TimerImpl<T extends TimerImpl<T>> implements Timer<T> {

    public static class Concrete extends TimerImpl<Concrete> {

        private Concrete(Function<Concrete, TimerExecutor> function, int delay, int period) {
            super(function, delay, period);
        }

        private Concrete(Function<Concrete, TimerExecutor> function, @Nullable Predicate<Concrete> cancelPredicate, int delay, int period) {
            super(function, cancelPredicate, delay, period);
        }

    }

    /**
     *
     * @param function - the task to execute.
     * @param delay - delay in milliseconds before task is to be executed.
     * @param period - time in milliseconds between successive task executions.
     */
    public static TimerImpl<Concrete> create(Function<Concrete, TimerExecutor> function, int delay, int period) {
        return new Concrete(function, delay, period);
    }

    /**
     *
     * @param function - the task to execute.
     * @param cancelPredicate - the predicate to check if the task should be cancelled.
     * @param delay - delay in milliseconds before task is to be executed.
     * @param period - time in milliseconds between successive task executions.
     */
    public static TimerImpl<Concrete> create(Function<Concrete, TimerExecutor> function, @Nullable Predicate<Concrete> cancelPredicate, int delay, int period) {
        return new Concrete(function, cancelPredicate, delay, period);
    }

    private final Function<T, TimerExecutor> function;
    private final @Nullable Predicate<T> cancelPredicate;
    private final int delay;
    private final int period;
    private final AtomicInteger loopCount = new AtomicInteger(0);
    private boolean cancelled;
    private final AtomicBoolean running = new AtomicBoolean(false);

    /**
     *
     * @param function - the task to execute.
     * @param delay - delay in milliseconds before task is to be executed.
     * @param period - time in milliseconds between successive task executions.
     */
    protected TimerImpl(Function<T, TimerExecutor> function, int delay, int period) {
        this(function, null, delay, period);
    }

    /**
     *
     * @param function - the task to execute.
     * @param cancelPredicate - the predicate to check if the task should be cancelled.
     * @param delay - delay in milliseconds before task is to be executed.
     * @param period - time in milliseconds between successive task executions.
     */
    protected TimerImpl(
            Function<T, TimerExecutor> function,
            @Nullable Predicate<T> cancelPredicate,
            int delay,
            int period
    ) {
        this.function = function;
        this.cancelPredicate = cancelPredicate;
        this.delay = delay;
        this.period = period;
    }


    @Override
    public void start(TimerExecutor executor) {
        this.cancelled = false;
        if (this.running.get()) return;
        this.running.set(true);
        this.loopCount.set(0);
        this.doNextTask(executor);
    }

    private void doNextTask(TimerExecutor executor) {
        executor.executeLater(() -> {
            if (this.cancelled || (this.cancelPredicate != null && this.cancelPredicate.test((T) this))) {
                this.running.set(false);
                return;
            }
            final TimerExecutor newExecutor = this.function.apply((T) this);
            this.loopCount.incrementAndGet();
            this.doNextTask(newExecutor);
        }, this.getNextDelay());
    }

    private int getNextDelay() {
        if (this.loopCount.get() == 0) {
            return this.delay;
        }
        return this.period;
    }

    @Override
    public boolean isRunning() {
        return this.running.get();
    }

    @Override
    public void stop() {
        this.cancelled = true;
    }

    @Override
    public int getLoopCount() {
        return this.loopCount.get();
    }

    @Override
    public int getDelay() {
        return this.delay;
    }

    @Override
    public int getPeriod() {
        return this.period;
    }

}
