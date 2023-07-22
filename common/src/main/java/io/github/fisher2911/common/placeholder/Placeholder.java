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

/**
 * Represents a placeholder to be used in {@link Placeholders}
 * It takes the format of "{{@link Placeholder#value()}_index}" where the index is the index of the object
 * passed in {@link Placeholders#apply(String, Object...)} that is the same class
 */
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

    private static final char L_BRACKET_CHAR = '{';
    private static final char R_BRACKET_CHAR = '}';
    private static final String L_BRACKET = "{";
    private static final String R_BRACKET = "}";
    private static final String UNDERSCORE = "_";
    private static final String SPACE = " ";

    /**
     * @param name the name of the placeholder
     * @return a placeholder with the given name in the format of "{{@code name}}"
     */
    public static Placeholder fromString(String name) {
        if (name.isBlank()) throw new IllegalArgumentException("Name cannot be blank: " + name);
        final String prepend = name.charAt(0) == L_BRACKET_CHAR ? "" : L_BRACKET;
        final String append = name.charAt(name.length() - 1) == R_BRACKET_CHAR ? "" : R_BRACKET;
        return new Placeholder(prepend + name.replace(SPACE, UNDERSCORE) + append);
    }

    @Override
    public String toString() {
        return this.placeholder;
    }


}
