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

import io.github.fisher2911.fisherlib.user.CoreUser;

public interface Price {

    Price FREE = new Price() {
        @Override
        public boolean canAfford(CoreUser user) {
            return true;
        }

        @Override
        public void pay(CoreUser user) {

        }

        @Override
        public boolean payIfCanAfford(CoreUser user) {
            return true;
        }

        @Override
        public String getDisplay() {
            return "Free";
        }
    };

    Price IMPOSSIBLE = new Price() {
        @Override
        public boolean canAfford(CoreUser user) {
            return false;
        }

        @Override
        public void pay(CoreUser user) {

        }

        @Override
        public boolean payIfCanAfford(CoreUser user) {
            return false;
        }

        @Override
        public String getDisplay() {
            return "Unaffordable";
        }
    };

    static Price money(double cost) {
        return new MoneyPrice(cost);
    }

    boolean canAfford(CoreUser user);
    void pay(CoreUser user);
    boolean payIfCanAfford(CoreUser user);
    String getDisplay();

}
