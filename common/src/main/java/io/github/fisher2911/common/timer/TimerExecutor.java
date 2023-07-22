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

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Executor;

/**
 * Represents an executor that is used for {@link Timer Timers}
 */
public interface TimerExecutor extends Executor {

    /**
     * Executes the given runnable
     *
     * @param runnable - the runnable to execute
     */
    void execute(@NotNull Runnable runnable);

    /**
     * Executes the given runnable after the given delay
     *
     * @param runnable - the runnable to execute
     * @param delay    - the delay in ticks
     */
    void executeLater(Runnable runnable, int delay);

}
