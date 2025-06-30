package com.darksoldier1404.dsp;

import com.darksoldier1404.dppc.lang.DLang;
import org.bukkit.plugin.java.*;
import org.bukkit.configuration.file.*;
import java.util.*;
import com.darksoldier1404.dppc.utils.*;
import com.darksoldier1404.dsp.functions.*;
import com.darksoldier1404.dsp.events.*;
import com.darksoldier1404.dsp.commands.*;

public class SimplePrefix extends JavaPlugin
{
    private static SimplePrefix plugin;
    public YamlConfiguration config;
    public Map<UUID, YamlConfiguration> udata = new HashMap<>();
    public static String prefix;
    public static boolean isLuckpermMode = true;
    public static int prefixPriority = 0;
    public static DLang lang;

    public static SimplePrefix getInstance() {
        return plugin;
    }

    public void onLoad() {
        plugin = this;
        PluginUtil.addPlugin(plugin, 24491);
        DSPFunction.initPlaceholderAPI();
    }

    public void onEnable() {
        DSPFunction.initConfig();
        lang = new DLang(config.getString("Settings.Lang") == null ? "Korean" : config.getString("Settings.Lang"), plugin);
        getServer().getPluginManager().registerEvents(new DSPEvent(), plugin);
        getCommand("dsp").setExecutor(new DSPCommand().getExecutor());
    }

    public void onDisable() {
        DSPFunction.saveConfig();
    }
}
