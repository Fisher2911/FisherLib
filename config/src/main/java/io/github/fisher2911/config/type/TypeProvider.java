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

package io.github.fisher2911.config.type;

import io.github.fisher2911.common.item.ItemBuilder;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TypeProvider {

    private final Map<Type, TypeSerializer<?>> serializers;

    private TypeProvider(Map<Type, TypeSerializer<?>> serializers) {
        this.serializers = serializers;
    }

    public static TypeProvider create() {
        final TypeProvider provider = new TypeProvider(new HashMap<>());
        provider.addDefaults();
        return provider;
    }

    public @Nullable TypeSerializer<?> getTypeSerializer(Field field) {
        final Class<?> clazz = field.getType();
        if (clazz.isAssignableFrom(Map.class)) {
            return this.getMapSerializer(field);
        }
        if (clazz.isAssignableFrom(List.class)) {
            return this.getListSerializer(field);
        }
        return this.serializers.get(clazz);
    }

    private TypeSerializer<?> getMapSerializer(Field field) {
        final ParameterizedType serializerType = (ParameterizedType) field.getGenericType();
        final TypeSerializer<?> serializer = this.serializers.get(serializerType.getActualTypeArguments()[1]);
        if (serializer == null) {
            throw new IllegalArgumentException("No serializer found for key type " + serializerType);
        }
        return MapTypeSerializer.create(serializer);
    }

    private TypeSerializer<?> getListSerializer(Field field) {
        final ParameterizedType serializerType = (ParameterizedType) field.getGenericType();
        final TypeSerializer<?> serializer = this.serializers.get(serializerType.getActualTypeArguments()[0]);
        if (serializer == null) {
            throw new IllegalArgumentException("No serializer found for key type " + serializerType);
        }
        return serializer;
    }

    public <T> TypeProvider addTypeSerializer(Class<T> clazz, TypeSerializer<T> serializer) {
        this.serializers.put(clazz, serializer);
        return this;
    }

    public void addDefaults() {
        this.serializers.put(Byte.class, ByteTypeSerializer.INSTANCE);
        this.serializers.put(byte.class, ByteTypeSerializer.INSTANCE);
        this.serializers.put(Boolean.class, BooleanTypeSerializer.INSTANCE);
        this.serializers.put(boolean.class, BooleanTypeSerializer.INSTANCE);
        this.serializers.put(double.class, DoubleTypeSerializer.INSTANCE);
        this.serializers.put(Double.class, DoubleTypeSerializer.INSTANCE);
        this.serializers.put(Float.class, FloatTypeSerializer.INSTANCE);
        this.serializers.put(float.class, FloatTypeSerializer.INSTANCE);
        this.serializers.put(Integer.class, IntTypeSerializer.INSTANCE);
        this.serializers.put(int.class, IntTypeSerializer.INSTANCE);
        this.serializers.put(Long.class, LongTypeSerializer.INSTANCE);
        this.serializers.put(long.class, LongTypeSerializer.INSTANCE);
        this.serializers.put(String.class, StringTypeSerializer.INSTANCE);
        this.serializers.put(Material.class, MaterialTypeSerializer.INSTANCE);
        this.serializers.put(ItemBuilder.class, ItemBuilderTypeSerializer.INSTANCE);
    }

}
