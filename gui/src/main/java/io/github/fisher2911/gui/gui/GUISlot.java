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

package io.github.fisher2911.gui.gui;

import io.github.fisher2911.common.util.ServerVersion;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;

public abstract class GUISlot {

    private static final ServerVersion SERVER_VERSION = ServerVersion.getServerVersion();

    public abstract @Nullable ItemStack getItem(Inventory inventory);

    public abstract void setItem(Inventory inventory, @Nullable ItemStack itemStack);

    public static GUISlot of(int slot) {
        if (slot == 0) {
            return Number.ZERO;
        }
        return new Number(slot);
    }

    public static class Potion extends GUISlot {

        public static Potion FUEL = new Potion(Slot.FUEL);
        public static Potion INGREDIENT = new Potion(Slot.INGREDIENT);
        public static Potion ZERO = new Potion(Slot.ZERO);
        public static Potion ONE = new Potion(Slot.ONE);
        public static Potion TWO = new Potion(Slot.TWO);

        private final Slot slot;

        private Potion(Slot slot) {
            this.slot = slot;
        }

        @Override
        public @Nullable ItemStack getItem(Inventory inventory) {
            return this.slot.get(inventory);
        }

        @Override
        public void setItem(Inventory inventory, @Nullable ItemStack itemStack) {
            this.slot.set(inventory, itemStack);
        }

        private enum Slot {

            FUEL((inv, item) -> inv.setItem(4, item), inv -> inv.getItem(4)),
            INGREDIENT((inv, item) -> inv.setItem(3, item), inv -> inv.getItem(3)),
            ZERO((inv, item) -> inv.setItem(0, item), inv -> inv.getItem(0)),
            ONE((inv, item) -> inv.setItem(1, item), inv -> inv.getItem(1)),
            TWO((inv, item) -> inv.setItem(2, item), inv -> inv.getItem(2));

            private final BiConsumer<Inventory, ItemStack> setter;
            private final Function<Inventory, ItemStack> getter;

            Slot(BiConsumer<Inventory, ItemStack> setter, Function<Inventory, ItemStack> getter) {
                this.setter = setter;
                this.getter = getter;
            }

            public void set(Inventory inventory, ItemStack item) {
                this.setter.accept(inventory, item);
            }

            @Nullable
            public ItemStack get(Inventory inventory) {
                return this.getter.apply(inventory);
            }
        }

    }

    public static class WorkBench extends GUISlot {

        public static final WorkBench RESULT = new WorkBench(Slot.RESULT);

        private final Slot slot;

        private WorkBench(Slot slot) {
            this.slot = slot;
        }

        @Override
        public @Nullable ItemStack getItem(Inventory inventory) {
            return this.slot.get(inventory);
        }

        @Override
        public void setItem(Inventory inventory, @Nullable ItemStack itemStack) {
            this.slot.set(inventory, itemStack);
        }

        private enum Slot {

            RESULT(0, (inv, item) -> inv.setItem(0, item), inv -> inv.getItem(0));

            private final int slot;
            private final BiConsumer<Inventory, ItemStack> setter;
            private final Function<Inventory, ItemStack> getter;

            Slot(int slot, BiConsumer<Inventory, ItemStack> setter, Function<Inventory, ItemStack> getter) {
                this.slot = slot;
                this.setter = setter;
                this.getter = getter;
            }

            public int getSlot() {
                return this.slot;
            }

            public void set(Inventory inventory, ItemStack item) {
                this.setter.accept(inventory, item);
            }

            @Nullable
            public ItemStack get(Inventory inventory) {
                return this.getter.apply(inventory);
            }
        }

    }

    public static class Furnace extends GUISlot {

        public static final Furnace SMELTING = new Furnace(Slot.SMELTING);
        public static final Furnace FUEL = new Furnace(Slot.FUEL);
        public static final Furnace RESULT = new Furnace(Slot.RESULT);

