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

package io.github.fisher2911.fisherlib.gui;

import io.github.fisher2911.fisherlib.FishPlugin;
import io.github.fisher2911.fisherlib.config.serializer.GuiSerializer;
import io.github.fisher2911.fisherlib.user.CoreUser;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("unused")
public abstract class AbstractGuiManager<T extends CoreUser, Z extends FishPlugin<T, Z>> {

    protected final Path guiFolder;

    protected final Z plugin;
    protected final List<String> defaultFiles;
    protected final Map<String, GuiOpener<T>> guiMap;
    protected final GuiSerializer<T, Z> guiSerializer;

    public AbstractGuiManager(Z plugin, List<String> defaultFiles, GuiSerializer<T, Z> guiSerializer) {
        this.plugin = plugin;
        this.guiMap = new HashMap<>();
        this.defaultFiles = defaultFiles;
        this.guiFolder = this.plugin.getDataFolder().toPath().resolve("guis");
        this.guiSerializer = guiSerializer;
    }

    public void open(String gui, T user) {
        final GuiOpener<T> opener = this.guiMap.get(gui);
        if (opener == null) return;
        opener.open(user);
    }

    public void open(String gui, T user, Map<Object, Object> metadata, Set<Object> keysToOverwrite) {
        final GuiOpener<T> opener = this.guiMap.get(gui);
        if (opener == null) return;
        opener.open(user, metadata, keysToOverwrite);
    }

    @Nullable
    public GuiOpener<T> getGuiOpener(String gui) {
        return this.guiMap.get(gui);
    }

    public void addGuiOpener(GuiOpener<T> opener) {
        this.guiMap.put(opener.getId(), opener);
    }

    public void load() {
        final File folder = guiFolder.toFile();
        this.createFiles(folder);
        this.loadFolder(folder);
    }

    private void loadFolder(File folder) {
        final File[] files = folder.listFiles();
        if (files == null) return;
        for (final File file : files) {
            if (file.isDirectory()) {
                this.loadFolder(file);
            } else {
                this.loadFile(file);
            }
        }
    }

    public void reload() {
        this.guiMap.clear();
        this.load();
    }

    protected abstract void openHandler(GuiOpener<T> guiOpener, Gui.Builder builder, T user);

    private void loadFile(File file) {
        final YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
                .path(file.toPath())
                .build();
        try {
            final var source = loader.load();
            final GuiOpener<T> opener = this.guiSerializer.deserialize(this.plugin, source, this::openHandler);
            this.guiMap.put(opener.getId(), opener);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createFiles(File folder) {
        if (!folder.exists()) {
            folder.mkdirs();
        }
        for (final String file : this.defaultFiles) {
            final Path path = this.guiFolder.resolve(file);
            if (!path.toFile().exists()) {
                this.plugin.saveResource("guis/" + file, false);
            }
        }
    }

    private void createFile(String name) {
        final Path path = this.guiFolder.resolve(name);
        if (!path.toFile().exists()) {
            this.plugin.saveResource("guis/" + name, false);
        }
    }
}
