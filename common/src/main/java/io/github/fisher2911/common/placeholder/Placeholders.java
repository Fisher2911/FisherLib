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

import io.github.fisher2911.common.util.collection.MapOfMaps;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class Placeholders {

    protected final MapOfMaps<Class<?>, Placeholder, Function<Object, Object>> placeholders = new MapOfMaps<>(new HashMap<>(), HashMap::new);

    private static final DecimalFormat POSITION_FORMAT = new DecimalFormat("#.0");

    public void load() {
        this.put(Player.class, Placeholder.PLAYER_NAME, p -> castAndParsePlayer(p, Player::getName));
        this.put(Player.class, Placeholder.PLAYER_UUID, p -> castAndParsePlayer(p, Player::getUniqueId));
        this.put(Player.class, Placeholder.PLAYER_HEALTH, p -> castAndParsePlayer(p, Player::getHealth));
    }

    protected Object castAndParsePlayer(Object o, Function<Player, Object> parse) {
        return castAndParse(Player.class, o, parse);
    }

    protected <T> Object castAndParse(Class<T> clazz, Object o, Function<T, Object> parse) {
        return parse.apply(clazz.cast(o));
    }

    protected <T> void put(Class<T> clazz, Placeholder placeholder, Function<Object, Object> parse) {
        this.placeholders.put(clazz, placeholder, parse);
    }

    public String apply(String s, Object... objects) {
        final Map<String, Integer> placeholderCounts = new HashMap<>();
        for (Object o : objects) {
            s = replaceSuperClasses(s, o, placeholderCounts);
        }
        return s;
    }

    protected String replaceInterfaces(String s, Object o, Class<?> clazz, Map<String, Integer> placeholderCounts) {
        final String original = s;
        for (Class<?> i : clazz.getInterfaces()) {
            final var map = placeholders.get(i);
            if (map == null) continue;
            for (var entry : map.entrySet()) {
                final String key = entry.getKey().toString();
                final Object value = entry.getValue().apply(o);
                s = replace(s, key, value, placeholderCounts);
                if (!s.equals(original)) {
                    placeholderCounts.merge(key, 1, Integer::sum);
                }
            }
        }
        return s;
    }

    protected String replaceSuperClasses(String s, Object o, Map<String, Integer> placeholderCounts) {
        var superClass = o.getClass();
        Map<Placeholder, Function<Object, Object>> map = null;
        final String original = s;
        while (superClass != null && (map == null || map.isEmpty())) {
            s = replaceInterfaces(s, o, superClass, placeholderCounts);
            map = this.placeholders.get(superClass);
            if (map != null && !map.isEmpty()) break;
            superClass = superClass.getSuperclass();
            map = this.placeholders.get(superClass);
        }
        if (map == null) return s;
        for (var entry : map.entrySet()) {
            final String key = entry.getKey().toString();
            final Object value = entry.getValue().apply(o);
            s = replace(s, key, value, placeholderCounts);
            if (!s.equals(original)) {
                placeholderCounts.merge(key, 1, Integer::sum);
            }
        }
        return s;
    }

    private static final String SEPARATOR = "_";

    protected static String replace(String original, String key, Object value, Map<String, Integer> placeholderCounts) {
        final int count = placeholderCounts.getOrDefault(key, 0);
        return original.replace(key.replace("}", SEPARATOR + count + "}"), String.valueOf(value));
    }

    public Builder builder(String current) {
        return new Builder(this, current);
    }

    public static class Builder {

        protected final Placeholders placeholders;
        protected String current;

        protected Builder(Placeholders placeholders, String current) {
            this.placeholders = placeholders;
            this.current = current;
        }

        public Builder apply(Object... objects) {
            this.current = this.placeholders.apply(this.current, objects);
            return this;
        }

        public String build() {
            return this.current;
        }

    }

}
