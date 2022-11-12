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
