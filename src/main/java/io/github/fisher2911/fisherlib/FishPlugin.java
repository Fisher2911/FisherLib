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

import io.github.fisher2911.fisherlib.gui.GuiManager;
import io.github.fisher2911.fisherlib.listener.GlobalListener;
import io.github.fisher2911.fisherlib.message.MessageHandler;
import io.github.fisher2911.fisherlib.placeholder.Placeholders;
import io.github.fisher2911.fisherlib.user.CoreUser;
import io.github.fisher2911.fisherlib.user.CoreUserManager;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class FishPlugin<T extends CoreUser> extends JavaPlugin {

    public abstract MessageHandler getMessageHandler();
    public abstract GlobalListener getGlobalListener();
    public abstract Placeholders getPlaceholders();
    public abstract GuiManager getGuiManager();
    public abstract CoreUserManager<T> getUserManager();

}
