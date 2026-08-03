package net.maksim6opka.STVcore.utils;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.FileConfiguration;

public final class Config {

    private static JavaPlugin plugin;

    private Config() {
    }

    public static void init(JavaPlugin javaPlugin) {
        plugin = javaPlugin;
    }

    public static FileConfiguration get() {
        if (plugin == null) {
            throw new IllegalStateException("Config не ініціалізований. Виклич Config.init(plugin) в onEnable().");
        }

        return plugin.getConfig();
    }

    public static String getString(String path) {
        return get().getString(path);
    }

    public static String getString(String path, String def) {
        return get().getString(path, def);
    }

    public static boolean getBoolean(String path) {
        return get().getBoolean(path);
    }

    public static boolean getBoolean(String path, boolean def) {
        return get().getBoolean(path, def);
    }

    public static int getInt(String path) {
        return get().getInt(path);
    }

    public static int getInt(String path, int def) {
        return get().getInt(path, def);
    }

    public static void reload() {
        if (plugin == null) {
            throw new IllegalStateException("Config не ініціалізований. Виклич Config.init(plugin) в onEnable().");
        }

        plugin.reloadConfig();
    }
}
