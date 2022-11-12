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


import com.mojang.authlib.yggdrasil.response.User;

import javax.management.relation.RelationType;
import java.util.Set;

public interface EntryUpgrades<T, Z> extends Upgrades<T> {

    void onEnter(Z entering, User user, int level);

    void onLeave(Z leaving, User user, int level);

    Set<RelationType> appliesTo();

    boolean appliesToSelf();


}
