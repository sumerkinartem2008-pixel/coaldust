/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.ChatFormatting
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Holder
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.tags.DamageTypeTags
 *  net.minecraft.world.damagesource.DamageSource
 *  net.minecraft.world.entity.AgeableMob
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.ai.attributes.Attribute
 *  net.minecraft.world.entity.ai.attributes.AttributeInstance
 *  net.minecraft.world.entity.ai.attributes.AttributeModifier
 *  net.minecraft.world.entity.ai.attributes.AttributeModifier$Operation
 *  net.minecraft.world.entity.ai.attributes.Attributes
 *  net.minecraft.world.entity.item.ItemEntity
 *  net.minecraft.world.entity.monster.Enemy
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.entity.projectile.Projectile
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.BonemealableBlock
 *  net.minecraft.world.level.block.CropBlock
 *  net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BrewingStandBlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.saveddata.SavedData
 *  net.minecraft.world.level.saveddata.SavedData$Factory
 *  net.minecraft.world.phys.AABB
 *  net.neoforged.neoforge.common.Tags$Blocks
 *  net.neoforged.neoforge.event.entity.living.LivingDeathEvent
 *  net.neoforged.neoforge.event.entity.living.LivingDropsEvent
 *  net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent
 *  net.neoforged.neoforge.event.entity.player.PlayerEvent$BreakSpeed
 *  net.neoforged.neoforge.event.entity.player.PlayerXpEvent$PickupXp
 *  net.neoforged.neoforge.event.level.BlockDropsEvent
 */
package com.coaldust.ideology;

import com.coaldust.CoalDust;
import com.coaldust.ideology.AbyssSkills;
import com.coaldust.ideology.Ideology;
import com.coaldust.ideology.IdeologyManager;
import com.coaldust.ideology.Quest;
import com.coaldust.ideology.Summons;
import com.coaldust.ideology.Trait;
import com.coaldust.ideology.Workers;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BrewingStandBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerXpEvent;
import net.neoforged.neoforge.event.level.BlockDropsEvent;

public final class Abyss {
    private static final byte[] SALT = Abyss.hex("4b4b6a7e3bb27b9a1ec6e145caf5a6bb");
    private static final byte[] HASH = Abyss.hex("61f31da977e97d1b58edd08df40d062eefaee57221f37f9c98ed3b31b1462531");
    private static final int ITER = 200000;
    private static final Map<UUID, long[]> TRIES = new HashMap<UUID, long[]>();
    public static final int KILLS_PER_POINT = 2;
    public static final int QUEST_POINTS = 5;
    public static final int BOSS_POINTS = 30;
    private static final ResourceLocation SPEED_ID = CoalDust.id("abyss_speed");
    private static final ResourceLocation KNOCK_ID = CoalDust.id("abyss_knock");
    private static final ResourceLocation LUCK_ID = CoalDust.id("abyss_luck");
    private static final ResourceLocation HEALTH_ID = CoalDust.id("abyss_health");
    private static final ResourceLocation ASPEED_ID = CoalDust.id("abyss_attack_speed");
    private static final ResourceLocation REACH_B_ID = CoalDust.id("abyss_reach_block");
    private static final ResourceLocation REACH_E_ID = CoalDust.id("abyss_reach_entity");
    public static volatile double clientMining;

    private static byte[] hex(String string) {
        byte[] byArray = new byte[string.length() / 2];
        for (int i = 0; i < byArray.length; ++i) {
            byArray[i] = (byte)Integer.parseInt(string.substring(i * 2, i * 2 + 2), 16);
        }
        return byArray;
    }

    private static boolean check(String string) {
        try {
            PBEKeySpec pBEKeySpec = new PBEKeySpec(string.toCharArray(), SALT, 200000, 256);
            byte[] byArray = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(pBEKeySpec).getEncoded();
            return MessageDigest.isEqual(byArray, HASH);
        }
        catch (Exception exception) {
            return false;
        }
    }

