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

package io.github.fisher2911.common.placeholder;

public class Placeholder {

    public static final Placeholder PLAYER_NAME = fromString("player_name");
    public static final Placeholder PLAYER_UUID = fromString("player_uuid");
    public static final Placeholder PLAYER_HEALTH = fromString("player_health");

    private final String placeholder;

    private Placeholder(String placeholder) {
        this.placeholder = placeholder;
    }

    public String value() {
        return this.placeholder;
    }

    public static Placeholder fromString(String name) {
        if (name.isBlank()) throw new IllegalArgumentException("Name cannot be blank: " + name);
        final String prepend = name.charAt(0) == '{' ? "" : "{";
        final String append = name.charAt(name.length() - 1) == '}' ? "" : "}";
        return new Placeholder(prepend + name.replace(" ", "_") + append);
    }

    @Override
    public String toString() {
        return this.placeholder;
    }


}