        private final Slot slot;

        private Furnace(Slot slot) {
            this.slot = slot;
        }

        @Override
        public @Nullable ItemStack getItem(Inventory inventory) {
            return this.slot.get(inventory);
        }

        @Override
        public void setItem(Inventory inventory, @Nullable ItemStack itemStack) {
            this.slot.set(inventory, itemStack);
        }

        private enum Slot {

            SMELTING((inv, item) -> inv.setItem(0, item), inv -> inv.getItem(0)),
            FUEL((inv, item) -> inv.setItem(1, item), inv -> inv.getItem(1)),
            RESULT((inv, item) -> inv.setItem(2, item), inv -> inv.getItem(2));

            private final BiConsumer<Inventory, ItemStack> setter;
            private final Function<Inventory, ItemStack> getter;

            Slot(BiConsumer<Inventory, ItemStack> setter, Function<Inventory, ItemStack> getter) {
                this.setter = setter;
                this.getter = getter;
            }

            public void set(Inventory inventory, ItemStack item) {
                this.setter.accept(inventory, item);
            }

            @Nullable
            public ItemStack get(Inventory inventory) {
                return this.getter.apply(inventory);
            }
        }
    }

    public static class SmithingTable extends GUISlot {

        public static final SmithingTable TEMPLATE = new SmithingTable(Slot.TEMPLATE);
        public static final SmithingTable BASE_ITEM = new SmithingTable(Slot.BASE_ITEM);
        public static final SmithingTable ADDITIONAL_ITEM = new SmithingTable(Slot.ADDITIONAL_ITEM);
        public static final SmithingTable RESULT = new SmithingTable(Slot.RESULT);

        public static GUISlot fromNum(int slot) {
            if (SERVER_VERSION.earlierThan(ServerVersion.ONE_DOT_TWENTY)) {
                return switch (slot) {
                    case 0 -> BASE_ITEM;
                    case 1 -> ADDITIONAL_ITEM;
                    case 2 -> RESULT;
                    default -> GUISlot.of(slot);
                };
            }
            return switch (slot) {
                case 0 -> TEMPLATE;
                case 1 -> BASE_ITEM;
                case 2 -> ADDITIONAL_ITEM;
                case 3 -> RESULT;
                default -> GUISlot.of(slot);
            };
        }

        private final Slot slot;

        private SmithingTable(Slot slot) {
            this.slot = slot;
        }

        @Override
        public @Nullable ItemStack getItem(Inventory inventory) {
            return this.slot.get(inventory);
        }

        @Override
        public void setItem(Inventory inventory, @Nullable ItemStack itemStack) {
            this.slot.set(inventory, itemStack);
        }

        private enum Slot {

            TEMPLATE((inv, item) -> inv.setItem(0, item), inv -> inv.getItem(0)),
            BASE_ITEM((inv, item) -> {
                if (SERVER_VERSION.earlierThan(ServerVersion.ONE_DOT_TWENTY)) {
                    inv.setItem(0, item);
                }
                inv.setItem(1, item);
            }, inv -> {
                if (SERVER_VERSION.earlierThan(ServerVersion.ONE_DOT_TWENTY)) {
                    return inv.getItem(0);
                }
                return inv.getItem(1);
            }),
            ADDITIONAL_ITEM((inv, item) -> {
                if (SERVER_VERSION.earlierThan(ServerVersion.ONE_DOT_TWENTY)) {
                    inv.setItem(1, item);
                }
                inv.setItem(2, item);
            }, inv -> {
                if (SERVER_VERSION.earlierThan(ServerVersion.ONE_DOT_TWENTY)) {
                    return inv.getItem(1);
                }
                return inv.getItem(2);
            }),
            RESULT((inv, item) -> {
                if (SERVER_VERSION.earlierThan(ServerVersion.ONE_DOT_TWENTY)) {
                    inv.setItem(2, item);
                }
                inv.setItem(3, item);
            }, inv -> {
                if (SERVER_VERSION.earlierThan(ServerVersion.ONE_DOT_TWENTY)) {
                    return inv.getItem(2);
                }
                return inv.getItem(3);
            });

