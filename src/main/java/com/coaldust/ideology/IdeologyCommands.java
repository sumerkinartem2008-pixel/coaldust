/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.arguments.IntegerArgumentType
 *  com.mojang.brigadier.arguments.StringArgumentType
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  net.minecraft.ChatFormatting
 *  net.minecraft.commands.CommandSourceStack
 *  net.minecraft.commands.Commands
 *  net.minecraft.core.BlockPos
 *  net.minecraft.network.chat.Component
 *  net.minecraft.server.level.ServerPlayer
 *  net.neoforged.neoforge.event.RegisterCommandsEvent
 */
package com.coaldust.ideology;

import com.coaldust.ideology.Abyss;
import com.coaldust.ideology.Ideology;
import com.coaldust.ideology.IdeologyManager;
import com.coaldust.ideology.IdeologyStructure;
import com.coaldust.ideology.Summons;
import com.coaldust.ideology.Trait;
import com.coaldust.ideology.Workers;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

public final class IdeologyCommands {
    public static void register(RegisterCommandsEvent registerCommandsEvent) {
        registerCommandsEvent.getDispatcher().register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal((String)"ideology").then(Commands.literal((String)"create").then(Commands.argument((String)"structure", (ArgumentType)StringArgumentType.word()).then(Commands.argument((String)"trait", (ArgumentType)StringArgumentType.word()).then(Commands.argument((String)"name", (ArgumentType)StringArgumentType.greedyString()).executes(commandContext -> {
            ServerPlayer serverPlayer = ((CommandSourceStack)commandContext.getSource()).getPlayerOrException();
            IdeologyStructure ideologyStructure = IdeologyStructure.byId(StringArgumentType.getString((CommandContext)commandContext, (String)"structure"));
            Trait trait = Trait.byId(StringArgumentType.getString((CommandContext)commandContext, (String)"trait"));
            if (ideologyStructure == null || trait == null) {
                serverPlayer.sendSystemMessage((Component)Component.translatable((String)"ideology.coaldust.msg.bad_args").withStyle(ChatFormatting.RED));
                return 0;
            }
            IdeologyManager.create(serverPlayer, StringArgumentType.getString((CommandContext)commandContext, (String)"name"), ideologyStructure, trait);
            return 1;
        })))))).then(Commands.literal((String)"found").then(Commands.argument((String)"structure", (ArgumentType)StringArgumentType.word()).then(Commands.argument((String)"trait", (ArgumentType)StringArgumentType.word()).then(Commands.argument((String)"color", (ArgumentType)IntegerArgumentType.integer((int)0, (int)0xFFFFFF)).then(Commands.argument((String)"icon", (ArgumentType)StringArgumentType.word()).then(Commands.argument((String)"name", (ArgumentType)StringArgumentType.greedyString()).executes(commandContext -> {
            ServerPlayer serverPlayer = ((CommandSourceStack)commandContext.getSource()).getPlayerOrException();
            IdeologyStructure ideologyStructure = IdeologyStructure.byId(StringArgumentType.getString((CommandContext)commandContext, (String)"structure"));
            Trait trait = Trait.byId(StringArgumentType.getString((CommandContext)commandContext, (String)"trait"));
            if (ideologyStructure == null || trait == null) {
                return 0;
            }
            IdeologyManager.create(serverPlayer, StringArgumentType.getString((CommandContext)commandContext, (String)"name"), ideologyStructure, trait, IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"color"), StringArgumentType.getString((CommandContext)commandContext, (String)"icon"));
            return 1;
        })))))))).then(Commands.literal((String)"reroll").executes(commandContext -> {
            IdeologyManager.reroll(((CommandSourceStack)commandContext.getSource()).getPlayerOrException());
            IdeologyManager.sync(((CommandSourceStack)commandContext.getSource()).getPlayerOrException());
            return 1;
        }))).then(Commands.literal((String)"sync").executes(commandContext -> {
            IdeologyManager.sync(((CommandSourceStack)commandContext.getSource()).getPlayerOrException());
            return 1;
        }))).then(Commands.literal((String)"request").then(Commands.argument((String)"code", (ArgumentType)StringArgumentType.word()).executes(commandContext -> {
            IdeologyManager.request(((CommandSourceStack)commandContext.getSource()).getPlayerOrException(), StringArgumentType.getString((CommandContext)commandContext, (String)"code"));
            return 1;
        })))).then(Commands.literal((String)"approve").then(Commands.argument((String)"player", (ArgumentType)StringArgumentType.word()).executes(commandContext -> {
            IdeologyManager.approve(((CommandSourceStack)commandContext.getSource()).getPlayerOrException(), StringArgumentType.getString((CommandContext)commandContext, (String)"player"), true);
            return 1;
        })))).then(Commands.literal((String)"deny").then(Commands.argument((String)"player", (ArgumentType)StringArgumentType.word()).executes(commandContext -> {
            IdeologyManager.approve(((CommandSourceStack)commandContext.getSource()).getPlayerOrException(), StringArgumentType.getString((CommandContext)commandContext, (String)"player"), false);
            return 1;
        })))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal((String)"set").then(Commands.literal((String)"name").then(Commands.argument((String)"v", (ArgumentType)StringArgumentType.greedyString()).executes(commandContext -> {
            IdeologyManager.setName(((CommandSourceStack)commandContext.getSource()).getPlayerOrException(), StringArgumentType.getString((CommandContext)commandContext, (String)"v"));
            return 1;
        })))).then(Commands.literal((String)"desc").then(Commands.argument((String)"v", (ArgumentType)StringArgumentType.greedyString()).executes(commandContext -> {
            IdeologyManager.setDesc(((CommandSourceStack)commandContext.getSource()).getPlayerOrException(), StringArgumentType.getString((CommandContext)commandContext, (String)"v"));
            return 1;
        })))).then(Commands.literal((String)"color").then(Commands.argument((String)"v", (ArgumentType)IntegerArgumentType.integer((int)0, (int)0xFFFFFF)).executes(commandContext -> {
            IdeologyManager.setColor(((CommandSourceStack)commandContext.getSource()).getPlayerOrException(), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"v"));
            return 1;
        })))).then(Commands.literal((String)"icon").then(Commands.argument((String)"v", (ArgumentType)StringArgumentType.word()).executes(commandContext -> {
            IdeologyManager.setIcon(((CommandSourceStack)commandContext.getSource()).getPlayerOrException(), StringArgumentType.getString((CommandContext)commandContext, (String)"v"));
            return 1;
        })))).then(Commands.literal((String)"structure").then(Commands.argument((String)"v", (ArgumentType)StringArgumentType.word()).executes(commandContext -> {
            IdeologyManager.setStructure(((CommandSourceStack)commandContext.getSource()).getPlayerOrException(), IdeologyStructure.byId(StringArgumentType.getString((CommandContext)commandContext, (String)"v")));
            return 1;
        }))))).then(Commands.literal((String)"invite").then(Commands.argument((String)"player", (ArgumentType)StringArgumentType.word()).executes(commandContext -> {
            IdeologyManager.invite(((CommandSourceStack)commandContext.getSource()).getPlayerOrException(), StringArgumentType.getString((CommandContext)commandContext, (String)"player"));
            return 1;
        })))).then(Commands.literal((String)"accept").then(Commands.argument((String)"code", (ArgumentType)StringArgumentType.word()).executes(commandContext -> {
            IdeologyManager.accept(((CommandSourceStack)commandContext.getSource()).getPlayerOrException(), StringArgumentType.getString((CommandContext)commandContext, (String)"code"));
            return 1;
        })))).then(Commands.literal((String)"leave").executes(commandContext -> {
            IdeologyManager.leave(((CommandSourceStack)commandContext.getSource()).getPlayerOrException());
            return 1;
        }))).then(Commands.literal((String)"kick").then(Commands.argument((String)"player", (ArgumentType)StringArgumentType.word()).executes(commandContext -> {
            IdeologyManager.kick(((CommandSourceStack)commandContext.getSource()).getPlayerOrException(), StringArgumentType.getString((CommandContext)commandContext, (String)"player"));
            return 1;
        })))).then(Commands.literal((String)"trait").then(Commands.argument((String)"trait", (ArgumentType)StringArgumentType.word()).executes(commandContext -> {
            ServerPlayer serverPlayer = ((CommandSourceStack)commandContext.getSource()).getPlayerOrException();
            Trait trait = Trait.byId(StringArgumentType.getString((CommandContext)commandContext, (String)"trait"));
            if (trait == null) {
                return 0;
            }
            IdeologyManager.pickTrait(serverPlayer, trait);
            return 1;
        })))).then(Commands.literal((String)"veil").then(Commands.argument((String)"w", (ArgumentType)StringArgumentType.greedyString()).executes(commandContext -> {
            ServerPlayer serverPlayer = ((CommandSourceStack)commandContext.getSource()).getPlayerOrException();
            Abyss.tryUnlock(serverPlayer, StringArgumentType.getString((CommandContext)commandContext, (String)"w"));
            IdeologyManager.sync(serverPlayer);
            return 1;
        })))).then(Commands.literal((String)"unveil").executes(commandContext -> {
            ServerPlayer serverPlayer = ((CommandSourceStack)commandContext.getSource()).getPlayerOrException();
            Abyss.close(serverPlayer);
            IdeologyManager.sync(serverPlayer);
            return 1;
        }))).then(Commands.literal((String)"wname").then(Commands.argument((String)"k", (ArgumentType)IntegerArgumentType.integer((int)0, (int)99)).then(Commands.argument((String)"name", (ArgumentType)StringArgumentType.greedyString()).executes(commandContext -> {
            ServerPlayer serverPlayer = ((CommandSourceStack)commandContext.getSource()).getPlayerOrException();
            Workers.rename(serverPlayer, IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"k"), StringArgumentType.getString((CommandContext)commandContext, (String)"name"));
            IdeologyManager.sync(serverPlayer);
            return 1;
        }))))).then(Commands.literal((String)"wshow").executes(commandContext -> {
            Workers.show(((CommandSourceStack)commandContext.getSource()).getPlayerOrException());
            return 1;
        }))).then(Commands.literal((String)"wcorner").then(Commands.argument((String)"n", (ArgumentType)IntegerArgumentType.integer((int)1, (int)2)).then(Commands.argument((String)"x", (ArgumentType)IntegerArgumentType.integer()).then(Commands.argument((String)"y", (ArgumentType)IntegerArgumentType.integer()).then(Commands.argument((String)"z", (ArgumentType)IntegerArgumentType.integer()).executes(commandContext -> {
            ServerPlayer serverPlayer = ((CommandSourceStack)commandContext.getSource()).getPlayerOrException();
            Workers.corner(serverPlayer, IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"n"), new BlockPos(IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"x"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"y"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"z")));
            IdeologyManager.sync(serverPlayer);
            return 1;
        }))))))).then(Commands.literal((String)"wsel").then(Commands.argument((String)"k", (ArgumentType)IntegerArgumentType.integer((int)-1, (int)1000)).executes(commandContext -> {
            ServerPlayer serverPlayer = ((CommandSourceStack)commandContext.getSource()).getPlayerOrException();
            Workers.select(serverPlayer, IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"k"));
            IdeologyManager.sync(serverPlayer);
            return 1;
        })))).then(Commands.literal((String)"work").then(((RequiredArgumentBuilder)Commands.argument((String)"task", (ArgumentType)StringArgumentType.word()).executes(commandContext -> {
            ServerPlayer serverPlayer = ((CommandSourceStack)commandContext.getSource()).getPlayerOrException();
            Workers.order(serverPlayer, StringArgumentType.getString((CommandContext)commandContext, (String)"task"), null);
            IdeologyManager.sync(serverPlayer);
            return 1;
        })).then(Commands.argument((String)"arg", (ArgumentType)StringArgumentType.greedyString()).executes(commandContext -> {
            ServerPlayer serverPlayer = ((CommandSourceStack)commandContext.getSource()).getPlayerOrException();
            Workers.order(serverPlayer, StringArgumentType.getString((CommandContext)commandContext, (String)"task"), StringArgumentType.getString((CommandContext)commandContext, (String)"arg"));
            IdeologyManager.sync(serverPlayer);
            return 1;
        }))))).then(Commands.literal((String)"wchest").then(Commands.argument((String)"x", (ArgumentType)IntegerArgumentType.integer()).then(Commands.argument((String)"y", (ArgumentType)IntegerArgumentType.integer()).then(Commands.argument((String)"z", (ArgumentType)IntegerArgumentType.integer()).then(((RequiredArgumentBuilder)Commands.argument((String)"act", (ArgumentType)StringArgumentType.word()).executes(commandContext -> {
            ServerPlayer serverPlayer = ((CommandSourceStack)commandContext.getSource()).getPlayerOrException();
            Workers.chest(serverPlayer, new BlockPos(IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"x"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"y"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"z")), StringArgumentType.getString((CommandContext)commandContext, (String)"act"), null);
            IdeologyManager.sync(serverPlayer);
            return 1;
        })).then(Commands.argument((String)"val", (ArgumentType)StringArgumentType.greedyString()).executes(commandContext -> {
            ServerPlayer serverPlayer = ((CommandSourceStack)commandContext.getSource()).getPlayerOrException();
            Workers.chest(serverPlayer, new BlockPos(IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"x"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"y"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"z")), StringArgumentType.getString((CommandContext)commandContext, (String)"act"), StringArgumentType.getString((CommandContext)commandContext, (String)"val"));
            IdeologyManager.sync(serverPlayer);
            return 1;
        })))))))).then(Commands.literal((String)"summon").executes(commandContext -> {
            ServerPlayer serverPlayer = ((CommandSourceStack)commandContext.getSource()).getPlayerOrException();
            Summons.summonSquad(serverPlayer);
            return 1;
        }))).then(Commands.literal((String)"evolve").then(Commands.argument((String)"skill", (ArgumentType)StringArgumentType.word()).executes(commandContext -> {
            ServerPlayer serverPlayer = ((CommandSourceStack)commandContext.getSource()).getPlayerOrException();
            Abyss.learn(serverPlayer, StringArgumentType.getString((CommandContext)commandContext, (String)"skill"));
            IdeologyManager.sync(serverPlayer);
            return 1;
        })))).then(Commands.literal((String)"info").executes(commandContext -> {
            ServerPlayer serverPlayer = ((CommandSourceStack)commandContext.getSource()).getPlayerOrException();
            Ideology ideology = IdeologyManager.of(serverPlayer);
            if (ideology == null) {
                serverPlayer.sendSystemMessage((Component)Component.translatable((String)"ideology.coaldust.msg.no_ideology"));
                return 1;
            }
            StringBuilder stringBuilder = new StringBuilder("[" + ideology.name + "] lvl " + ideology.level + " xp " + ideology.xp + "/" + ideology.xpForNext() + " | ");
            for (Trait trait : ideology.traits) {
                stringBuilder.append(trait.id).append(' ');
            }
            stringBuilder.append("| ").append(String.join((CharSequence)", ", ideology.members.values()));
            serverPlayer.sendSystemMessage((Component)Component.literal((String)stringBuilder.toString()));
            return 1;
        })));
    }

    private IdeologyCommands() {
    }
}
