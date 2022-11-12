/*
 *     Kingdoms Plugin
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

package io.github.fisher2911.fisherlib.placeholder;

import io.github.fisher2911.fisherlib.message.MessageHandler;
import io.github.fisher2911.fisherlib.user.CoreUser;
import io.github.fisher2911.fisherlib.util.MapOfMaps;
import net.kyori.adventure.text.Component;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@SuppressWarnings("unused")
public abstract class Placeholders {

    private final MapOfMaps<Class<?>, Placeholder, Function<Object, Object>> placeholders = new MapOfMaps<>(new HashMap<>(), HashMap::new);

    private static final DecimalFormat POSITION_FORMAT = new DecimalFormat("#.0");

    public void load() {
        this.put(CoreUser.class, Placeholder.USER_NAME, u -> castAndParseUser(u, CoreUser::getName));
        this.put(CoreUser.class, Placeholder.USER_BALANCE, u -> castAndParseUser(u, CoreUser::getMoney));
        this.put(CoreUser.class, Placeholder.USER_UUID, u -> castAndParseUser(u, CoreUser::getMoney));
    }

    private Object castAndParseUser(Object o, Function<CoreUser, Object> parse) {
        return castAndParse(CoreUser.class, o, parse);
    }

    private <T> Object castAndParse(Class<T> clazz, Object o, Function<T, Object> parse) {
        return parse.apply(clazz.cast(o));
    }

    private <T> void put(Class<T> clazz, Placeholder placeholder, Function<Object, Object> parse) {
        this.placeholders.put(clazz, placeholder, parse);
    }

    public String apply(String s, Object... objects) {
        for (Object o : objects) {
            s = replaceSuperClasses(s, o);
        }
        return s;
    }

    private String replaceInterfaces(String s, Object o, Class<?> clazz) {
        for (Class<?> i : clazz.getInterfaces()) {
            final var map = placeholders.get(i);
            if (map == null) continue;
            for (var entry : map.entrySet()) {
                final String key = entry.getKey().toString();
                final Object value = entry.getValue().apply(o);
                s = replace(s, key, value);
            }
        }
        return s;
    }

    private String replaceSuperClasses(String s, Object o) {
        var superClass = o.getClass();
        Map<Placeholder, Function<Object, Object>> map = null;
        while (superClass != null && (map == null || map.isEmpty())) {
            s = replaceInterfaces(s, o, superClass);
            map = this.placeholders.get(superClass);
            if (map != null && !map.isEmpty()) break;
            superClass = superClass.getSuperclass();
            map = this.placeholders.get(superClass);
        }
        if (map == null) return s;
        for (var entry : map.entrySet()) {
            final String key = entry.getKey().toString();
            final Object value = entry.getValue().apply(o);
            s = replace(s, key, value);
        }
        return s;
    }

    private static String replace(String original, String key, Object value) {
        if (value instanceof Component component) {
            return original.replace(key, MessageHandler.MINI_MESSAGE.serialize(component));
        }
        return original.replace(key, String.valueOf(value));
    }

    public Builder builder(String current) {
        return new Builder(this, current);
    }

    public static class Builder {

        private final Placeholders placeholders;
        private String current;

        private Builder(Placeholders placeholders, String current) {
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
