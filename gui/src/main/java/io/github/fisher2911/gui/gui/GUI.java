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

import io.github.fisher2911.common.metadata.Metadata;
import io.github.fisher2911.common.metadata.MetadataKey;
import io.github.fisher2911.common.metadata.Metadatable;
import io.github.fisher2911.common.placeholder.Placeholders;
import io.github.fisher2911.common.timer.Timeable;
import io.github.fisher2911.common.timer.TimerExecutor;
import io.github.fisher2911.gui.event.GUICloseEvent;
import io.github.fisher2911.gui.event.GUIEvent;
import io.github.fisher2911.gui.event.GUIOpenEvent;
import io.github.fisher2911.gui.gui.pattern.Pattern;
import io.github.fisher2911.gui.gui.type.AnvilGUI;
import io.github.fisher2911.gui.gui.type.BeaconGUI;
import io.github.fisher2911.gui.gui.type.CartographyGUI;
import io.github.fisher2911.gui.gui.type.ChestGUI;
import io.github.fisher2911.gui.gui.type.DropperGUI;
import io.github.fisher2911.gui.gui.type.EnchantingGUI;
import io.github.fisher2911.gui.gui.type.FurnaceGUI;
import io.github.fisher2911.gui.gui.type.GrindstoneGUI;
import io.github.fisher2911.gui.gui.type.HopperGUI;
import io.github.fisher2911.gui.gui.type.LoomGUI;
import io.github.fisher2911.gui.gui.type.MerchantGUI;
import io.github.fisher2911.gui.gui.type.PotionGUI;
import io.github.fisher2911.gui.gui.type.SmithingGUI;
import io.github.fisher2911.gui.gui.type.StonecutterGUI;
import io.github.fisher2911.gui.gui.type.WorkBenchGUI;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * The base GUI class used for all GUI types.
 */
@SuppressWarnings("unused")
public abstract class GUI implements ListenerHandler, Timeable<Void>, Metadatable {

    protected final JavaPlugin plugin;
    private String title;
    private final Map<GUISlot, GUIItem> guiItems;
    private final Map<Class<? extends GUIEvent<? extends InventoryEvent>>, Consumer<? extends GUIEvent<? extends InventoryEvent>>> listeners;
    private final Type type;
    protected final Metadata metadata;
    private final Set<Player> viewers;
    protected @Nullable GUITimer<? extends GUI> timer;
    private @Nullable TimerExecutor timerExecutor;
    protected Inventory inventory;
    // order is important, earlier patterns take priority
    private final List<Pattern> patterns;
    private @Nullable Placeholders placeholders;
    private @Nullable Object[] placeholdersArgs;

    protected GUI(
            String title,
            Map<GUISlot, GUIItem> guiItems,
            Map<Class<? extends GUIEvent<? extends InventoryEvent>>, Consumer<? extends GUIEvent<? extends InventoryEvent>>> listeners,
            Type type,
            Metadata metadata,
            List<Pattern> patterns
    ) {
        this.plugin = JavaPlugin.getProvidingPlugin(this.getClass());
        this.metadata = metadata;
        this.title = title;
        this.guiItems = guiItems;
        this.guiItems.forEach((slot, item) -> {
            this.setItemGUI(item);
            item.setSlot(slot);
            item.observe(i -> this.update(item));
        });
        this.listeners = listeners;
        this.type = type;
        this.viewers = new HashSet<>();
        this.patterns = patterns;
        Collections.sort(this.patterns);
    }

    private static final String GUI_KEY = "gui";
    private static final String PAGINATED_KEY = "paginated";

    /**
     * If this GUI is part of a {@link PaginatedGUI}, this method will return the {@link PaginatedGUI} instance.
     *
     * @return the {@link PaginatedGUI} instance if this GUI is part of a {@link PaginatedGUI}, otherwise null.
     */
    public @Nullable PaginatedGUI getOwner() {
        return this.metadata.get(this.getPaginatedGUIMetadataKey());
    }

    protected void setOwner(PaginatedGUI owner) {
        this.metadata.set(this.getPaginatedGUIMetadataKey(), owner);
        this.getGUIItems().values().forEach(this::setItemGUI);
    }

    private MetadataKey<PaginatedGUI> getPaginatedGUIMetadataKey() {
        return MetadataKey.of(new NamespacedKey(this.plugin, PAGINATED_KEY), PaginatedGUI.class);
    }

    /**
     * Sets the timer of the GUI.
     *
     * @param timer         the timer to set
     * @param timerExecutor the timer executor to set
     */
    public void setTimer(GUITimer<? extends GUI> timer, TimerExecutor timerExecutor) {
        this.timer = timer;
        this.timerExecutor = timerExecutor;
    }

