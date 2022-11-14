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

package io.github.fisher2911.fisherlib.upgrade;

import io.github.fisher2911.fisherlib.FishPlugin;
import io.github.fisher2911.fisherlib.config.Config;
import io.github.fisher2911.fisherlib.user.CoreUser;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

@SuppressWarnings("unused")
public abstract class UpgradeManager<Z, U extends CoreUser> extends Config {

    public UpgradeManager(FishPlugin<?, ?> plugin, String... path) {
        super(plugin, path);
    }

    protected UpgradeHolder<Z, U> upgradeHolder;

    public UpgradeHolder<Z, U> getUpgradeHolder() {
        return upgradeHolder;
    }

    protected static final String UPGRADE_TYPE = "type";
    protected final Map<String, BiConsumer<ConfigurationNode, UpgradeManager<Z, U>>> typeUpgradeLoaders = new HashMap<>();

    public void addLoader(String type, BiConsumer<ConfigurationNode, UpgradeManager<Z, U>> loader) {
        this.typeUpgradeLoaders.put(type, loader);
    }

    public void register(Upgrades<?> upgrades) {
        this.upgradeHolder.addUpgrade(upgrades);
    }

    public void register(EntryUpgrades<?, Z, U> entryUpgrades) {
        this.upgradeHolder.addEntryUpgrade(entryUpgrades);
    }

    protected void load(ConfigurationNode node) {
        final String type = node.node(UPGRADE_TYPE).getString("");
        final var consumer = this.typeUpgradeLoaders.get(type);
        if (consumer == null) {
            this.plugin.getLogger().severe("Could not find upgrade type: " + type);
            return;
        }
        consumer.accept(node, this);
    }

    protected static final String UPGRADES = "upgrades";

    public void reload() {
        this.load();
    }

    public void load() {
        this.typeUpgradeLoaders.put(NumberUpgrades.DOUBLE_UPGRADE_TYPE, (source, manager) -> manager.register(NumberUpgrades.deserialize(source)));
        this.typeUpgradeLoaders.put(NumberUpgrades.INT_UPGRADE_TYPE, (source, manager) -> manager.register(NumberUpgrades.deserialize(source)));
        final YamlConfigurationLoader loader = YamlConfigurationLoader.
                builder().
                path(this.path).
                build();
        this.upgradeHolder = new UpgradeHolder<Z, U>(new HashMap<>(), new HashMap<>(), new ArrayList<>());
        try {
            final var source = loader.load();
            final var upgradesNode = source.node(UPGRADES);
            for (final var entry : upgradesNode.childrenMap().entrySet()) {
                this.load(entry.getValue());
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not load upgrades", e);
        }
    }

}
