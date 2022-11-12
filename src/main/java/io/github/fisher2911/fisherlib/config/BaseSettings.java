package io.github.fisher2911.fisherlib.config;

import io.github.fisher2911.fisherlib.FishPlugin;

public abstract class BaseSettings extends Config {

    public BaseSettings(FishPlugin<?> plugin, String... path) {
        super(plugin, path);
    }

    public abstract int getCommandsPerHelpPage();
    public abstract String getAdminHelpPermission();

}
