package com.astrazoey.indexed;

import com.astrazoey.indexed.blocks.CrystalGlobeBlock;
import com.astrazoey.indexed.criterion.*;
import com.astrazoey.indexed.enchantments.*;
//import com.astrazoey.indexed.mixins.CriterionRegistryAccessor;
import com.astrazoey.indexed.registry.IndexedItems;
import com.astrazoey.indexed.registry.IndexedParticles;
import com.astrazoey.indexed.status_effects.EnchantedStatusEffect;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.loot.v2.*;
//import net.fabricmc.fabric.api.object.builder.v1.advancement.CriterionRegistry;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.SharedConstants;
import net.minecraft.block.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.item.*;
import net.minecraft.loot.*;
import net.minecraft.loot.entry.LootPoolEntry;
import net.minecraft.loot.entry.LootTableEntry;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import org.lwjgl.system.CallbackI;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;


public class Indexed implements ModInitializer {

    public static final Identifier CONFIG_PACKET = new Identifier("indexed", "config");

    public static final String MOD_ID = "indexed";

    //Enchantments
    public static Enchantment SLOW_BURN = Registry.register(
            Registries.ENCHANTMENT,
            new Identifier("indexed", "slow_burn"),
            new SlowBurnEnchantment()
    );

    public static Enchantment QUICK_FLIGHT = Registry.register(
            Registries.ENCHANTMENT,
            new Identifier("indexed", "quick_flight"),
            new SlowBurnEnchantment()
    );

    public static Enchantment FORGERY = Registry.register(
            Registries.ENCHANTMENT,
            new Identifier("indexed", "forgery"),
            new ForgeryEnchantment()
    );

    public static Enchantment MYSTERY_CURSE = Registry.register(
            Registries.ENCHANTMENT,
            new Identifier("indexed", "mystery_curse"),
            new MysteryCurseEnchantment()
    );

    public static Enchantment HIDDEN_CURSE = Registry.register(
            Registries.ENCHANTMENT,
            new Identifier("indexed", "hidden_curse"),
            new HiddenCurseEnchantment()
    );

    public static Enchantment KEEPING = Registry.register(
            Registries.ENCHANTMENT,
            new Identifier("indexed", "keeping"),
            new KeepingEnchantment()
    );

    public static Enchantment ESSENCE = Registry.register(
            Registries.ENCHANTMENT,
            new Identifier("indexed", "essence"),
            new EssenceEnchantment()
    );

    public static Enchantment DEBUFF_RESISTANCE = Registry.register(
            Registries.ENCHANTMENT,
            new Identifier("indexed", "debuff_resistance"),
            new DebuffResistanceEnchantment()
    );



    //Blocks
    public static final Block CRYSTAL_GLOBE = new CrystalGlobeBlock(FabricBlockSettings.
            create().
            mapColor(DyeColor.MAGENTA).
            solid().
            solid().
            strength(1.5f).
            hardness(1.5f).
            luminance(CrystalGlobeBlock.STATE_TO_LUMINANCE).
            sounds(BlockSoundGroup.AMETHYST_BLOCK).
            nonOpaque()
    );

    //Sounds
    public static final Identifier CRYSTAL_USE_SOUND = new Identifier("indexed","use_crystal_globe");
    public static SoundEvent CRYSTAL_USE_SOUND_EVENT = SoundEvent.of(CRYSTAL_USE_SOUND);
    public static final Identifier CRYSTAL_HARVEST_SOUND = new Identifier("indexed","harvest_crystal_globe");
    public static SoundEvent CRYSTAL_HARVEST_SOUND_EVENT = SoundEvent.of(CRYSTAL_HARVEST_SOUND);
    public static final Identifier CRYSTAL_AMBIENT_SOUND = new Identifier("indexed","crystal_globe_ambient");
    public static SoundEvent CRYSTAL_AMBIENT_SOUND_EVENT = SoundEvent.of(CRYSTAL_AMBIENT_SOUND);

    //Status Effects
    public static final StatusEffect ENCHANTED_STATUS_EFFECT = new EnchantedStatusEffect(StatusEffectCategory.BENEFICIAL, 0xD400FF);

    //Loot Tables
    public static Identifier INDEXED_LOOT = new Identifier("indexed", "indexed_items");
    public static Identifier INDEXED_NETHER_BRIDGE = new Identifier("indexed", "indexed_nether_bridge");
    public static Identifier INDEXED_OUTPOST = new Identifier("indexed", "indexed_outpost");
    public static Identifier INDEXED_MANSION = new Identifier("indexed", "indexed_mansion");
    public static Identifier INDEXED_MINESHAFT = new Identifier("indexed", "indexed_mineshaft");
    public static Identifier INDEXED_SHIPWRECK = new Identifier("indexed", "indexed_shipwreck");
    public static Identifier INDEXED_TEMPLE = new Identifier("indexed", "indexed_temple");
    public static Identifier INDEXED_BURIED_TREASURE = new Identifier("indexed", "indexed_buried_treasure");
    public static Identifier INDEXED_WATER_RUIN = new Identifier("indexed", "indexed_water_ruin");



