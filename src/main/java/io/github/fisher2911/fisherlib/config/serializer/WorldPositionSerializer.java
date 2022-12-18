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

package io.github.fisher2911.fisherlib.config.serializer;

import io.github.fisher2911.fisherlib.world.Position;
import io.github.fisher2911.fisherlib.world.WorldPosition;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.Objects;
import java.util.UUID;

public class WorldPositionSerializer implements TypeSerializer<WorldPosition> {

    public static final WorldPositionSerializer INSTANCE = new WorldPositionSerializer();

    private WorldPositionSerializer() {}

    private static final String WORLD_FIELD = "world";

    @Override
    public WorldPosition deserialize(Type type, ConfigurationNode node) throws SerializationException {
        final Position position = PositionSerializer.INSTANCE.deserialize(type, node);
        final String world = Objects.requireNonNull(node.node(WORLD_FIELD).getString(), "World cannot be null");
        return new WorldPosition(UUID.fromString(world), position);
    }

    @Override
    public void serialize(Type type, @Nullable WorldPosition obj, ConfigurationNode node) throws SerializationException {
        if (obj == null) return;
        PositionSerializer.INSTANCE.serialize(type, obj.position(), node);
        node.node(WORLD_FIELD).set(obj.world().toString());
    }

}
