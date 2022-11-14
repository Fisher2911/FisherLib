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

package io.github.fisher2911.fisherlib.config.serializer;

import io.github.fisher2911.fisherlib.FishPlugin;
import io.github.fisher2911.fisherlib.gui.BaseGui;
import io.github.fisher2911.fisherlib.gui.ConditionalItem;
import io.github.fisher2911.fisherlib.gui.Gui;
import io.github.fisher2911.fisherlib.gui.GuiFillerType;
import io.github.fisher2911.fisherlib.gui.GuiKey;
import io.github.fisher2911.fisherlib.gui.GuiOpener;
import io.github.fisher2911.fisherlib.upgrade.UpgradeHolder;
import io.github.fisher2911.fisherlib.upgrade.Upgradeable;
import io.github.fisher2911.fisherlib.upgrade.Upgrades;
import io.github.fisher2911.fisherlib.user.CoreUser;
import io.github.fisher2911.fisherlib.util.function.TriConsumer;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

public class GuiSerializer<T extends CoreUser, Z extends FishPlugin<T, Z>> {

    public static final String ID_PATH = "id";
    public static final String TITLE_PATH = "title";
    public static final String ROWS_PATH = "rows";
    public static final String BORDER_ITEMS_PATH = "border";
    public static final String ITEMS_PATH = "items";
    public static final boolean CANCEL_CLICKS_PATH = true;
    public static final String REQUIRED_METADATA_PATH = "required-metadata";
    public static final String REPEAT_ON_ALL_PAGES_PATH = "repeat-on-all-pages";

    public static final String GUI_FILLERS_PATH = "gui-fillers";

    protected final Map<String, BiFunction<Z, ConfigurationNode, Function<BaseGui, List<ConditionalItem>>>> guiFillerLoaders = new HashMap<>();

    protected final ItemSerializers<T, Z> itemSerializers;

    public GuiSerializer(ItemSerializers<T, Z> itemSerializers) {
        this.itemSerializers = itemSerializers;
        this.registerGuiFillerLoader(GuiFillerType.UPGRADES, this::loadUpgradesFillers);
    }

    public void registerGuiFillerLoader(
            final String id,
            final BiFunction<Z, ConfigurationNode, Function<BaseGui, List<ConditionalItem>>> loader
    ) {
        this.guiFillerLoaders.put(id.toUpperCase(Locale.ROOT), loader);
    }

    public GuiOpener<T> deserialize(
            Z plugin,
            ConfigurationNode source,
            TriConsumer<GuiOpener<T>, Gui.Builder, T> openConsumer
    ) throws SerializationException {
        final String id = source.node(ID_PATH).getString();
        final String title = source.node(TITLE_PATH).getString("Kingdoms");
        final int rows = source.node(ROWS_PATH).getInt();
        final boolean cancelClicks = source.node(CANCEL_CLICKS_PATH).getBoolean();
        final var borderNode = source.node(BORDER_ITEMS_PATH);
        final List<ConditionalItem> borders = new ArrayList<>();
        for (var entry : borderNode.childrenMap().entrySet()) {
            borders.add(this.itemSerializers.guiItemSerializer().deserialize(plugin, this.itemSerializers, entry.getValue()));
        }
        final Set<Integer> repeatOnAllPagesSlots = new HashSet<>();
        final var itemsNode = source.node(ITEMS_PATH);
        final Map<Integer, ConditionalItem> items = new HashMap<>();
        for (var entry : itemsNode.childrenMap().entrySet()) {
            if (!(entry.getKey() instanceof final Integer slot)) {
                continue;
            }
            final boolean repeatOnAllPages = entry.getValue().node(REPEAT_ON_ALL_PAGES_PATH).getBoolean();
            if (repeatOnAllPages) repeatOnAllPagesSlots.add(slot);
            items.put(slot, this.itemSerializers.guiItemSerializer().deserialize(plugin, this.itemSerializers, entry.getValue()));
        }

        final var guiFillersNode = source.node(GUI_FILLERS_PATH);
        final List<Function<BaseGui, List<ConditionalItem>>> guiFillers = new ArrayList<>();
        for (var entry : guiFillersNode.childrenMap().entrySet()) {
            if (!(entry.getKey() instanceof final String typeStr)) continue;
            final String fillerType = typeStr.toUpperCase();
            final BiFunction<Z, ConfigurationNode, Function<BaseGui, List<ConditionalItem>>> loader = this.guiFillerLoaders.get(fillerType);
            if (loader == null) continue;
            guiFillers.add(loader.apply(plugin, entry.getValue()));
        }

        final List<GuiKey> requiredMetaData = source.node(REQUIRED_METADATA_PATH)
                .getList(String.class, new ArrayList<>())
                .stream()
                .map(s -> GuiKey.key(plugin, s.toLowerCase(), true))
                .toList();

        return new GuiOpener<>(
                plugin,
                id,
                Gui.builder(plugin, id)
                        .name(title)
                        .rows(rows)
                        .items(items)
                        .filler(guiFillers)
                        .border(borders)
                        .repeatPageSlots(repeatOnAllPagesSlots)
                        .cancelAllClicks(true),
                requiredMetaData,
                openConsumer
        );
    }

    private Function<BaseGui, List<ConditionalItem>> loadUpgradesFillers(
            Z plugin,
            ConfigurationNode source
    ) {
        // stupid checked exceptions
        try {
            final ConditionalItem filler = this.itemSerializers.guiItemSerializer().deserialize(plugin, this.itemSerializers, source);
            final ConditionalItem maxLevelItem = this.itemSerializers.guiItemSerializer().deserialize(plugin, this.itemSerializers, source.node(GuiItemSerializer.MAX_LEVEL_ITEM_PATH));
            return gui -> {
                final Upgradeable<?, ?> upgradeable = gui.getMetadata(GuiKey.UPGRADEABLE, Upgradeable.class);
                if (upgradeable == null) return new ArrayList<>();
                final List<ConditionalItem> items = new ArrayList<>();
                final UpgradeHolder<?, ?> upgradeHolder = upgradeable.getUpgradeHolder();
                for (String id : upgradeHolder.getUpgradeIdOrder()) {
                    final Upgrades<?> upgrades = upgradeHolder.getUpgrades(id);
                    final Integer level = upgradeable.getUpgradeLevel(id);
                    if (level == null || upgrades == null) continue;
                    final ConditionalItem.Builder builder;
                    if (level >= upgrades.getMaxLevel()) {
                        builder = ConditionalItem.builder(maxLevelItem);
                    } else {
                        builder = ConditionalItem.builder(filler);
                    }
                    this.itemSerializers.guiItemSerializer().applyUpgradesItemData(builder, id, maxLevelItem);
                    items.add(builder.build());
                }
                return items;
            };
        } catch (SerializationException e) {
            throw new RuntimeException(e);
        }
    }

}
