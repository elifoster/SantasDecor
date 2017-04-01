package io.github.elifoster.santasdecor.blocks;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockFamily extends ItemBlock {
    public ItemBlockFamily(Block block) {
        super(block);
        setHasSubtypes(true);
    }

    @Override
    public int getMetadata(int meta) {
        return meta;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return getUnlocalizedName() + "." + Family.LOOKUP[stack.getItemDamage()].getName();
    }
}
