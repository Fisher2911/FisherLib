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

package io.github.fisher2911.fisherlib.gui;

import io.github.fisher2911.fisherlib.FishPlugin;
import io.github.fisher2911.fisherlib.gui.wrapper.InventoryEventWrapper;
import io.github.fisher2911.fisherlib.util.ArrayUtil;
import io.github.fisher2911.fisherlib.util.Metadata;
import io.github.fisher2911.fisherlib.util.builder.BaseItemBuilder;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.BiFunction;

public abstract class BaseGuiItem {

    protected final FishPlugin<?> plugin;
    protected final BaseItemBuilder itemBuilder;
    protected final List<BiFunction<BaseGui, BaseGuiItem, Object>> placeholders;
    protected final Metadata metadata;

    public BaseGuiItem(FishPlugin<?> plugin, BaseItemBuilder itemBuilder, Metadata metadata, List<BiFunction<BaseGui, BaseGuiItem, Object>> placeholders) {
        this.plugin = plugin;
        this.itemBuilder = itemBuilder;
        this.metadata = metadata;
        this.placeholders = placeholders;
    }

    public abstract BaseGuiItem withItem(FishPlugin<?> plugin, BaseItemBuilder item);
    public abstract BaseGuiItem withItem(FishPlugin<?> plugin, ItemStack item);
    public abstract void handleClick(InventoryEventWrapper<InventoryClickEvent> wrapper);
    public abstract void handleDrag(InventoryEventWrapper<InventoryDragEvent> event);

    public ItemStack getItemStack(BaseGui gui, Object... placeholders) {
        if (placeholders.length == 0) return this.itemBuilder.build(this.plugin.getPlaceholders(), this.getPlaceholdersAsArray(this.metadata));
        if (this.placeholders.size() == 0) return this.itemBuilder.build(
                this.plugin.getPlaceholders(),
                GuiKey.toPlaceholders(
                this.metadata.copyWith(gui.getMetadata(), false)
        ).toArray());
        return this.itemBuilder.build(this.plugin.getPlaceholders(), ArrayUtil.combine(this.getPlaceholdersAsArray(metadata), placeholders));
    }

    protected Object[] getPlaceholdersAsArray(Metadata metadata) {
        return GuiKey.toPlaceholders(metadata).toArray();
    }

    public abstract BaseGuiItem withPlaceholders(List<BiFunction<BaseGui, BaseGuiItem, Object>> placeholders);

    public abstract BaseGuiItem withMetaData(Metadata metadata, boolean overWrite);

    public abstract BaseGuiItem copy();

    public void setMetadata(Object key, Object value) {
        this.metadata.set(key, value);
    }

    @Nullable
    public <T> T getMetadata(Object key, Class<T> clazz) {
        return this.metadata.get(key, clazz);
    }

    @Nullable
    public Object getMetadata(Object key) {
        return this.metadata.get(key);
    }

    public Metadata getMetadata() {
        return metadata;
    }

    @Override
    public String toString() {
        return "BaseGuiItem{" +
                "itemBuilder=" + itemBuilder +
                ", metadata=" + metadata +
                '}';
    }

}