            private final BiConsumer<Inventory, ItemStack> setter;
            private final Function<Inventory, ItemStack> getter;

            Slot(BiConsumer<Inventory, ItemStack> setter, Function<Inventory, ItemStack> getter) {
                this.setter = setter;
                this.getter = getter;
            }

            public void set(Inventory inventory, ItemStack item) {
                this.setter.accept(inventory, item);
            }

            @Nullable
            public ItemStack get(Inventory inventory) {
                return this.getter.apply(inventory);
            }
        }
    }

    public static class EnchantingTable extends GUISlot {

        public static final EnchantingTable ITEM = new EnchantingTable(Slot.ITEM);
        public static final EnchantingTable LAPIS = new EnchantingTable(Slot.LAPIS);

        private final Slot slot;

        private EnchantingTable(Slot slot) {
            this.slot = slot;
        }

        @Override
        public @Nullable ItemStack getItem(Inventory inventory) {
            return this.slot.get(inventory);
        }

        @Override
        public void setItem(Inventory inventory, @Nullable ItemStack itemStack) {
            this.slot.set(inventory, itemStack);
        }

        private enum Slot {

            ITEM((inv, item) -> inv.setItem(0, item), inv -> inv.getItem(0)),
            LAPIS((inv, item) -> inv.setItem(1, item), inv -> inv.getItem(1));

            private final BiConsumer<Inventory, ItemStack> setter;
            private final Function<Inventory, ItemStack> getter;

            Slot(BiConsumer<Inventory, ItemStack> setter, Function<Inventory, ItemStack> getter) {
                this.setter = setter;
                this.getter = getter;
            }

            public void set(Inventory inventory, ItemStack item) {
                this.setter.accept(inventory, item);
            }

            @Nullable
            public ItemStack get(Inventory inventory) {
                return this.getter.apply(inventory);
            }
        }
    }

    public static class Anvil extends GUISlot {

        public static final Anvil FIRST = new Anvil(Slot.FIRST);
        public static final Anvil SECOND = new Anvil(Slot.SECOND);
        public static final Anvil RESULT = new Anvil(Slot.RESULT);

        private final Slot slot;

        private Anvil(Slot slot) {
            this.slot = slot;
        }

        @Override
        public @Nullable ItemStack getItem(Inventory inventory) {
            return this.slot.get(inventory);
        }

        @Override
        public void setItem(Inventory inventory, @Nullable ItemStack itemStack) {
            this.slot.set(inventory, itemStack);
        }

        private enum Slot {

            FIRST((inv, item) -> inv.setItem(0, item), inv -> inv.getItem(0)),
            SECOND((inv, item) -> inv.setItem(1, item), inv -> inv.getItem(1)),
            RESULT((inv, item) -> inv.setItem(2, item), inv -> inv.getItem(2));


            private final BiConsumer<Inventory, ItemStack> setter;
            private final Function<Inventory, ItemStack> getter;

            Slot(BiConsumer<Inventory, ItemStack> setter, Function<Inventory, ItemStack> getter) {
                this.setter = setter;
                this.getter = getter;
            }

            public void set(Inventory inventory, ItemStack item) {
                this.setter.accept(inventory, item);
            }

            @Nullable
            public ItemStack get(Inventory inventory) {
                return this.getter.apply(inventory);
            }
        }
    }

    public static class Grindstone extends GUISlot {

        public static final Grindstone FIRST = new Grindstone(Slot.FIRST);
        public static final Grindstone SECOND = new Grindstone(Slot.SECOND);
        public static final Grindstone RESULT = new Grindstone(Slot.RESULT);

        private final Slot slot;

        private Grindstone(Slot slot) {
            this.slot = slot;
        }

