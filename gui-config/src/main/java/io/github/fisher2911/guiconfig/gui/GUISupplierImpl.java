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

package io.github.fisher2911.guiconfig.gui;

import io.github.fisher2911.gui.gui.GUI;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Supplier;

public class GUISupplierImpl implements GUISupplier {

    private final Map<String, Supplier<GUI.Builder<?, ?>>> guiSuppliers;

    public GUISupplierImpl(Map<String, Supplier<GUI.Builder<?, ?>>> guiSuppliers) {
        this.guiSuppliers = guiSuppliers;
    }

    @Override
    public @Nullable GUI.Builder<?, ?> getBuilderById(String id) {
        final Supplier<GUI.Builder<?, ?>> supplier = this.guiSuppliers.get(id);
        if (supplier == null) return null;
        return supplier.get();
    }

    @Override
    public @Nullable GUI getGUIById(String id) {
        final GUI.Builder<?, ?> builder = this.getBuilderById(id);
        if (builder == null) return null;
        return builder.build();
    }

}
