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

import io.github.fisher2911.fisherlib.economy.Price;
import io.github.fisher2911.fisherlib.user.CoreUser;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public class UpgradeHolder<Z, U extends CoreUser> {

    private final List<String> upgradeIdOrder;
    private final Map<String, Upgrades<?>> upgradesMap;
    private final Map<String, EntryUpgrades<?, Z, U>> entryUpgrades;

    public UpgradeHolder(Map<String, Upgrades<?>> upgradesMap, Map<String, EntryUpgrades<?, Z, U>> entryUpgrades, List<String> upgradeIdOrder) {
        this.upgradeIdOrder = upgradeIdOrder;
        this.upgradesMap = upgradesMap;
        this.entryUpgrades = entryUpgrades;
    }

    public void addUpgrade(Upgrades<?> upgrades) {
        this.upgradesMap.put(upgrades.getId(), upgrades);
        this.upgradeIdOrder.add(upgrades.getId());
    }

    public void addEntryUpgrade(EntryUpgrades<?, Z, U> entryUpgrades) {
        this.entryUpgrades.put(entryUpgrades.getId(), entryUpgrades);
        this.upgradeIdOrder.add(entryUpgrades.getId());
    }

    public List<String> getUpgradeIdOrder() {
        return this.upgradeIdOrder;
    }

    @Nullable
    public <T, X extends Upgrades<T>> X getUpgrades(String id, Class<X> clazz) {
        Object o = this.upgradesMap.get(id);
        if (o == null) {
            o = this.entryUpgrades.get(id);
            if (o == null) return null;
        }
        if (!clazz.isInstance(o)) return null;
        return clazz.cast(o);
    }

    @Nullable
    public Upgrades<?> getUpgrades(String id) {
        final Upgrades<?> upgrades = this.upgradesMap.get(id);
        if (upgrades == null) return this.entryUpgrades.get(id);
        return upgrades;
    }

    @Nullable
    public <T> T getValueAtLevel(String id, Class<? extends Upgrades<T>> clazz, int level) {
        final Upgrades<T> upgrades = this.getUpgrades(id, clazz);
        if (upgrades == null) return null;
        return upgrades.getValueAtLevel(level);
    }

    @Nullable
    public <T> Price getPriceAtLevel(String id, Class<? extends Upgrades<T>> clazz, int level) {
        final Upgrades<T> upgrades = this.getUpgrades(id, clazz);
        if (upgrades == null) return null;
        return upgrades.getPriceAtLevel(level);
    }

    public void handleEntry(Consumer<EntryUpgrades<?, Z, U>> consumer) {
        for (EntryUpgrades<?, Z, U> entryUpgrades : this.entryUpgrades.values()) {
            consumer.accept(entryUpgrades);
        }
    }

    public Map<String, EntryUpgrades<?, Z, U>> getEntryUpgrades() {
        return entryUpgrades;
    }

    public Map<String, Upgrades<?>> getUpgradesMap() {
        return upgradesMap;
    }

}