    /**
     * @return the {@link GUITimer} of the GUI
     */
    public @Nullable GUITimer<? extends GUI> getTimer() {
        return this.timer;
    }

    /**
     * This is used to handle {@link GUIEvent}s, mostly for internal usage.
     *
     * @param event the event to handle
     * @param clazz the class of the event
     * @param <T>   the type of the bukkit event
     * @param <G>   the type of the GUI event
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T extends InventoryEvent, G extends GUIEvent<T>> void handle(G event, Class<G> clazz) {
        if (!this.listeners.containsKey(clazz)) return;
        final var consumer = (Consumer<G>) this.listeners.get(clazz);
        consumer.accept(event);
    }

    /**
     * This sets all the {@link GUIItem}s of the GUI to the inventory.
     */
    public void populate() {
        if (this.placeholders != null && this.placeholdersArgs != null) {
            this.populate(this.placeholders, this.placeholdersArgs);
            return;
        }
        this.patterns.forEach(pattern -> pattern.apply(this));
        this.guiItems.forEach((slot, guiItem) -> slot.setItem(this.getInventory(), guiItem.getItemBuilder().build()));
        if (this.timer != null) {
            this.timer.stop();
            this.timer.start(this.timerExecutor);
        }
    }

    /**
     * This sets all the {@link GUIItem}s of the GUI to the inventory.
     *
     * @param placeholders      the placeholders to use
     * @param parsePlaceholders the parse placeholders to use
     */
    public void populate(Placeholders placeholders, Object... parsePlaceholders) {
        this.patterns.forEach(pattern -> pattern.apply(this));
        this.guiItems.forEach((slot, guiItem) -> {
                    slot.setItem(
                            this.getInventory(placeholders, parsePlaceholders),
                            guiItem.getItemBuilder().build(placeholders, parsePlaceholders));
                }
        );
        if (this.timer != null) {
            this.timer.stop();
            this.timer.start(this.timerExecutor);
        }
    }

    /**
     * This ticks all the {@link GUIItem}s of the GUI if a timer is set
     *
     * @param v - empty parameter, nothing should be passed
     */
    @Override
    public void tick(@Nullable Void v) {
        this.guiItems().values().forEach(item -> item.tick(this));
    }

    /**
     * This updates the {@link GUIItem} of the given slot.
     *
     * @param slot the slot to update
     */
    public void update(GUISlot slot) {
        final GUIItem guiItem = this.getItem(slot);
        if (guiItem == null) return;
        this.update(guiItem);
    }

    /**
     * This updates the {@link GUIItem} using the {@link GUIItem#getSlot()}
     *
     * @param item the item to update
     */
    public void update(GUIItem item) {
        if (this.placeholders != null && this.placeholdersArgs != null) {
            this.update(item, this.placeholders, this.placeholdersArgs);
            return;
        }
        item.getSlot().setItem(this.getInventory(), item.getItemBuilder().build());
    }

    /**
     * This updates the {@link GUIItem} using the {@link GUIItem#getSlot()}
     *
     * @param item              the item to update
     * @param placeholders      the placeholders to use
     * @param parsePlaceholders the parse placeholders to use
     */
    public void update(GUIItem item, Placeholders placeholders, Object... parsePlaceholders) {
        item.getSlot().setItem(this.getInventory(placeholders, parsePlaceholders), item.getItemBuilder().build(placeholders, parsePlaceholders));
    }

    private Map<GUISlot, GUIItem> guiItems() {
        return this.guiItems;
    }

    /**
     * @return the map of {@link GUISlot} and {@link GUIItem} of the GUI in an unmodifiable map
     */
    @Unmodifiable
    public Map<GUISlot, GUIItem> getGUIItems() {
        return Collections.unmodifiableMap(this.guiItems);
    }

    /**
     * Clears all the {@link GUIItem}s of the GUI.
     */
    public void clearItems() {
        this.guiItems.clear();
    }

    /**
     * @param slot the slot to get the {@link GUIItem} from
     * @return the {@link GUIItem} of the given slot
     */
    public @Nullable GUIItem getItem(GUISlot slot) {
        return this.guiItems.get(slot);
    }

    protected Inventory getInventory() {
        if (this.placeholders != null && this.placeholdersArgs != null) {
            return this.getInventory(this.placeholders, this.placeholdersArgs);
        }
        if (this.inventory == null) {
            return this.createInventory(this.title);
        }
        return this.inventory;
    }

