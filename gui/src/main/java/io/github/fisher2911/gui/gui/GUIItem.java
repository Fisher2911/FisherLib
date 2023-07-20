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
import io.github.fisher2911.common.metadata.Metadatable;
import io.github.fisher2911.common.timer.Timeable;
import io.github.fisher2911.gui.event.GUIEvent;
import io.github.fisher2911.gui.util.Observable;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class GUIItem<P extends JavaPlugin> extends Observable<ItemBuilder> implements ListenerHandler<P>, Timeable<GUI<P>>, Metadatable {

    private final Map<Class<? extends GUIEvent<? extends InventoryEvent, P>>, Consumer<? extends GUIEvent<? extends InventoryEvent, P>>> listeners;
    private ItemBuilder itemBuilder;
    private final Metadata metadata;
    private @Nullable BiConsumer<GUIItem<P>, GUI<P>> timerConsumer;
    private GUISlot slot;

    private GUIItem(
            Map<Class<? extends GUIEvent<? extends InventoryEvent, P>>, Consumer<? extends GUIEvent<? extends InventoryEvent, P>>> listeners,
            ItemBuilder itemBuilder,
            Metadata metadata,
            @Nullable BiConsumer<GUIItem<P>, GUI<P>> timerConsumer
    ) {
        super(itemBuilder, new ArrayList<>());
        this.listeners = listeners;
        this.itemBuilder = itemBuilder;
        this.metadata = metadata;
        this.timerConsumer = timerConsumer;
    }

    @Override
    public <T extends InventoryEvent, G extends GUIEvent<T, P>> void handle(G event, Class<G> clazz) {
        if (!this.listeners.containsKey(clazz)) return;
        final var consumer = (Consumer<G>) this.listeners.get(clazz);
        consumer.accept(event);
    }

    public ItemBuilder copyItemBuilder() {
        return this.itemBuilder.copy();
    }

    public ItemBuilder getItemBuilder() {
        return this.itemBuilder;
    }

    public void setItemBuilder(ItemBuilder itemBuilder) {
        this.itemBuilder = itemBuilder;
    }

    @Override
    public void tick(GUI<P> gui) {
        if (this.timerConsumer == null) return;
        this.timerConsumer.accept(this, gui);
    }

    public boolean hasTimer() {
        return this.timerConsumer != null;
    }

    public void setTimerConsumer(@Nullable BiConsumer<GUIItem<P>, GUI<P>> timerConsumer) {
        this.timerConsumer = timerConsumer;
    }

    public GUISlot getSlot() {
        return slot;
    }

    protected void setSlot(GUISlot slot) {
        this.slot = slot;
    }

    @Override
    public @NotNull Metadata getMetaData() {
        return this.metadata;
    }

    public static <P extends JavaPlugin> Builder<P> builder(ItemBuilder itemBuilder) {
        return new Builder<>(itemBuilder);
    }

    public static <P extends JavaPlugin> Builder<P> builder(ItemStack itemStack) {
        return new Builder<>(itemStack);
    }

    public static <P extends JavaPlugin> Builder<P> builder(Material material) {
        return new Builder<>(material);
    }

    public static class Builder<P extends JavaPlugin> extends ListenerHandler.Builder<P, Builder<P>> {

        private final ItemBuilder itemBuilder;
        private Metadata metadata;
        private @Nullable BiConsumer<GUIItem<P>, GUI<P>> timerConsumer;


        private Builder(ItemBuilder itemBuilder) {
            super();
            this.itemBuilder = itemBuilder;
        }

        private Builder(Material material) {
            this(ItemBuilder.from(new ItemStack(material)));
        }

        private Builder(ItemStack itemStack) {
            this(ItemBuilder.from(itemStack));
        }

        public Builder<P> metadata(Metadata metadata) {
            this.metadata = metadata;
            return this;
        }

        public Builder<P> timer(BiConsumer<GUIItem<P>, GUI<P>> timerConsumer) {
            this.timerConsumer = timerConsumer;
            return this;
        }

        public GUIItem<P> build() {
            if (this.metadata == null) this.metadata = Metadata.mutableEmpty();
            return new GUIItem<P>(this.listeners, this.itemBuilder, this.metadata, timerConsumer);
        }

    }

}
