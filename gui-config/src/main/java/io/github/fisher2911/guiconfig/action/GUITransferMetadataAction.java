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

package io.github.fisher2911.guiconfig.action;

import io.github.fisher2911.common.metadata.Metadata;
import io.github.fisher2911.common.metadata.MetadataKey;
import io.github.fisher2911.gui.event.GUIClickEvent;
import io.github.fisher2911.guiconfig.metadata.BuiltInMetadata;

import java.util.HashMap;
import java.util.Objects;
import java.util.Set;

public class GUITransferMetadataAction implements GUIClickAction {

    public static final String ID = "transfer_metadata";

    private final Set<String> keys;

    public GUITransferMetadataAction(Set<String> keys) {
        this.keys = keys;
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public void onClick(GUIClickEvent event) {
        final Metadata metadata = Objects.requireNonNullElse(
                event.getGUI().getMetadata().get(BuiltInMetadata.TRANSFER_METADATA_KEY),
                new Metadata(new HashMap<>())
        );
        for (final String key : keys) {
            final MetadataKey<Object> metadataKey = BuiltInMetadata.getKey(key);
            if (metadataKey == null) continue;
            final Object object = metadata.get(metadataKey);
            if (object == null) continue;
            metadata.set(metadataKey, object);
        }
        event.getGUI().getMetadata().set(BuiltInMetadata.TRANSFER_METADATA_KEY, metadata);
    }

}
