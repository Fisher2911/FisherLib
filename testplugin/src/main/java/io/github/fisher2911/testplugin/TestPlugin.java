package io.github.fisher2911.testplugin;

import io.github.fisher2911.common.item.ItemBuilder;
import io.github.fisher2911.common.placeholder.Placeholders;
import io.github.fisher2911.common.timer.BukkitTimerExecutor;
import io.github.fisher2911.gui.gui.GUI;
import io.github.fisher2911.gui.gui.GUIItem;
import io.github.fisher2911.gui.gui.GUIManager;
import io.github.fisher2911.gui.gui.GUISlot;
import io.github.fisher2911.gui.gui.GUITimer;
import io.github.fisher2911.gui.gui.PaginatedGUI;
import io.github.fisher2911.gui.gui.pattern.Pattern;
import io.github.fisher2911.gui.gui.type.ChestGUI;
import io.github.fisher2911.gui.gui.type.DropperGUI;
import io.github.fisher2911.gui.gui.type.HopperGUI;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class TestPlugin extends JavaPlugin implements Listener {

    private GUIManager<TestPlugin> guiManager;

    @Override
    public void onEnable() {
        this.guiManager = new GUIManager<>(
                this,
                new ConcurrentHashMap<>()
        );
        this.getServer().getPluginManager().registerEvents(this, this);
        this.getServer().getPluginManager().registerEvents(this.guiManager, this);
    }

    @Override
    public void onDisable() {
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        final Player player = event.getPlayer();
        final Placeholders placeholders = new Placeholders();
        placeholders.load();
        final ItemBuilder previousPageItem = ItemBuilder.from(Material.ARROW)
                .name(ChatColor.RED + "Previous Page");
        final ItemBuilder nextPageItem = ItemBuilder.from(Material.ARROW)
                .name(ChatColor.RED + "Next Page");
        final Pattern<TestPlugin> pattern = Pattern.borderPattern(
                this,
                List.of(
                        GUIItem.<TestPlugin>builder(ItemBuilder.from(Material.BLUE_STAINED_GLASS_PANE)
                                        .name(ChatColor.RED + "Border Item").build())
                                .build(),
                        GUIItem.<TestPlugin>builder(ItemBuilder.from(Material.RED_STAINED_GLASS_PANE)
                                        .name(ChatColor.RED + "Border Item").build())
                                .build(),
                        GUIItem.<TestPlugin>builder(ItemBuilder.from(Material.BLACK_STAINED_GLASS_PANE)
                                        .name(ChatColor.RED + "Border Item").build())
                                .build(),
                        GUIItem.<TestPlugin>builder(ItemBuilder.from(Material.PURPLE_STAINED_GLASS_PANE)
                                        .name(ChatColor.RED + "Border Item").build())
                                .build()
                ),
                1
        );
        final ItemBuilder newItemBuilder = ItemBuilder.from(Material.RED_MUSHROOM)
                .name(ChatColor.GREEN + "Health Tracker - {player_name_0}")
                .lore("", ChatColor.GRAY + "Health: {player_health_0}", ChatColor.RED + "Click to set random health")
                .enchant(Enchantment.DAMAGE_ALL, 1)
                .flag(ItemFlag.HIDE_ENCHANTS);
        final ItemBuilder hopperItemBuilder = ItemBuilder.from(Material.GOLD_INGOT)
                .name(ChatColor.GREEN + "UUID of - {player_name_0}")
                .lore("", ChatColor.GRAY + "{player_uuid_0}")
                .flag(ItemFlag.HIDE_ENCHANTS);
        final ItemBuilder dropperItemBuilder = ItemBuilder.from(Material.ENCHANTED_GOLDEN_APPLE)
                .name(ChatColor.GREEN + "{player_name_0}'s Golden Apple")
                .flag(ItemFlag.HIDE_ENCHANTS);
        final GUIItem<TestPlugin> guiItem = GUIItem.<TestPlugin>builder(newItemBuilder)
                .listenClick(e -> {
                    final double randomHealth = Math.min(Math.max(1, Math.random() * 21), 20);
                    player.setHealth(randomHealth);
                })
                .timer((item, gui) -> {
                    item.setItemBuilder(item.getItemBuilder().name("Timer ticked: " + gui.getTimer().getLoopCount()));
                    gui.update(item, placeholders, player, player);
                })
                .build();
        final GUIItem<TestPlugin> hopperItem = GUIItem.<TestPlugin>builder(hopperItemBuilder)
                .listenClick(e -> player.sendMessage("You clicked the gold ingot"))
                .timer((item, gui) -> {
                    item.setItemBuilder(item.getItemBuilder().name(ChatColor.GREEN + "Health Tracker - {player_name_0}")
                            .lore(
                                    "",
                                    ChatColor.GRAY + "Health: {player_health_0}",
                                    ChatColor.RED + "Click to set random health",
                                    ChatColor.AQUA + "Seconds passed: " + gui.getTimer().getLoopCount()
                            )
                            .enchant(Enchantment.DAMAGE_ALL, 1)
                            .flag(ItemFlag.HIDE_ENCHANTS)
                    );
                    gui.update(item, placeholders, player, player);
                })
                .build();
        final GUIItem<TestPlugin> dropperItem = GUIItem.<TestPlugin>builder(dropperItemBuilder)
                .listenClick(e -> player.sendMessage("You clicked the enchanted golden apple!"))
                .build();
        final ChestGUI<TestPlugin> gui = GUI.<TestPlugin>chestBuilder()
                .title("Test GUI")
                .rows(6)
                .guiItems(Map.of(GUISlot.of(10), guiItem))
                .listenClick(e -> player.sendMessage("You clicked the chest GUI!"))
                .addPatterns(List.of(pattern))
                .cancelTopInventoryClick()
                .cancelTopInventoryDrag()
                .build();
        final HopperGUI<TestPlugin> hopperGUI = GUI.<TestPlugin>hopperBuilder()
                .title("Test Hopper GUI")
                .guiItems(Map.of(GUISlot.of(3), hopperItem))
                .listenClick(e -> player.sendMessage("You clicked the hopper GUI!"))
                .cancelTopInventoryClick()
                .cancelTopInventoryDrag()
                .build();
        final DropperGUI<TestPlugin> dropperGUI = GUI.<TestPlugin>dropperBuilder()
                .title("Test Hopper GUI")
                .guiItems(Map.of(GUISlot.of(4), dropperItem))
                .addPatterns(List.of(pattern))
                .listenClick(e -> player.sendMessage("You clicked the dropper GUI!"))
                .cancelTopInventoryClick()
                .cancelTopInventoryDrag()
                .build();
        final BukkitTimerExecutor executor = BukkitTimerExecutor.sync(this, this.getServer().getScheduler());
        final GUITimer<TestPlugin, ChestGUI<TestPlugin>> timer = new GUITimer<>(
                t -> executor,
                20,
                20,
                gui
        );
        final PaginatedGUI<TestPlugin> paginated = GUI.paginatedBuilder(this, this.guiManager)
                .addPages(List.of(gui, dropperGUI, hopperGUI))
                .pagePatterns(List.of(Pattern.paginatedPattern(
                        this,
                        GUIItem.<TestPlugin>builder(previousPageItem).build(),
                        GUIItem.<TestPlugin>builder(nextPageItem).build(),
                        0
                )))
                .build();
        paginated.setPlaceholders(placeholders, player, player);
        gui.setPlaceholders(placeholders, player, player);
        dropperGUI.setPlaceholders(placeholders, player, player);
        hopperGUI.setPlaceholders(placeholders, player, player);
        gui.setTimer(timer, executor);
        paginated.populate(placeholders, player, player);
        this.guiManager.openGUI(paginated, player);
        player.updateInventory();
//        gui.populate(placeholders, player, player);


//        this.guiManager.openGUI(gui, player);
//        player.updateInventory();
    }

}
