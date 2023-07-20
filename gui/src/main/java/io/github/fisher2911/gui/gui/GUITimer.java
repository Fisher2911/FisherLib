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
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;
import java.util.function.Predicate;

public class GUITimer<P extends JavaPlugin, G extends GUI<P>> extends TimerImpl<GUITimer<P, G>> {

    private final GUI<P> gui;

    public GUITimer(
            Function<GUITimer<P, G>, TimerExecutor> function,
            int delay,
            int period,
            GUI<P> gui
    ) {
        super(t -> {
            gui.tick(null);
            return function.apply(t);
        }, delay, period);
        this.gui = gui;
    }

    public GUITimer(
            Function<GUITimer<P, G>, TimerExecutor> function,
            @Nullable Predicate<GUITimer<P, G>> cancelPredicate,
            int delay,
            int period,
            GUI<P> gui
    ) {
        super(t -> {
            gui.tick(null);
            return function.apply(t);
        }, cancelPredicate, delay, period);
        this.gui = gui;
    }

    public GUI<P> getGui() {
        return this.gui;
    }

}
