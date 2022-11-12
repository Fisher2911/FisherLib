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