        @Override
        public @Nullable ItemStack getItem(Inventory inventory) {
            return this.slot.get(inventory);
        }

        @Override
        public void setItem(Inventory inventory, @Nullable ItemStack itemStack) {
            this.slot.set(inventory, itemStack);
        }

        private enum Slot {

            FIRST((inv, item) -> inv.setItem(0, item), inv -> inv.getItem(0)),
            SECOND((inv, item) -> inv.setItem(1, item), inv -> inv.getItem(1)),
            RESULT((inv, item) -> inv.setItem(2, item), inv -> inv.getItem(2));


            private final BiConsumer<Inventory, ItemStack> setter;
            private final Function<Inventory, ItemStack> getter;

            Slot(BiConsumer<Inventory, ItemStack> setter, Function<Inventory, ItemStack> getter) {
                this.setter = setter;
                this.getter = getter;
            }

            public void set(Inventory inventory, ItemStack item) {
                this.setter.accept(inventory, item);
            }

            @Nullable
            public ItemStack get(Inventory inventory) {
                return this.getter.apply(inventory);
            }
        }
    }

    public static class Stonecutter extends GUISlot {

        public static final Stonecutter INPUT = new Stonecutter(Slot.FIRST);
        public static final Stonecutter RESULT = new Stonecutter(Slot.RESULT);

        private final Slot slot;

        private Stonecutter(Slot slot) {
            this.slot = slot;
        }

        @Override
        public @Nullable ItemStack getItem(Inventory inventory) {
            return this.slot.get(inventory);
        }

        @Override
        public void setItem(Inventory inventory, @Nullable ItemStack itemStack) {
            this.slot.set(inventory, itemStack);
        }

        private enum Slot {

            FIRST((inv, item) -> inv.setItem(0, item), inv -> inv.getItem(0)),
            RESULT((inv, item) -> inv.setItem(1, item), inv -> inv.getItem(1));


            private final BiConsumer<Inventory, ItemStack> setter;
            private final Function<Inventory, ItemStack> getter;

            Slot(BiConsumer<Inventory, ItemStack> setter, Function<Inventory, ItemStack> getter) {
                this.setter = setter;
                this.getter = getter;
            }

            public void set(Inventory inventory, ItemStack item) {
                this.setter.accept(inventory, item);
            }

            @Nullable
            public ItemStack get(Inventory inventory) {
                return this.getter.apply(inventory);
            }
        }
    }

    public static class Loom extends GUISlot {

        public static final Loom BANNER = new Loom(Slot.BANNER);
        public static final Loom DYE = new Loom(Slot.DYE);
        public static final Loom PATTERN = new Loom(Slot.PATTERN);
        public static final Loom RESULT = new Loom(Slot.RESULT);

        private final Slot slot;

        private Loom(Slot slot) {
            this.slot = slot;
        }

        @Override
        public @Nullable ItemStack getItem(Inventory inventory) {
            return this.slot.get(inventory);
        }

        @Override
        public void setItem(Inventory inventory, @Nullable ItemStack itemStack) {
            this.slot.set(inventory, itemStack);
        }

        private enum Slot {

            BANNER((inv, item) -> inv.setItem(0, item), inv -> inv.getItem(0)),
            DYE((inv, item) -> inv.setItem(1, item), inv -> inv.getItem(1)),
            PATTERN((inv, item) -> inv.setItem(2, item), inv -> inv.getItem(2)),
            RESULT((inv, item) -> inv.setItem(3, item), inv -> inv.getItem(3));


            private final BiConsumer<Inventory, ItemStack> setter;
            private final Function<Inventory, ItemStack> getter;

            Slot(BiConsumer<Inventory, ItemStack> setter, Function<Inventory, ItemStack> getter) {
                this.setter = setter;
                this.getter = getter;
            }

            public void set(Inventory inventory, ItemStack item) {
                this.setter.accept(inventory, item);
            }

