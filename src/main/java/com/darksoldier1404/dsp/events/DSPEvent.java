package com.darksoldier1404.dsp.events;

import com.darksoldier1404.dsp.*;
import org.bukkit.configuration.file.*;
import org.bukkit.event.*;
import org.bukkit.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.event.block.*;
import org.bukkit.inventory.*;
import com.darksoldier1404.dppc.api.inventory.*;
import com.darksoldier1404.dppc.utils.*;
import com.darksoldier1404.dsp.functions.*;
import org.bukkit.event.inventory.*;

public class DSPEvent implements Listener
{
    private final SimplePrefix plugin;

    public DSPEvent() {
        this.plugin = SimplePrefix.getInstance();
    }

    @EventHandler
    public void onJoin(final PlayerJoinEvent e) {
        this.plugin.udata.put(e.getPlayer().getUniqueId(), ConfigUtils.initUserData(this.plugin, e.getPlayer().getUniqueId().toString(), "users"));
        ConfigUtils.saveCustomData(this.plugin, this.plugin.udata.get(e.getPlayer().getUniqueId()), e.getPlayer().getUniqueId().toString(), "users");
        DSPFunction.syncPrefix(e.getPlayer());
    }

    @EventHandler
    public void onQuit(final PlayerQuitEvent e) {
        final YamlConfiguration data = this.plugin.udata.get(e.getPlayer().getUniqueId());
        ConfigUtils.saveCustomData(this.plugin, data, e.getPlayer().getUniqueId().toString(), "users");
        this.plugin.udata.remove(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onChat(final PlayerChatEvent e) {
        final Player p = e.getPlayer();
        if (DSPFunction.currentEditPrefix.containsKey(p.getUniqueId())) {
            e.setCancelled(true);
            final String name = DSPFunction.currentEditPrefix.get(p.getUniqueId());
            final String prefix = e.getMessage();
            this.plugin.config.set("Settings.PrefixList." + name, prefix);
            ConfigUtils.savePluginConfig(this.plugin, this.plugin.config);
            p.sendMessage(this.plugin.prefix + name + plugin.lang.get("prefix_display_set") + ColorUtils.applyColor(prefix));
            DSPFunction.currentEditPrefix.remove(p.getUniqueId());
            return;
        }
        if(SimplePrefix.isLuckpermMode) {
            return;
        }
        final String name = (this.plugin.udata.get(p.getUniqueId()).getString("Player.Prefix") == null) ? DSPFunction.giveDefaultPrefix(p) : this.plugin.udata.get(p.getUniqueId()).getString("Player.Prefix");
        this.plugin.config.getConfigurationSection("Settings.PrefixList").getKeys(false).forEach(s -> {
            if (s.equals(name)) {
                e.setFormat(ColorUtils.applyColor(this.plugin.config.getString("Settings.PrefixList." + name)) + e.getFormat());
            }
        });
    }

    @EventHandler
    public void onInteract(final PlayerInteractEvent e) {
        if (e.getAction().equals(Action.LEFT_CLICK_AIR) || e.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
            return;
        }
        if (e.getHand() == EquipmentSlot.OFF_HAND) {
            return;
        }
        if (e.getItem() == null) {
            return;
        }
        final ItemStack item = e.getItem();
        if (NBT.hasTagKey(item, "dsp.prefix")) {
            e.setCancelled(true);
            final String name = NBT.getStringTag(item, "dsp.prefix");
            this.plugin.config.getConfigurationSection("Settings.PrefixList").getKeys(false).forEach(s -> {
                if (s.equals(name) && DSPFunction.givePrefix(e.getPlayer(), name)) {
                    item.setAmount(item.getAmount() - 1);
                }
            });
        }
    }

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent e) {
        if (e.getInventory().getHolder() instanceof DInventory) {
            final DInventory inv = (DInventory)e.getInventory().getHolder();
            if (inv.isValidHandler(this.plugin)) {
                if (inv.getObj() != null) {
                    final Tuple<String, SettingType> tpl = (Tuple<String, SettingType>)inv.getObj();
                    if (tpl.getB() == SettingType.INDIVIDUAL_COUPON || tpl.getB() == SettingType.GLOBAL_COUPON) {
                        if (e.getClickedInventory().getHolder() instanceof DInventory && e.getSlot() != 13) {
                            e.setCancelled(true);
                        }
                        return;
                    }
                }
                e.setCancelled(true);
                if (e.getCurrentItem() != null && NBT.hasTagKey(e.getCurrentItem(), "dsp.prefix")) {
                    final String name = NBT.getStringTag(e.getCurrentItem(), "dsp.prefix");
                    DSPFunction.equipPrefix((Player)e.getWhoClicked(), name);
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClose(final InventoryCloseEvent e) {
        final Player p = (Player)e.getPlayer();
        if (e.getInventory().getHolder() instanceof DInventory) {
            final DInventory inv = (DInventory)e.getInventory().getHolder();
            if (inv.isValidHandler(this.plugin) && inv.getObj() != null) {
                final Tuple<String, SettingType> tpl = (Tuple<String, SettingType>)inv.getObj();
                if (tpl.getB() == SettingType.GLOBAL_COUPON) {
                    DSPFunction.saveGlobalCouponSetting(p, inv);
                }
                if (tpl.getB() == SettingType.INDIVIDUAL_COUPON) {
                    DSPFunction.saveCouponSetting(p, inv);
                }
            }
        }
    }
}
