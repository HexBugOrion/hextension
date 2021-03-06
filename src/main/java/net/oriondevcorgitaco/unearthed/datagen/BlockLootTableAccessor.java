package net.oriondevcorgitaco.unearthed.datagen;

import com.google.common.collect.ImmutableSet;
import net.minecraft.advancements.criterion.EnchantmentPredicate;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.advancements.criterion.MinMaxBounds;
import net.minecraft.advancements.criterion.StatePropertiesPredicate;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.loot.BlockLootTables;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.loot.*;
import net.minecraft.loot.conditions.*;
import net.minecraft.loot.functions.ExplosionDecay;
import net.minecraft.loot.functions.SetCount;
import net.minecraft.util.IItemProvider;
import net.oriondevcorgitaco.unearthed.block.SixwaySlabBlock;
import net.oriondevcorgitaco.unearthed.block.properties.ModBlockProperties;
import net.oriondevcorgitaco.unearthed.core.UEBlocks;
import net.oriondevcorgitaco.unearthed.core.UETags;
import net.oriondevcorgitaco.unearthed.util.BlockStatePropertiesMatch;

import java.util.Set;
import java.util.stream.Stream;

public class BlockLootTableAccessor extends BlockLootTables {
    private static final ILootCondition.IBuilder SILK_TOUCH = MatchTool.toolMatches(ItemPredicate.Builder.item().hasEnchantment(new EnchantmentPredicate(Enchantments.SILK_TOUCH, MinMaxBounds.IntBound.atLeast(1))));
    private static final ILootCondition.IBuilder HOES = MatchTool.toolMatches(ItemPredicate.Builder.item().of(UETags.Items.REGOLITH_USABLE));
    private static final Set<Item> IMMUNE_TO_EXPLOSIONS = Stream.of(Blocks.DRAGON_EGG, Blocks.BEACON, Blocks.CONDUIT, Blocks.SKELETON_SKULL, Blocks.WITHER_SKELETON_SKULL, Blocks.PLAYER_HEAD, Blocks.ZOMBIE_HEAD, Blocks.CREEPER_HEAD, Blocks.DRAGON_HEAD, Blocks.SHULKER_BOX, Blocks.BLACK_SHULKER_BOX, Blocks.BLUE_SHULKER_BOX, Blocks.BROWN_SHULKER_BOX, Blocks.CYAN_SHULKER_BOX, Blocks.GRAY_SHULKER_BOX, Blocks.GREEN_SHULKER_BOX, Blocks.LIGHT_BLUE_SHULKER_BOX, Blocks.LIGHT_GRAY_SHULKER_BOX, Blocks.LIME_SHULKER_BOX, Blocks.MAGENTA_SHULKER_BOX, Blocks.ORANGE_SHULKER_BOX, Blocks.PINK_SHULKER_BOX, Blocks.PURPLE_SHULKER_BOX, Blocks.RED_SHULKER_BOX, Blocks.WHITE_SHULKER_BOX, Blocks.YELLOW_SHULKER_BOX).map(IItemProvider::asItem).collect(ImmutableSet.toImmutableSet());


    public static <T> T withExplosionDecayWithoutImmuneCheck(IItemProvider item, ILootFunctionConsumer<T> function) {
        return function.apply(ExplosionDecay.explosionDecay());
    }

    protected static <T> T withSurvivesExplosion(IItemProvider item, ILootConditionConsumer<T> condition) {
        return (T) (!IMMUNE_TO_EXPLOSIONS.contains(item.asItem()) ? condition.when(SurvivesExplosion.survivesExplosion()) : condition.unwrap());
    }

    public static LootTable.Builder droppingItemWithFortune(Block block, Item item) {
        return BlockLootTables.createOreDrop(block, item);
    }

    public static LootTable.Builder droppingSlab(Block slab) {
        return BlockLootTables.createSlabItemTable(slab);
    }

    protected static LootTable.Builder droppingSixwaySlab(Block slab) {
        return LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantRange.exactly(1))
                .add(applyExplosionDecay(slab, ItemLootEntry.lootTableItem(slab).apply(SetCount.setCount(ConstantRange.exactly(2))
                        .when(Inverted.invert(BlockStatePropertiesMatch.builder(slab).propertiesToCompare(SixwaySlabBlock.FACE, SixwaySlabBlock.SECONDARY_FACING)))))));
    }

    public static LootTable.Builder dropping(IItemProvider item) {
        return BlockLootTables.createSingleItemTable(item);
    }

    public static LootTable.Builder droppingWithSilkTouch(Block block, LootEntry.Builder<?> builder) {
        return BlockLootTables.createSelfDropDispatchTable(block, SILK_TOUCH, builder);
    }

    public static LootTable.Builder droppingWithSilkTouch(Block block, IItemProvider noSilkTouch) {
        return BlockLootTables.createSingleItemTableWithSilkTouch(block, noSilkTouch);
    }

    public static LootTable.Builder droppingWithHoe(Block block, IItemProvider hoed) {
        return droppingWithHoe(block, withSurvivesExplosion(block, ItemLootEntry.lootTableItem(hoed)));
    }

    public static LootTable.Builder droppingWithHoe(Block block, LootEntry.Builder<?> builder) {
        return BlockLootTables.createSelfDropDispatchTable(block, HOES.invert(), builder);
    }

    public static LootTable.Builder onlyWithShears(Block block) {
        return BlockLootTables.createShearsOnlyDrop(block);
    }

    public static LootTable.Builder regolithGrassBlock(Block block, IItemProvider noSilkTouch, IItemProvider withHoe) {
        return LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantRange.exactly(1))
                .add(ItemLootEntry.lootTableItem(block).when(SILK_TOUCH)
                        .otherwise(ItemLootEntry.lootTableItem(withHoe).when(HOES).otherwise(withSurvivesExplosion(block, ItemLootEntry.lootTableItem(noSilkTouch))))));
    }
}