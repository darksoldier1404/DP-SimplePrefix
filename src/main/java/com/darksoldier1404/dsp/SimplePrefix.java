package com.darksoldier1404.dsp;

import com.darksoldier1404.dppc.data.DPlugin;
import com.darksoldier1404.dppc.data.DataContainer;
import com.darksoldier1404.dppc.data.DataType;
import org.bukkit.configuration.file.*;

import java.util.*;

import com.darksoldier1404.dppc.utils.*;
import com.darksoldier1404.dsp.functions.*;
import com.darksoldier1404.dsp.events.*;
import com.darksoldier1404.dsp.commands.*;

public class SimplePrefix extends DPlugin {
    public static SimplePrefix plugin;
    public DataContainer<UUID, YamlConfiguration> udata;
    public static boolean isLuckpermMode = true;
    public static int prefixPriority = 0;

    public SimplePrefix() {
        super(true);
        plugin = this;
        init();
    }

    public static SimplePrefix getInstance() {
        return plugin;
    }

    public void onLoad() {
        PluginUtil.addPlugin(plugin, 24491);
        udata = loadDataContainer(new DataContainer<UUID, YamlConfiguration>(this, DataType.USER, "users"), null);
        DSPFunction.initPlaceholderAPI();
    }

    public void onEnable() {
        DSPFunction.initConfig();
        getServer().getPluginManager().registerEvents(new DSPEvent(), plugin);
        getCommand("dsp").setExecutor(new DSPCommand().getExecutor());
    }

    public void onDisable() {
        saveDataContainer();
    }
}
