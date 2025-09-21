package com.darksoldier1404.dsp.commands;

import com.darksoldier1404.dppc.builder.command.CommandBuilder;
import com.darksoldier1404.dsp.functions.DSPFunction;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;

import java.util.stream.Collectors;

import static com.darksoldier1404.dppc.DPPCore.plugin;

public class DSPCommand {
    private final CommandBuilder commandBuilder;

    public DSPCommand() {

        CommandBuilder builder = new CommandBuilder(plugin);

        builder.addSubCommand("equip", plugin.getLang().get("help_equip"), (player, args) -> {
            if (args.length == 2) {
                DSPFunction.equipPrefix((Player) player, args[1]);
            } else {
                player.sendMessage(plugin.getPrefix() + plugin.getLang().get("help_equip"));
            }
            return true;
        });

        builder.addSubCommand("unequip", plugin.getLang().get("help_unequip"), (player, args) -> {
            if (args.length == 1) {
                DSPFunction.unequipPrefix((Player) player);
            } else {
                player.sendMessage(plugin.getPrefix() + plugin.getLang().get("help_unequip"));
            }
            return true;
        });

        builder.addSubCommand("my", plugin.getLang().get("help_my"), (player, args) -> {
            if (args.length == 1) {
                DSPFunction.showPrefixList((Player) player);
            }
            return true;
        });

        // Admin commands (require dsp.admin permission)
        builder.addSubCommand("create", "dsp.admin", plugin.getLang().get("help_create"), (player, args) -> {
            if (args.length == 1) {
                player.sendMessage(plugin.getPrefix() + plugin.getLang().get("help_create"));
            } else {
                DSPFunction.createPrefix((Player) player, args[1]);
            }
            return true;
        });

        builder.addSubCommand("set", "dsp.admin", plugin.getLang().get("help_set"), (player, args) -> {
            if (args.length == 1) {
                player.sendMessage(plugin.getPrefix() + plugin.getLang().get("help_set"));
            } else {
                DSPFunction.openSetPrefixGUI((Player) player, args[1]);
            }
            return true;
        });

        builder.addSubCommand("delete", "dsp.admin", plugin.getLang().get("help_delete"), (player, args) -> {
            if (args.length == 1) {
                player.sendMessage(plugin.getPrefix() + plugin.getLang().get("help_delete"));
            } else {
                DSPFunction.deletePrefix((Player) player, args[1]);
            }
            return true;
        });

        builder.addSubCommand("coupon", "dsp.admin", plugin.getLang().get("help_coupon"), (player, args) -> {
            if (args.length == 1) {
                DSPFunction.openGlobalCouponSetting((Player) player);
            } else if (args.length == 2) {
                DSPFunction.openCouponSetting((Player) player, args[1]);
            }
            return true;
        });

        builder.addSubCommand("givecoupon", "dsp.admin", plugin.getLang().get("help_givecoupon"), (player, args) -> {
            if (args.length == 1) {
                player.sendMessage(plugin.getPrefix() + plugin.getLang().get("help_givecoupon"));
            } else if (args.length == 2) {
                DSPFunction.getPrefixCoupon((Player) player, args[1]);
            } else if (args.length == 3) {
                Player target = Bukkit.getPlayer(args[2]);
                if (target == null) {
                    player.sendMessage(plugin.getPrefix() + plugin.getLang().get("player_online"));
                } else {
                    DSPFunction.getPrefixCoupon(target, args[1]);
                }
            }
            return true;
        });

        builder.addSubCommand("default", "dsp.admin", plugin.getLang().get("help_default"), (player, args) -> {
            if (args.length == 1) {
                player.sendMessage(plugin.getPrefix() + plugin.getLang().get("help_default"));
            } else {
                DSPFunction.setDefaultPrefix((Player) player, args[1]);
            }
            return true;
        });

        builder.addSubCommand("list", "dsp.admin", plugin.getLang().get("help_list"), (player, args) -> {
            DSPFunction.showAllPrefixList((Player) player);
            return true;
        });

        builder.addSubCommand("reload", "dsp.admin", plugin.getLang().get("config_reload"), (player, args) -> {
            plugin.reload();
            player.sendMessage(plugin.getPrefix() + plugin.getLang().get("config_reload"));
            return true;
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
        builder.setNoSubCommandsMessage(plugin.getPrefix() + plugin.getLang().get("no_commands_available"));
        this.commandBuilder = builder;
    }

    public CommandExecutor getExecutor() {
        return commandBuilder;
    }
}