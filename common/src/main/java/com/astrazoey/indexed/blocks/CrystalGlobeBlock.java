package com.astrazoey.indexed.blocks;

import com.astrazoey.indexed.CommonClass;
import com.astrazoey.indexed.registry.IndexedParticles;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.world.level.block.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.ToIntFunction;


public class CrystalGlobeBlock extends Block {

    public static final int MAX_LEVEL = 8;
    public static final IntegerProperty LEVEL = BlockStateProperties.LEVEL_COMPOSTER;
    public static final ToIntFunction<BlockState> STATE_TO_LUMINANCE;

    public CrystalGlobeBlock(BlockBehaviour.Properties settings) {
        super(settings);
        this.registerDefaultState((BlockState)this.stateDefinition.any().setValue(LEVEL, 0));
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter view, @NotNull BlockPos pos, @NotNull CollisionContext context) {

        VoxelShape base = Shapes.box(0.125f, 0f, 0.125f, 0.875f, 0.125f, 0.875f);
        VoxelShape stand = Shapes.box(0.375f, 0.125f, 0.375f, 0.625f, 0.25f, 0.625);
        VoxelShape head = Shapes.box(0.1875f, 0.25f, 0.1875f, 0.8125f, 0.875f, 0.8125f);

        return Shapes.or(head, Shapes.or(base, stand));
    }

    private void crystalUseEffects(BlockState state, Level world, BlockPos pos) {
        world.playSound(null, pos, CommonClass.CRYSTAL_USE_SOUND_EVENT, SoundSource.BLOCKS, 0.2f, getRandomPitch(world));
        if(world instanceof ServerLevel) {
            ((ServerLevel) world).sendParticles(IndexedParticles.CRYSTAL_BREAK, pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5, 20, 0.25, 0.25, 0.25, 0);
        }
    }

    @Override
    public @NotNull InteractionResult useItemOn(@NotNull ItemStack stack, @NotNull BlockState state, @NotNull Level world, @NotNull BlockPos pos, Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        if((player.getItemInHand(hand).isEnchanted() || player.getItemInHand(hand).is(Items.ENCHANTED_BOOK))) {
            ItemStack heldItem = player.getItemInHand(hand);
            ItemEnchantments heldEnchantments = EnchantmentHelper.getEnchantmentsForCrafting(heldItem);
            ItemEnchantments.Mutable newEnchantments = new ItemEnchantments.Mutable(EnchantmentHelper.getEnchantmentsForCrafting(heldItem));

            int totalEnchants = 0;
            for(Object2IntMap.Entry<Holder<@NotNull Enchantment>> entry : heldEnchantments.entrySet()) {
                Holder<@NotNull Enchantment> enchantmentEntry = entry.getKey();
                int enchantmentLevel = heldEnchantments.getLevel(enchantmentEntry);

                if(enchantmentEntry.is(EnchantmentTags.CURSE)) {
                    newEnchantments.set(enchantmentEntry, enchantmentLevel);
                } else {
                    enchantmentLevel--;
                    incrementCrystalLevel(state, world, pos);
                    totalEnchants++;
                }


                newEnchantments.set(enchantmentEntry, enchantmentLevel);
            }

            EnchantmentHelper.setEnchantments(heldItem, newEnchantments.toImmutable());
            if(heldItem.is(Items.ENCHANTED_BOOK) && EnchantmentHelper.getEnchantmentsForCrafting(heldItem).isEmpty()) {
                ItemStack newItem = heldItem.transmuteCopy(Items.BOOK, heldItem.getCount());
                player.setItemInHand(hand, newItem);
            }

            if(totalEnchants > 0) {
                crystalUseEffects(state, world, pos);

                return InteractionResult.SUCCESS;
            } else {
                return InteractionResult.FAIL;
            }
        } else if(player.getMainHandItem().isEmpty() && state.getValue(LEVEL) >= MAX_LEVEL) {
            world.playSound(null, pos, CommonClass.CRYSTAL_HARVEST_SOUND_EVENT, SoundSource.BLOCKS, 1f, getRandomPitch(world));

            world.setBlock(pos, state.setValue(LEVEL, 0), Block.UPDATE_ALL);

            if (!player.level().isClientSide()) {
                MobEffectInstance statusEffectInstance = new MobEffectInstance(CommonClass.ENCHANTED_STATUS_EFFECT, 300*20);
                player.addEffect(statusEffectInstance);
            }


            if(world instanceof ServerLevel) {
                this.popExperience((ServerLevel) world, pos, getCrystalPower(world, pos));
            }

            if(player instanceof ServerPlayer) {
                CommonClass.USE_CRYSTAL_GLOBE.trigger((ServerPlayer) player);
            }


            return InteractionResult.SUCCESS;
        } else {
            int xp = getCrystalFuelValue(player.getItemInHand(hand));
            if (xp >= 0) {
                crystalUseEffects(state, world, pos);

                if(world instanceof ServerLevel) {
                    this.popExperience((ServerLevel) world, pos, xp);
                }

                if(!player.isCreative()) {
                    player.getItemInHand(hand).shrink(1);
                }

                return InteractionResult.SUCCESS;
            } else {
                return InteractionResult.PASS;
            }
        }
    }

