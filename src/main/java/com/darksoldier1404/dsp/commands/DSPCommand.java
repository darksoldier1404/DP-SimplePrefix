package com.darksoldier1404.dsp.commands;

import com.darksoldier1404.dppc.builder.command.CommandBuilder;
import com.darksoldier1404.dppc.lang.DLang;
import com.darksoldier1404.dsp.SimplePrefix;
import com.darksoldier1404.dsp.functions.DSPFunction;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;

import java.util.stream.Collectors;

public class DSPCommand {
    private final CommandBuilder commandBuilder;
    private final String prefix;
    private final DLang lang;

    public DSPCommand() {
        this.prefix = SimplePrefix.prefix;
        this.lang = SimplePrefix.lang;

        CommandBuilder builder = new CommandBuilder(prefix);

        builder.addSubCommand("equip", lang.get("help_equip"), (player, args) -> {
            if (args.length == 2) {
                DSPFunction.equipPrefix((Player) player, args[1]);
            } else {
                player.sendMessage(prefix + lang.get("help_equip"));
            }
        });

        builder.addSubCommand("unequip",  lang.get("help_unequip"), (player, args) -> {
            if (args.length == 1) {
                DSPFunction.unequipPrefix((Player) player);
            } else {
                player.sendMessage(prefix + lang.get("help_unequip"));
            }
        });

        builder.addSubCommand("my", lang.get("help_my"), (player, args) -> {
            if (args.length == 1) {
                DSPFunction.showPrefixList((Player) player);
            }
        });

        // Admin commands (require dsp.admin permission)
        builder.addSubCommand("create", "dsp.admin", lang.get("help_create"), (player, args) -> {
            if (args.length == 1) {
                player.sendMessage(prefix + lang.get("help_create"));
            } else {
                DSPFunction.createPrefix((Player) player, args[1]);
            }
        });

        builder.addSubCommand("set", "dsp.admin", lang.get("help_set"), (player, args) -> {
            if (args.length == 1) {
                player.sendMessage(prefix + lang.get("help_set"));
            } else {
                DSPFunction.openSetPrefixGUI((Player) player, args[1]);
            }
        });

        builder.addSubCommand("delete", "dsp.admin", lang.get("help_delete"), (player, args) -> {
            if (args.length == 1) {
                player.sendMessage(prefix + lang.get("help_delete"));
            } else {
                DSPFunction.deletePrefix((Player) player, args[1]);
            }
        });

        builder.addSubCommand("coupon", "dsp.admin",  lang.get("help_coupon"), (player, args) -> {
            if (args.length == 1) {
                DSPFunction.openGlobalCouponSetting((Player) player);
            } else if (args.length == 2) {
                DSPFunction.openCouponSetting((Player) player, args[1]);
            }
        });

        builder.addSubCommand("givecoupon", "dsp.admin",  lang.get("help_givecoupon"), (player, args) -> {
            if (args.length == 1) {
                player.sendMessage(prefix + lang.get("help_givecoupon"));
            } else if (args.length == 2) {
                DSPFunction.getPrefixCoupon((Player) player, args[1]);
            } else if (args.length == 3) {
                Player target = Bukkit.getPlayer(args[2]);
                if (target == null) {
                    player.sendMessage(prefix + lang.get("player_online"));
                } else {
                    DSPFunction.getPrefixCoupon(target, args[1]);
                }
            }
        });

        builder.addSubCommand("default", "dsp.admin",  lang.get("help_default"), (player, args) -> {
            if (args.length == 1) {
                player.sendMessage(prefix + lang.get("help_default"));
            } else {
                DSPFunction.setDefaultPrefix((Player) player, args[1]);
            }
        });

        builder.addSubCommand("list", "dsp.admin",  lang.get("help_list"), (player, args) -> {
            DSPFunction.showAllPrefixList((Player) player);
        });

        builder.addSubCommand("reload", "dsp.admin",  lang.get("config_reload"), (player, args) -> {
            DSPFunction.initConfig();
            player.sendMessage(prefix + lang.get("config_reload"));
        });

        builder.addTabCompletion("equip", args -> DSPFunction.getPrefixList());
        builder.addTabCompletion("create", args -> DSPFunction.getPrefixList());
        builder.addTabCompletion("set", args -> DSPFunction.getPrefixList());
        builder.addTabCompletion("delete", args -> DSPFunction.getPrefixList());
        builder.addTabCompletion("coupon", args -> DSPFunction.getPrefixList());
        builder.addTabCompletion("givecoupon", args -> {
            if (args.length == 2) {
                return DSPFunction.getPrefixList();
            } else if (args.length == 3) {
                return Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
            }
            return null;
        });

        builder.addTabCompletion("default", args -> DSPFunction.getPrefixList());
        builder.setNoSubCommandsMessage(prefix + lang.get("no_commands_available"));
        this.commandBuilder = builder;
    }

    public CommandExecutor getExecutor() {
        return commandBuilder;
    }
}