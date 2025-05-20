package com.darksoldier1404.dsp.functions;

import com.darksoldier1404.dppc.api.inventory.DInventory;
import com.darksoldier1404.dppc.api.luckperms.LuckpermAPI;
import com.darksoldier1404.dppc.lang.DLang;
import com.darksoldier1404.dppc.utils.ColorUtils;
import com.darksoldier1404.dppc.utils.ConfigUtils;
import com.darksoldier1404.dppc.utils.NBT;
import com.darksoldier1404.dppc.utils.Tuple;
import com.darksoldier1404.dsp.SimplePrefix;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class DSPFunction {
    private static final SimplePrefix plugin = SimplePrefix.getInstance();
    private static String prefix = plugin.prefix;
    public static final Map<UUID, String> currentEditPrefix = new HashMap<>();

    public static void initConfig() {
        plugin.config = ConfigUtils.loadDefaultPluginConfig(plugin);
        plugin.prefix = ChatColor.translateAlternateColorCodes('&', plugin.config.getString("Settings.prefix"));
        prefix = plugin.prefix;
        plugin.lang = new DLang(plugin.config.getString("Settings.Lang") == null ? "Korean" : plugin.config.getString("Settings.Lang"), plugin);
        plugin.isLuckpermMode = plugin.config.getBoolean("Settings.enable_luckperm_prefix");
        plugin.prefixPriority = plugin.config.getInt("Settings.luckperm_prefix_priority");
        if(plugin.config.getBoolean("Settings.lubkperm_prefix_delete_by_priority")) {
            removeAllLuckpermPrefix();
        }
    }

    public static void createPrefix(Player player, String name) {
        if (isExistPrefix(name)) {
            player.sendMessage(prefix + plugin.lang.get("prefix_exists"));
            return;
        }
        plugin.config.set("Settings.PrefixList." + name, "temp");
        ConfigUtils.savePluginConfig(plugin, plugin.config);
        player.sendMessage(prefix + name + plugin.lang.get("prefix_create"));
    }

    public static void deletePrefix(Player player, String name) {
        if (!isExistPrefix(name)) {
            player.sendMessage(prefix + plugin.lang.get("prefix_not_exists"));
            return;
        }
        plugin.config.set("Settings.PrefixList." + name, null);
        ConfigUtils.savePluginConfig(plugin, plugin.config);
        player.sendMessage(prefix + name + plugin.lang.get("prefix_delete"));
    }

    public static void openSetPrefixGUI(Player player, String name) {
        currentEditPrefix.put(player.getUniqueId(), name);
        player.sendMessage(prefix + plugin.lang.get("prefix_type"));
    }

    public static void showAllPrefixList(Player player) {
        player.sendMessage(prefix + plugin.lang.get("prefix_list"));
        for (String key : plugin.config.getConfigurationSection("Settings.PrefixList").getKeys(false)) {
            player.sendMessage(prefix + key + " : " + ColorUtils.applyColor(plugin.config.getString("Settings.PrefixList." + key)));
        }
    }

    public static boolean isExistPrefix(String name) {
        return plugin.config.get("Settings.PrefixList." + name) != null;
    }

    public static void showPrefixList(Player player) {
        List<String> prefixList = plugin.udata.get(player.getUniqueId()).getStringList("Player.PrefixList");
        if (prefixList.isEmpty()) {
            player.sendMessage(prefix + plugin.lang.get("prefix_not_have_any"));
            return;
        }

        List<ItemStack> prefixItems = new ArrayList<>();
        for (String key : prefixList) {
            String prefixValue = plugin.config.getString("Settings.PrefixList." + key);
            if (prefixValue != null) {
                ItemStack item = new ItemStack(Material.PAPER);
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(ColorUtils.applyColor(prefixValue));
                meta.setLore(Arrays.asList("", plugin.lang.get("prefix_lore1"), plugin.lang.get("prefix_lore2") + key));
                item.setItemMeta(meta);
                prefixItems.add(NBT.setStringTag(item, "dsp.prefix", key));
            }
        }

        DInventory inventory = new DInventory(null, plugin.lang.get("prefix_title"), 54, true, plugin);
        ItemStack pane = NBT.setStringTag(new ItemStack(Material.BLACK_STAINED_GLASS_PANE), "page", "true");
        ItemStack prev = NBT.setStringTag(new ItemStack(Material.PINK_DYE), "prev", "true");
        ItemStack current = NBT.setStringTag(new ItemStack(Material.PAPER), "current", "true");
        ItemStack next = NBT.setStringTag(new ItemStack(Material.LIME_DYE), "next", "true");

        ItemMeta prevMeta = prev.getItemMeta();
        prevMeta.setDisplayName(plugin.lang.get("prev_page"));
        prev.setItemMeta(prevMeta);

        ItemMeta nextMeta = next.getItemMeta();
        nextMeta.setDisplayName(plugin.lang.get("next_page"));
        next.setItemMeta(nextMeta);

        ItemMeta currentMeta = current.getItemMeta();
        currentMeta.setDisplayName(plugin.lang.get("current_page") + (inventory.getCurrentPage() + 1));
        current.setItemMeta(currentMeta);

        inventory.setPageTools(new ItemStack[]{pane, pane, prev, pane, current, pane, next, pane, pane});

        int maxPages = (int) Math.ceil(prefixItems.size() / 45.0);
        inventory.setPages(maxPages);
        ItemStack[] contents = new ItemStack[45];
        int count = 0;

        for (int page = 0; page <= maxPages; page++) {
            for (int i = 0; i < 45 && count < prefixItems.size(); i++) {
                contents[i] = prefixItems.get(count++);
            }
            inventory.setPageContent(page, contents);
        }

        inventory.update();
        player.openInventory(inventory);
    }

    public static void equipPrefix(Player player, String name) {
        YamlConfiguration data = plugin.udata.get(player.getUniqueId());
        List<String> prefixList = data.getStringList("Player.PrefixList");

        if (prefixList.contains(name)) {
            if (name.equals(data.getString("Player.Prefix"))) {
                player.sendMessage(prefix + plugin.lang.get("prefix_already_equipped"));
                return;
            }
            data.set("Player.Prefix", name);
            player.sendMessage(prefix + name + plugin.lang.get("prefix_equipped"));
            ConfigUtils.saveCustomData(plugin, data, player.getUniqueId().toString(), "users");
            if (SimplePrefix.isLuckpermMode) {
                LuckpermAPI.setPrefix(player, getPrefix(name), 0);
            }
        } else {
            player.sendMessage(prefix + plugin.lang.get("prefix_not_have"));
        }
    }

    public static void unequipPrefix(Player player) {
        YamlConfiguration data = plugin.udata.get(player.getUniqueId());
        if (data.getString("Player.Prefix") == null) {
            player.sendMessage(prefix + plugin.lang.get("prefix_not_have_any_equipped"));
            return;
        }
        data.set("Player.Prefix", "");
        if (SimplePrefix.isLuckpermMode) {
            LuckpermAPI.delPrefix(player, 0);
        }
        player.sendMessage(prefix + plugin.lang.get("prefix_unequipped"));
        ConfigUtils.saveCustomData(plugin, data, player.getUniqueId().toString(), "users");
    }

    public static boolean givePrefix(Player player, String name) {
        YamlConfiguration data = plugin.udata.get(player.getUniqueId());
        if (data == null) {
            System.out.println("data is null");
            return false;
        }

        List<String> prefixList = data.getStringList("Player.PrefixList");
        if (prefixList.contains(name)) {
            player.sendMessage(prefix + plugin.lang.get("prefix_already_have"));
            return false;
        }

        prefixList.add(name);
        data.set("Player.PrefixList", prefixList);
        plugin.udata.put(player.getUniqueId(), data);
        player.sendMessage(prefix + name + plugin.lang.get("prefix_added"));
        ConfigUtils.saveCustomData(plugin, data, player.getUniqueId().toString(), "users");
        return true;
    }

    public static void getPrefixCoupon(Player player, String name) {
        if (!plugin.config.getConfigurationSection("Settings.PrefixList").getKeys(false).contains(name)) {
            player.sendMessage(prefix + plugin.lang.get("prefix_not_exists"));
            return;
        }

        String prefixValue = plugin.config.getString("Settings.PrefixList." + name);
        String itemName = ColorUtils.applyColor(plugin.config.getString("Settings.couponNameFormat").replace("<name>", name).replace("<prefix>", getPrefix(name)));
        ItemStack item;

        if (hasIndividualCoupon(name)) {
            item = plugin.config.getItemStack("PrefixCoupon." + name + ".Coupon").clone();
        } else {
            item = plugin.config.getItemStack("GlobalPrefixCoupon.Coupon").clone();
            if (item == null) {
                player.sendMessage(prefix + plugin.lang.get("no_coupon"));
                return;
            }
        }
        item.setAmount(1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(itemName);
        item.setItemMeta(meta);
        item = initPlaceholder(item, name);
        player.getInventory().addItem(item);
        player.sendMessage(ColorUtils.applyColor(prefixValue) + plugin.lang.get("coupon_give"));
    }

    public static ItemStack initPlaceholder(ItemStack item, String name) {
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ColorUtils.applyColor(meta.getDisplayName().replace("<dsp_prefix>", getPrefix(name))));

        if (meta.hasLore()) {
            List<String> lore = meta.getLore();
            for (int i = 0; i < lore.size(); i++) {
                lore.set(i, ColorUtils.applyColor(lore.get(i).replace("<dsp_prefix>", getPrefix(name))));
            }
            meta.setLore(lore);
        }

        item.setItemMeta(meta);
        return NBT.setStringTag(item, "dsp.prefix", name);
    }

    public static void setDefaultPrefix(Player player, String name) {
        plugin.config.set("Settings.DefaultPrefix", name);
        ConfigUtils.savePluginConfig(plugin, plugin.config);
        player.sendMessage(prefix + name + plugin.lang.get("set_default"));

        for (YamlConfiguration data : plugin.udata.values()) {
            List<String> prefixList = data.getStringList("Player.PrefixList");
            if (!prefixList.contains(name)) {
                prefixList.add(name);
                data.set("Player.PrefixList", prefixList);
                if (data.getString("Player.Prefix") == null) {
                    data.set("Player.Prefix", name);
                }
            }
        }
    }

    public static String giveDefaultPrefix(Player player) {
        String defaultPrefix = plugin.config.getString("Settings.DefaultPrefix");
        if (defaultPrefix == null) {
            return "";
        }

        YamlConfiguration data = plugin.udata.get(player.getUniqueId());
        List<String> prefixList = data.getStringList("Player.PrefixList");

        if (!prefixList.contains(defaultPrefix)) {
            prefixList.add(defaultPrefix);
            data.set("Player.PrefixList", prefixList);
            if (data.getString("Player.Prefix") == null) {
                data.set("Player.Prefix", defaultPrefix);
            }
        }

        return plugin.config.getString("Settings.PrefixList." + defaultPrefix);
    }

    public static void openCouponSetting(Player player, String name) {
        if (!isExistPrefix(name)) {
            player.sendMessage(prefix + plugin.lang.get("prefix_not_exists"));
            return;
        }

        DInventory inventory = new DInventory(null, plugin.lang.get("coupon_setting_title"), 27, plugin);
        inventory.setObj(Tuple.of(name, SettingType.INDIVIDUAL_COUPON));

        ItemStack pane = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, pane);
        }

        ItemStack coupon = plugin.config.getItemStack("PrefixCoupon." + name + ".Coupon");
        inventory.setItem(13, coupon != null ? coupon : new ItemStack(Material.AIR));

        player.openInventory(inventory);
    }

    public static void openGlobalCouponSetting(Player player) {
        DInventory inventory = new DInventory(null, plugin.lang.get("global_coupon_setting_title"), 27, plugin);
        inventory.setObj(Tuple.of("Global", SettingType.GLOBAL_COUPON));

        ItemStack pane = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, pane);
        }

        ItemStack coupon = plugin.config.getItemStack("GlobalPrefixCoupon.Coupon");
        inventory.setItem(13, coupon != null ? coupon : new ItemStack(Material.AIR));

        player.openInventory(inventory);
    }

    public static void saveCouponSetting(Player player, DInventory inventory) {
        Tuple<String, SettingType> tuple = (Tuple<String, SettingType>) inventory.getObj();
        plugin.config.set("PrefixCoupon." + tuple.getA() + ".Coupon", inventory.getItem(13));
        player.sendMessage(prefix + plugin.lang.get("coupon_set"));
        ConfigUtils.savePluginConfig(plugin, plugin.config);
    }

    public static void saveGlobalCouponSetting(Player player, DInventory inventory) {
        plugin.config.set("GlobalPrefixCoupon.Coupon", inventory.getItem(13));
        player.sendMessage(prefix + plugin.lang.get("coupon_set"));
        ConfigUtils.savePluginConfig(plugin, plugin.config);
    }

    public static boolean hasIndividualCoupon(String name) {
        return plugin.config.get("PrefixCoupon." + name + ".Coupon") != null;
    }

    public static List<String> getPrefixList() {
        return new ArrayList<>(plugin.config.getConfigurationSection("Settings.PrefixList").getKeys(false));
    }

    public static String getPrefix(String name) {
        return ColorUtils.applyColor(plugin.config.getString("Settings.PrefixList." + name));
    }

    public static void saveConfig() {
        ConfigUtils.savePluginConfig(plugin, plugin.config);
        plugin.udata.values().forEach(data ->
                ConfigUtils.saveCustomData(plugin, data, data.getString("uuid"), "users"));
    }

    public static void removeAllLuckpermPrefix() {
        Arrays.stream(Bukkit.getOfflinePlayers()).collect(Collectors.toList()).forEach(player -> LuckpermAPI.delPrefix(player, plugin.prefixPriority));
    }
}