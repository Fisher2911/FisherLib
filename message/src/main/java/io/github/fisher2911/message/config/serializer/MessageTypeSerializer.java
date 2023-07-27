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

package io.github.fisher2911.message.config.serializer;

import io.github.fisher2911.config.type.TypeSerializer;
import io.github.fisher2911.message.Message;
import io.github.fisher2911.message.type.ActionBarMessage;
import io.github.fisher2911.message.type.SimpleMessage;
import io.github.fisher2911.message.type.SoundMessage;
import io.github.fisher2911.message.type.TitleMessage;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MessageTypeSerializer implements TypeSerializer<Message<?>> {

    public static final MessageTypeSerializer INSTANCE = new MessageTypeSerializer();

    private MessageTypeSerializer() {}

    private static final String TYPE_PATH = "type";
    private static final String TITLE_MESSAGE_TYPE_PATH = "title";
    private static final String ACTION_BAR_TYPE_PATH = "subtitle";
    private static final String SOUND_TYPE_PATH = "sound";

    private static final String TITLE_PATH = "title";
    private static final String SUBTITLE_PATH = "subtitle";
    private static final String FADE_IN_PATH = "fade-in";
    private static final String STAY_PATH = "stay";
    private static final String FADE_OUT_PATH = "fade-out";

    private static final String SOUND_PATH = "sound";
    private static final String SOUND_SOURCE_PATH = "source";
    private static final String SOUND_VOLUME_PATH = "volume";
    private static final String SOUND_PITCH_PATH = "pitch";

    @Override
    public @Nullable Message<?> load(ConfigurationSection section, String path) {
        if (!section.contains(TYPE_PATH)) return new SimpleMessage(section.getString(path));
        final String type = section.getString(TYPE_PATH);
        if (type == null) throw new IllegalArgumentException("Message type cannot be null");
        return switch (type) {
            case TITLE_MESSAGE_TYPE_PATH -> {
                final String title = section.getString(TITLE_PATH);
                final String subtitle = section.getString(SUBTITLE_PATH);
                final int fadeIn = section.getInt(FADE_IN_PATH);
                final int stay = section.getInt(STAY_PATH);
                final int fadeOut = section.getInt(FADE_OUT_PATH);
                yield new TitleMessage(title, subtitle, fadeIn, stay, fadeOut);
            }
            case ACTION_BAR_TYPE_PATH -> new ActionBarMessage(section.getString(path));
            case SOUND_TYPE_PATH -> {
                final String soundKey = section.getString(SOUND_PATH);
                if (soundKey == null) throw new IllegalArgumentException("Sound key cannot be null");
                final Sound.Source source = Sound.Source.valueOf(soundKey.toUpperCase());
                Sound sound = Sound.sound(
                        Key.key(soundKey),
                        source,
                        section.getInt(SOUND_VOLUME_PATH),
                        section.getInt(SOUND_PITCH_PATH)
                );
                yield new SoundMessage(sound);
            }
            default -> new SimpleMessage(section.getString(path));
        };
    }

    @Override
    public @NotNull List<Message<?>> loadList(ConfigurationSection section, String path) {
        final List<Message<?>> messages = new ArrayList<>();
        for (final String key : section.getKeys(false)) {
            messages.add(load(section, key));
        }
        return messages;
    }

    @Override
    public void save(ConfigurationSection section, String path, Message<?> value) {

    }

    @Override
    public void saveList(ConfigurationSection section, String path, List<Message<?>> value) {

    }

}
