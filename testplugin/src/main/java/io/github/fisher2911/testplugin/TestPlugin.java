package io.github.fisher2911.testplugin;

import io.github.fisher2911.common.item.ItemBuilder;
import io.github.fisher2911.common.placeholder.Placeholders;
import io.github.fisher2911.common.timer.BukkitTimerExecutor;
import io.github.fisher2911.gui.gui.GUI;
import io.github.fisher2911.gui.gui.GUIItem;
import io.github.fisher2911.gui.gui.GUIManager;
import io.github.fisher2911.gui.gui.GUISlot;
import io.github.fisher2911.gui.gui.GUITimer;
import io.github.fisher2911.gui.gui.pattern.Pattern;
import io.github.fisher2911.gui.gui.type.ChestGUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
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
                )
        );
        final ItemBuilder newItemBuilder = ItemBuilder.from(Material.DIAMOND)
                .name(ChatColor.GREEN + "Test Diamond {player_name_0} {player_name_1}")
                .lore("", ChatColor.GRAY + "{player_name_0} {player_name_1}")
                .enchant(Enchantment.DAMAGE_ALL, 1);
        final GUIItem<TestPlugin> guiItem = GUIItem.<TestPlugin>builder(newItemBuilder)
                .listenClick(e -> player.sendMessage("You clicked me!"))
                .timer((item, gui) -> {
                    item.setItemBuilder(item.getItemBuilder().name("Timer ticked: " + gui.getTimer().getLoopCount()));
                    gui.update(item, placeholders, player, player);
                })
                .build();
        final ChestGUI<TestPlugin> gui = GUI.<TestPlugin>chestBuilder()
                .title("Test GUI")
                .rows(6)
                .guiItems(Map.of(GUISlot.of(0), guiItem))
                .listenClick(e -> player.sendMessage("You clicked the GUI!"))
                .patterns(List.of(pattern))
                .cancelTopInventoryClick()
                .cancelTopInventoryDrag()
                .build();
        final BukkitTimerExecutor executor = BukkitTimerExecutor.sync(this, this.getServer().getScheduler());
        final GUITimer<TestPlugin, ChestGUI<TestPlugin>> timer = new GUITimer<>(
                t -> {
                    Bukkit.broadcastMessage("Timer ticked: " + t.getLoopCount());
                    return executor;
                },
                20,
                20,
                gui
        );
        gui.setTimer(timer, executor);
        gui.populate(placeholders, player, player);
        this.guiManager.openGUI(gui, player);
        player.updateInventory();
    }

}
