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

package io.github.fisher2911.message;

import io.github.fisher2911.common.placeholder.Placeholders;
import io.github.fisher2911.message.config.serializer.MessageTypeSerializer;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.title.Title;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Map;
import java.util.Optional;

public class MessageHandler {

    public static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    public static final MiniMessage SAFE_MINI_MESSAGE = MiniMessage.builder()
            .tags(TagResolver.resolver(
                    StandardTags.decorations(),
                    StandardTags.clickEvent(),
                    StandardTags.insertion(),
                    StandardTags.hoverEvent(),
                    StandardTags.font(),
                    StandardTags.keybind(),
                    StandardTags.newline(),
                    StandardTags.selector(),
                    StandardTags.transition(),
                    StandardTags.translatable()
            ))
            .build();
    public static final LegacyComponentSerializer LEGACY_COMPONENT_SERIALIZER = LegacyComponentSerializer.builder()
            .hexColors()
            .useUnusualXRepeatedCharacterHexFormat()
            .build();

    public static MessageHandler create(Placeholders placeholders, Map<String, Message<?>> messages, String... filePath) {
        final MessageHandler messageHandler = new MessageHandler(placeholders, messages, filePath);
        messageHandler.load();
        return messageHandler;
    }

    private final JavaPlugin plugin;
    private final BukkitAudiences audiences;
    private final Placeholders placeholders;
    private final Map<String, Message<?>> messages;
    private final String[] filePathArray;
    private final Path filePath;

    private MessageHandler(Placeholders placeholders, Map<String, Message<?>> messages, String... filePath) {
        this.plugin = JavaPlugin.getProvidingPlugin(this.getClass());
        this.audiences = BukkitAudiences.create(this.plugin);
        this.placeholders = placeholders;
        this.messages = messages;
        this.filePathArray = filePath;
        Path path = this.plugin.getDataFolder().toPath();
        for (String s : filePath) {
            path = path.resolve(s);
        }
        this.filePath = path;
    }

    public static String serialize(String s) {
        return LEGACY_COMPONENT_SERIALIZER.serialize(MINI_MESSAGE.deserialize(s));
    }

    public static String removeUnsafeTags(String s) {
        return SAFE_MINI_MESSAGE.stripTags(s);
    }

    public static String removeAllTags(String s) {
        return MINI_MESSAGE.stripTags(s);
    }

    public Message<?> getMessage(String key) {
        return this.messages.get(key);
    }

    public Optional<Message<?>> getOptionalMessage(String key) {
        return Optional.ofNullable(this.messages.get(key));
    }

    public <S extends CommandSender> void sendMessageKey(S sender, String key, Object... placeholders) {
        final Message<S> message = (Message<S>) this.messages.get(key);
        if (message == null) return;
        message.send(sender, this, placeholders);
    }

    public void sendMessage(CommandSender sender, String message, Object... placeholders) {
        final Component component = MINI_MESSAGE.deserialize(this.placeholders.apply(message, placeholders));
        this.audiences.sender(sender).sendMessage(component);
    }

    public void sendTitle(
            Player player,
            String title,
            String subtitle,
            int fadeInTicks,
            int stayTicks,
            int fadeOutTicks,
            Object... placeholders
    ) {
        this.audiences.sender(player).showTitle(Title.title(
                MINI_MESSAGE.deserialize(this.placeholders.apply(title, placeholders)),
                MINI_MESSAGE.deserialize(this.placeholders.apply(subtitle, placeholders)),
                Title.Times.times(
                        Duration.ofMillis(fadeInTicks * 50L),
                        Duration.ofMillis(stayTicks * 50L),
                        Duration.ofMillis(fadeOutTicks * 50L)
                )
        ));
    }

    public void sendActionBar(CommandSender sender, String message, Object... placeholders) {
        this.audiences.sender(sender)
                .sendActionBar(MINI_MESSAGE.deserialize(this.placeholders.apply(message, placeholders)));
    }

    public void playSound(CommandSender sender, Sound sound) {
        this.audiences.sender(sender).playSound(sound);
    }

    public Component deserialize(String s, Object... placeholders) {
        return MINI_MESSAGE.deserialize(this.placeholders.apply(s, placeholders));
    }

    public void load() {
        final File file = this.filePath.toFile();
        if (!file.exists()) {
            this.plugin.saveResource(String.join("/", this.filePathArray), false);
        }
        final YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        for (final String key : config.getKeys(false)) {
            final Message<?> message = MessageTypeSerializer.INSTANCE.load(config, key);
            this.messages.put(key, message);
        }
    }

}
