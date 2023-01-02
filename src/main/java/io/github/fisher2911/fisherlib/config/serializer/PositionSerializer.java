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
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.lang.reflect.Type;

public class PositionSerializer implements SavedTypeSerializer<Position> {

    public static final PositionSerializer INSTANCE = new PositionSerializer();

    private PositionSerializer() {}

    private static final String X_FIELD = "x";
    private static final String Y_FIELD = "y";
    private static final String Z_FIELD = "z";
    private static final String YAW_FIELD = "yaw";
    private static final String PITCH_FIELD = "pitch";

    @Override
    public Position deserialize(Type type, ConfigurationNode node) throws SerializationException {
        final double x = node.node(X_FIELD).getDouble();
        final double y = node.node(Y_FIELD).getDouble();
        final double z = node.node(Z_FIELD).getDouble();
        final float yaw = (float) node.node(YAW_FIELD).getDouble();
        final float pitch = (float) node.node(PITCH_FIELD).getDouble();
        return new Position(x, y, z, yaw, pitch);
    }

    @Override
    public void serialize(Type type, @Nullable Position obj, ConfigurationNode node) throws SerializationException {
        if (obj == null) return;
        node.node(X_FIELD).set(obj.x());
        node.node(Y_FIELD).set(obj.y());
        node.node(Z_FIELD).set(obj.z());
        node.node(YAW_FIELD).set(obj.yaw());
        node.node(PITCH_FIELD).set(obj.pitch());
    }

    @Override
    public Class<Position> getType() {
        return Position.class;
    }

}
