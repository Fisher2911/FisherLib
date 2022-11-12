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

package io.github.fisher2911.fisherlib;

import io.github.fisher2911.fisherlib.gui.Gui;
import io.github.fisher2911.fisherlib.gui.GuiManager;
import io.github.fisher2911.fisherlib.gui.GuiOpener;
import io.github.fisher2911.fisherlib.listener.GlobalListener;
import io.github.fisher2911.fisherlib.message.MessageHandler;
import io.github.fisher2911.fisherlib.placeholder.Placeholders;
import io.github.fisher2911.fisherlib.user.CoreUser;
import io.github.fisher2911.fisherlib.user.CoreUserManager;
import io.github.fisher2911.fisherlib.user.CoreUserManagerImpl;

import java.util.HashMap;
import java.util.List;

public final class FisherLib extends FishPlugin<CoreUser> {

    private MessageHandler messageHandler;
    private GlobalListener globalListener;
    private Placeholders placeholders;
    private GuiManager guiManager;
    private CoreUserManager<CoreUser> userManager;

    @Override
    public void onEnable() {
        this.messageHandler = MessageHandler.getInstance(this);
        this.messageHandler.load(List.of());
        this.globalListener = new GlobalListener(this);
        this.placeholders = new Placeholders() {};
        this.guiManager = new GuiManager(this) {
            @Override
            protected void openHandler(GuiOpener guiOpener, Gui.Builder builder, CoreUser coreUser) {
                guiOpener.open(coreUser);
            }
        };
        this.userManager = new CoreUserManagerImpl<>(new HashMap<>());
    }

    @Override
    public void onDisable() {

    }

    @Override
    public MessageHandler getMessageHandler() {
        return this.messageHandler;
    }

    @Override
    public GlobalListener getGlobalListener() {
        return this.globalListener;
    }

    @Override
    public Placeholders getPlaceholders() {
        return this.placeholders;
    }

    @Override
    public GuiManager getGuiManager() {
        return this.guiManager;
    }

    @Override
    public CoreUserManager<CoreUser> getUserManager() {
        return this.userManager;
    }

}
