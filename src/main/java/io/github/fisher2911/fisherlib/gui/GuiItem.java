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
import io.github.fisher2911.fisherlib.util.Metadata;
import io.github.fisher2911.fisherlib.util.builder.BaseItemBuilder;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class GuiItem extends BaseGuiItem {

    private final Consumer<InventoryEventWrapper<InventoryClickEvent>> clickHandler;
    @Nullable
    private final Consumer<InventoryEventWrapper<InventoryDragEvent>> dragHandler;

    public GuiItem(
            FishPlugin<?, ?> plugin,
            BaseItemBuilder itemBuilder,
            Metadata metadata,
            @Nullable Consumer<InventoryEventWrapper<InventoryClickEvent>> clickHandler,
            @Nullable Consumer<InventoryEventWrapper<InventoryDragEvent>> dragHandler,
            List<BiFunction<BaseGui, BaseGuiItem, Object>> placeholders
    ) {
        super(plugin, itemBuilder, metadata, placeholders);
        this.clickHandler = clickHandler;
        this.dragHandler = dragHandler;
    }

    @Override
    public BaseGuiItem withItem(FishPlugin<?, ?> plugin, BaseItemBuilder item) {
        return new GuiItem(plugin, item, this.metadata.copy(), this.clickHandler, this.dragHandler, this.placeholders);
    }

    @Override
    public BaseGuiItem withItem(FishPlugin<?, ?> plugin, ItemStack item) {
        return this.withItem(plugin, BaseItemBuilder.from(item));
    }

    @Override
    public void handleClick(InventoryEventWrapper<InventoryClickEvent> event) {
        if (this.clickHandler == null) return;
        this.clickHandler.accept(event);
    }

    @Override
    public void handleDrag(InventoryEventWrapper<InventoryDragEvent> event) {
        if (this.dragHandler == null) return;
        this.dragHandler.accept(event);
    }

    public static GuiItem nextPage(FishPlugin<?, ?> plugin, BaseItemBuilder itemBuilder, Collection<ClickType> clickTypes) {
        return new GuiItem(plugin, itemBuilder, new Metadata(new HashMap<>()), nextPageWrapper(clickTypes), null, new ArrayList<>());
    }

    public static GuiItem nextPage(FishPlugin<?, ?> plugin, BaseItemBuilder itemBuilder) {
        return nextPage(plugin, itemBuilder, List.of(ClickType.values()));
    }

    public static Consumer<InventoryEventWrapper<InventoryClickEvent>> nextPageWrapper(Collection<ClickType> clickTypes) {
        return event -> {
            if (!clickTypes.contains(event.event().getClick())) return;
            event.gui().goToNextPage();
        };
    }

    public static GuiItem previousPage(FishPlugin<?, ?> plugin, BaseItemBuilder itemBuilder, Collection<ClickType> clickTypes) {
        return new GuiItem(plugin, itemBuilder, new Metadata(new HashMap<>()), previousPageWrapper(clickTypes), null, new ArrayList<>());
    }

    public static GuiItem previousPage(FishPlugin<?, ?> plugin, BaseItemBuilder itemBuilder) {
        return previousPage(plugin, itemBuilder, List.of(ClickType.values()));
    }

    public static Consumer<InventoryEventWrapper<InventoryClickEvent>> previousPageWrapper(Collection<ClickType> clickTypes) {
        return event -> {
            if (!clickTypes.contains(event.event().getClick())) return;
            event.gui().goToPreviousPage();
        };
    }

    @Override
    public BaseGuiItem withPlaceholders(List<BiFunction<BaseGui, BaseGuiItem, Object>> placeholders) {
        return new GuiItem(this.plugin, this.itemBuilder, this.metadata.copy(), this.clickHandler, this.dragHandler, placeholders);
    }

    @Override
    public BaseGuiItem withMetaData(Metadata metadata, boolean overwrite) {
        return new GuiItem(this.plugin, this.itemBuilder, this.metadata.copyWith(metadata, overwrite), this.clickHandler, this.dragHandler, this.placeholders);
    }

    @Override
    public BaseGuiItem copy() {
        return new GuiItem(this.plugin, this.itemBuilder, this.metadata.copy(), this.clickHandler, this.dragHandler, this.placeholders);
    }

    public static BaseGuiItem air(FishPlugin<?, ?> plugin) {
        return new GuiItem(plugin, BaseItemBuilder.from(Material.AIR), Metadata.empty(), InventoryEventWrapper::cancel, InventoryEventWrapper::cancel, new ArrayList<>());
    }

    public static Builder builder(FishPlugin<?, ?> plugin, BaseItemBuilder itemBuilder) {
        return Builder.of(plugin, itemBuilder);
    }

    public static Builder builder(GuiItem guiItem) {
        return Builder.of(guiItem);
    }

    public static Builder builder(BaseGuiItem guiItem) {
        return Builder.of(guiItem);
    }

    public static class Builder {

        private final FishPlugin<?, ?> plugin;
        private final BaseItemBuilder itemBuilder;
        private final Metadata metadata = new Metadata(new HashMap<>());
        private Consumer<InventoryEventWrapper<InventoryClickEvent>> clickHandler;
        @Nullable
        private Consumer<InventoryEventWrapper<InventoryDragEvent>> dragHandler;
        private final List<BiFunction<BaseGui, BaseGuiItem, Object>> placeholders = new ArrayList<>();

        private Builder(FishPlugin<?, ?> plugin, BaseItemBuilder itemBuilder) {
            this.plugin = plugin;
            this.itemBuilder = itemBuilder;
        }

        private Builder(GuiItem guiItem) {
            this.plugin = guiItem.plugin;
            this.itemBuilder = guiItem.itemBuilder;
            this.metadata.putAll(guiItem.metadata, true);
            this.clickHandler = guiItem.clickHandler;
            this.dragHandler = guiItem.dragHandler;
            this.placeholders.addAll(guiItem.placeholders);
        }

        private Builder(BaseGuiItem guiItem) {
            this.plugin = guiItem.plugin;
            this.itemBuilder = guiItem.itemBuilder;
            this.metadata.putAll(guiItem.metadata, true);
            this.placeholders.addAll(guiItem.placeholders);
            if (guiItem instanceof GuiItem item) {
                this.clickHandler = item.clickHandler;
                this.dragHandler = item.dragHandler;
            }
        }

        private static Builder of(FishPlugin<?, ?> plugin, BaseItemBuilder itemBuilder) {
            return new Builder(plugin, itemBuilder);
        }

        private static Builder of(GuiItem guiItem) {
            return new Builder(guiItem);
        }

        private static Builder of(BaseGuiItem guiItem) {
            return new Builder(guiItem);
        }

        public Builder clickHandler(Consumer<InventoryEventWrapper<InventoryClickEvent>> handler) {
            this.clickHandler = handler;
            return this;
        }

        public Builder dragHandler(Consumer<InventoryEventWrapper<InventoryDragEvent>> handler) {
            this.dragHandler = handler;
            return this;
        }

        public Builder placeholder(BiFunction<BaseGui, BaseGuiItem, Object> placeholder) {
            this.placeholders.add(placeholder);
            return this;
        }

        public Builder placeholders(Collection<BiFunction<BaseGui, BaseGuiItem, Object>> placeholders) {
            this.placeholders.addAll(placeholders);
            return this;
        }

        public Builder metadata(Object key, Object value) {
            this.metadata.set(key, value);
            return this;
        }

        public Builder metadata(Map<Object, Object> metadata, boolean overwrite) {
            this.metadata.putAll(metadata, overwrite);
            return this;
        }

        public GuiItem build() {
            return new GuiItem(this.plugin, this.itemBuilder, this.metadata, this.clickHandler, this.dragHandler, this.placeholders);
        }
    }
}
