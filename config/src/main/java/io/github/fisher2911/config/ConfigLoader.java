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

package io.github.fisher2911.config;

import io.github.fisher2911.config.annotation.Config;
import io.github.fisher2911.config.annotation.ConfigPath;
import io.github.fisher2911.config.type.TypeProvider;
import io.github.fisher2911.config.type.TypeSerializer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;

public class ConfigLoader {

    public static <T> T load(
            Class<T> configObjectClass,
            TypeProvider typeProvider,
            NamingStrategy namingStrategy
    ) {
        try {
            final Constructor<T> constructor = configObjectClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            final Object configObject = constructor.newInstance();
            final Class<?> clazz = configObject.getClass();
            final Config config = clazz.getAnnotation(Config.class);
            if (config == null) {
                throw new IllegalArgumentException("Class " + clazz.getName() + " is not annotated with @Config");
            }
            final JavaPlugin plugin = JavaPlugin.getProvidingPlugin(clazz);
            Path path = plugin.getDataFolder().toPath();
            for (String filePath : config.filePath()) {
                path = path.resolve(filePath);
            }
            final File file = path.toFile();
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                try {
                    plugin.saveResource(String.join("/", config.filePath()), false);
                    file.createNewFile();
                } catch (IOException e) {
                    throw new RuntimeException("Failed to create file " + file.getAbsolutePath(), e);
                }
            }
            final FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(file);
            for (final Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);
                final ConfigPath configPath = field.getAnnotation(ConfigPath.class);
                if (configPath == null) continue;
                final TypeSerializer<?> serializer = typeProvider.getTypeSerializer(field);
                if (serializer == null) continue;
                try {
                    final String pathString = namingStrategy.apply(configPath.value().isEmpty() ? field.getName() : configPath.value());
                    if (field.getType().isAssignableFrom(List.class)) {
                        field.set(configObject, serializer.loadList(fileConfiguration, pathString));
                        continue;
                    }
                    field.set(configObject, serializer.load(fileConfiguration, pathString));
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Failed to set field " + field.getName() + " in class " + clazz.getName(), e);
                }
            }
            return (T) configObject;
        } catch (InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("Failed to instantiate class " + configObjectClass.getName(), e);
        }
    }

    public enum NamingStrategy {
        SNAKE_CASE(s -> separateWords(s).replace("_", "-")),
        KEBAB_CASE(s -> separateWords(s).replace("_", "-")),
        CAMEL_CASE(s -> Character.toLowerCase(s.charAt(0)) + separateWords(s).substring(1).replace(" ", "")),
        PASCAL_CASE(s -> Character.toUpperCase(s.charAt(0)) + separateWords(s).substring(1).replace(" ", ""));

        private final Function<String, String> function;

        NamingStrategy(Function<String, String> function) {
            this.function = function;
        }

        public String apply(String s) {
            return this.function.apply(s);
        }

        private static String separateWords(String string) {
            final StringBuilder builder = new StringBuilder();
            for (final char c : string.toCharArray()) {
                if (Character.isUpperCase(c)) {
                    builder.append("_");
                }
                builder.append(c);
            }
            return builder.toString();
        }
    }

}
