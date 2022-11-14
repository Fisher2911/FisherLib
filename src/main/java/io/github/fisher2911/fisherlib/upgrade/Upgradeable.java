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
import io.github.fisher2911.fisherlib.util.collections.DirtyMap;
import org.jetbrains.annotations.Nullable;

public interface Upgradeable<U extends CoreUser, Z> {

    UpgradeHolder<Z, U> getUpgradeHolder();

    void setUpgradeLevel(String id, int level);

    @Nullable
    Integer getUpgradeLevel(String id);

    Price getUpgradePrice(String id);

    <T> T getUpgradesValue(String id, Class<? extends Upgrades<T>> clazz);

    DirtyMap<String, Integer> getUpgradeLevels();

    void tryLevelUpUpgrade(U user, Upgrades<?> upgrades);

}
