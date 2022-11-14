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

import io.github.fisher2911.fisherlib.gui.AbstractGuiManager;
import io.github.fisher2911.fisherlib.listener.GlobalListener;
import io.github.fisher2911.fisherlib.message.MessageHandler;
import io.github.fisher2911.fisherlib.placeholder.Placeholders;
import io.github.fisher2911.fisherlib.update.UpdateChecker;
import io.github.fisher2911.fisherlib.user.CoreUser;
import io.github.fisher2911.fisherlib.user.CoreUserManager;
import org.bukkit.Bukkit;

import java.util.List;

public final class FisherLib extends FishPlugin<CoreUser, FisherLib> {

    private MessageHandler messageHandler;
    private Placeholders placeholders;

    @Override
    public void onEnable() {
        this.messageHandler = MessageHandler.createInstance(this, this.placeholders);
        this.messageHandler.load(List.of());
        this.placeholders = new Placeholders() {};
        new UpdateChecker(this, 106260).getVersion(version -> {
            if (!this.getDescription().getVersion().equalsIgnoreCase(version)) {
                this.messageHandler.sendMessage(
                        Bukkit.getConsoleSender(),
                        "<red>[FisherLib] You are running an outdated version of FisherLib! " +
                                "The latest version is <gold>" + version + " <reset><red>and you are running <gold> " +
                                this.getDescription().getVersion() + "<reset><red>!<newline>" +
                                "<aqua>Click here for the latest version: <click:open_url:https://www.spigotmc.org/resources/fisherlib.106260/>" +
                                "https://www.spigotmc.org/resources/fisherlib.106260/</click>"
                );
            }
        });
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
        return null;
    }

    @Override
    public Placeholders getPlaceholders() {
        return this.placeholders;
    }

    @Override
    public AbstractGuiManager getGuiManager() {
        return null;
    }

    @Override
    public CoreUserManager<CoreUser> getUserManager() {
        return null;
    }

}
