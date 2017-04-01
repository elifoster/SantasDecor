package io.github.elifoster.santasdecor.blocks;

import io.github.elifoster.santasdecor.SantasDecor;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.List;

public class BlockDecorStandard extends Block {
    public PropertyEnum<Family> family;
    private final boolean isQuartz;

    BlockDecorStandard(boolean isQuartz) {
        this(Material.ROCK, isQuartz);
    }

    BlockDecorStandard() {
        this(Material.ROCK);
    }

    BlockDecorStandard(Material material) {
        this(material, false);
    }

    BlockDecorStandard(Material material, boolean isQuartz) {
        super(material);
        setCreativeTab(SantasDecor.CREATIVE_TAB);
        setHardness(1.5F);
        setResistance(10.0F);
        this.isQuartz = isQuartz;
    }

    private boolean isValid(Family family) {
        return family != null && family.isEnabled() && (!isQuartz() || family.canHaveQuartz());
    }

    private void initializeFamily() {
        family = PropertyEnum.create("family", Family.class, this::isValid);
    }

    private boolean isQuartz() {
        return isQuartz;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        if (family == null) {
            initializeFamily();
        }
        return new BlockStateContainer(this, family);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(family, Family.LOOKUP[meta]);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(family).ordinal();
    }

    @Override
    public int damageDropped(IBlockState state) {
        return getMetaFromState(state);
    }

    @Override
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
        for (Family family : Family.LOOKUP) {
            if (isValid(family)) {
                list.add(new ItemStack(this, 1, family.ordinal()));
            }
        }
    }
}