    protected Inventory getInventory(Placeholders placeholders, Object... parsePlaceholders) {
        if (this.inventory == null) {
            return this.createInventory(placeholders.apply(this.title, parsePlaceholders));
        }
        return this.inventory;
    }

    protected abstract Inventory createInventory(String title);

    /**
     * @return the slot of the {@link GUIItem} that will be used to go to the previous page in a paginated GUI
     */
    public abstract GUISlot getDefaultPaginatedPreviousPageSlot();

    /**
     * @return the slot of the {@link GUIItem} that will be used to go to the next page in a paginated GUI
     */
    public abstract GUISlot getDefaultPaginatedNextPageSlot();

    protected void open(Player viewer) {
        this.viewers.add(viewer);
        viewer.openInventory(this.getInventory());
        viewer.updateInventory();
    }

    protected void removeViewer(Player viewer) {
        this.removeViewers(List.of(viewer));
    }

    protected void removeViewers(Collection<Player> viewers) {
        this.viewers.removeAll(viewers);
        if (this.viewers.isEmpty() && this.timer != null && this.timer.isRunning()) {
            this.timer.stop();
        }
    }

    protected void open(Collection<Player> players) {
        players.forEach(this::open);
    }

    /**
     * This sets the {@link GUIItem} of the given slot.
     *
     * @param slot    the slot to set the {@link GUIItem} to
     * @param guiItem the {@link GUIItem} to set
     */
    public void setItem(int slot, GUIItem guiItem) {
        this.setItem(GUISlot.of(slot), guiItem);
    }

    /**
     * This sets the {@link GUIItem} of the given slot.
     *
     * @param slot    the slot to set the {@link GUIItem} to
     * @param guiItem the {@link GUIItem} to set
     */
    public void setItem(GUISlot slot, GUIItem guiItem) {
        if (this.placeholders != null && this.placeholdersArgs != null) {
            this.setItem(slot, guiItem, placeholders, this.placeholdersArgs);
            return;
        }
        this.guiItems.put(slot, guiItem);
        this.setItemGUI(guiItem);
        guiItem.setSlot(slot);
        guiItem.observe(i -> this.update(guiItem));
    }

    /**
     * This sets the {@link GUIItem} of the given slot.
     *
     * @param slot              the slot to set the {@link GUIItem} to
     * @param guiItem           the {@link GUIItem} to set
     * @param placeholders      the placeholders to use
     * @param parsePlaceholders the parse placeholders to use
     */
    public void setItem(int slot, GUIItem guiItem, Placeholders placeholders, Object... parsePlaceholders) {
        this.setItem(GUISlot.of(slot), guiItem, placeholders, parsePlaceholders);
    }

    /**
     * This sets the {@link GUIItem} of the given slot.
     *
     * @param slot              the slot to set the {@link GUIItem} to
     * @param guiItem           the {@link GUIItem} to set
     * @param placeholders      the placeholders to use
     * @param parsePlaceholders the parse placeholders to use
     */
    public void setItem(GUISlot slot, GUIItem guiItem, Placeholders placeholders, Object... parsePlaceholders) {
        this.guiItems.put(slot, guiItem);
        this.setItemGUI(guiItem);
        guiItem.setSlot(slot);
        guiItem.observe(i -> this.update(guiItem, placeholders, parsePlaceholders));
    }

    /**
     * This replaces the {@link GUIItem} of the given slot only if the
     * {@code replacePredicate} returns {@code true}
     *
     * @param slot             the slot to set the {@link GUIItem} to
     * @param guiItem          the {@link GUIItem} to set
     * @param replacePredicate the predicate to test if the {@link GUIItem} should be replaced,
     *                         it tests against the current {@link GUIItem} of the slot
     * @return {@code true} if the {@link GUIItem} was replaced, {@code false} otherwise
     */
    public boolean replaceItem(int slot, GUIItem guiItem, Predicate<@Nullable GUIItem> replacePredicate) {
        return this.replaceItem(GUISlot.of(slot), guiItem, replacePredicate);
    }

    /**
     * This replaces the {@link GUIItem} of the given slot only if the
     * {@code replacePredicate} returns {@code true}
     *
     * @param slot             the slot to set the {@link GUIItem} to
     * @param guiItem          the {@link GUIItem} to set
     * @param replacePredicate the predicate to test if the {@link GUIItem} should be replaced,
     *                         it tests against the current {@link GUIItem} of the slot
     * @return {@code true} if the {@link GUIItem} was replaced, {@code false} otherwise
     */
    public boolean replaceItem(GUISlot slot, GUIItem guiItem, Predicate<@Nullable GUIItem> replacePredicate) {
        if (!replacePredicate.test(this.getItem(slot))) return false;
        this.setItem(slot, guiItem);
        return true;
    }