            @Nullable
            public ItemStack get(Inventory inventory) {
                return this.getter.apply(inventory);
            }
        }
    }

    public static class CartographyTable extends GUISlot {

        public static final CartographyTable MAP = new CartographyTable(Slot.MAP);
        public static final CartographyTable PAPER = new CartographyTable(Slot.PAPER);
        public static final CartographyTable OUTPUT = new CartographyTable(Slot.OUTPUT);

        private final Slot slot;

        private CartographyTable(Slot slot) {
            this.slot = slot;
        }

        @Override
        public @Nullable ItemStack getItem(Inventory inventory) {
            return this.slot.get(inventory);
        }

        @Override
        public void setItem(Inventory inventory, @Nullable ItemStack itemStack) {
            this.slot.set(inventory, itemStack);
        }

        private enum Slot {

            MAP((inv, item) -> inv.setItem(0, item), inv -> inv.getItem(0)),
            PAPER((inv, item) -> inv.setItem(1, item), inv -> inv.getItem(1)),
            OUTPUT((inv, item) -> inv.setItem(2, item), inv -> inv.getItem(2));


            private final BiConsumer<Inventory, ItemStack> setter;
            private final Function<Inventory, ItemStack> getter;

            Slot(BiConsumer<Inventory, ItemStack> setter, Function<Inventory, ItemStack> getter) {
                this.setter = setter;
                this.getter = getter;
            }

            public void set(Inventory inventory, ItemStack item) {
                this.setter.accept(inventory, item);
            }

            @Nullable
            public ItemStack get(Inventory inventory) {
                return this.getter.apply(inventory);
            }
        }
    }

    public static class Merchant extends GUISlot {

        public static final Merchant FIRST = new Merchant(Slot.FIRST);
        public static final Merchant SECOND = new Merchant(Slot.SECOND);
        public static final Merchant RESULT = new Merchant(Slot.RESULT);

        private final Slot slot;

        private Merchant(Slot slot) {
            this.slot = slot;
        }

        @Override
        public @Nullable ItemStack getItem(Inventory inventory) {
            return this.slot.get(inventory);
        }

        @Override
        public void setItem(Inventory inventory, @Nullable ItemStack itemStack) {
            this.slot.set(inventory, itemStack);
        }

        private enum Slot {

            FIRST((inv, item) -> inv.setItem(0, item), inv -> inv.getItem(0)),
            SECOND((inv, item) -> inv.setItem(1, item), inv -> inv.getItem(1)),
            RESULT((inv, item) -> inv.setItem(2, item), inv -> inv.getItem(2));


            private final BiConsumer<Inventory, ItemStack> setter;
            private final Function<Inventory, ItemStack> getter;

            Slot(BiConsumer<Inventory, ItemStack> setter, Function<Inventory, ItemStack> getter) {
                this.setter = setter;
                this.getter = getter;
            }

            public void set(Inventory inventory, ItemStack item) {
                this.setter.accept(inventory, item);
            }

            @Nullable
            public ItemStack get(Inventory inventory) {
                return this.getter.apply(inventory);
            }
        }
    }

    public static class Number extends GUISlot {

        public static final Number ZERO = new Number(0);

        private final int slot;

        public Number(int slot) {
            this.slot = slot;
        }

        @Override
        public @Nullable ItemStack getItem(Inventory inventory) {
            return inventory.getItem(this.slot);
        }

        @Override
        public void setItem(Inventory inventory, @Nullable ItemStack itemStack) {
            inventory.setItem(this.slot, itemStack);
        }

        public int getSlot() {
            return this.slot;
        }

        @Override
        public String toString() {
            return "Number{" +
                    "slot=" + slot +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            final Number number = (Number) o;
            return getSlot() == number.getSlot();
        }

        @Override
        public int hashCode() {
            return Objects.hash(getSlot());
        }

    }

}