    private int getCrystalFuelValue(ItemStack stack) {
        Map<String, Integer> fuelMap = CommonClass.getConfig().getCrystalGlobeFuel();
        for (Map.Entry<String, Integer> entry : fuelMap.entrySet()) {
            Identifier id = Identifier.parse(entry.getKey());
            if (BuiltInRegistries.ITEM.containsKey(id)) {
                Item item = BuiltInRegistries.ITEM.getValue(id);
                if (stack.is(item)) {
                    return entry.getValue();
                }
            }
        }
        return -1;
    }

    private int getCrystalPower(Level world, BlockPos pos) {
        return 30;
    }

    private void incrementCrystalLevel(BlockState state, Level world, BlockPos pos) {
        int i = state.getValue(LEVEL);

        if(i < MAX_LEVEL && (world.getRandom().nextInt(100)) <= getCrystalPower(world, pos) && !world.isClientSide()) {
            i++;
            world.setBlock(pos, state.setValue(LEVEL, i), Block.UPDATE_ALL);
        }
    }

    public void animateTick(@NotNull BlockState state, @NotNull Level world, @NotNull BlockPos pos, net.minecraft.util.RandomSource random) {
        if (random.nextInt(3) == 0 && state.getValue(LEVEL) >= MAX_LEVEL) {
            world.addParticle(IndexedParticles.CRYSTAL_HARVEST, pos.getX() + world.getRandom().nextFloat()*1, pos.getY()+ world.getRandom().nextFloat()*1, pos.getZ()+ world.getRandom().nextFloat()*1, random.nextGaussian() * 0.005D, random.nextGaussian() * 0.005D, random.nextGaussian() * 0.005D);
        }

        if (random.nextInt(24) == 0 && state.getValue(LEVEL) >= MAX_LEVEL) {
            world.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), CommonClass.CRYSTAL_AMBIENT_SOUND_EVENT, SoundSource.BLOCKS, 2f, getRandomPitch(world), true);
        }

    }

    private float getRandomPitch(Level world) {
        return 0.8f + (world.getRandom().nextFloat() * 0.4f);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<@NotNull Block, @NotNull BlockState> builder) {
        builder.add(LEVEL);
    }

    @Override
    public boolean hasAnalogOutputSignal(@NotNull BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, @NotNull Level world, @NotNull BlockPos pos, @NotNull Direction direction) {
        return state.getValue(LEVEL);
    }

    @Override
    public boolean isPathfindable(@NotNull BlockState state, @NotNull PathComputationType type) {
        return false;
    }


    static {
        STATE_TO_LUMINANCE = (state) -> {
            return Math.min(15, 2 * (Integer)state.getValue(LEVEL));
        };
    }

}
