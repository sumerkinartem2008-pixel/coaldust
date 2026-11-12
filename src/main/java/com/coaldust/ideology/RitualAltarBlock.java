/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.ChatFormatting
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Holder
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.network.chat.Component
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.ItemInteractionResult
 *  net.minecraft.world.effect.MobEffect
 *  net.minecraft.world.effect.MobEffects
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.animal.IronGolem
 *  net.minecraft.world.entity.animal.Wolf
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.BoneMealItem
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.CropBlock
 *  net.minecraft.world.level.block.RenderShape
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.IntegerProperty
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.Shapes
 *  net.minecraft.world.phys.shapes.VoxelShape
 *  net.neoforged.neoforge.event.tick.EntityTickEvent$Post
 */
package com.coaldust.ideology;

import com.coaldust.disease.Disease;
import com.coaldust.disease.Diseases;
import com.coaldust.ideology.Ideology;
import com.coaldust.ideology.IdeologyManager;
import com.coaldust.ideology.Quest;
import com.coaldust.ideology.RitualFx;
import com.coaldust.ideology.Trait;
import com.coaldust.ideology.TraitEffects;
import com.coaldust.meteor.MeteorManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

public class RitualAltarBlock
extends Block {
    private static final Map<BlockPos, Long> COOLDOWN = new HashMap<BlockPos, Long>();
    public static final int COOLDOWN_TICKS = 2400;
    private static final Map<BlockPos, Long> METEOR_COOLDOWN = new HashMap<BlockPos, Long>();
    public static final int METEOR_COOLDOWN_TICKS = 36000;
    public static final int SUMMON_TICKS = 6000;
    public static final String SUMMON_TAG = "coaldust_summon";
    public static final IntegerProperty PX = IntegerProperty.create((String)"px", (int)0, (int)2);
    public static final IntegerProperty PY = IntegerProperty.create((String)"py", (int)0, (int)1);
    public static final IntegerProperty PZ = IntegerProperty.create((String)"pz", (int)0, (int)2);
    private static final VoxelShape STEP = Block.box((double)0.0, (double)0.0, (double)0.0, (double)16.0, (double)7.0, (double)16.0);
    private static final VoxelShape CENTER = Block.box((double)0.0, (double)0.0, (double)0.0, (double)16.0, (double)16.0, (double)16.0);
    private static final VoxelShape OBELISK = Block.box((double)2.0, (double)0.0, (double)2.0, (double)14.0, (double)16.0, (double)14.0);
    private static final VoxelShape SPIKE = Block.box((double)3.0, (double)0.0, (double)3.0, (double)13.0, (double)9.0, (double)13.0);

    public RitualAltarBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue((Property)PX, (Comparable)Integer.valueOf(1))).setValue((Property)PY, (Comparable)Integer.valueOf(0))).setValue((Property)PZ, (Comparable)Integer.valueOf(1)));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{PX, PY, PZ});
    }

    public static boolean isMaster(BlockState blockState) {
        return (Integer)blockState.getValue((Property)PX) == 1 && (Integer)blockState.getValue((Property)PY) == 0 && (Integer)blockState.getValue((Property)PZ) == 1;
    }

    public static BlockPos master(BlockState blockState, BlockPos blockPos) {
        return blockPos.offset(1 - (Integer)blockState.getValue((Property)PX), -((Integer)blockState.getValue((Property)PY)).intValue(), 1 - (Integer)blockState.getValue((Property)PZ));
    }

    public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
        BlockPos blockPos = blockPlaceContext.getClickedPos();
        Level level = blockPlaceContext.getLevel();
        for (int i = -1; i <= 1; ++i) {
            for (int j = 0; j <= 1; ++j) {
                for (int k = -1; k <= 1; ++k) {
                    BlockPos blockPos2 = blockPos.offset(i, j, k);
                    if (blockPos2.equals(blockPos) || level.getBlockState(blockPos2).canBeReplaced() && blockPos2.getY() < level.getMaxBuildHeight()) continue;
                    return null;
                }
            }
        }
        return this.defaultBlockState();
    }

    public void setPlacedBy(Level level, BlockPos blockPos, BlockState blockState, LivingEntity livingEntity, ItemStack itemStack) {
        if (level.isClientSide()) {
            return;
        }
        for (int i = 0; i <= 2; ++i) {
            for (int j = 0; j <= 1; ++j) {
                for (int k = 0; k <= 2; ++k) {
                    if (i == 1 && j == 0 && k == 1) continue;
                    level.setBlock(blockPos.offset(i - 1, j, k - 1), (BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue((Property)PX, (Comparable)Integer.valueOf(i))).setValue((Property)PY, (Comparable)Integer.valueOf(j))).setValue((Property)PZ, (Comparable)Integer.valueOf(k)), 3);
                }
            }
        }
    }

    protected void onRemove(BlockState blockState, Level level, BlockPos blockPos, BlockState blockState2, boolean bl) {
        if (!blockState2.is((Block)this)) {
            BlockPos blockPos2 = RitualAltarBlock.master(blockState, blockPos);
            for (int i = -1; i <= 1; ++i) {
                for (int j = 0; j <= 1; ++j) {
                    for (int k = -1; k <= 1; ++k) {
                        BlockPos blockPos3 = blockPos2.offset(i, j, k);
                        if (blockPos3.equals(blockPos) || !level.getBlockState(blockPos3).is((Block)this)) continue;
                        level.setBlock(blockPos3, Blocks.AIR.defaultBlockState(), 35);
                    }
                }
            }
        }
        super.onRemove(blockState, level, blockPos, blockState2, bl);
    }

    public BlockState playerWillDestroy(Level level, BlockPos blockPos, BlockState blockState, Player player) {
        BlockPos blockPos2;
        if (!level.isClientSide() && !RitualAltarBlock.isMaster(blockState) && !player.getAbilities().instabuild && level.getBlockState(blockPos2 = RitualAltarBlock.master(blockState, blockPos)).is((Block)this)) {
            level.destroyBlock(blockPos2, true, (Entity)player);
        }
        return super.playerWillDestroy(level, blockPos, blockState, player);
    }

    protected RenderShape getRenderShape(BlockState blockState) {
        return RitualAltarBlock.isMaster(blockState) ? RenderShape.MODEL : RenderShape.INVISIBLE;
    }

    protected VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        boolean bl;
        int n = (Integer)blockState.getValue((Property)PX);
        int n2 = (Integer)blockState.getValue((Property)PY);
        int n3 = (Integer)blockState.getValue((Property)PZ);
        boolean bl2 = n == 1 && n3 == 1;
        boolean bl3 = bl = n != 1 && n3 != 1;
        if (n2 == 0) {
            return bl2 ? CENTER : STEP;
        }
        if (bl2) {
            return OBELISK;
        }
        return bl ? SPIKE : Shapes.empty();
    }

    protected ItemInteractionResult useItemOn(ItemStack itemStack, BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        Object object;
        ServerLevel serverLevel;
        String string;
        block16: {
            block15: {
                string = RitualAltarBlock.ritualFor(itemStack);
                if (string == null) {
                    return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
                }
                blockPos = RitualAltarBlock.master(blockState, blockPos);
                if (!(level instanceof ServerLevel)) break block15;
                serverLevel = (ServerLevel)level;
                if (player instanceof ServerPlayer) break block16;
            }
            return ItemInteractionResult.SUCCESS;
        }
        ServerPlayer serverPlayer = (ServerPlayer)player;
        if (!IdeologyManager.has(serverPlayer, Trait.RITUALISM)) {
            serverPlayer.displayClientMessage((Component)Component.translatable((String)"ideology.coaldust.msg.need_trait", (Object[])new Object[]{Component.translatable((String)"ideology.coaldust.trait.ritualism")}), true);
            return ItemInteractionResult.FAIL;
        }
        Ideology ideology = IdeologyManager.of(serverPlayer);
        boolean bl = false;
        if (bl && (string.equals("guardians") || string.equals("meteor"))) {
            serverPlayer.displayClientMessage((Component)Component.translatable((String)"ideology.coaldust.msg.buffs_locked", (Object[])new Object[]{5}), true);
            return ItemInteractionResult.FAIL;
        }
        int n = RitualAltarBlock.cost(string);
        if (!serverPlayer.getAbilities().instabuild && itemStack.getCount() < n) {
            serverPlayer.displayClientMessage((Component)Component.translatable((String)"ideology.coaldust.msg.altar_need", (Object[])new Object[]{n, itemStack.getHoverName()}), true);
            return ItemInteractionResult.FAIL;
        }
        long l = serverLevel.getGameTime();
        Long l2 = COOLDOWN.get(blockPos.immutable());
        if (l2 != null && l - l2 < 2400L) {
            serverPlayer.displayClientMessage((Component)Component.translatable((String)"ideology.coaldust.msg.altar_cooldown", (Object[])new Object[]{(2400L - (l - l2)) / 20L}), true);
            return ItemInteractionResult.FAIL;
        }
        if (string.equals("meteor")) {
            object = METEOR_COOLDOWN.get(blockPos.immutable());
            if (object != null && l - (Long)object < 36000L) {
                serverPlayer.displayClientMessage((Component)Component.translatable((String)"ideology.coaldust.msg.meteor_cooldown", (Object[])new Object[]{(36000L - (l - (Long)object) + 1199L) / 1200L}), true);
                return ItemInteractionResult.FAIL;
            }
            if (!MeteorManager.launchRitual(serverLevel, blockPos)) {
                serverPlayer.displayClientMessage((Component)Component.translatable((String)"ideology.coaldust.msg.meteor_busy"), true);
                return ItemInteractionResult.FAIL;
            }
            METEOR_COOLDOWN.put(blockPos.immutable(), l);
        }
        COOLDOWN.put(blockPos.immutable(), l);
        RitualAltarBlock.perform(string, serverLevel, blockPos, serverPlayer);
        if (!serverPlayer.getAbilities().instabuild) {
            itemStack.shrink(n);
        }
        object = IdeologyManager.of(serverPlayer);
        TraitEffects.markRitual(serverPlayer);
        IdeologyManager.progress(serverPlayer, Quest.RITUALS, 1);
        if (!serverLevel.isDay()) {
            IdeologyManager.progress(serverPlayer, Quest.NIGHT_RITUALS, 1);
        }
        if (object != null && ((Ideology)object).ritualKinds.add(string)) {
            IdeologyManager.setProgress(serverLevel.getServer(), (Ideology)object, Quest.RITUAL_KINDS, ((Ideology)object).ritualKinds.size(), serverPlayer.getRandom());
            IdeologyManager.data(serverLevel.getServer()).setDirty();
        }
        serverLevel.playSound(null, (double)blockPos.getX() + 0.5, (double)(blockPos.getY() + 2), (double)blockPos.getZ() + 0.5, SoundEvents.EVOKER_CAST_SPELL, SoundSource.BLOCKS, 1.2f, 0.8f);
        serverPlayer.sendSystemMessage((Component)Component.translatable((String)("ideology.coaldust.ritual." + string + ".done")).withStyle(ChatFormatting.LIGHT_PURPLE));
        if (bl) {
            serverPlayer.sendSystemMessage((Component)Component.translatable((String)"ideology.coaldust.msg.ritual_weak", (Object[])new Object[]{5}).withStyle(ChatFormatting.GRAY));
        }
        return ItemInteractionResult.SUCCESS;
    }

    protected InteractionResult useWithoutItem(BlockState blockState, Level level, BlockPos blockPos, Player player, BlockHitResult blockHitResult) {
        if (!level.isClientSide()) {
            player.sendSystemMessage((Component)Component.translatable((String)"ideology.coaldust.ritual.help").withStyle(ChatFormatting.LIGHT_PURPLE));
            for (String string : new String[]{"fire", "night", "strength", "harvest", "wind", "shadow", "life", "depths", "crystal", "guardians", "meteor", "prosperity", "cleansing"}) {
                player.sendSystemMessage((Component)Component.translatable((String)("ideology.coaldust.ritual." + string)).withStyle(ChatFormatting.GRAY));
            }
        }
        return InteractionResult.SUCCESS;
    }

    private static String ritualFor(ItemStack itemStack) {
        if (itemStack.is(Items.BLAZE_POWDER)) {
            return "fire";
        }
        if (itemStack.is(Items.ENDER_PEARL)) {
            return "night";
        }
        if (itemStack.is(Items.GOLD_BLOCK)) {
            return "strength";
        }
        if (itemStack.is(Items.HAY_BLOCK)) {
            return "harvest";
        }
        if (itemStack.is(Items.RABBIT_FOOT)) {
            return "wind";
        }
        if (itemStack.is(Items.PHANTOM_MEMBRANE)) {
            return "shadow";
        }
        if (itemStack.is(Items.GLISTERING_MELON_SLICE)) {
            return "life";
        }
        if (itemStack.is(Items.NAUTILUS_SHELL)) {
            return "depths";
        }
        if (itemStack.is(Items.AMETHYST_SHARD)) {
            return "crystal";
        }
        if (itemStack.is(Items.IRON_BLOCK)) {
            return "guardians";
        }
        if (itemStack.is(Items.OBSIDIAN)) {
            return "meteor";
        }
        if (itemStack.is(Items.EMERALD_BLOCK)) {
            return "prosperity";
        }
        if (itemStack.is(Items.GOLDEN_APPLE)) {
            return "cleansing";
        }
        return null;
    }

    public static int cost(String string) {
        return switch (string) {
            case "fire", "crystal", "meteor" -> 8;
            case "guardians" -> 3;
            case "night", "harvest", "shadow", "life" -> 4;
            case "wind", "depths" -> 2;
            case "cleansing" -> 3;
            default -> 1;
        };
    }

    private static void perform(String string, ServerLevel serverLevel, BlockPos blockPos, ServerPlayer serverPlayer2) {
        Ideology ideology = IdeologyManager.of(serverPlayer2);
        ArrayList<ServerPlayer> arrayList = new ArrayList<ServerPlayer>();
        if (ideology != null) {
            for (ServerPlayer serverPlayer3 : IdeologyManager.online(serverLevel.getServer(), ideology)) {
                if (serverPlayer3.level() != serverLevel || !(serverPlayer3.distanceToSqr((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ()) <= 256.0)) continue;
                arrayList.add(serverPlayer3);
            }
        }
        RitualFx.start(serverLevel, blockPos.above(), string, serverPlayer2, arrayList);
        switch (string) {
            case "fire": {
                arrayList.forEach(serverPlayer -> TraitEffects.giveFree((LivingEntity)serverPlayer, (Holder<MobEffect>)MobEffects.FIRE_RESISTANCE, 0, 12000));
                break;
            }
            case "night": {
                arrayList.forEach(serverPlayer -> TraitEffects.giveFree((LivingEntity)serverPlayer, (Holder<MobEffect>)MobEffects.NIGHT_VISION, 0, 12000));
                break;
            }
            case "strength": {
                arrayList.forEach(serverPlayer -> {
                    TraitEffects.giveFree((LivingEntity)serverPlayer, (Holder<MobEffect>)MobEffects.DAMAGE_RESISTANCE, 0, 6000);
                    TraitEffects.giveFree((LivingEntity)serverPlayer, (Holder<MobEffect>)MobEffects.ABSORPTION, 1, 6000);
                });
                break;
            }
            case "harvest": {
                for (int i = -8; i <= 8; ++i) {
                    for (int j = -2; j <= 2; ++j) {
                        for (int k = -8; k <= 8; ++k) {
                            BlockPos blockPos2 = blockPos.offset(i, j, k);
                            if (!(serverLevel.getBlockState(blockPos2).getBlock() instanceof CropBlock)) continue;
                            for (int i2 = 0; i2 < 3; ++i2) {
                                BoneMealItem.growCrop((ItemStack)new ItemStack((ItemLike)Items.BONE_MEAL), (Level)serverLevel, (BlockPos)blockPos2);
                            }
                        }
                    }
                }
                arrayList.forEach(serverPlayer -> TraitEffects.giveFree((LivingEntity)serverPlayer, (Holder<MobEffect>)MobEffects.SATURATION, 0, 10));
                break;
            }
            case "wind": {
                arrayList.forEach(serverPlayer -> {
                    TraitEffects.giveFree((LivingEntity)serverPlayer, (Holder<MobEffect>)MobEffects.MOVEMENT_SPEED, 1, 6000);
                    TraitEffects.giveFree((LivingEntity)serverPlayer, (Holder<MobEffect>)MobEffects.JUMP, 1, 6000);
                });
                break;
            }
            case "shadow": {
                arrayList.forEach(serverPlayer -> {
                    TraitEffects.giveFree((LivingEntity)serverPlayer, (Holder<MobEffect>)MobEffects.INVISIBILITY, 0, 3600);
                    TraitEffects.giveFree((LivingEntity)serverPlayer, (Holder<MobEffect>)MobEffects.SLOW_FALLING, 0, 6000);
                });
                break;
            }
            case "life": {
                arrayList.forEach(serverPlayer -> {
                    TraitEffects.giveFree((LivingEntity)serverPlayer, (Holder<MobEffect>)MobEffects.REGENERATION, 1, 600);
                    TraitEffects.giveFree((LivingEntity)serverPlayer, (Holder<MobEffect>)MobEffects.HEALTH_BOOST, 1, 6000);
                    serverPlayer.heal(6.0f);
                });
                break;
            }
            case "depths": {
                arrayList.forEach(serverPlayer -> {
                    TraitEffects.giveFree((LivingEntity)serverPlayer, (Holder<MobEffect>)MobEffects.WATER_BREATHING, 0, 12000);
                    TraitEffects.giveFree((LivingEntity)serverPlayer, (Holder<MobEffect>)MobEffects.CONDUIT_POWER, 0, 12000);
                    TraitEffects.giveFree((LivingEntity)serverPlayer, (Holder<MobEffect>)MobEffects.DOLPHINS_GRACE, 0, 12000);
                });
                break;
            }
            case "guardians": {
                RitualAltarBlock.summonGuardians(serverLevel, blockPos, serverPlayer2);
                break;
            }
            case "meteor": {
                break;
            }
            case "crystal": {
                arrayList.forEach(serverPlayer -> {
                    TraitEffects.giveFree((LivingEntity)serverPlayer, (Holder<MobEffect>)MobEffects.DIG_SPEED, 1, 6000);
                    TraitEffects.giveFree((LivingEntity)serverPlayer, (Holder<MobEffect>)MobEffects.LUCK, 0, 6000);
                });
                break;
            }
            case "prosperity": {
                arrayList.forEach(serverPlayer -> {
                    TraitEffects.giveFree((LivingEntity)serverPlayer, (Holder<MobEffect>)MobEffects.HERO_OF_THE_VILLAGE, 1, 12000);
                    TraitEffects.giveFree((LivingEntity)serverPlayer, (Holder<MobEffect>)MobEffects.LUCK, 1, 12000);
                });
                break;
            }
            case "cleansing": {
                for (Disease disease : Diseases.ALL) {
                    boolean bl;
                    int n = disease.getStage(serverPlayer2);
                    boolean bl2 = bl = RitualAltarBlock.isTransform(disease) && n >= disease.getMaxStage();
                    if (n <= 0 || bl) continue;
                    disease.setStage(serverPlayer2, n - 1);
                }
                TraitEffects.giveFree((LivingEntity)serverPlayer2, (Holder<MobEffect>)MobEffects.REGENERATION, 1, 200);
            }
        }
    }

    private static void summonGuardians(ServerLevel serverLevel, BlockPos blockPos, ServerPlayer serverPlayer) {
        long l = serverLevel.getGameTime() + 6000L;
        for (int i = 0; i < 5; ++i) {
            IronGolem ironGolem;
            double d = (double)i * Math.PI * 2.0 / 5.0;
            double d2 = i < 2 ? 2.6 : 2.2;
            double d3 = (double)blockPos.getX() + 0.5 + Math.cos(d) * d2;
            double d4 = (double)blockPos.getZ() + 0.5 + Math.sin(d) * d2;
            if (i < 2) {
                var15_10 = (IronGolem)EntityType.IRON_GOLEM.create((Level)serverLevel);
                if (var15_10 == null) continue;
                var15_10.setPlayerCreated(true);
                ironGolem = var15_10;
            } else {
                var15_10 = (Wolf)EntityType.WOLF.create((Level)serverLevel);
                if (var15_10 == null) continue;
                var15_10.tame((Player)serverPlayer);
                ironGolem = var15_10;
            }
            ironGolem.moveTo(d3, (double)blockPos.getY() + 0.6, d4, (float)(d * 180.0 / Math.PI), 0.0f);
            ironGolem.addTag(SUMMON_TAG);
            ironGolem.getPersistentData().putLong("coaldust_expire", l);
            serverLevel.addFreshEntity((Entity)ironGolem);
            serverLevel.sendParticles((ParticleOptions)ParticleTypes.POOF, d3, (double)(blockPos.getY() + 1), d4, 12, 0.3, 0.5, 0.3, 0.02);
        }
    }

    public static void onEntityTick(EntityTickEvent.Post post) {
        Level level;
        Entity entity = post.getEntity();
        if (entity.tickCount % 20 != 0 || !entity.getTags().contains(SUMMON_TAG) || !((level = entity.level()) instanceof ServerLevel)) {
            return;
        }
        ServerLevel serverLevel = (ServerLevel)level;
        if (serverLevel.getGameTime() >= entity.getPersistentData().getLong("coaldust_expire")) {
            serverLevel.sendParticles((ParticleOptions)ParticleTypes.POOF, entity.getX(), entity.getY() + 1.0, entity.getZ(), 20, 0.4, 0.6, 0.4, 0.02);
            serverLevel.sendParticles((ParticleOptions)ParticleTypes.ENCHANT, entity.getX(), entity.getY() + 1.0, entity.getZ(), 30, 0.4, 0.8, 0.4, 0.5);
            entity.discard();
        }
    }

    private static boolean isTransform(Disease disease) {
        return disease.getName().equals("dotafication");
    }

    public void animateTick(BlockState blockState, Level level, BlockPos blockPos, RandomSource randomSource) {
        if (!RitualAltarBlock.isMaster(blockState)) {
            return;
        }
        double d = (double)blockPos.getX() + 0.5;
        double d2 = (double)blockPos.getZ() + 0.5;
        if (randomSource.nextInt(2) == 0) {
            level.addParticle((ParticleOptions)ParticleTypes.SMALL_FLAME, d + (randomSource.nextDouble() - 0.5) * 0.4, (double)blockPos.getY() + 2.05, d2 + (randomSource.nextDouble() - 0.5) * 0.4, 0.0, 0.02, 0.0);
        }
        if (randomSource.nextInt(4) == 0) {
            double d3 = randomSource.nextDouble() * Math.PI * 2.0;
            level.addParticle((ParticleOptions)ParticleTypes.LAVA, d + Math.cos(d3) * 0.9, (double)blockPos.getY() + 0.8, d2 + Math.sin(d3) * 0.9, 0.0, 0.0, 0.0);
        }
        if (randomSource.nextInt(3) == 0) {
            level.addParticle((ParticleOptions)ParticleTypes.ENCHANT, d, (double)blockPos.getY() + 2.4, d2, (randomSource.nextDouble() - 0.5) * 2.0, -0.8, (randomSource.nextDouble() - 0.5) * 2.0);
        }
    }
}
