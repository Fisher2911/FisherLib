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

package io.github.fisher2911.fisherlib.listener;

import io.github.fisher2911.fisherlib.FishPlugin;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public abstract class CoreListener {

    protected final FishPlugin<?, ?> plugin;
    protected final GlobalListener globalListener;

    public CoreListener(FishPlugin<?, ?> plugin) {
        this.plugin = plugin;
        this.globalListener = this.plugin.getGlobalListener();
    }

    public void init() {
        final Method[] methods = this.getClass().getMethods();
        for (Method method : methods) {
            if (method.getParameterCount() != 1) return;
            if (!method.isAnnotationPresent(EventHandler.class)) return;
            final Class<?> parameter = method.getParameterTypes()[0];
            if (!Event.class.isAssignableFrom(parameter)) return;
            final Class<? extends Event> eventClass = (Class<? extends Event>) parameter;
            this.globalListener.register(eventClass, e -> {
                try {
                    method.invoke(this, e);
                } catch (IllegalAccessException | InvocationTargetException ex) {
                    ex.printStackTrace();
                }
            });
        }
    }
}
