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
import io.github.fisher2911.fisherlib.user.CoreUser;
import io.github.fisher2911.fisherlib.util.Metadata;
import io.github.fisher2911.fisherlib.util.function.TriConsumer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

@SuppressWarnings("unused")
public class GuiOpener<T extends CoreUser> {

    private final FishPlugin<?, ?> plugin;
    protected final String id;
    protected final Gui.Builder builder;
    protected final List<GuiKey> requiredMetadata;
    protected final TriConsumer<GuiOpener<T>, Gui.Builder, T> openConsumer;

    public GuiOpener(FishPlugin<?, ?> plugin, String id, Gui.Builder builder, List<GuiKey> requiredMetadata, TriConsumer<GuiOpener<T>, Gui.Builder, T> openConsumer) {
        this.plugin = plugin;
        this.id = id;
        this.builder = builder;
        this.requiredMetadata = requiredMetadata;
        this.openConsumer = openConsumer;
    }

    private static final Map<GuiKey, BiConsumer<Gui.Builder, Metadata>> metdataConsumers = new HashMap<>();

    public static void registerMetadataConsumer(GuiKey key, BiConsumer<Gui.Builder, Metadata> consumer) {
        metdataConsumers.put(key, consumer);
    }

    public static Map<GuiKey, BiConsumer<Gui.Builder, Metadata>> getMetadataConsumers() {
        return new HashMap<>(metdataConsumers);
    }

//    private static final Map<GuiKey, TriConsumer<Gui.Builder, User, Kingdom>> METADATA_MAP = Map.of(
//            GuiKey.KINGDOM, (builder, user, kingdom) -> builder.metadata(GuiKey.KINGDOM, kingdom),
//            GuiKey.USER, (builder, user, kingdom) -> builder.metadata(GuiKey.USER, user),
//            GuiKey.CHUNK, (builder, user, kingdom) -> {
//                if (!user.isOnline()) return;
//                builder.metadata(GuiKey.CHUNK, PLUGIN.getWorldManager().getAt(user.getPlayer().getLocation()));
//            },
//            GuiKey.ROLE_ID, (builder, user, kingdom) -> builder.metadata(GuiKey.ROLE_ID, kingdom.getRole(user).id())/*,
//            GuiKeys.USER_KINGDOM_WRAPPER, (builder, user, kingdom) -> builder.metadata(GuiKeys.USER_KINGDOM_WRAPPER, new UserKingdomWrapper(user, kingdom), false)*/
//    );

    public void open(T user, Map<Object, Object> metadata, Set<Object> keysToOverwrite) {
        final Gui.Builder copy = this.builder.copy().metadata(metadata, true);
        for (Object key : keysToOverwrite) {
            final Object o = metadata.get(key);
            if (o == null) continue;
            copy.metadata(key, o);
        }
        copy.metadata(GuiKey.USER, user);
        if (!user.isOnline()) return;
        this.openConsumer.accept(this, copy, user);
//        TaskChain.create(this.plugin)
//                .supplyAsync(() -> PLUGIN.getKingdomManager().getKingdom(user.getKingdomId(), true))
//                .consumeSync(opt -> {
//                    if (!user.isOnline()) return;
//                    opt.ifPresent(kingdom -> {
//                        for (final GuiKey key : this.requiredMetadata) {
//                            METADATA_MAP.get(key).accept(copy, user, kingdom);
//                        }
//                    });
//                    copy.build().open(user.getPlayer());
//                })
//                .execute();
    }

    public void open(T user) {
        this.open(user, Map.of(), Set.of());
    }

    public String getId() {
        return id;
    }

    public List<GuiKey> getRequiredMetadata() {
        return requiredMetadata;
    }

}
