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

import io.github.fisher2911.gui.event.GUIClickEvent;
import io.github.fisher2911.gui.event.GUICloseEvent;
import io.github.fisher2911.gui.event.GUIDragEvent;
import io.github.fisher2911.gui.event.GUIEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@SuppressWarnings({"unchecked", "unused"})
public interface ListenerHandler {

    <T extends InventoryEvent, G extends GUIEvent<T>> void handle(G event, Class<G> clazz);

    class Builder<B extends Builder<B>> {

        protected final Map<Class<? extends GUIEvent<? extends InventoryEvent>>, Consumer<? extends GUIEvent<? extends InventoryEvent>>> listeners;

        protected Builder() {
            this.listeners = new HashMap<>();
        }

        public B listenClick(Consumer<GUIClickEvent> consumer) {
            return this.listenClick(consumer, false);
        }

        public B listenClick(Consumer<GUIClickEvent> consumer, boolean append) {
            this.listen(GUIClickEvent.class, consumer, append);
            return (B) this;
        }

        public B listenDrag(Consumer<GUIDragEvent> consumer) {
            return this.listenDrag(consumer, false);
        }

        public B listenDrag(Consumer<GUIDragEvent> consumer, boolean append) {
            this.listen(GUIDragEvent.class, consumer, append);
            return (B) this;
        }

        public B cancelClick() {
            this.listen(GUIClickEvent.class, e -> e.setCancelled(true), true);
            return (B) this;
        }

        public B cancelTopInventoryClick() {
            this.listen(GUIClickEvent.class, e -> {
                final InventoryClickEvent event = (InventoryClickEvent) e.getBukkitEvent();
                final InventoryView view = event.getView();
                if (view.getTopInventory().equals(event.getClickedInventory())) {
                    e.setCancelled(true);
                }
            }, true);
            return (B) this;
        }

        public B cancelTopInventoryDrag() {
            this.listen(GUIDragEvent.class, e -> {
                final InventoryDragEvent event = (InventoryDragEvent) e.getBukkitEvent();
                final InventoryView view = event.getView();
                final Inventory topInventory = view.getTopInventory();
                for (final int slot : event.getInventorySlots()) {
                    if (slot <= topInventory.getSize()) {
                        e.setCancelled(true);
                        return;
                    }
                }
            }, true);
            return (B) this;
        }

        public B cancelDrag() {
            this.listen(GUIDragEvent.class, e -> e.setCancelled(true), true);
            return (B) this;
        }

        public B cancelClose() {
            this.listen(GUICloseEvent.class, e -> e.setCancelled(true), true);
            return (B) this;
        }

        protected <T extends InventoryEvent, G extends GUIEvent<T>> void listen(Class<?> clazz, Consumer<G> consumer, boolean append) {
            if (!GUIEvent.class.isAssignableFrom(clazz)) throw new IllegalArgumentException("Class must be a subclass of GUIEvent: " + clazz.getName());
            final Class<? extends G> castedClass = (Class<? extends G>) clazz;
            if (append) {
                final var oldConsumer = (Consumer<G>) this.listeners.get(clazz);
                if (oldConsumer == null) {
                    this.listeners.put(castedClass, consumer);
                    return;
                }
                this.listeners.put(castedClass, oldConsumer.andThen(consumer));
                return;
            }
            this.listeners.put(castedClass, consumer);
        }

    }

}
