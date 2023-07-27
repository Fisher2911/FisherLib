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
import org.bukkit.Chunk;

import java.util.Objects;
import java.util.UUID;

public class ChunkPosition {

    private final UUID world;
    private final int x;
    private final int z;

    private ChunkPosition(UUID world, int x, int z) {
        this.world = world;
        this.x = x;
        this.z = z;
    }

    public static ChunkPosition of(UUID world, int x, int z) {
        return new ChunkPosition(world, x, z);
    }

    public Chunk toBukkitChunk() {
        return Bukkit.getWorld(this.world).getChunkAt(this.x, this.z);
    }

    public UUID world() {
        return this.world;
    }

    public int x() {
        return this.x;
    }

    public int z() {
        return this.z;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final ChunkPosition that = (ChunkPosition) o;
        return x == that.x && z == that.z && Objects.equals(this.world, that.world);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.world, this.x, this.z);
    }

}
