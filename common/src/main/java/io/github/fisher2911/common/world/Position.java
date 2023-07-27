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

public class Position {

    private final int x;
    private final int y;
    private final int z;

    private Position(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static Position of(int x, int y, int z) {
        return new Position(x, y, z);
    }

    public Position add(int x, int y, int z) {
        return new Position(this.x + x, this.y + y, this.z + z);
    }

    public Position subtract(int x, int y, int z) {
        return new Position(this.x - x, this.y - y, this.z - z);
    }

    public Position multiply(int x, int y, int z) {
        return new Position(this.x * x, this.y * y, this.z * z);
    }

    public Position divide(int x, int y, int z) {
        return new Position(this.x / x, this.y / y, this.z / z);
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

}
