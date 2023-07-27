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

package io.github.fisher2911.guiconfig.serializer;

import io.github.fisher2911.config.type.TypeSerializer;
import io.github.fisher2911.gui.gui.pattern.Pattern;
import io.github.fisher2911.guiconfig.GUIItemManager;
import io.github.fisher2911.guiconfig.serializer.pattern.BorderPatternTypeSerializer;
import io.github.fisher2911.guiconfig.serializer.pattern.FillPatternTypeSerializer;
import io.github.fisher2911.guiconfig.serializer.pattern.PaginatedPatternTypeSerializer;
import io.github.fisher2911.guiconfig.serializer.pattern.PatternTypeSerializer;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PatternSerializer implements TypeSerializer<Pattern> {

    private static PatternSerializer instance;

    public static PatternSerializer getInstance(
            Map<String, PatternTypeSerializer<? extends Pattern>> serializers,
            GUIItemManager guiItemManager
    ) {
        if (instance == null) {
            instance = new PatternSerializer(serializers, guiItemManager);
        }
        return instance;
    }

    private final Map<String, PatternTypeSerializer<? extends Pattern>> serializers;

    private PatternSerializer(
            Map<String, PatternTypeSerializer<? extends Pattern>> serializers,
            GUIItemManager guiItemManager
    ) {
        this.serializers = serializers;
        this.serializers.put("border", BorderPatternTypeSerializer.getInstance(guiItemManager));
        this.serializers.put("fill", FillPatternTypeSerializer.getInstance(guiItemManager));
        this.serializers.put("paginated", PaginatedPatternTypeSerializer.getInstance(guiItemManager));
    }

    private static final String TYPE_PATH = "type";

    @Override
    public @Nullable Pattern load(ConfigurationSection section, String path) {
        final String type = section.getString(path + "." + TYPE_PATH);
        if (type == null) throw new IllegalStateException("Pattern type not found at path " + path + "." + TYPE_PATH);
        final PatternTypeSerializer<? extends Pattern> serializer = this.serializers.get(type);
        if (serializer == null) throw new IllegalStateException("Pattern type " + type + " not found");
        return serializer.load(section, path);
    }

    @Override
    public @NotNull List<Pattern> loadList(ConfigurationSection section, String path) {
        return new ArrayList<>();
    }

    @Override
    public void save(ConfigurationSection section, String path, Pattern value) {

    }

    @Override
    public void saveList(ConfigurationSection section, String path, List<Pattern> value) {

    }

}
