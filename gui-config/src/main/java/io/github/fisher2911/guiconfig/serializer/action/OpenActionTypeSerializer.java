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

package io.github.fisher2911.guiconfig.serializer.action;

import io.github.fisher2911.guiconfig.action.GUIOpenAction;
import io.github.fisher2911.guiconfig.gui.GUISupplier;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class OpenActionTypeSerializer implements ActionTypeSerializer<GUIOpenAction> {

    private static OpenActionTypeSerializer INSTANCE;
    private static final String MENU_PATH = "menu";

    public static OpenActionTypeSerializer getInstance(GUISupplier guiSupplier) {
        if (INSTANCE == null) {
            INSTANCE = new OpenActionTypeSerializer(guiSupplier);
        }
        return INSTANCE;
    }

    private final GUISupplier guiSupplier;

    private OpenActionTypeSerializer(GUISupplier guiSupplier) {
        this.guiSupplier = guiSupplier;
    }

    @Override
    public @Nullable GUIOpenAction load(ConfigurationSection section, String path) {
        final String menuId = section.getString(path + "." + MENU_PATH);
        return new GUIOpenAction(this.guiSupplier, menuId);
    }

    @Override
    public @NotNull List<GUIOpenAction> loadList(ConfigurationSection section, String path) {
        return new ArrayList<>();
    }

    @Override
    public void save(ConfigurationSection section, String path, GUIOpenAction value) {

    }

    @Override
    public void saveList(ConfigurationSection section, String path, List<GUIOpenAction> value) {

    }

}
