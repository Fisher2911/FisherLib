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

package io.github.fisher2911.common.util;

import org.bukkit.Bukkit;

public enum ServerVersion {

    UNKNOWN,
    ONE_DOT_SIXTEEN,
    ONE_DOT_SEVENTEEN,
    ONE_DOT_EIGHTEEN,
    ONE_DOT_NINETEEN,
    ONE_DOT_TWENTY;

    public static ServerVersion getServerVersion() {
        final String version = Bukkit.getVersion();
        if (version.contains("1.16")) return ONE_DOT_SIXTEEN;
        if (version.contains("1.17")) return ONE_DOT_SEVENTEEN;
        if (version.contains("1.18")) return ONE_DOT_EIGHTEEN;
        if (version.contains("1.19")) return ONE_DOT_NINETEEN;
        if (version.contains("1.20")) return ONE_DOT_TWENTY;
        return UNKNOWN;
    }

    public boolean earlierThan(ServerVersion version) {
        return this.ordinal() < version.ordinal();
    }

    public boolean laterThan(ServerVersion version) {
        return this.ordinal() > version.ordinal();
    }

}
