package com.darksoldier1404.dsp.commands;

import com.darksoldier1404.dppc.lang.DLang;
import com.darksoldier1404.dsp.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import com.darksoldier1404.dsp.functions.*;
import java.util.*;
import org.bukkit.*;
import java.util.stream.*;
import org.jetbrains.annotations.*;
import sun.java2d.pipe.SpanShapeRenderer;

public class DSPCommand implements CommandExecutor, TabCompleter
{
    private final String prefix;
    private final DLang lang;
    public DSPCommand() {
        this.lang = SimplePrefix.getInstance().lang;
        this.prefix = SimplePrefix.getInstance().prefix;
    }

    public boolean onCommand(@NotNull final CommandSender sender, @NotNull final Command command, @NotNull final String label, @NotNull final String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(this.prefix + lang.get("player_only"));
            return false;
        }
        final Player p = (Player)sender;
        if (args.length == 0) {
            if (p.isOp()) {
                sender.sendMessage(this.prefix + lang.get("help_create"));
                sender.sendMessage(this.prefix + lang.get("help_set"));
                sender.sendMessage(this.prefix + lang.get("help_delete"));
                sender.sendMessage(this.prefix + lang.get("help_coupon"));
                sender.sendMessage(this.prefix + lang.get("help_givecoupon"));
                sender.sendMessage(this.prefix + lang.get("help_default"));
                sender.sendMessage(this.prefix + lang.get("help_list"));
            }
            sender.sendMessage(this.prefix + lang.get("help_equip"));
            sender.sendMessage(this.prefix + lang.get("help_unequip"));
            sender.sendMessage(this.prefix + lang.get("help_my"));
            return false;
        }
        if (args[0].equals("equip")) {
            if (args.length == 1) {
                sender.sendMessage(this.prefix + lang.get("help_equip"));
                return false;
            }
            if (args.length == 2) {
                DSPFunction.equipPrefix(p, args[1]);
                return false;
            }
            sender.sendMessage(this.prefix + lang.get("help_equip"));
            return false;
        }
        else if (args[0].equals("unequip")) {
            if (args.length == 1) {
                DSPFunction.unequipPrefix(p);
                return false;
            }
            sender.sendMessage(this.prefix + lang.get("help_unequip"));
            return false;
        }
        else {
            if (!args[0].equals("my")) {
                if (p.isOp()) {
                    if (args[0].equals("create")) {
                        if (args.length == 1) {
                            sender.sendMessage(this.prefix + "/dsp create <prefix>");
                            return false;
                        }
                        DSPFunction.createPrefix(p, args[1]);
                        return false;
                    }
                    else if (args[0].equals("set")) {
                        if (args.length == 1) {
                            sender.sendMessage(this.prefix + "/dsp set <prefix>");
                            return false;
                        }
                        DSPFunction.openSetPrefixGUI(p, args[1]);
                        return false;
                    }
                    else if (args[0].equals("delete")) {
                        if (args.length == 1) {
                            sender.sendMessage(this.prefix + "/dsp delete <prefix>");
                            return false;
                        }
                        DSPFunction.deletePrefix(p, args[1]);
                        return false;
                    }
                    else if (args[0].equals("coupon")) {
                        if (args.length == 1) {
                            DSPFunction.openGlobalCouponSetting(p);
                            return false;
                        }
                        if (args.length == 2) {
                            DSPFunction.openCouponSetting(p, args[1]);
                            return false;
                        }
                        return false;
                    }
                    else if (args[0].equalsIgnoreCase("givecoupon")) {
                        if (args.length == 1) {
                            sender.sendMessage(this.prefix + "/dsp givecoupon <prefix> (username)");
                            return false;
                        }
                        if (args.length == 2) {
                            DSPFunction.getPrefixCoupon(p, args[1]);
                            return false;
                        }
                        if (args.length != 3) {
                            return false;
                        }
                        final Player target = Bukkit.getPlayer(args[2]);
                        if (target == null) {
                            sender.sendMessage(this.prefix + lang.get("player_online"));
                            return false;
                        }
                        DSPFunction.getPrefixCoupon(target, args[1]);
                        return false;
                    }
                    else if (args[0].equals("default")) {
                        if (args.length == 1) {
                            sender.sendMessage(this.prefix + "/dsp default <prefix>");
                            return false;
                        }
                        DSPFunction.setDefaultPrefix(p, args[1]);
                        return false;
                    }
                    else {
                        if (args[0].equals("list")) {
                            DSPFunction.showAllPrefixList(p);
                            return false;
                        }
                        if (args[0].equalsIgnoreCase("reload")) {
                            DSPFunction.initConfig();
                            sender.sendMessage(this.prefix + lang.get("config_reload"));
                            return false;
                        }
                    }
                }
                return false;
            }
            if (args.length == 1) {
                DSPFunction.showPrefixList(p);
                return false;
            }
            return false;
        }
    }

    @Nullable
    public List<String> onTabComplete(@NotNull final CommandSender sender, @NotNull final Command command, @NotNull final String alias, @NotNull final String[] args) {
        if (args.length == 1) {
            if (sender.isOp()) {
                return Arrays.asList("create", "set", "delete", "coupon", "givecoupon", "default", "list", "reload", "equip", "unequip", "my");
            }
            return Arrays.asList("equip", "unequip", "my");
        }
        else {
            if (args.length == 2 && !args[0].equalsIgnoreCase("list")) {
                return DSPFunction.getPrefixList();
            }
            if (args.length == 3 && args[0].equalsIgnoreCase("givecoupon")) {
                return Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
            }
            return null;
        }
    }
}