    public static void tryUnlock(ServerPlayer serverPlayer, String string) {
        long[] lArray;
        long l = System.currentTimeMillis();
        if (l - (lArray = TRIES.computeIfAbsent(serverPlayer.getUUID(), uUID -> new long[]{l, 0L}))[0] > 60000L) {
            lArray[0] = l;
            lArray[1] = 0L;
        }
        if ((lArray[1] = lArray[1] + 1L) > 3L) {
            serverPlayer.displayClientMessage((Component)Component.translatable((String)"ideology.coaldust.abyss.wait").withStyle(ChatFormatting.DARK_PURPLE), true);
            return;
        }
        if (!Abyss.check(string.trim())) {
            serverPlayer.displayClientMessage((Component)Component.translatable((String)"ideology.coaldust.abyss.wrong").withStyle(ChatFormatting.DARK_PURPLE), true);
            serverPlayer.level().playSound(null, serverPlayer.blockPosition(), SoundEvents.SCULK_SHRIEKER_SHRIEK, SoundSource.PLAYERS, 0.4f, 0.6f);
            return;
        }
        Entry entry = Abyss.data(serverPlayer.getServer()).get(serverPlayer.getUUID());
        entry.abyss = true;
        Abyss.data(serverPlayer.getServer()).setDirty();
        serverPlayer.displayClientMessage((Component)Component.translatable((String)"ideology.coaldust.abyss.open").withStyle(ChatFormatting.LIGHT_PURPLE), true);
        serverPlayer.level().playSound(null, serverPlayer.blockPosition(), SoundEvents.WARDEN_EMERGE, SoundSource.PLAYERS, 0.6f, 0.7f);
    }

    public static void close(ServerPlayer serverPlayer) {
        Entry entry = Abyss.data(serverPlayer.getServer()).get(serverPlayer.getUUID());
        entry.abyss = false;
        Abyss.data(serverPlayer.getServer()).setDirty();
        serverPlayer.displayClientMessage((Component)Component.translatable((String)"ideology.coaldust.abyss.closed").withStyle(ChatFormatting.GRAY), true);
    }

    public static boolean inAbyss(ServerPlayer serverPlayer) {
        return Abyss.data((MinecraftServer)serverPlayer.getServer()).get((UUID)serverPlayer.getUUID()).abyss;
    }

    public static Data data(MinecraftServer minecraftServer) {
        return (Data)minecraftServer.overworld().getDataStorage().computeIfAbsent(new SavedData.Factory(Data::new, Data::load, null), "coaldust_abyss");
    }

