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

import org.jetbrains.annotations.Nullable;

public class EnumUtils {

    /**
     * @param enumAsString Enum value as a string to be parsed
     * @param enumClass    enum type enumAsString is to be converted to
     * @param defaultEnum  default value to be returned
     * @return enumAsString as an enum, or default enum if it could not be parsed
     */
    public static <E extends Enum<E>> E stringToEnum(
            String enumAsString,
            final Class<E> enumClass,
            E defaultEnum
    ) {
        try {
            return Enum.valueOf(enumClass, enumAsString);
        } catch (final IllegalArgumentException exception) {
            return defaultEnum;
        }
    }

    public static <E extends Enum<E>> @Nullable E tryParseEnum(
            String enumAsString,
            final Class<E> enumClass
    ) {
        return stringToEnum(enumAsString, enumClass, null);
    }


}
