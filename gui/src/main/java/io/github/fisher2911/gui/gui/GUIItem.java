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

import io.github.fisher2911.common.item.ItemBuilder;
import io.github.fisher2911.common.metadata.Metadata;
import io.github.fisher2911.common.metadata.MetadataKey;
import io.github.fisher2911.common.metadata.Metadatable;
import io.github.fisher2911.common.timer.Timeable;
import io.github.fisher2911.common.util.Observable;
import io.github.fisher2911.gui.event.GUIEvent;
import io.github.fisher2911.gui.gui.conditional.ConditionalItemBuilder;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@SuppressWarnings({"unused", "unchecked"})
public class GUIItem extends Observable<ConditionalItemBuilder> implements ListenerHandler, Timeable<GUI>, Metadatable {

    private final JavaPlugin plugin;
    private final Map<Class<? extends GUIEvent<? extends InventoryEvent>>, Consumer<? extends GUIEvent<? extends InventoryEvent>>> listeners;
    private ConditionalItemBuilder itemBuilder;
    private final Metadata metadata;
    private @Nullable BiConsumer<GUIItem, GUI> timerConsumer;
    private GUISlot slot;

    private GUIItem(
            Map<Class<? extends GUIEvent<? extends InventoryEvent>>, Consumer<? extends GUIEvent<? extends InventoryEvent>>> listeners,
            ConditionalItemBuilder itemBuilder,
            Metadata metadata,
            @Nullable BiConsumer<GUIItem, GUI> timerConsumer
    ) {
        super(itemBuilder, new ArrayList<>());
        this.plugin = JavaPlugin.getProvidingPlugin(this.getClass());
        this.listeners = listeners;
        this.itemBuilder = itemBuilder;
        this.metadata = metadata;
        this.timerConsumer = timerConsumer;
    }

    private static final String GUI_KEY = "gui";

    /**
     * @return The {@link MetadataKey} for the {@link GUI} this {@link GUIItem} is in.
     */
    public MetadataKey<GUI> getGUIMetadataKey() {
        return MetadataKey.of(new NamespacedKey(this.plugin, GUI_KEY), GUI.class);
    }

    /**
     * @param gui The {@link GUI} that this {@link GUIItem} is in.
     */
    public void setGUI(GUI gui) {
        this.metadata.set(this.getGUIMetadataKey(), gui);
    }

    /**
     * Removes the {@link GUI} from this {@link GUIItem}.
     */
    public void removeGUI() {
        this.metadata.remove(this.getGUIMetadataKey());
    }

    /**
     * @return The {@link GUI} that this {@link GUIItem} is in.
     */
    public @Nullable GUI getGUI() {
        return this.metadata.get(this.getGUIMetadataKey());
    }

    @Override
    public <T extends InventoryEvent, G extends GUIEvent<T>> void handle(G event, Class<G> clazz) {
        if (!this.listeners.containsKey(clazz)) return;
        final var consumer = (Consumer<G>) this.listeners.get(clazz);
        consumer.accept(event);
    }

    /**
     * Appends a new listener to the previous {@link Consumer}.
     *
     * @param clazz    The {@link GUIEvent} class to append a listener to.
     * @param consumer The {@link Consumer} to append to the {@link GUIEvent} class.
     * @param <T>      The {@link InventoryEvent} type.
     * @param <G>      The {@link GUIEvent} type.
     */
    public <T extends InventoryEvent, G extends GUIEvent<T>> void appendListener(Class<?> clazz, Consumer<G> consumer) {
        if (!GUIEvent.class.isAssignableFrom(clazz))
            throw new IllegalArgumentException("Class must be a subclass of GUIEvent: " + clazz.getName());
        final Class<? extends G> castedClass = (Class<? extends G>) clazz;
        final var oldConsumer = (Consumer<G>) this.listeners.get(clazz);
        if (oldConsumer == null) {
            this.listeners.put(castedClass, consumer);
            return;
        }
        this.listeners.put(castedClass, oldConsumer.andThen(consumer));
    }

    /**
     * @return A copy of the {@link ItemBuilder} for this {@link GUIItem}.
     */
    public ItemBuilder copyItemBuilder() {
        return this.getItemBuilder().copy();
    }

