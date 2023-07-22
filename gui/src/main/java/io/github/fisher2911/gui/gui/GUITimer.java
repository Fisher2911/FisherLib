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

package io.github.fisher2911.gui.gui;

import io.github.fisher2911.common.timer.TimerExecutor;
import io.github.fisher2911.common.timer.TimerImpl;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;
import java.util.function.Predicate;

/**
 * This class is a wrapper for {@link TimerImpl} that allows you to easily
 * create a timer that ticks the GUI it is attached to.
 * @param <G> The GUI type
 */
@SuppressWarnings("unused")
public class GUITimer<G extends GUI> extends TimerImpl<GUITimer<G>> {

    private final GUI gui;

    public GUITimer(
            Function<GUITimer<G>, TimerExecutor> function,
            int delay,
            int period,
            GUI gui
    ) {
        super(t -> {
            gui.tick(null);
            return function.apply(t);
        }, delay, period);
        this.gui = gui;
    }

    public GUITimer(
            Function<GUITimer<G>, TimerExecutor> function,
            @Nullable Predicate<GUITimer<G>> cancelPredicate,
            int delay,
            int period,
            GUI gui
    ) {
        super(t -> {
            gui.tick(null);
            return function.apply(t);
        }, cancelPredicate, delay, period);
        this.gui = gui;
    }

    /**
     *
     * @return The GUI that this timer is attached to
     */
    public GUI getGui() {
        return this.gui;
    }

}