    /**
     * This replaces the {@link GUIItem} of the given slot only if the
     * {@code replacePredicate} returns {@code true}
     *
     * @param slot              the slot to set the {@link GUIItem} to
     * @param guiItem           the {@link GUIItem} to set
     * @param replacePredicate  the predicate to test if the {@link GUIItem} should be replaced,
     *                          it tests against the current {@link GUIItem} of the slot
     * @param placeholders      the placeholders to use
     * @param parsePlaceholders the parse placeholders to use
     * @return {@code true} if the {@link GUIItem} was replaced, {@code false} otherwise
     */
    public boolean replaceItem(
            GUISlot slot,
            GUIItem guiItem,
            Predicate<@Nullable GUIItem> replacePredicate,
            Placeholders placeholders,
            Object... parsePlaceholders
    ) {
        if (!replacePredicate.test(this.getItem(slot))) return false;
        this.setItem(slot, guiItem, placeholders, parsePlaceholders);
        return true;
    }

    /**
     * This removes the {@link GUIItem} of the given slot.
     *
     * @param slot the slot to set the {@link GUIItem} to
     */
    public void removeItem(GUISlot slot) {
        final GUIItem item = this.guiItems.remove(slot);
        if (item == null) return;
        item.removeGUI();
    }

    /**
     * @return the {@link Player}s that are viewing this {@link GUI}
     */
    @Unmodifiable
    public Set<Player> getViewers() {
        return Collections.unmodifiableSet(this.viewers);
    }

    /**
     * @return the {@link String} title of this {@link GUI}
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * @return the {@link Type} of this {@link GUI}
     */
    public Type getType() {
        return this.type;
    }

    /**
     * @return the {@link Metadata} of this {@link GUI}
     */
    @Override
    public @NotNull Metadata getMetadata() {
        return this.metadata;
    }

    /**
     * @return the amount of slots in the inventory
     */
    public int getInventorySize() {
        return this.getInventory().getSize();
    }

    /**
     * @param title the new title of this {@link GUI}
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @param patterns the {@link Pattern}s that should be added to this {@link GUI}
     */
    public void addPatterns(Collection<Pattern> patterns) {
        this.patterns.addAll(patterns);
        Collections.sort(this.patterns);
    }

    /**
     * @param pattern the {@link Pattern} that should be added to this {@link GUI}
     */
    public void addPattern(Pattern pattern) {
        this.patterns.add(pattern);
    }

    private void setItemGUI(GUIItem item) {
        if (this.type == Type.PAGINATED) {
            return;
        }
        item.setGUI(this);
    }

    /**
     * @param placeholders     the {@link Placeholders} to use
     * @param placeholdersArgs the placeholders args to use
     */
    public void setPlaceholders(@Nullable Placeholders placeholders, @Nullable Object... placeholdersArgs) {
        this.placeholders = placeholders;
        this.placeholdersArgs = placeholdersArgs;
    }

    /**
     * The {@link GUI} type
     */
    public enum Type {

        CHEST,
        HOPPER,
        DROPPER,
        FURNACE,
        POTION,
        SMITHING_TABLE,
        WORK_BENCH,
        ENCHANTING,
        ANVIL,
        LOOM,
        CARTOGRAPHY,
        GRINDSTONE,
        STONECUTTER,
        MERCHANT,
        BEACON,
        PAGINATED

    }

    /**
     * @return a new {@link GUI.Builder}
     */
    public static AnvilGUI.Builder anvilBuilder() {
        return AnvilGUI.builder();
    }

    /**
     * @return a new {@link BeaconGUI.Builder}
     */
    public static BeaconGUI.Builder beaconBuilder() {
        return BeaconGUI.builder();
    }

    /**
     * @return a new {@link CartographyGUI.Builder}
     */
    public static CartographyGUI.Builder cartographyBuilder() {
        return CartographyGUI.builder();
    }

    /**
     * @return a new {@link ChestGUI.Builder}
     */
    public static ChestGUI.Builder chestBuilder() {
        return ChestGUI.builder();
    }

    /**
     * @return a new {@link DropperGUI.Builder}
     */
    public static DropperGUI.Builder dropperBuilder() {
        return DropperGUI.builder();
    }

    /**
     * @return a new {@link EnchantingGUI.Builder}
     */
    public static EnchantingGUI.Builder enchantingBuilder() {
        return EnchantingGUI.builder();
    }

