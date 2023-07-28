package io.github.fisher2911.testplugin;

import io.github.fisher2911.command.CommandManager;
import io.github.fisher2911.command.command.ArgumentSupplier;
import io.github.fisher2911.common.item.ItemBuilder;
import io.github.fisher2911.common.metadata.MetadataKey;
import io.github.fisher2911.common.placeholder.Placeholders;
import io.github.fisher2911.config.ConfigLoader;
import io.github.fisher2911.config.type.TypeProvider;
import io.github.fisher2911.gui.gui.GUI;
import io.github.fisher2911.gui.gui.GUIItem;
import io.github.fisher2911.gui.gui.GUIManager;
import io.github.fisher2911.gui.gui.GUISlot;
import io.github.fisher2911.gui.gui.pattern.Pattern;
import io.github.fisher2911.gui.gui.type.ChestGUI;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public final class TestPlugin extends JavaPlugin implements Listener {

    private GUIManager guiManager;

    @Override
    public void onEnable() {
        this.guiManager = new GUIManager(
                new ConcurrentHashMap<>()
        );
        this.getServer().getPluginManager().registerEvents(this, this);
        this.getServer().getPluginManager().registerEvents(this.guiManager, this);
        final CommandManager commandManager = new CommandManager(new HashMap<>());
        final ArgumentSupplier supplier = new ArgumentSupplier(new HashMap<>(), new HashMap<>());
        supplier.addDefaults();
        commandManager.register(TestCommand.class, supplier);
        final TestConfig config = ConfigLoader.load(
                TestConfig.class,
                TypeProvider.create(),
                ConfigLoader.NamingStrategy.CAMEL_CASE
        );
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
        final Pattern pattern = Pattern.borderPattern(
                List.of(
                        GUIItem.builder(ItemBuilder.from(Material.BLUE_STAINED_GLASS_PANE)
                                        .name(ChatColor.RED + "Border Item").build())
                                .build(),
                        GUIItem.builder(ItemBuilder.from(Material.RED_STAINED_GLASS_PANE)
                                        .name(ChatColor.RED + "Border Item").build())
                                .build(),
                        GUIItem.builder(ItemBuilder.from(Material.BLACK_STAINED_GLASS_PANE)
                                        .name(ChatColor.RED + "Border Item").build())
                                .build(),
                        GUIItem.builder(ItemBuilder.from(Material.PURPLE_STAINED_GLASS_PANE)
                                        .name(ChatColor.RED + "Border Item").build())
                                .build()
                ),
                1
        );
        final AtomicInteger atomicInteger = new AtomicInteger(0);
        final MetadataKey<Integer> fillKey = MetadataKey.of(new NamespacedKey(this, "fill_number"), Integer.class);
        final Pattern fillPattern = Pattern.fillPattern(
                Stream.generate(() ->
                        GUIItem.builder(
                                        ItemBuilder.from(Material.GRAY_STAINED_GLASS_PANE)
                                                .name(ChatColor.RED + "Fill Item - " + atomicInteger.get()).build())
                                .listenClick(e -> e.getPlayer().sendMessage("You clicked the fill item: " + e.getItem().getMetadata().get(fillKey)))
                                .metadata(fillKey, atomicInteger.getAndIncrement())
                                .build()
                ).takeWhile(i -> atomicInteger.get() < 108).toList(),
                slot -> true,
                item -> item.getMetadata().get(fillKey) % 2 == 0,
                2
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
        final GUIItem guiItem = GUIItem.builder(newItemBuilder)
                .listenClick(e -> {
                    final double randomHealth = Math.min(Math.max(1, Math.random() * 21), 20);
                    player.setHealth(randomHealth);
                })
                .timer((item, gui) -> {
                    item.setItemBuilder(item.getItemBuilder().name("Timer ticked: " + gui.getTimer().getLoopCount()));
                    gui.update(item, placeholders, player, player);
                })
                .build();
        final GUIItem hopperItem = GUIItem.builder(hopperItemBuilder)
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
        final GUIItem dropperItem = GUIItem.builder(dropperItemBuilder)
                .listenClick(e -> player.sendMessage("You clicked the enchanted golden apple!"))
                .build();
        final ChestGUI gui = GUI.chestBuilder()
                .title("Test GUI")
                .rows(6)
                .guiItems(Map.of(GUISlot.of(10), guiItem, GUISlot.of(11), GUIItem.builder(
                        ItemBuilder.from(Material.GOLD_BLOCK)
                                .name("Next page")
                                .build())
                        .listenClick(e -> e.getGUI().nextPage())
                        .build()
                ))
                .nextPage()
                .set(GUISlot.of(10), GUIItem.builder(Material.DIAMOND).build())
                .listenClick(e -> player.sendMessage("You clicked the chest GUI!"))
                .addPatterns(List.of(pattern, fillPattern))
                .cancelTopInventoryClick()
                .cancelTopInventoryDrag()
                .build();
//        final ChestGUI gui2 = GUI.chestBuilder()
//                .title("Test2 GUI")
//                .rows(3)
//                .guiItems(Map.of(GUISlot.of(13), guiItem))
//                .listenClick(e -> player.sendMessage("You clicked the 2nd chest GUI!"))
//                .addPatterns(List.of(pattern))
//                .cancelTopInventoryClick()
//                .cancelTopInventoryDrag()
//                .build();
//        final ChestGUI gui3 = GUI.chestBuilder()
//                .title("Test2 GUI")
//                .rows(5)
//                .guiItems(Map.of(GUISlot.of(15), guiItem))
//                .listenClick(e -> player.sendMessage("You clicked the 3rd chest GUI!"))
//                .addPatterns(List.of(pattern))
//                .cancelTopInventoryClick()
//                .cancelTopInventoryDrag()
//                .build();
//        final HopperGUI hopperGUI = GUI.hopperBuilder()
//                .title("Test Hopper GUI")
//                .guiItems(Map.of(GUISlot.of(3), hopperItem))
//                .listenClick(e -> player.sendMessage("You clicked the hopper GUI!"))
//                .cancelTopInventoryClick()
//                .cancelTopInventoryDrag()
//                .build();
//        final DropperGUI dropperGUI = GUI.dropperBuilder()
//                .title("Test Hopper GUI")
//                .guiItems(Map.of(GUISlot.of(4), dropperItem))
//                .addPatterns(List.of(pattern))
//                .listenClick(e -> player.sendMessage("You clicked the dropper GUI!"))
//                .cancelTopInventoryClick()
//                .cancelTopInventoryDrag()
//                .build();
//        final BukkitTimerExecutor executor = BukkitTimerExecutor.sync(this, this.getServer().getScheduler());
//        final GUITimer<ChestGUI> timer = new GUITimer<>(
//                t -> executor,
//                20,
//                20,
//                gui
//        );
//        final PaginatedGUI paginated = GUI.paginatedBuilder(this.guiManager)
//                .addPages(List.of(gui, gui2, gui3, dropperGUI, hopperGUI))
//                .addPatterns(List.of(fillPattern))
//                .pagePatterns(List.of(Pattern.paginatedPattern(
//                        GUIItem.builder(previousPageItem).build(),
//                        GUIItem.builder(nextPageItem).build(),
//                        0
//                )))
//                .build();
//        paginated.setPlaceholders(placeholders, player, player);
        gui.setPlaceholders(placeholders, player, player);
        gui.populate();
        this.guiManager.openGUI(gui, player);
//        gui2.setPlaceholders(placeholders, player, player);
//        gui3.setPlaceholders(placeholders, player, player);
//        dropperGUI.setPlaceholders(placeholders, player, player);
//        hopperGUI.setPlaceholders(placeholders, player, player);
//        gui.setTimer(timer, executor);
//        paginated.populate(placeholders, player, player);
//        this.guiManager.openGUI(paginated, player);
        player.updateInventory();
    }

}
