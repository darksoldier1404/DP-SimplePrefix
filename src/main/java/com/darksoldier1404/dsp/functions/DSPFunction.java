package com.darksoldier1404.dsp.functions;

import com.darksoldier1404.dppc.api.inventory.DInventory;
import com.darksoldier1404.dppc.api.luckperms.LuckpermAPI;
import com.darksoldier1404.dppc.api.placeholder.PlaceholderBuilder;
import com.darksoldier1404.dppc.utils.ColorUtils;
import com.darksoldier1404.dppc.utils.NBT;
import com.darksoldier1404.dppc.utils.Tuple;
import com.darksoldier1404.dsp.SimplePrefix;
import org.bukkit.Bukkit;
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
    public static final Map<UUID, String> currentEditPrefix = new HashMap<>();

    public static void initConfig() {
        plugin.isLuckpermMode = plugin.getConfig().getBoolean("Settings.enable_luckperm_prefix");
        plugin.prefixPriority = plugin.getConfig().getInt("Settings.luckperm_prefix_priority");
        if (plugin.getConfig().getBoolean("Settings.lubkperm_prefix_delete_by_priority")) {
            removeAllLuckpermPrefix();
        }
    }

    public static void createPrefix(Player player, String name) {
        if (isExistPrefix(name)) {
            player.sendMessage(plugin.getPrefix() + plugin.getLang().get("prefix_exists"));
            return;
        }
        plugin.getConfig().set("Settings.PrefixList." + name, "temp");
        plugin.saveDataContainer();
        player.sendMessage(plugin.getPrefix() + name + plugin.getLang().get("prefix_create"));
    }

    public static void deletePrefix(Player player, String name) {
        if (!isExistPrefix(name)) {
            player.sendMessage(plugin.getPrefix() + plugin.getLang().get("prefix_not_exists"));
            return;
        }
        plugin.getConfig().set("Settings.PrefixList." + name, null);
        plugin.saveDataContainer();
        player.sendMessage(plugin.getPrefix() + name + plugin.getLang().get("prefix_delete"));
    }

    public static void openSetPrefixGUI(Player player, String name) {
        currentEditPrefix.put(player.getUniqueId(), name);
        player.sendMessage(plugin.getPrefix() + plugin.getLang().get("prefix_type"));
    }

    public static void showAllPrefixList(Player player) {
        player.sendMessage(plugin.getPrefix() + plugin.getLang().get("prefix_list"));
        for (String key : plugin.getConfig().getConfigurationSection("Settings.PrefixList").getKeys(false)) {
            player.sendMessage(plugin.getPrefix() + key + " : " + ColorUtils.applyColor(plugin.getConfig().getString("Settings.PrefixList." + key)));
        }
    }

    public static boolean isExistPrefix(String name) {
        return plugin.getConfig().get("Settings.PrefixList." + name) != null;
    }

    public static void showPrefixList(Player player) {
        List<String> prefixList = plugin.udata.get(player.getUniqueId()).getStringList("Player.PrefixList");
        if (prefixList.isEmpty()) {
            player.sendMessage(plugin.getPrefix() + plugin.getLang().get("prefix_not_have_any"));
            return;
        }

        List<ItemStack> prefixItems = new ArrayList<>();
        for (String key : prefixList) {
            String prefixValue = plugin.getConfig().getString("Settings.PrefixList." + key);
            if (prefixValue != null) {
                ItemStack item = new ItemStack(Material.PAPER);
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(ColorUtils.applyColor(prefixValue));
                meta.setLore(Arrays.asList("", plugin.getLang().get("prefix_lore1"), plugin.getLang().get("prefix_lore2") + key));
                item.setItemMeta(meta);
                prefixItems.add(NBT.setStringTag(item, "dsp.prefix", key));
            }
        }

        DInventory inventory = new DInventory(plugin.getLang().get("prefix_title"), 54, true, plugin);
        ItemStack pane = NBT.setStringTag(new ItemStack(Material.BLACK_STAINED_GLASS_PANE), "page", "true");
        ItemStack prev = NBT.setStringTag(new ItemStack(Material.PINK_DYE), "prev", "true");
        ItemStack current = NBT.setStringTag(new ItemStack(Material.PAPER), "current", "true");
        ItemStack next = NBT.setStringTag(new ItemStack(Material.LIME_DYE), "next", "true");

        ItemMeta prevMeta = prev.getItemMeta();
        prevMeta.setDisplayName(plugin.getLang().get("prev_page"));
        prev.setItemMeta(prevMeta);

        ItemMeta nextMeta = next.getItemMeta();
        nextMeta.setDisplayName(plugin.getLang().get("next_page"));
        next.setItemMeta(nextMeta);

        ItemMeta currentMeta = current.getItemMeta();
        currentMeta.setDisplayName(plugin.getLang().get("current_page") + (inventory.getCurrentPage() + 1));
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
        player.openInventory(inventory.getInventory());
    }

    public static void equipPrefix(Player player, String name) {
        YamlConfiguration data = plugin.udata.get(player.getUniqueId());
        List<String> prefixList = data.getStringList("Player.PrefixList");

        if (prefixList.contains(name)) {
            if (name.equals(data.getString("Player.Prefix"))) {
                player.sendMessage(plugin.getPrefix() + plugin.getLang().get("prefix_already_equipped"));
                return;
            }
            data.set("Player.Prefix", name);
            player.sendMessage(plugin.getPrefix() + name + plugin.getLang().get("prefix_equipped"));
            plugin.saveDataContainer();
            if (SimplePrefix.isLuckpermMode) {
                removeLuckpermPrefix(player);
                LuckpermAPI.setPrefix(player, getPrefix(name), 0);
            }
        } else {
            player.sendMessage(plugin.getPrefix() + plugin.getLang().get("prefix_not_have"));
        }
    }

    public static void syncPrefix(Player player) {
        YamlConfiguration data = plugin.udata.get(player.getUniqueId());
        String prefixName = data.getString("Player.Prefix");
        if (prefixName == null || prefixName.isEmpty()) {
            return;
        }
        if (SimplePrefix.isLuckpermMode) {
            LuckpermAPI.setPrefix(player, getPrefix(prefixName), 0);
        }
    }

    public static void unequipPrefix(Player player) {
        YamlConfiguration data = plugin.udata.get(player.getUniqueId());
        if (data.getString("Player.Prefix") == null) {
            player.sendMessage(plugin.getPrefix() + plugin.getLang().get("prefix_not_have_any_equipped"));
            return;
        }
        data.set("Player.Prefix", "");
        if (SimplePrefix.isLuckpermMode) {
            LuckpermAPI.delPrefix(player, 0);
        }
        player.sendMessage(plugin.getPrefix() + plugin.getLang().get("prefix_unequipped"));
        plugin.saveDataContainer();
    }

    public static boolean givePrefix(Player player, String name) {
        YamlConfiguration data = plugin.udata.get(player.getUniqueId());
        if (data == null) {
            System.out.println("data is null");
            return false;
        }

        List<String> prefixList = data.getStringList("Player.PrefixList");
        if (prefixList.contains(name)) {
            player.sendMessage(plugin.getPrefix() + plugin.getLang().get("prefix_already_have"));
            return false;
        }

        prefixList.add(name);
        data.set("Player.PrefixList", prefixList);
        plugin.udata.put(player.getUniqueId(), data);
        player.sendMessage(plugin.getPrefix() + name + plugin.getLang().get("prefix_added"));
        plugin.saveDataContainer();
        return true;
    }

    public static void getPrefixCoupon(Player player, String name) {
        if (!plugin.getConfig().getConfigurationSection("Settings.PrefixList").getKeys(false).contains(name)) {
            player.sendMessage(plugin.getPrefix() + plugin.getLang().get("prefix_not_exists"));
            return;
        }

        String prefixValue = plugin.getConfig().getString("Settings.PrefixList." + name);
        String itemName = ColorUtils.applyColor(plugin.getConfig().getString("Settings.couponNameFormat").replace("<name>", name).replace("<prefix>", getPrefix(name)));
        ItemStack item;

        if (hasIndividualCoupon(name)) {
            item = plugin.getConfig().getItemStack("PrefixCoupon." + name + ".Coupon").clone();
        } else {
            item = plugin.getConfig().getItemStack("GlobalPrefixCoupon.Coupon").clone();
            if (item == null) {
                player.sendMessage(plugin.getPrefix() + plugin.getLang().get("no_coupon"));
                return;
            }
        }
        item.setAmount(1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(itemName);
        item.setItemMeta(meta);
        item = initPlaceholder(item, name);
        player.getInventory().addItem(item);
        player.sendMessage(ColorUtils.applyColor(prefixValue) + plugin.getLang().get("coupon_give"));
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

    @Deprecated // This logic will be removed
    public static void setDefaultPrefix(Player player, String name) {
//        plugin.getConfig().set("Settings.DefaultPrefix", name);
//        plugin.saveDataContainer();
//        player.sendMessage(prefix + name + plugin.getLang().get("set_default"));
//
//        for (YamlConfiguration data : plugin.udata.values()) {
//            List<String> prefixList = data.getStringList("Player.PrefixList");
//            if (!prefixList.contains(name)) {
//                prefixList.add(name);
//                data.set("Player.PrefixList", prefixList);
//                if (data.getString("Player.Prefix") == null) {
//                    data.set("Player.Prefix", name);
//                }
//            }
//        }
    }

    public static String giveDefaultPrefix(Player player) {
        String defaultPrefix = plugin.getConfig().getString("Settings.DefaultPrefix");
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

        return plugin.getConfig().getString("Settings.PrefixList." + defaultPrefix);
    }

    public static void openCouponSetting(Player player, String name) {
        if (!isExistPrefix(name)) {
            player.sendMessage(plugin.getPrefix() + plugin.getLang().get("prefix_not_exists"));
            return;
        }

        DInventory inventory = new DInventory(plugin.getLang().get("coupon_setting_title"), 27, plugin);
        inventory.setObj(Tuple.of(name, SettingType.INDIVIDUAL_COUPON));

        ItemStack pane = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, pane);
        }

        ItemStack coupon = plugin.getConfig().getItemStack("PrefixCoupon." + name + ".Coupon");
        inventory.setItem(13, coupon != null ? coupon : new ItemStack(Material.AIR));

        player.openInventory(inventory.getInventory());
    }

    public static void openGlobalCouponSetting(Player player) {
        DInventory inventory = new DInventory(plugin.getLang().get("global_coupon_setting_title"), 27, plugin);
        inventory.setObj(Tuple.of("Global", SettingType.GLOBAL_COUPON));

        ItemStack pane = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, pane);
        }

        ItemStack coupon = plugin.getConfig().getItemStack("GlobalPrefixCoupon.Coupon");
        inventory.setItem(13, coupon != null ? coupon : new ItemStack(Material.AIR));

        player.openInventory(inventory.getInventory());
    }

    public static void saveCouponSetting(Player player, DInventory inventory) {
        Tuple<String, SettingType> tuple = (Tuple<String, SettingType>) inventory.getObj();
        plugin.getConfig().set("PrefixCoupon." + tuple.getA() + ".Coupon", inventory.getItem(13));
        player.sendMessage(plugin.getPrefix() + plugin.getLang().get("coupon_set"));
        plugin.saveDataContainer();
    }

    public static void saveGlobalCouponSetting(Player player, DInventory inventory) {
        plugin.getConfig().set("GlobalPrefixCoupon.Coupon", inventory.getItem(13));
        player.sendMessage(plugin.getPrefix() + plugin.getLang().get("coupon_set"));
        plugin.saveDataContainer();
    }

    public static boolean hasIndividualCoupon(String name) {
        return plugin.getConfig().get("PrefixCoupon." + name + ".Coupon") != null;
    }

    public static List<String> getPrefixList() {
        return new ArrayList<>(plugin.getConfig().getConfigurationSection("Settings.PrefixList").getKeys(false));
    }

    public static String getPrefix(String name) {
        return ColorUtils.applyColor(plugin.getConfig().getString("Settings.PrefixList." + name));
    }

    public static void removeAllLuckpermPrefix() {
        Arrays.stream(Bukkit.getOfflinePlayers()).collect(Collectors.toList()).forEach(player -> LuckpermAPI.delPrefix(player, plugin.prefixPriority));
    }

    public static void removeLuckpermPrefix(Player player) {
        if (SimplePrefix.isLuckpermMode) {
            LuckpermAPI.delPrefix(player, plugin.prefixPriority);
        }
    }

    public static void initPlaceholderAPI() {
        PlaceholderBuilder.Builder pb = new PlaceholderBuilder.Builder(plugin);
        pb.identifier("dsp");
        pb.version(plugin.getDescription().getVersion());
        pb.onRequest(
                (player, string) -> {
                    if (string.equals("prefix")) {
                        YamlConfiguration data = plugin.udata.get(player.getUniqueId());
                        String prefixName = data.getString("Player.Prefix");
                        if (prefixName == null || prefixName.isEmpty()) {
                            return "";
                        }
                        return getPrefix(prefixName);
                    }
                    return null;
                }
        );
        pb.build();
    }
}