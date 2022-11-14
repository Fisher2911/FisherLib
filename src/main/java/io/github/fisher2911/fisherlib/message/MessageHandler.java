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

package io.github.fisher2911.fisherlib.message;

import io.github.fisher2911.fisherlib.FishPlugin;
import io.github.fisher2911.fisherlib.config.Config;
import io.github.fisher2911.fisherlib.placeholder.Placeholders;
import io.github.fisher2911.fisherlib.user.CoreConsoleUser;
import io.github.fisher2911.fisherlib.user.CoreUser;
import io.github.fisher2911.fisherlib.user.UserGroup;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public class MessageHandler extends Config {

    private static final Map<Class<? extends JavaPlugin>, MessageHandler> registry = new HashMap<>();
    private static final String FILE_NAME = "messages.yml";

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

    private final Map<Message, String> messages = new HashMap<>();
    private final BukkitAudiences audiences;
    private final Placeholders placeholders;

    private MessageHandler(FishPlugin<?, ?> plugin, Placeholders placeholders, String... path) {
        super(plugin, path);
        this.audiences = BukkitAudiences.create(this.plugin);
        this.placeholders = placeholders;
    }

    public static MessageHandler createInstance(FishPlugin<?, ?> plugin, Placeholders placeholders) {
        return registry.computeIfAbsent(plugin.getClass(), aClass -> new MessageHandler(plugin, placeholders, FILE_NAME));
    }

    public static MessageHandler getInstance(FishPlugin<?, ?> plugin) {
        return registry.get(plugin.getClass());
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

    public void sendMessage(CoreUser user, Message message) {
        final String value = this.getMessage(message);
        if (value.isBlank()) return;
        sendMessage(user, MINI_MESSAGE.deserialize(value));
    }

    public void sendMessage(UserGroup<?> userGroup, Message message) {
        final String value = this.getMessage(message);
        if (value.isBlank()) return;
        sendToAll(userGroup, MINI_MESSAGE.deserialize(value));
    }

    public void sendMessage(CoreUser user, Message message, Object... placeholders) {
        final String value = this.getMessage(message);
        if (value.isBlank()) return;
        sendMessage(user, MINI_MESSAGE.deserialize(this.placeholders.apply(value, placeholders)));
    }

    public void sendMessage(UserGroup<?> userGroup, Message message, Object... placeholders) {
        final String value = this.getMessage(message);
        if (value.isBlank()) return;
        sendToAll(userGroup, MINI_MESSAGE.deserialize(this.placeholders.apply(value, placeholders)));
    }

    public void sendMessage(CoreUser user, String message) {
        sendMessage(user, MINI_MESSAGE.deserialize(message));
    }

    public void sendMessage(UserGroup<?> userGroup, String message, Object... placeholders) {
        sendToAll(userGroup, MINI_MESSAGE.deserialize(this.placeholders.apply(message, placeholders)));
    }

    public void sendMessage(UserGroup<?> userGroup, String message) {
        sendToAll(userGroup, MINI_MESSAGE.deserialize(message));
    }

    public void sendToAll(UserGroup<?> userGroup, Component component) {
        for (CoreUser user : userGroup.getUsers()) {
            sendMessage(user, component);
        }
    }

    public void sendMessage(CoreUser user, String message, Object... placeholders) {
        sendMessage(user, MINI_MESSAGE.deserialize(this.placeholders.apply(message, placeholders)));
    }

    public void sendMessage(CoreUser user, Component component) {
        if (!user.isOnline()) {
            if (user instanceof CoreConsoleUser) {
                Bukkit.getConsoleSender().sendMessage(LEGACY_COMPONENT_SERIALIZER.serialize(component));
            }
            return;
        }
        final Player player = user.getPlayer();
        if (player == null) return;
        final Audience audience = this.audiences.player(player);
        audience.sendMessage(component);
    }

    public void sendMessage(CommandSender sender, Message message) {
        final String value = this.getMessage(message);
        if (value.isBlank()) return;
        sendMessage(sender, MINI_MESSAGE.deserialize(value));
    }

    public void sendMessage(CommandSender sender, String message) {
        sendMessage(sender, MINI_MESSAGE.deserialize(message));
    }

    public void sendMessage(CommandSender sender, Component component) {
        final Audience audience = this.audiences.sender(sender);
        audience.sendMessage(component);
    }

    public Component deserialize(String s, Object... placeholders) {
        return MINI_MESSAGE.deserialize(this.placeholders.apply(s, placeholders));
    }

    public String getMessage(Message message) {
        return this.messages.get(message);
    }

    public void reload(Collection<Message> messageKeys) {
        this.messages.clear();
        this.load(messageKeys);
    }

    public void load(Collection<Message> messageKeys) {
        this.loadMessages(messageKeys);
    }

    private void loadMessages(Collection<Message> messageKeys) {
        final Collection<Message> all = Message.values();
        all.addAll(messageKeys);
        final File file = this.path.toFile();
        final boolean exists = file.exists();
        if (!exists) {
            file.getParentFile().mkdirs();
            this.plugin.saveResource(this.path.getFileName().toString(), false);
        }
        final YamlConfigurationLoader loader = YamlConfigurationLoader.
                builder().
                path(this.path).nodeStyle(NodeStyle.BLOCK).
                defaultOptions(opts ->
                        opts.serializers(build -> {
                        }))
                .build();
        try {
            final ConfigurationNode source = loader.load();
            for (Message message : all) {
                final String messagePath = message.getConfigPath();
                if (!source.hasChild(messagePath)) {
                    source.node(messagePath).set(message.toString());
                    this.messages.put(message, message.toString());
                    continue;
                }
                this.messages.put(message, source.node(messagePath).getString(""));
            }
            loader.save(source);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