    /**
     * @return a new {@link FurnaceGUI.Builder}
     */
    public static FurnaceGUI.Builder furnaceBuilder() {
        return FurnaceGUI.builder();
    }

    /**
     * @return a new {@link GrindstoneGUI.Builder}
     */
    public static GrindstoneGUI.Builder grindstoneBuilder() {
        return GrindstoneGUI.builder();
    }

    /**
     * @return a new {@link HopperGUI.Builder}
     */
    public static HopperGUI.Builder hopperBuilder() {
        return HopperGUI.builder();
    }

    /**
     * @return a new {@link LoomGUI.Builder}
     */
    public static LoomGUI.Builder loomBuilder() {
        return LoomGUI.builder();
    }

    /**
     * @return a new {@link MerchantGUI.Builder}
     */
    public static MerchantGUI.Builder merchantBuilder() {
        return MerchantGUI.builder();
    }

    /**
     * @return a new {@link PotionGUI.Builder}
     */
    public static PotionGUI.Builder potionBuilder() {
        return PotionGUI.builder();
    }

    /**
     * @return a new {@link SmithingGUI.Builder}
     */
    public static SmithingGUI.Builder smithingBuilder() {
        return SmithingGUI.builder();
    }

    /**
     * @return a new {@link StonecutterGUI.Builder}
     */
    public static StonecutterGUI.Builder stonecutterBuilder() {
        return StonecutterGUI.builder();
    }

    /**
     * @return a new {@link WorkBenchGUI.Builder}
     */
    public static WorkBenchGUI.Builder workBenchBuilder() {
        return WorkBenchGUI.builder();
    }

    /**
     * @param guiManager the {@link GUIManager} to use
     * @return a new {@link PaginatedGUI.Builder}
     */
    public static PaginatedGUI.Builder paginatedBuilder(GUIManager guiManager) {
        return PaginatedGUI.builder(guiManager);
    }

    @SuppressWarnings("unchecked")
    public static abstract class Builder<B extends Builder<B, G>, G extends GUI> extends ListenerHandler.Builder<B> {

        protected String title;
        protected Map<GUISlot, GUIItem> guiItems;
        protected Metadata metadata = Metadata.mutableEmpty();
        protected List<Pattern> patterns = new ArrayList<>();

        protected Builder() {
            this.guiItems = new HashMap<>();
        }

        public B title(String title) {
            this.title = title;
            return (B) this;
        }

        /**
         * @param guiItems the {@link Map}s to use
         * @return this {@link Builder}
         */
        public B guiItems(Map<GUISlot, GUIItem> guiItems) {
            this.guiItems.putAll(guiItems);
            return (B) this;
        }

        /**
         * Sets the {@link GUIItem} for the given {@link GUISlot}
         *
         * @param slot    the {@link GUISlot} to use
         * @param guiItem the {@link GUIItem} to use
         * @return this {@link Builder}
         */
        public B set(GUISlot slot, GUIItem guiItem) {
            this.guiItems.put(slot, guiItem);
            return (B) this;
        }

        /**
         * @param metadata the {@link Metadata} to use
         * @return this {@link Builder}
         */
        public B metadata(Metadata metadata) {
            this.metadata = metadata;
            return (B) this;
        }

        /**
         * @param patterns the {@link Pattern}s to use
         * @return this {@link Builder}
         */
        public B addPatterns(List<Pattern> patterns) {
            this.patterns.addAll(patterns);
            return (B) this;
        }

        /**
         * @param consumer the {@link Consumer} to use
         * @return this {@link Builder}
         */
        public B listenClose(Consumer<GUICloseEvent> consumer) {
            this.listenClose(consumer, false);
            return (B) this;
        }

        /**
         * @param consumer the {@link Consumer} to use
         * @param append   whether to append the {@link Consumer} to the previous one or not
         * @return this {@link Builder}
         */
        public B listenClose(Consumer<GUICloseEvent> consumer, boolean append) {
            this.listen(GUICloseEvent.class, consumer, append);
            return (B) this;
        }

        /**
         * @param consumer the {@link Consumer} to use
         * @return this {@link Builder}
         */
        public B listenOpen(Consumer<GUIOpenEvent> consumer) {
            return this.listenOpen(consumer, false);
        }

        /**
         * @param consumer the {@link Consumer} to use
         * @param append   whether to append the {@link Consumer} to the previous one or not
         * @return this {@link Builder}
         */
        public B listenOpen(Consumer<GUIOpenEvent> consumer, boolean append) {
            this.listen(GUIOpenEvent.class, consumer, append);
            return (B) this;
        }

        /**
         * @return a new {@link G}
         */
        public abstract G build();

    }

}