    /**
     * @return The {@link ItemBuilder} for this {@link GUIItem}.
     */
    public ItemBuilder getItemBuilder() {
        return this.itemBuilder.getItemBuilder(this.getGUI(), this);
    }

    /**
     * @param itemBuilder The {@link ConditionalItemBuilder} to set for this {@link GUIItem}.
     */
    public void setConditionalItemBuilder(ConditionalItemBuilder itemBuilder) {
        this.itemBuilder = itemBuilder;
    }

    public void setItemBuilder(ItemBuilder itemBuilder) {
        this.itemBuilder = ConditionalItemBuilder.constant(itemBuilder);
    }

    @Override
    public void tick(GUI gui) {
        if (this.timerConsumer == null) return;
        this.timerConsumer.accept(this, gui);
    }

    /**
     * @return Whether this {@link GUIItem} has a timer {@link BiConsumer}.
     */
    public boolean hasTimer() {
        return this.timerConsumer != null;
    }

    /**
     * @param timerConsumer The {@link BiConsumer} to set for this {@link GUIItem}.
     */
    public void setTimerConsumer(@Nullable BiConsumer<GUIItem, GUI> timerConsumer) {
        this.timerConsumer = timerConsumer;
    }

    /**
     * @return The {@link GUISlot} for this {@link GUIItem}.
     */
    public GUISlot getSlot() {
        return slot;
    }

    protected void setSlot(GUISlot slot) {
        this.slot = slot;
    }

    @Override
    public @NotNull Metadata getMetadata() {
        return this.metadata;
    }

    /**
     * @param itemBuilder The {@link ItemBuilder} to use for the new {@link Builder}
     * @return A new {@link Builder} with the given {@link ItemBuilder}.
     */
    public static Builder builder(ItemBuilder itemBuilder) {
        return new Builder(itemBuilder);
    }

    /**
     * @param itemStack The {@link ItemStack} to use for the new {@link Builder}
     * @return A new {@link Builder} with the given {@link ItemStack}.
     */
    public static Builder builder(ItemStack itemStack) {
        return new Builder(itemStack);
    }

    /**
     * @param material The {@link Material} to use for the new {@link Builder}
     * @return A new {@link Builder} with the given {@link Material}.
     */
    public static Builder builder(Material material) {
        return new Builder(material);
    }

    public static class Builder extends ListenerHandler.Builder<Builder> {

        private final ConditionalItemBuilder itemBuilder;
        private Metadata metadata = Metadata.mutableEmpty();
        private @Nullable BiConsumer<GUIItem, GUI> timerConsumer;


        private Builder(ItemBuilder itemBuilder) {
            super();
            this.itemBuilder = ConditionalItemBuilder.constant(itemBuilder);
        }

        private Builder(ConditionalItemBuilder itemBuilder) {
            super();
            this.itemBuilder = itemBuilder;
        }

        private Builder(Material material) {
            this((ItemBuilder) ItemBuilder.from(new ItemStack(material)));
        }

        private Builder(ItemStack itemStack) {
            this((ItemBuilder) ItemBuilder.from(itemStack));
        }

        /**
         *
         * @param metadata The {@link Metadata} to set for the new {@link Builder}.
         * @return The {@link Builder} with the given {@link Metadata}.
         */
        public Builder metadata(Metadata metadata) {
            this.metadata = metadata;
            return this;
        }

        /**
         *
         * @param metadataKey The {@link MetadataKey} to set for the new {@link Builder}.
         * @param v The value to set for the {@link MetadataKey}.
         * @param <V> The type of the {@link MetadataKey}.
         * @return The {@link Builder} with the given {@link MetadataKey} and value.
         */
        public <V> Builder metadata(MetadataKey<V> metadataKey, V v) {
            this.metadata.set(metadataKey, v);
            return this;
        }

        /**
         *
         * @param timerConsumer The {@link BiConsumer} to set for the new {@link Builder}.
         * @return The {@link Builder} with the given {@link BiConsumer}.
         */
        public Builder timer(BiConsumer<GUIItem, GUI> timerConsumer) {
            this.timerConsumer = timerConsumer;
            return this;
        }

        /**
         *
         * @return The {@link GUIItem} built from this {@link Builder}.
         */
        public GUIItem build() {
            return new GUIItem(this.listeners, this.itemBuilder, this.metadata, timerConsumer);
        }

    }

}
