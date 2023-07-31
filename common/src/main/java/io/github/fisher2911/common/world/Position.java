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

package io.github.fisher2911.common.world;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.Objects;
import java.util.UUID;

public class Position {

    private final UUID world;
    private final int x;
    private final int y;
    private final int z;

    private Position(UUID world, int x, int y, int z) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static Position of(UUID world, int x, int y, int z) {
        return new Position(world, x, y, z);
    }

    public static Position fromBukkitLocation(Location location) {
        return new Position(Objects.requireNonNull(location.getWorld()).getUID(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    public Location toBukkitLocation() {
        return new Location(Bukkit.getWorld(this.world), this.x, this.y, this.z);
    }

    public Position add(int x, int y, int z) {
        return new Position(this.world, this.x + x, this.y + y, this.z + z);
    }

    public Position subtract(int x, int y, int z) {
        return new Position(this.world, this.x - x, this.y - y, this.z - z);
    }

    public Position multiply(int x, int y, int z) {
        return new Position(this.world, this.x * x, this.y * y, this.z * z);
    }

    public Position divide(int x, int y, int z) {
        return new Position(this.world, this.x / x, this.y / y, this.z / z);
    }

    public Position divide(int i) {
        return divide(i, i, i);
    }

    public Position multiply(int i) {
        return multiply(i, i, i);
    }

    public Position add(Position position) {
        return add(position.x, position.y, position.z);
    }

    public Position subtract(Position position) {
        return subtract(position.x, position.y, position.z);
    }

    public Position multiply(Position position) {
        return multiply(position.x, position.y, position.z);
    }

    public Position divide(Position position) {
        return divide(position.x, position.y, position.z);
    }

    public int x() {
        return this.x;
    }

    public int y() {
        return this.y;
    }

    public int z() {
        return this.z;
    }

    public UUID world() {
        return this.world;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Position position = (Position) o;
        return this.x == position.x && this.y == position.y && this.z == position.z && Objects.equals(this.world, position.world);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.world, this.x, this.y, this.z);
    }

}
