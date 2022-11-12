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

package io.github.fisher2911.fisherlib.economy;

import io.github.fisher2911.fisherlib.manager.Manager;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class PriceManager implements Manager<Price, String> {

    private final Map<String, Price> priceMap = new HashMap<>();

    public PriceManager() {
    }

    public Price getPrice(String priceType) {
        return this.priceMap.getOrDefault(priceType, Price.IMPOSSIBLE);
    }

    public Price getPrice(String priceType, Price def) {
        return this.priceMap.getOrDefault(priceType, def);
    }

    public void setPrice(String priceType, Price price) {
        this.priceMap.put(priceType, price);
    }

    @Override
    public Optional<Price> get(String priceType) {
        return Optional.ofNullable(this.priceMap.get(priceType));
    }

    @Override
    public Price forceGet(String priceType) {
        return this.priceMap.get(priceType);
    }

}
