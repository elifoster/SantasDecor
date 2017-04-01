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
import net.minecraft.util.BlockRenderLayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class BlockGlass extends Block {
    public static final PropertyEnum<Family> FAMILY = PropertyEnum.create("family", Family.class, Family::isEnabled);

    public BlockGlass() {
        super(Material.GLASS);
        setCreativeTab(SantasDecor.CREATIVE_TAB);
        setHardness(1.5F);
        setResistance(10.0F);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FAMILY);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(FAMILY, Family.LOOKUP[meta]);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FAMILY).ordinal();
    }

    @Override
    public int damageDropped(IBlockState state) {
        return getMetaFromState(state);
    }

    @Override
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
        for (Family family : Family.LOOKUP) {
            if (family.isEnabled()) {
                list.add(new ItemStack(this, 1, family.ordinal()));
            }
        }
    }
}
