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

    public @Nullable PaginatedGUI getOwner() {
        return this.metadata.get(this.getPaginatedGUIMetadataKey());
    }

    public void setOwner(PaginatedGUI owner) {
        this.metadata.set(this.getPaginatedGUIMetadataKey(), owner);
        this.getGUIItems().values().forEach(this::setItemGUI);
    }

    private MetadataKey<PaginatedGUI> getPaginatedGUIMetadataKey() {
        return MetadataKey.of(new NamespacedKey(this.plugin, PAGINATED_KEY), PaginatedGUI.class);
    }

    public void setTimer(GUITimer<? extends GUI> timer, TimerExecutor timerExecutor) {
        this.timer = timer;
        this.timerExecutor = timerExecutor;
    }

    public @Nullable GUITimer<? extends GUI> getTimer() {
        return this.timer;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends InventoryEvent, G extends GUIEvent<T>> void handle(G event, Class<G> clazz) {
        if (!this.listeners.containsKey(clazz)) return;
        final var consumer = (Consumer<G>) this.listeners.get(clazz);
        consumer.accept(event);
    }

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

    @Override
    public void tick(@Nullable Void v) {
        this.guiItems().values().forEach(item -> item.tick(this));
    }

    public void update(GUISlot slot) {
        final GUIItem guiItem = this.getItem(slot);
        if (guiItem == null) return;
        this.update(guiItem);
    }

    public void update(GUIItem item) {
        if (this.placeholders != null && this.placeholdersArgs != null) {
            this.update(item, this.placeholders, this.placeholdersArgs);
            return;
        }
        item.getSlot().setItem(this.getInventory(), item.getItemBuilder().build());
    }

    public void update(GUIItem item, Placeholders placeholders, Object... parsePlaceholders) {
        item.getSlot().setItem(this.getInventory(placeholders, parsePlaceholders), item.getItemBuilder().build(placeholders, parsePlaceholders));
    }

    private Map<GUISlot, GUIItem> guiItems() {
        return this.guiItems;
    }

    @Unmodifiable
    public Map<GUISlot, GUIItem> getGUIItems() {
        return Collections.unmodifiableMap(this.guiItems);
    }

    public void clearItems() {
        this.guiItems.clear();
    }

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

    public abstract GUISlot getDefaultPaginatedPreviousPageSlot();

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

    public void setItem(int slot, GUIItem guiItem) {
        this.setItem(GUISlot.of(slot), guiItem);
    }

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

    public void setItem(int slot, GUIItem guiItem, Placeholders placeholders, Object... parsePlaceholders) {
        this.setItem(GUISlot.of(slot), guiItem, placeholders, parsePlaceholders);
    }

    public void setItem(GUISlot slot, GUIItem guiItem, Placeholders placeholders, Object... parsePlaceholders) {
        this.guiItems.put(slot, guiItem);
        this.setItemGUI(guiItem);
        guiItem.setSlot(slot);
        guiItem.observe(i -> this.update(guiItem, placeholders, parsePlaceholders));
    }

    public boolean replaceItem(int slot, GUIItem guiItem, Predicate<@Nullable GUIItem> replacePredicate) {
        return this.replaceItem(GUISlot.of(slot), guiItem, replacePredicate);
    }

    public boolean replaceItem(GUISlot slot, GUIItem guiItem, Predicate<@Nullable GUIItem> replacePredicate) {
        if (!replacePredicate.test(this.getItem(slot))) return false;
        this.setItem(slot, guiItem);
        return true;
    }

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

    public void removeItem(GUISlot slot) {
        final GUIItem item = this.guiItems.remove(slot);
        if (item == null) return;
        item.removeGUI();
    }

    @Unmodifiable
    public Set<Player> getViewers() {
        return Collections.unmodifiableSet(this.viewers);
    }

    public String getTitle() {
        return this.title;
    }

    public Type getType() {
        return this.type;
    }

    @Override
    public @NotNull Metadata getMetadata() {
        return this.metadata;
    }

    public int getInventorySize() {
        return this.getInventory().getSize();
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void addPatterns(Collection<Pattern> patterns) {
        this.patterns.addAll(patterns);
        Collections.sort(this.patterns);
    }

    public void addPattern(Pattern pattern) {
        this.patterns.add(pattern);
    }

    private void setItemGUI(GUIItem item) {
        if (this.type == Type.PAGINATED) {
            return;
        }
        item.setGUI(this);
    }

    public void setPlaceholders(@Nullable Placeholders placeholders, @Nullable Object... placeholdersArgs) {
        this.placeholders = placeholders;
        this.placeholdersArgs = placeholdersArgs;
    }

    public enum Type {

        CHEST,
        HOPPER,
        DROPPER,
        FURNACE,
        BREWING_STAND,
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

    public static AnvilGUI.Builder anvilBuilder() {
        return AnvilGUI.builder();
    }

    public static BeaconGUI.Builder beaconBuilder() {
        return BeaconGUI.builder();
    }

    public static CartographyGUI.Builder cartographyBuilder() {
        return CartographyGUI.builder();
    }

    public static ChestGUI.Builder chestBuilder() {
        return ChestGUI.builder();
    }

    public static DropperGUI.Builder dropperBuilder() {
        return DropperGUI.builder();
    }

    public static EnchantingGUI.Builder enchantingBuilder() {
        return EnchantingGUI.builder();
    }

    public static FurnaceGUI.Builder furnaceBuilder() {
        return FurnaceGUI.builder();
    }

    public static GrindstoneGUI.Builder grindstoneBuilder() {
        return GrindstoneGUI.builder();
    }

    public static HopperGUI.Builder hopperBuilder() {
        return HopperGUI.builder();
    }

    public static LoomGUI.Builder loomBuilder() {
        return LoomGUI.builder();
    }

    public static MerchantGUI.Builder merchantBuilder() {
        return MerchantGUI.builder();
    }

    public static PotionGUI.Builder potionBuilder() {
        return PotionGUI.builder();
    }

    public static SmithingGUI.Builder smithingBuilder() {
        return SmithingGUI.builder();
    }

    public static StonecutterGUI.Builder stonecutterBuilder() {
        return StonecutterGUI.builder();
    }

    public static WorkBenchGUI.Builder workBenchBuilder() {
        return WorkBenchGUI.builder();
    }

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

        public B guiItems(Map<GUISlot, GUIItem> guiItems) {
            this.guiItems.putAll(guiItems);
            return (B) this;
        }

        public B set(GUISlot slot, GUIItem guiItem) {
            this.guiItems.put(slot, guiItem);
            return (B) this;
        }

        public B metadata(Metadata metadata) {
            this.metadata = metadata;
            return (B) this;
        }

        public B addPatterns(List<Pattern> patterns) {
            this.patterns.addAll(patterns);
            return (B) this;
        }

        public B listenClose(Consumer<GUICloseEvent> consumer) {
            this.listenClose(consumer, false);
            return (B) this;
        }

        public B listenClose(Consumer<GUICloseEvent> consumer, boolean append) {
            this.listen(GUICloseEvent.class, consumer, append);
            return (B) this;
        }

        public B listenOpen(Consumer<GUIOpenEvent> consumer) {
            return this.listenOpen(consumer, false);
        }

        public B listenOpen(Consumer<GUIOpenEvent> consumer, boolean append) {
            this.listen(GUIOpenEvent.class, consumer, append);
            return (B) this;
        }

        public abstract G build();

    }

}
