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
import io.github.fisher2911.fisherlib.config.condition.ItemConditions;
import io.github.fisher2911.fisherlib.gui.BaseGui;
import io.github.fisher2911.fisherlib.gui.BaseGuiItem;
import io.github.fisher2911.fisherlib.gui.ConditionalItem;
import io.github.fisher2911.fisherlib.gui.GuiItem;
import io.github.fisher2911.fisherlib.gui.GuiKey;
import io.github.fisher2911.fisherlib.gui.wrapper.InventoryEventWrapper;
import io.github.fisher2911.fisherlib.upgrade.Upgradeable;
import io.github.fisher2911.fisherlib.upgrade.Upgrades;
import io.github.fisher2911.fisherlib.user.CoreUser;
import io.github.fisher2911.fisherlib.util.Metadata;
import io.github.fisher2911.fisherlib.util.builder.BaseItemBuilder;
import io.github.fisher2911.fisherlib.util.function.TriConsumer;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class GuiItemSerializer<T extends CoreUser, Z extends FishPlugin<T, Z>> {

//    public static final GuiItemSerializer INSTANCE = new GuiItemSerializer();
//
//    private GuiItemSerializer() {
//    }

    public static final String TYPE_PATH = "type";
    public static final String ITEM_PATH = "item";
    public static final String ACTIONS_PATH = "actions";
    public static final String CONDITIONALS_PATH = "conditionals";

    public static final String TYPE_UPGRADE = "upgrade";
    public static final String UPGRADE_PATH = "upgrade";

    public ConditionalItem deserialize(
            Z plugin,
            ItemSerializers<T, Z> serializers,
            ConfigurationNode node
    ) throws SerializationException {
        final var conditionalsNode = node.node(CONDITIONALS_PATH);
        final ConditionalItem.Builder builder = ConditionalItem.builder(plugin);
        if (conditionalsNode.virtual()) {
            final BaseGuiItem item = deserializeItem(plugin, serializers, node);
            return builder.addConditionalItem(ItemConditions.alwaysTrue(ConditionalItem.of(item))).build();
        }
        for (var entry : conditionalsNode.childrenMap().entrySet()) {
            builder.addConditionalItem(serializers.conditionSerializer().loadConditional(plugin, serializers, entry.getValue()));
        }
        return builder.build();
    }

    private final Map<String, TriConsumer<ConditionalItem.Builder, ConfigurationNode, ItemSerializers<T, Z>>> dataAppliers = new HashMap<>();

    protected final Z plugin;

    public GuiItemSerializer(Z plugin) {
        this.plugin = plugin;
        this.registerDataApplier(TYPE_UPGRADE, this::applyUpgradesItemData);
    }

    public void registerDataApplier(String key, TriConsumer<ConditionalItem.Builder, ConfigurationNode, ItemSerializers<T, Z>> applier) {
        this.dataAppliers.put(key, applier);
    }

    private BaseGuiItem deserializeItem(
            Z plugin,
            ItemSerializers<T, Z> serializers,
            ConfigurationNode node
    ) throws SerializationException {
        final String typeString = node.node(TYPE_PATH).getString();
        final BaseItemBuilder itemBuilder = ItemSerializer.INSTANCE.deserialize(BaseItemBuilder.class, node.node(ITEM_PATH));
        final GuiItem.Builder builder = GuiItem.builder(plugin, itemBuilder);
        final List<Consumer<InventoryEventWrapper<InventoryClickEvent>>> clickHandlers = serializers.clickActionSerializer().deserializeAll(plugin, serializers, node.node(ACTIONS_PATH));
        builder.clickHandler(wrapper -> {
            clickHandlers.forEach(consumer -> consumer.accept(wrapper));
            wrapper.cancel();
        });
        if (typeString == null) return builder.build();
        final ConditionalItem.Builder conditionalBuilder = ConditionalItem.builder(plugin);
        final var dataApplier = this.dataAppliers.get(typeString);
        if (dataApplier == null) {
            throw new SerializationException("Unknown type: " + typeString);
        }
//        switch (typeString) {
//            case TYPE_UPGRADE -> this.applyUpgradesItemData(
//                    conditionalBuilder,
//                    node.node(UPGRADE_PATH).getString(),
//                    ConditionalItem.builder(deserializeItem(plugin, clickActionSerializer, node.node(MAX_LEVEL_ITEM_PATH))).build()
//            );
//            default -> {
//            }
//        }
        return conditionalBuilder.build(builder.build()).getItem(Metadata.empty());
    }

    private static final Consumer<InventoryEventWrapper<InventoryClickEvent>> UPGRADES_INCREASE_LEVEL_ACTION =
            wrapper -> {
                final InventoryClickEvent event = wrapper.event();
                event.setCancelled(true);
                final BaseGui gui = wrapper.gui();
                final CoreUser user = gui.getMetadata(GuiKey.USER, CoreUser.class);
                final int slot = event.getSlot();
                final BaseGuiItem clicked = gui.getBaseGuiItem(slot);
                if (clicked == null) return;
                final String upgradeId = clicked.getMetadata(GuiKey.UPGRADE_ID, String.class);
                final Upgradeable<CoreUser, ?> upgradeable = gui.getMetadata(GuiKey.UPGRADEABLE, Upgradeable.class);
                if (upgradeable == null || upgradeId == null) return;
                final Upgrades<?> upgrades = upgradeable.getUpgradeHolder().getUpgrades(upgradeId);
                if (upgrades == null) return;
                upgradeable.tryLevelUpUpgrade(user, upgrades);
                final Integer newLevel = upgradeable.getUpgradeLevel(upgrades.getId());
                if (newLevel != null && newLevel >= upgrades.getMaxLevel()) {
                    ConditionalItem maxLevelItem = clicked.getMetadata(GuiKey.MAX_LEVEL_ITEM, ConditionalItem.class);
                    if (maxLevelItem == null) return;
                    gui.set(slot, maxLevelItem);
                }
                gui.refresh(slot);
            };

    @Nullable
    private ConditionalItem getUpgradeItem(Upgrades<?> upgrades, int upgradeLevel, BaseGuiItem clicked) {
        if (upgradeLevel >= upgrades.getMaxLevel()) {
            return clicked.getMetadata(GuiKey.MAX_LEVEL_ITEM, ConditionalItem.class);
        }
        return null;
    }

    public static final String MAX_LEVEL_ITEM_PATH = "max-level-item";

    public void applyUpgradesItemData(ConditionalItem.Builder builder, String upgradeId, ConditionalItem maxLevelItem) {
        final Map<Object, Object> metadata = new HashMap<>();
        metadata.put(GuiKey.INCREASE_UPGRADE_LEVEL_CONSUMER, UPGRADES_INCREASE_LEVEL_ACTION);
        metadata.put(GuiKey.UPGRADE_ID, upgradeId);
        metadata.put(GuiKey.MAX_LEVEL_ITEM, maxLevelItem);
        builder.metadata(metadata, true);
    }

    public void applyUpgradesItemData(ConditionalItem.Builder builder, ConfigurationNode node, ItemSerializers<T, Z> serializers /*String upgradeId, ConditionalItem maxLevelItem*/) {
        final String upgradeId = node.node(UPGRADE_PATH).getString();
//        final Map<Object, Object> metadata = new HashMap<>();
        try {
            final ConditionalItem maxLevelItem = ConditionalItem.builder(
                    this.deserializeItem(this.plugin, serializers, node.node(MAX_LEVEL_ITEM_PATH))
            ).build();
//            metadata.put(GuiKey.INCREASE_UPGRADE_LEVEL_CONSUMER, UPGRADES_INCREASE_LEVEL_ACTION);
//            metadata.put(GuiKey.UPGRADE_ID, upgradeId);
//            metadata.put(GuiKey.MAX_LEVEL_ITEM, maxLevelItem);
//            builder.metadata(metadata, true);
            this.applyUpgradesItemData(builder, upgradeId, maxLevelItem);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
