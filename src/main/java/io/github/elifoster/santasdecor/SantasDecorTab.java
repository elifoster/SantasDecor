package io.github.elifoster.santasdecor;

import io.github.elifoster.santasdecor.blocks.BlockHandler;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SantasDecorTab extends CreativeTabs {
    public SantasDecorTab(String tabLabel) {
        super(tabLabel);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Item getTabIconItem() {
        return new ItemStack(BlockHandler.BRICK, 1, 9).getItem();
    }

    @Override
    public boolean hasSearchBar() {
        return true;
    }

}