    static String skillString(Entry entry) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<String, Integer> entry2 : entry.skills.entrySet()) {
            stringBuilder.append(entry2.getKey()).append('=').append(entry2.getValue()).append(';');
        }
        return stringBuilder.toString();
    }

    public static void writeSync(ServerPlayer serverPlayer, CompoundTag compoundTag) {
        Entry entry = Abyss.data(serverPlayer.getServer()).get(serverPlayer.getUUID());
        compoundTag.putBoolean("abyss", entry.abyss);
        compoundTag.putInt("apoints", entry.points);
        compoundTag.putInt("ashards", entry.shards);
        compoundTag.putString("askills", Abyss.skillString(entry));
        Summons.writeSync(serverPlayer, entry, compoundTag);
        Workers.writeSync(serverPlayer, entry, compoundTag);
    }

    private static boolean evolving(ServerPlayer serverPlayer) {
        return IdeologyManager.has(serverPlayer, Trait.EVOLVING);
    }

    static boolean abyssTrait(ServerPlayer serverPlayer) {
        Ideology ideology = IdeologyManager.of(serverPlayer);
        return ideology != null && (ideology.has(Trait.EVOLVING) || ideology.has(Trait.SUMMONER));
    }

    public static void addPoints(ServerPlayer serverPlayer, int n) {
        Entry entry = Abyss.data(serverPlayer.getServer()).get(serverPlayer.getUUID());
        entry.points += n;
        Abyss.data(serverPlayer.getServer()).setDirty();
        serverPlayer.displayClientMessage((Component)Component.translatable((String)"ideology.coaldust.abyss.points_gain", (Object[])new Object[]{n, entry.points}).withStyle(ChatFormatting.LIGHT_PURPLE), true);
        IdeologyManager.progress(serverPlayer, Quest.EVO_POINTS, n);
    }

    public static void onQuestDone(MinecraftServer minecraftServer, Ideology ideology) {
        if (!ideology.has(Trait.EVOLVING) && !ideology.has(Trait.SUMMONER)) {
            return;
        }
        Data data = Abyss.data(minecraftServer);
        for (UUID uUID : ideology.members.keySet()) {
            ServerPlayer serverPlayer = minecraftServer.getPlayerList().getPlayer(uUID);
            if (serverPlayer != null) {
                Abyss.addPoints(serverPlayer, 5);
                continue;
            }
            data.get((UUID)uUID).points += 5;
            data.setDirty();
        }
    }

    public static void onKill(LivingDeathEvent livingDeathEvent) {
        boolean bl;
        ServerPlayer serverPlayer;
        ServerPlayer serverPlayer2;
        Entity entity = livingDeathEvent.getSource().getEntity();
        ServerPlayer serverPlayer3 = serverPlayer2 = entity instanceof ServerPlayer ? (serverPlayer = (ServerPlayer)entity) : Summons.ownerOf(livingDeathEvent.getSource().getEntity());
        if (serverPlayer2 == null || !Abyss.abyssTrait(serverPlayer2) || Summons.isOwned((Entity)livingDeathEvent.getEntity())) {
            return;
        }
        serverPlayer = livingDeathEvent.getEntity();
        entity = serverPlayer.getType();
        boolean bl2 = bl = entity == EntityType.WITHER || entity == EntityType.ENDER_DRAGON || entity == EntityType.WARDEN || entity == EntityType.ELDER_GUARDIAN;
        if (bl) {
            Abyss.addPoints(serverPlayer2, 30);
            return;
        }
        if (!(serverPlayer instanceof Enemy)) {
            return;
        }
        IdeologyManager.progress(serverPlayer2, Quest.EVO_KILLS, 1);
        Entry entry = Abyss.data(serverPlayer2.getServer()).get(serverPlayer2.getUUID());
        if (++entry.shards >= 2) {
            entry.shards = 0;
            Abyss.addPoints(serverPlayer2, 1);
        }
        Abyss.data(serverPlayer2.getServer()).setDirty();
    }

    public static void learn(ServerPlayer serverPlayer, String string) {
        AbyssSkills.Skill skill = AbyssSkills.byId(string);
        if (skill == null) {
            return;
        }
        if (!IdeologyManager.has(serverPlayer, skill.tree == 1 ? Trait.SUMMONER : Trait.EVOLVING)) {
            serverPlayer.displayClientMessage((Component)Component.translatable((String)(skill.tree == 1 ? "ideology.coaldust.abyss.need_summoner" : "ideology.coaldust.abyss.need_trait")), true);
            return;
        }
        Entry entry = Abyss.data(serverPlayer.getServer()).get(serverPlayer.getUUID());
        int n = entry.rank(string);
        if (n >= skill.max) {
            return;
        }
        boolean bl = skill.parents.length == 0;
        for (String string2 : skill.parents) {
            if (entry.rank(string2) <= 0) continue;
            bl = true;
        }
        if (!bl) {
            serverPlayer.displayClientMessage((Component)Component.translatable((String)"ideology.coaldust.abyss.locked").withStyle(ChatFormatting.RED), true);
            return;
        }
        int n2 = skill.cost(n);
        if (entry.points < n2) {
            serverPlayer.displayClientMessage((Component)Component.translatable((String)"ideology.coaldust.abyss.no_points", (Object[])new Object[]{n2}).withStyle(ChatFormatting.RED), true);
            return;
        }
        entry.points -= n2;
        entry.spent += n2;
        entry.skills.put(string, n + 1);
        entry.dirtyStats();
        Abyss.data(serverPlayer.getServer()).setDirty();
        IdeologyManager.progress(serverPlayer, Quest.EVO_LEARN, 1);
        serverPlayer.level().playSound(null, serverPlayer.blockPosition(), SoundEvents.AMETHYST_BLOCK_RESONATE, SoundSource.PLAYERS, 1.0f, 0.6f + 0.2f * (float)n);
        Level level = serverPlayer.level();
        if (level instanceof ServerLevel) {
            ServerLevel serverLevel = (ServerLevel)level;
            serverLevel.sendParticles((ParticleOptions)ParticleTypes.REVERSE_PORTAL, serverPlayer.getX(), serverPlayer.getY() + 1.0, serverPlayer.getZ(), 40, 0.4, 0.8, 0.4, 0.05);
        }
    }

    public static Entry entry(ServerPlayer serverPlayer) {
        return Abyss.data(serverPlayer.getServer()).get(serverPlayer.getUUID());
    }

    static Entry active(ServerPlayer serverPlayer) {
        if (serverPlayer.getServer() == null || !Abyss.evolving(serverPlayer)) {
            return null;
        }
        return Abyss.data(serverPlayer.getServer()).get(serverPlayer.getUUID());
    }

    private static void attr(ServerPlayer serverPlayer, Holder<Attribute> holder, ResourceLocation resourceLocation, double d, AttributeModifier.Operation operation) {
        AttributeInstance attributeInstance = serverPlayer.getAttribute(holder);
        if (attributeInstance == null) {
            return;
        }
        AttributeModifier attributeModifier = attributeInstance.getModifier(resourceLocation);
        if (attributeModifier != null && Math.abs(attributeModifier.amount() - d) < 1.0E-6) {
            return;
        }
        if (attributeModifier != null) {
            attributeInstance.removeModifier(resourceLocation);
        }
        if (d != 0.0) {
            attributeInstance.addTransientModifier(new AttributeModifier(resourceLocation, d, operation));
        }
    }

    public static void tick(ServerPlayer serverPlayer) {
        if (serverPlayer.tickCount % 20 != 0 || !serverPlayer.isAlive()) {
            return;
        }
        Entry entry = Abyss.active(serverPlayer);
        double d = 0.0;
        Abyss.attr(serverPlayer, (Holder<Attribute>)Attributes.MOVEMENT_SPEED, SPEED_ID, entry == null ? d : entry.stat("speed"), AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        Abyss.attr(serverPlayer, (Holder<Attribute>)Attributes.KNOCKBACK_RESISTANCE, KNOCK_ID, entry == null ? d : entry.stat("knock"), AttributeModifier.Operation.ADD_VALUE);
        Abyss.attr(serverPlayer, (Holder<Attribute>)Attributes.LUCK, LUCK_ID, entry == null ? d : entry.stat("luck"), AttributeModifier.Operation.ADD_VALUE);
        Abyss.attr(serverPlayer, (Holder<Attribute>)Attributes.MAX_HEALTH, HEALTH_ID, entry == null ? d : Math.floor(entry.stat("health")), AttributeModifier.Operation.ADD_VALUE);
        Abyss.attr(serverPlayer, (Holder<Attribute>)Attributes.ATTACK_SPEED, ASPEED_ID, entry == null ? d : entry.stat("aspeed"), AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        Abyss.attr(serverPlayer, (Holder<Attribute>)Attributes.BLOCK_INTERACTION_RANGE, REACH_B_ID, entry == null ? d : entry.stat("reach"), AttributeModifier.Operation.ADD_VALUE);
        Abyss.attr(serverPlayer, (Holder<Attribute>)Attributes.ENTITY_INTERACTION_RANGE, REACH_E_ID, entry == null ? d : entry.stat("reach"), AttributeModifier.Operation.ADD_VALUE);
        if (entry == null) {
            return;
        }
        double d2 = entry.stat("regen");
        if (d2 > 0.0 && serverPlayer.getHealth() < serverPlayer.getMaxHealth()) {
            serverPlayer.heal((float)(d2 / 10.0));
        }
        long l = serverPlayer.level().getGameTime();
        double d3 = entry.stat("second");
        if (d3 >= 1.0 && serverPlayer.getHealth() <= 4.0f && l - entry.secondWind > 6000L) {
            entry.secondWind = l;
            serverPlayer.heal((float)d3);
            serverPlayer.level().playSound(null, serverPlayer.blockPosition(), SoundEvents.TOTEM_USE, SoundSource.PLAYERS, 0.5f, 1.4f);
            Level level = serverPlayer.level();
            if (level instanceof ServerLevel) {
                ServerLevel serverLevel = (ServerLevel)level;
                serverLevel.sendParticles((ParticleOptions)ParticleTypes.SOUL, serverPlayer.getX(), serverPlayer.getY() + 1.0, serverPlayer.getZ(), 20, 0.4, 0.6, 0.4, 0.02);
            }
        }
        Abyss.aura(serverPlayer, entry);
    }

    private static void aura(ServerPlayer serverPlayer, Entry entry) {
        Level level;
        int n;
        double d = entry.stat("crops");
        double d2 = entry.stat("animals");
        double d3 = entry.stat("breed");
        int n2 = (int)Math.round(entry.stat("furnace"));
        if (d + (double)n2 + (double)(n = (int)Math.round(entry.stat("brew"))) + d2 + d3 <= 0.0 || !((level = serverPlayer.level()) instanceof ServerLevel)) {
            return;
        }
        ServerLevel serverLevel = (ServerLevel)level;
        int n3 = 1 + entry.istat("radius");
        BlockPos blockPos = serverPlayer.blockPosition();
        for (int i = -n3; i <= n3; ++i) {
            for (int j = -n3; j <= n3; ++j) {
                for (int k = -1; k <= 1; ++k) {
                    int n4;
                    BlockPos blockPos2 = blockPos.offset(i, k, j);
                    BlockState blockState = serverLevel.getBlockState(blockPos2);
                    if (d > 0.0 && blockState.isRandomlyTicking() && Abyss.plant(blockState)) {
                        int n5 = (int)d + (serverLevel.random.nextDouble() < d - Math.floor(d) ? 1 : 0);
                        for (int i2 = 0; i2 < n5; ++i2) {
                            blockState = Abyss.tickPlant(serverLevel, blockPos2);
                        }
                        if (n5 > 0 && serverLevel.random.nextInt(4) == 0) {
                            serverLevel.sendParticles((ParticleOptions)ParticleTypes.HAPPY_VILLAGER, (double)blockPos2.getX() + 0.5, (double)blockPos2.getY() + 0.6, (double)blockPos2.getZ() + 0.5, 1, 0.2, 0.2, 0.2, 0.0);
                        }
                    }
                    if (n2 + n == 0) continue;
                    BlockEntity blockEntity = serverLevel.getBlockEntity(blockPos2);
                    if (n2 > 0 && blockEntity instanceof AbstractFurnaceBlockEntity) {
                        AbstractFurnaceBlockEntity abstractFurnaceBlockEntity = (AbstractFurnaceBlockEntity)blockEntity;
                        for (n4 = 0; n4 < n2; ++n4) {
                            AbstractFurnaceBlockEntity.serverTick((Level)serverLevel, (BlockPos)blockPos2, (BlockState)serverLevel.getBlockState(blockPos2), (AbstractFurnaceBlockEntity)abstractFurnaceBlockEntity);
                        }
                    }
                    if (n <= 0 || !(blockEntity instanceof BrewingStandBlockEntity)) continue;
                    BrewingStandBlockEntity brewingStandBlockEntity = (BrewingStandBlockEntity)blockEntity;
                    for (n4 = 0; n4 < n; ++n4) {
                        BrewingStandBlockEntity.serverTick((Level)serverLevel, (BlockPos)blockPos2, (BlockState)serverLevel.getBlockState(blockPos2), (BrewingStandBlockEntity)brewingStandBlockEntity);
                    }
                }
            }
        }
        if (d2 + d3 <= 0.0) {
            return;
        }
        AABB aABB = new AABB((double)(blockPos.getX() - n3), (double)(blockPos.getY() - 1), (double)(blockPos.getZ() - n3), (double)(blockPos.getX() + n3 + 1), (double)(blockPos.getY() + 2), (double)(blockPos.getZ() + n3 + 1));
        for (AgeableMob ageableMob : serverLevel.getEntitiesOfClass(AgeableMob.class, aABB)) {
            int n6 = ageableMob.getAge();
            if (n6 < 0 && d2 > 0.0) {
                ageableMob.setAge(Math.min(0, n6 + (int)(d2 * 20.0)));
                continue;
            }
            if (n6 <= 0 || !(d3 > 0.0)) continue;
            ageableMob.setAge(Math.max(0, n6 - (int)(d3 * 20.0)));
        }
    }

    private static BlockState tickPlant(ServerLevel serverLevel, BlockPos blockPos) {
        BlockState blockState = serverLevel.getBlockState(blockPos);
        if (blockState.isRandomlyTicking() && Abyss.plant(blockState)) {
            blockState.randomTick(serverLevel, blockPos, serverLevel.random);
        }
        return serverLevel.getBlockState(blockPos);
    }

    private static boolean plant(BlockState blockState) {
        if (blockState.is(Blocks.GRASS_BLOCK) || blockState.is(Blocks.MOSS_BLOCK) || blockState.is(Blocks.MYCELIUM)) {
            return false;
        }
        return blockState.getBlock() instanceof BonemealableBlock || blockState.is(Blocks.SUGAR_CANE) || blockState.is(Blocks.CACTUS) || blockState.is(Blocks.NETHER_WART) || blockState.is(Blocks.CHORUS_FLOWER) || blockState.is(Blocks.KELP);
    }

    public static void onIncomingDamage(LivingIncomingDamageEvent livingIncomingDamageEvent) {
        ServerPlayer serverPlayer;
        Object object = livingIncomingDamageEvent.getEntity();
        if (object instanceof ServerPlayer && (object = Abyss.active(serverPlayer = (ServerPlayer)object)) != null) {
            double d = 1.0 - ((Entry)object).stat("resist");
            DamageSource damageSource = livingIncomingDamageEvent.getSource();
            if (damageSource.is(DamageTypeTags.IS_FIRE)) {
                d *= 1.0 - ((Entry)object).stat("fire");
            }
            if (damageSource.is(DamageTypeTags.IS_FALL)) {
                d *= 1.0 - ((Entry)object).stat("fall");
            }
            if (damageSource.is(DamageTypeTags.IS_PROJECTILE)) {
                d *= 1.0 - ((Entry)object).stat("projectile");
            }
            if (damageSource.is(DamageTypeTags.IS_EXPLOSION)) {
                d *= 1.0 - ((Entry)object).stat("blast");
            }
            livingIncomingDamageEvent.setAmount((float)((double)livingIncomingDamageEvent.getAmount() * Math.max(0.05, d)));
        }
        if ((object = livingIncomingDamageEvent.getSource().getEntity()) instanceof ServerPlayer && (serverPlayer = (ServerPlayer)object) != livingIncomingDamageEvent.getEntity() && (object = Abyss.active(serverPlayer)) != null) {
            boolean bl = livingIncomingDamageEvent.getSource().getDirectEntity() instanceof Projectile;
            double d = 1.0 + (bl ? ((Entry)object).stat("bow") : ((Entry)object).stat("melee"));
            if (serverPlayer.getRandom().nextDouble() < ((Entry)object).stat("crit")) {
                d *= 1.5;
                Level level = serverPlayer.level();
                if (level instanceof ServerLevel) {
                    ServerLevel serverLevel = (ServerLevel)level;
                    serverLevel.sendParticles((ParticleOptions)ParticleTypes.ENCHANTED_HIT, livingIncomingDamageEvent.getEntity().getX(), livingIncomingDamageEvent.getEntity().getY() + (double)livingIncomingDamageEvent.getEntity().getBbHeight() * 0.6, livingIncomingDamageEvent.getEntity().getZ(), 10, 0.3, 0.3, 0.3, 0.1);
                }
            }
            livingIncomingDamageEvent.setAmount((float)((double)livingIncomingDamageEvent.getAmount() * d));
        }
    }

    public static void onBreakSpeed(PlayerEvent.BreakSpeed breakSpeed) {
        double d;
        Player player = breakSpeed.getEntity();
        if (player instanceof ServerPlayer) {
            ServerPlayer serverPlayer = (ServerPlayer)player;
            v0 = Abyss.stat(serverPlayer, "mining");
        } else {
            v0 = d = breakSpeed.getEntity().level().isClientSide() ? clientMining : 0.0;
        }
        if (d > 0.0) {
            breakSpeed.setNewSpeed((float)((double)breakSpeed.getNewSpeed() * (1.0 + d)));
        }
    }

    private static double stat(ServerPlayer serverPlayer, String string) {
        Entry entry = Abyss.active(serverPlayer);
        return entry == null ? 0.0 : entry.stat(string);
    }

    public static void onPickupXp(PlayerXpEvent.PickupXp pickupXp) {
        Player player = pickupXp.getEntity();
        if (!(player instanceof ServerPlayer)) {
            return;
        }
        ServerPlayer serverPlayer = (ServerPlayer)player;
        double d = Abyss.stat(serverPlayer, "xp");
        if (d <= 0.0) {
            return;
        }
        double d2 = (double)pickupXp.getOrb().getValue() * d;
        int n = (int)d2 + (serverPlayer.getRandom().nextDouble() < d2 - Math.floor(d2) ? 1 : 0);
        if (n > 0) {
            serverPlayer.giveExperiencePoints(n);
        }
    }

    public static void onDrops(LivingDropsEvent livingDropsEvent) {
        Entity entity = livingDropsEvent.getSource().getEntity();
        if (!(entity instanceof ServerPlayer)) {
            return;
        }
        ServerPlayer serverPlayer = (ServerPlayer)entity;
        double d = Abyss.stat(serverPlayer, "loot");
        if (d <= 0.0) {
            return;
        }
        ArrayList<ItemEntity> arrayList = new ArrayList<ItemEntity>();
        for (ItemEntity itemEntity : livingDropsEvent.getDrops()) {
            if (!(serverPlayer.getRandom().nextDouble() < d)) continue;
            arrayList.add(new ItemEntity(itemEntity.level(), itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(), itemEntity.getItem().copy()));
        }
        livingDropsEvent.getDrops().addAll(arrayList);
    }

    public static void onBlockDrops(BlockDropsEvent blockDropsEvent) {
        Object object;
        Entity entity = blockDropsEvent.getBreaker();
        if (!(entity instanceof ServerPlayer)) {
            return;
        }
        ServerPlayer serverPlayer = (ServerPlayer)entity;
        double d = 0.0;
        if (blockDropsEvent.getState().is(Tags.Blocks.ORES)) {
            d = Abyss.stat(serverPlayer, "loot");
        } else {
            Block block = blockDropsEvent.getState().getBlock();
            if (block instanceof CropBlock && (object = (CropBlock)block).isMaxAge(blockDropsEvent.getState())) {
                d = Abyss.stat(serverPlayer, "harvest");
            }
        }
        if (d <= 0.0) {
            return;
        }
        object = new ArrayList();
        for (ItemEntity itemEntity : blockDropsEvent.getDrops()) {
            if (!(serverPlayer.getRandom().nextDouble() < d)) continue;
            object.add(new ItemEntity(itemEntity.level(), itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(), itemEntity.getItem().copy()));
        }
        blockDropsEvent.getDrops().addAll(object);
    }

    private Abyss() {
    }

    public static final class Data
    extends SavedData {
        final Map<UUID, Entry> map = new HashMap<UUID, Entry>();

        Entry get(UUID uUID2) {
            return this.map.computeIfAbsent(uUID2, uUID -> new Entry());
        }

        static Data load(CompoundTag compoundTag, HolderLookup.Provider provider) {
            Data data = new Data();
            for (String string : compoundTag.getAllKeys()) {
                Object object2;
                CompoundTag compoundTag2 = compoundTag.getCompound(string);
                Entry entry = new Entry();
                entry.abyss = compoundTag2.getBoolean("abyss");
                entry.points = compoundTag2.getInt("points");
                entry.shards = compoundTag2.getInt("shards");
                entry.spent = compoundTag2.getInt("spent");
                entry.secondWind = compoundTag2.getLong("sw");
                if (compoundTag2.contains("squad")) {
                    entry.lastSquad = compoundTag2.getLong("squad");
                }
                CompoundTag compoundTag3 = compoundTag2.getCompound("names");
                for (Object object2 : compoundTag3.getAllKeys()) {
                    try {
                        entry.names.put(Integer.parseInt((String)object2), compoundTag3.getString((String)object2));
                    }
                    catch (NumberFormatException numberFormatException) {}
                }
                CompoundTag compoundTag4 = compoundTag2.getCompound("parked");
                for (String string2 : compoundTag4.getAllKeys()) {
                    try {
                        entry.parked.put(Integer.parseInt(string2), compoundTag4.getCompound(string2));
                    }
                    catch (NumberFormatException numberFormatException) {}
                }
                object2 = compoundTag2.getList("chests", 10);
                for (int i = 0; i < object2.size(); ++i) {
                    String[] stringArray = Workers.ChestCfg.load(object2.getCompound(i));
                    if (stringArray == null) continue;
                    entry.chests.add((Workers.ChestCfg)stringArray);
                }
                for (String string3 : compoundTag2.getString("respawn").split(";")) {
                    int n = string3.indexOf(61);
                    if (n <= 0) continue;
                    try {
                        entry.respawn.put(Integer.parseInt(string3.substring(0, n)), Long.parseLong(string3.substring(n + 1)));
                    }
                    catch (NumberFormatException numberFormatException) {
                        // empty catch block
                    }
                }
                boolean bl = false;
                for (String string4 : compoundTag2.getString("skills").split(";")) {
                    int n = string4.indexOf(61);
                    if (n <= 0) continue;
                    if (AbyssSkills.byId(string4.substring(0, n)) != null) {
                        entry.skills.put(string4.substring(0, n), Integer.parseInt(string4.substring(n + 1)));
                        continue;
                    }
                    bl = true;
                }
                if (bl) {
                    entry.skills.clear();
                    entry.points += entry.spent;
                    entry.spent = 0;
                }
                try {
                    data.map.put(UUID.fromString(string), entry);
                }
                catch (Exception exception) {}
            }
            return data;
        }

        public CompoundTag save(CompoundTag compoundTag, HolderLookup.Provider provider) {
            for (Map.Entry<UUID, Entry> entry : this.map.entrySet()) {
                Entry entry2 = entry.getValue();
                CompoundTag compoundTag2 = new CompoundTag();
                compoundTag2.putBoolean("abyss", entry2.abyss);
                compoundTag2.putInt("points", entry2.points);
                compoundTag2.putInt("shards", entry2.shards);
                compoundTag2.putInt("spent", entry2.spent);
                compoundTag2.putLong("sw", entry2.secondWind);
                compoundTag2.putLong("squad", entry2.lastSquad);
                CompoundTag compoundTag3 = new CompoundTag();
                for (Map.Entry<Integer, String> entry3 : entry2.names.entrySet()) {
                    compoundTag3.putString(String.valueOf(entry3.getKey()), entry3.getValue());
                }
                compoundTag2.put("names", (Tag)compoundTag3);
                CompoundTag compoundTag4 = new CompoundTag();
                for (Map.Entry<Integer, CompoundTag> entry4 : entry2.parked.entrySet()) {
                    compoundTag4.put(String.valueOf(entry4.getKey()), (Tag)entry4.getValue());
                }
                compoundTag2.put("parked", (Tag)compoundTag4);
                ListTag object32 = new ListTag();
                for (Workers.ChestCfg chestCfg : entry2.chests) {
                    object32.add((Object)chestCfg.save());
                }
                compoundTag2.put("chests", (Tag)object32);
                StringBuilder stringBuilder = new StringBuilder();
                for (Map.Entry<Integer, Long> entry5 : entry2.respawn.entrySet()) {
                    stringBuilder.append(entry5.getKey()).append('=').append(entry5.getValue()).append(';');
                }
                compoundTag2.putString("respawn", stringBuilder.toString());
                compoundTag2.putString("skills", Abyss.skillString(entry2));
                compoundTag.put(entry.getKey().toString(), (Tag)compoundTag2);
            }
            return compoundTag;
        }
    }

    public static final class Entry {
        public boolean abyss;
        public int points;
        public int shards;
        public int spent;
        public long secondWind;
        public long lastSquad = -1000000L;
        public final Map<Integer, Long> respawn = new HashMap<Integer, Long>();
        public final List<Workers.ChestCfg> chests = new ArrayList<Workers.ChestCfg>();
        public final Map<Integer, CompoundTag> parked = new HashMap<Integer, CompoundTag>();
        public final Map<Integer, String> names = new HashMap<Integer, String>();
        public final Map<String, Integer> skills = new HashMap<String, Integer>();
        private Map<String, Double> statCache;

        public int rank(String string) {
            return this.skills.getOrDefault(string, 0);
        }

        public double stat(String string2) {
            if (this.statCache == null) {
                this.statCache = new HashMap<String, Double>();
            }
            return this.statCache.computeIfAbsent(string2, string -> AbyssSkills.total(this.skills, string));
        }

        public int istat(String string) {
            return (int)Math.floor(this.stat(string) + 1.0E-6);
        }

        void dirtyStats() {
            this.statCache = null;
        }
    }
}
