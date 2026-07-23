package com.astrazoey.indexed.blocks;

import com.astrazoey.indexed.Indexed;
import com.astrazoey.indexed.registry.IndexedItems;
import com.astrazoey.indexed.registry.IndexedParticles;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.world.level.block.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Random;
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
    public VoxelShape getShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext context) {

        VoxelShape base = Shapes.box(0.125f, 0f, 0.125f, 0.875f, 0.125f, 0.875f);
        VoxelShape stand = Shapes.box(0.375f, 0.125f, 0.375f, 0.625f, 0.25f, 0.625);
        VoxelShape head = Shapes.box(0.1875f, 0.25f, 0.1875f, 0.8125f, 0.875f, 0.8125f);

        return Shapes.or(head, Shapes.or(base, stand));
    }



    private double lerpDouble(double start, double finish, double alpha) {
        return start + alpha * (finish - start);
    }

    private Vec3 lerpVector(Vec3 start, Vec3 finish, double alpha) {
        return new Vec3(
                lerpDouble(start.x, finish.x, alpha),
                lerpDouble(start.y, finish.y, alpha),
                lerpDouble(start.z, finish.z, alpha));
    }

    private void crystalUseEffects(BlockState state, Level world, BlockPos pos) {
        world.playSound(null, pos, Indexed.CRYSTAL_USE_SOUND_EVENT, SoundSource.BLOCKS, 0.2f, getRandomPitch(world));
        if(world instanceof ServerLevel) {
            ((ServerLevel) world).sendParticles(IndexedParticles.CRYSTAL_BREAK, pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5, 20, 0.25, 0.25, 0.25, 0);
        }
    }

    @Override
    public InteractionResult useItemOn(ItemStack stack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if((player.getItemInHand(hand).isEnchanted() || player.getItemInHand(hand).is(Items.ENCHANTED_BOOK))) {
            ItemStack heldItem = player.getItemInHand(hand);
            ItemEnchantments heldEnchantments = EnchantmentHelper.getEnchantmentsForCrafting(heldItem);
            ItemEnchantments.Mutable newEnchantments = new ItemEnchantments.Mutable(EnchantmentHelper.getEnchantmentsForCrafting(heldItem));

            int totalEnchants = 0;
            for(Object2IntMap.Entry<Holder<Enchantment>> entry : heldEnchantments.entrySet()) {
                Holder<Enchantment> enchantmentEntry = entry.getKey();
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
            world.playSound(null, pos, Indexed.CRYSTAL_HARVEST_SOUND_EVENT, SoundSource.BLOCKS, 1f, getRandomPitch(world));

            world.setBlock(pos, state.setValue(LEVEL, 0), Block.UPDATE_ALL);

            if (!player.level().isClientSide()) {
                MobEffectInstance statusEffectInstance = new MobEffectInstance(Indexed.ENCHANTED_STATUS_EFFECT, 300*20);
                player.addEffect(statusEffectInstance);
            }


            if(world instanceof ServerLevel) {
                this.popExperience((ServerLevel) world, pos, getCrystalPower(world, pos));
            }

            //int finalClusterCount = countAmethystClusters(pos, world, true);

            if(player instanceof ServerPlayer) {
                Indexed.USE_CRYSTAL_GLOBE.trigger((ServerPlayer) player);
                //if(finalClusterCount >= MAX_LEVEL) {
                //    Indexed.FILL_CRYSTAL_GLOBE.trigger((ServerPlayerEntity) player);
                //}

            }


            return InteractionResult.SUCCESS;
        } else if(player.getItemInHand(hand).is(IndexedItems.VITALIS)
                || player.getItemInHand(hand).is(Items.ECHO_SHARD)) {
            crystalUseEffects(state, world, pos);

            if(world instanceof ServerLevel) {
                this.popExperience((ServerLevel) world, pos, 10);
            }

            if(!player.isCreative()) {
                player.getItemInHand(hand).shrink(1);
            }



            return InteractionResult.SUCCESS;
        } else {
            return InteractionResult.PASS;
        }
    }

    private int getCrystalPower(Level world, BlockPos pos) {
        //return 25 + (Math.min(countAmethystClusters(pos, world, false),8)*3);
        return 30;
    }

    private void incrementCrystalLevel(BlockState state, Level world, BlockPos pos) {
        int i = state.getValue(LEVEL);

        if(i < MAX_LEVEL && (world.getRandom().nextInt(100)) <= getCrystalPower(world, pos) && !world.isClientSide()) {
            i++;
            world.setBlock(pos, state.setValue(LEVEL, i), Block.UPDATE_ALL);
        }
    }



    private boolean destroyAmethyst(BlockPos.MutableBlockPos checkedPos, BlockPos pos, Level world) {
        int random = world.getRandom().nextInt(100);

        if(random <= 12) {
            if(!world.isClientSide()) {
                world.destroyBlock(checkedPos, true, null, Block.UPDATE_ALL);

                Vec3 positionOne = new Vec3(pos.getX(), pos.getY(),pos.getZ());
                Vec3 positionTwo = new Vec3(checkedPos.getX(), checkedPos.getY(), checkedPos.getZ());
                Vec3 positionLerp;

                for(double alpha = 0; alpha <= 1; alpha+=0.025) {
                    positionLerp = lerpVector(positionOne, positionTwo, alpha);
                    if(world instanceof ServerLevel) {
                        ((ServerLevel)world).sendParticles(IndexedParticles.CRYSTAL_BREAK, positionLerp.x+0.5, positionLerp.y+0.5, positionLerp.z+0.5, 1, 0, 0, 0, 0);
                    }
                }
            }
            return true;
        } else {
            return false;
        }
    }

    private int countAmethystClusters(BlockPos pos, Level world, boolean destroyAmethyst) {
        int scanSize = 3;
        int clusterCount = 0;
        int amethystBroken = 0;

        BlockPos.MutableBlockPos checkedPos = pos.mutable();
        checkedPos = checkedPos.move(-scanSize,0,-scanSize);

        for(int i = -scanSize; i <= scanSize; i++) {
            for(int j = -scanSize; j <= scanSize; j++) {

                if(isAmethystCluster(world.getBlockState(checkedPos))) {
                    clusterCount++;

                    if(destroyAmethyst && amethystBroken < 3) {
                        if(destroyAmethyst(checkedPos, pos, world)) {
                            amethystBroken++;
                        }



                    }

                }
                checkedPos = checkedPos.move(1,0,0);
            }
            checkedPos = checkedPos.move(-scanSize*2 - 1,0,1);
        }

        return clusterCount;
    }

    private boolean isAmethystCluster(BlockState state) {
        return state.getBlock() == Blocks.SMALL_AMETHYST_BUD ||
                state.getBlock() == Blocks.MEDIUM_AMETHYST_BUD ||
                state.getBlock() == Blocks.LARGE_AMETHYST_BUD ||
                state.getBlock() == Blocks.AMETHYST_CLUSTER;
    }


    public void animateTick(BlockState state, Level world, BlockPos pos, net.minecraft.util.RandomSource random) {
        if (random.nextInt(3) == 0 && state.getValue(LEVEL) >= MAX_LEVEL) {
            world.addParticle(IndexedParticles.CRYSTAL_HARVEST, pos.getX() + world.getRandom().nextFloat()*1, pos.getY()+ world.getRandom().nextFloat()*1, pos.getZ()+ world.getRandom().nextFloat()*1, random.nextGaussian() * 0.005D, random.nextGaussian() * 0.005D, random.nextGaussian() * 0.005D);
        }

        if (random.nextInt(24) == 0 && state.getValue(LEVEL) >= MAX_LEVEL) {
            world.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), Indexed.CRYSTAL_AMBIENT_SOUND_EVENT, SoundSource.BLOCKS, 2f, getRandomPitch(world), true);
        }

    }

    private float getRandomPitch(Level world) {
        return 0.8f + (world.getRandom().nextFloat() * 0.4f);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LEVEL);
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level world, BlockPos pos, Direction direction) {
        return state.getValue(LEVEL);
    }

    @Override
    public boolean isPathfindable(BlockState state, PathComputationType type) {
        return false;
    }


    static {
        STATE_TO_LUMINANCE = (state) -> {
            return 2 * (Integer)state.getValue(LEVEL);
        };
    }

}
