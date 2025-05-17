package com.darksoldier1404.dsp;

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
    public String prefix;
    public static boolean isLuckpermMode = true;
    public static int prefixPriority = 0;

    public static SimplePrefix getInstance() {
        return plugin;
    }

    public void onLoad() {
        plugin = this;
        PluginUtil.addPlugin(plugin, 24491);
    }

    public void onEnable() {
        DSPFunction.initConfig();
        getServer().getPluginManager().registerEvents(new DSPEvent(), plugin);
        getCommand("simpleprefix").setExecutor(new DSPCommand());
    }

    public void onDisable() {
        DSPFunction.saveConfig();
    }
}
