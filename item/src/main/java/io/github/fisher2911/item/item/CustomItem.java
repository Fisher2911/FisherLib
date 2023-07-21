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

package io.github.fisher2911.item.item;

import org.bukkit.event.Event;

import java.util.Collection;
import java.util.Map;
import java.util.function.BiConsumer;

public abstract class CustomItem {

    protected final Map<Class<? extends Event>, BiConsumer<? extends Event, ? extends CustomItem>> listeners;

    public CustomItem(Map<Class<? extends Event>, BiConsumer<? extends Event, ? extends CustomItem>> listeners) {
        this.listeners = listeners;
    }

    protected <E extends Event, C extends CustomItem> void registerListener(
            Class<E> clazz,
            BiConsumer<E, C> biConsumer
    ) {
        this.listeners.put(clazz, biConsumer);
    }

    public Collection<Class<? extends Event>> getEventsToListen() {
        return this.listeners.keySet();
    }

}