    //Criterion
    //public static OverchargeItemCriterion OVERCHARGE_ITEM = new OverchargeItemCriterion();
    //public static EnchantGoldBookCriterion ENCHANT_GOLD_BOOK = new EnchantGoldBookCriterion();
    //public static RepairItemCriterion REPAIR_ITEM = new RepairItemCriterion();
    //public static GrindEssenceCriterion GRIND_ESSENCE = new GrindEssenceCriterion();
    //public static MultishotCrossbowCriterion MULTISHOT_CROSSBOW = new MultishotCrossbowCriterion();
    //public static MaxGoldCriterion MAX_GOLD = new MaxGoldCriterion();
    //public static MaxKnockbackCriterion MAX_KNOCKBACK = new MaxKnockbackCriterion();
    public static UseCrystalGlobeCriterion USE_CRYSTAL_GLOBE = new UseCrystalGlobeCriterion();
    //public static FillCrystalGlobeCriterion FILL_CRYSTAL_GLOBE = new FillCrystalGlobeCriterion();
    //public static EnchantedCriterion ENCHANTED_ADVANCEMENT = new EnchantedCriterion();



    @Override
    public void onInitialize() {

        IndexedItems.registerItems();

        //Blocks
        Registry.register(Registries.BLOCK, new Identifier(MOD_ID, "crystal_globe"), CRYSTAL_GLOBE);
        Registry.register(Registries.ITEM, new Identifier(MOD_ID, "crystal_globe"), new BlockItem(CRYSTAL_GLOBE, new FabricItemSettings()
        //        .group(ItemGroup.MISC)
        ));

        BlockRenderLayerMap.INSTANCE.putBlock(CRYSTAL_GLOBE, RenderLayer.getCutout());

        //Sounds
        Registry.register(Registries.SOUND_EVENT, CRYSTAL_USE_SOUND, CRYSTAL_USE_SOUND_EVENT);
        Registry.register(Registries.SOUND_EVENT, CRYSTAL_HARVEST_SOUND, CRYSTAL_HARVEST_SOUND_EVENT);
        Registry.register(Registries.SOUND_EVENT, CRYSTAL_AMBIENT_SOUND, CRYSTAL_AMBIENT_SOUND_EVENT);

        //Particles
        IndexedParticles.init();

        //Status Effects
        Registry.register(Registries.STATUS_EFFECT, new Identifier(MOD_ID, "enchanted"), ENCHANTED_STATUS_EFFECT);

        //Criterion Registration
        //CriterionRegistryAccessor.registerCriterion(OVERCHARGE_ITEM);
        //CriterionRegistryAccessor.registerCriterion(ENCHANT_GOLD_BOOK);
        //CriterionRegistryAccessor.registerCriterion(REPAIR_ITEM);
        //CriterionRegistryAccessor.registerCriterion(GRIND_ESSENCE);
        //CriterionRegistryAccessor.registerCriterion(MULTISHOT_CROSSBOW);
        //CriterionRegistryAccessor.registerCriterion(MAX_GOLD);
        //CriterionRegistryAccessor.registerCriterion(MAX_KNOCKBACK);
        //CriterionRegistryAccessor.registerCriterion(USE_CRYSTAL_GLOBE);
        //CriterionRegistryAccessor.registerCriterion(FILL_CRYSTAL_GLOBE);
        //CriterionRegistryAccessor.registerCriterion(ENCHANTED_ADVANCEMENT);


        //Ores Drop Experience
        SetOreExperience.set(Blocks.COPPER_ORE, UniformIntProvider.create(1,3));
        SetOreExperience.set(Blocks.DEEPSLATE_COPPER_ORE, UniformIntProvider.create(1,3));
        SetOreExperience.set(Blocks.IRON_ORE, UniformIntProvider.create(1,3));
        SetOreExperience.set(Blocks.DEEPSLATE_IRON_ORE, UniformIntProvider.create(1,3));
        SetOreExperience.set(Blocks.GOLD_ORE, UniformIntProvider.create(2,4));
        SetOreExperience.set(Blocks.DEEPSLATE_GOLD_ORE, UniformIntProvider.create(2,4));


        //Add Items to Chests
        LootTableEvents.MODIFY.register(((resourceManager, manager, id, supplier, setter) -> {
            if(LootTables.END_CITY_TREASURE_CHEST.equals(id) ||
                    LootTables.ABANDONED_MINESHAFT_CHEST.equals(id) ||
                    LootTables.STRONGHOLD_LIBRARY_CHEST.equals(id) ||
                    LootTables.BASTION_TREASURE_CHEST.equals(id) ||
                    LootTables.WOODLAND_MANSION_CHEST.equals(id) ||
                    LootTables.NETHER_BRIDGE_CHEST.equals(id) ||
                    LootTables.PILLAGER_OUTPOST_CHEST.equals(id) ||
                    LootTables.RUINED_PORTAL_CHEST.equals(id)) {
                        LootPool.Builder poolBuilder = LootPool.builder()
                                .with(LootTableEntry.builder(INDEXED_LOOT));
                        supplier.pool(poolBuilder);
            }
        }));

        LootTableEvents.MODIFY.register(((resourceManager, manager, id, supplier, setter) -> {
            if(LootTables.NETHER_BRIDGE_CHEST.equals(id)) {
                LootPool.Builder poolBuilder = LootPool.builder()
                        .with(LootTableEntry.builder(INDEXED_NETHER_BRIDGE));
                supplier.pool(poolBuilder);
            }
        }));

        LootTableEvents.MODIFY.register(((resourceManager, manager, id, supplier, setter) -> {
            if(LootTables.PILLAGER_OUTPOST_CHEST.equals(id)) {
                LootPool.Builder poolBuilder = LootPool.builder()
                        .with(LootTableEntry.builder(INDEXED_OUTPOST));
                supplier.pool(poolBuilder);
            }
        }));

        LootTableEvents.MODIFY.register(((resourceManager, manager, id, supplier, setter) -> {
            if(LootTables.WOODLAND_MANSION_CHEST.equals(id)) {
                LootPool.Builder poolBuilder = LootPool.builder()
                        .with(LootTableEntry.builder(INDEXED_MANSION));
                supplier.pool(poolBuilder);
            }
        }));

        LootTableEvents.MODIFY.register(((resourceManager, manager, id, supplier, setter) -> {
            if(LootTables.ABANDONED_MINESHAFT_CHEST.equals(id)) {
                LootPool.Builder poolBuilder = LootPool.builder()
                        .with(LootTableEntry.builder(INDEXED_MINESHAFT));
                supplier.pool(poolBuilder);
            }
        }));

        LootTableEvents.MODIFY.register(((resourceManager, manager, id, supplier, setter) -> {
            if(LootTables.SHIPWRECK_TREASURE_CHEST.equals(id)) {
                LootPool.Builder poolBuilder = LootPool.builder()
                        .with(LootTableEntry.builder(INDEXED_SHIPWRECK));
                supplier.pool(poolBuilder);
            }
        }));

        LootTableEvents.MODIFY.register(((resourceManager, manager, id, supplier, setter) -> {
            if(LootTables.BURIED_TREASURE_CHEST.equals(id)) {
                LootPool.Builder poolBuilder = LootPool.builder()
                        .with(LootTableEntry.builder(INDEXED_BURIED_TREASURE));
                supplier.pool(poolBuilder);
            }
        }));

        LootTableEvents.MODIFY.register(((resourceManager, manager, id, supplier, setter) -> {
            if(LootTables.UNDERWATER_RUIN_BIG_CHEST.equals(id) ||
                    LootTables.UNDERWATER_RUIN_SMALL_CHEST.equals(id)) {
                LootPool.Builder poolBuilder = LootPool.builder()
                        .with(LootTableEntry.builder(INDEXED_WATER_RUIN));
                supplier.pool(poolBuilder);
            }
        }));

        LootTableEvents.MODIFY.register(((resourceManager, manager, id, supplier, setter) -> {
            if(LootTables.JUNGLE_TEMPLE_CHEST.equals(id) ||
                    LootTables.DESERT_PYRAMID_CHEST.equals(id)) {
                LootPool.Builder poolBuilder = LootPool.builder()
                        .with(LootTableEntry.builder(INDEXED_TEMPLE));
                supplier.pool(poolBuilder);
            }
        }));


        //Item Groups
        /*
        ItemGroup INDEXED_ITEM_GROUP = FabricItemGroup.builder(new Identifier(MOD_ID, "indexed_item_group"))
                .displayName(Text.translatable("itemGroup.indexed.indexed_item_group_display"))
                .icon(() -> new ItemStack(IndexedItems.GOLD_BOUND_BOOK))
                .entries((enabledFeatures, entries, operatorEnabled) -> {
                    entries.add(IndexedItems.GOLD_BOUND_BOOK);
                    entries.add(IndexedItems.VITALIS);
                    entries.add(Indexed.CRYSTAL_GLOBE);
                })
                .build();

         */

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(entries -> entries.addAfter(Items.DIAMOND ,IndexedItems.VITALIS));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(entries -> entries.addAfter(Items.BOOK ,IndexedItems.GOLD_BOUND_BOOK));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(entries -> entries.addAfter(Items.ENCHANTING_TABLE, Indexed.CRYSTAL_GLOBE));

        //Registers Config
        Identifier identifier = new Identifier(MOD_ID);
        ServerLifecycleEvents.SERVER_STARTING.register(identifier, callbacks -> {
            System.out.println("INDEXED: Starting starting. Loading config.");
            initializeConfig();
        });

        ServerLifecycleEvents.START_DATA_PACK_RELOAD.register(identifier, (server, serverResourceManager) -> {
            System.out.println("INDEXED: Server data pack reload. Loading config.");
            initializeConfig();
        });

    }

    public static void initializeConfig() {
        ConfigMain.load(false);
        boolean modOutOfDate = ConfigMain.isOutOfDate();
        Config.loadConfig(modOutOfDate);
        ConfigMain.load(modOutOfDate);
    }

    public static Identifier id(String path) {
        return new Identifier(MOD_ID, path);
    }

}
