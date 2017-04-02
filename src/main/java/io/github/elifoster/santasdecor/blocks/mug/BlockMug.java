package io.github.elifoster.santasdecor.blocks.mug;

import io.github.elifoster.santasdecor.SantasDecor;
import io.github.elifoster.santasdecor.util.WorldUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static net.minecraftforge.fluids.capability.CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY;

/**
 * Has two actual states: FACING (EnumFacing for plane horizontal); COLOR (EnumDyeColor)
 *      Both actual states are stored in the TileEntityMug NBT.
 *
 * In-world controls of the mug:
 *      Sneak + Use with empty hand: Rotate
 *      Use with empty hand: Remove flower or dirt (flower first, then dirt). No dirt drops.
 *      Use with a dirt: Place dirt inside the mug
 *      Use with a dye: Recolor to dye color
 *      Use with a flower: Place flower inside the mug if it has dirt
 *      Use with a fluid container: Put fluid inside the mug if it does not have dirt or a flower.
 */
public class BlockMug extends Block {
    private static final AxisAlignedBB AABB = new AxisAlignedBB(5.75F / 16F, 0, 5.75F / 16F, 10.25F / 16F, 4.5F / 16F, 10.25F / 16F);
    private static final PropertyDirection FACING = BlockHorizontal.FACING;
    private static final PropertyEnum<EnumDyeColor> COLOR = PropertyEnum.create("color", EnumDyeColor.class);

    public BlockMug() {
        super(Material.ROCK);
        setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(COLOR, EnumDyeColor.WHITE));
        setCreativeTab(SantasDecor.CREATIVE_TAB);
        // Values from BlockHardenedClay
        setHardness(1.25F);
        setResistance(7.0F);
        setSoundType(SoundType.STONE);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, @Nullable ItemStack heldItemStack, EnumFacing facing, float hitX, float hitY, float hitZ) {
        TileEntity te = world.getTileEntity(pos);
        if (!(te instanceof TileEntityMug)) {
            return false;
        }
        TileEntityMug mug = (TileEntityMug) te;
        boolean decrStackSize = false;
        EnumDyeColor currentColor = mug.getColor();

        if (player.isSneaking()) {
            if (heldItemStack == null) {
                mug.rotateBlock();
            }
        } else {
            // TODO: Fluids
            if (heldItemStack == null) {
                if (mug.hasDirt()) {
                    ItemStack toDrop = mug.getFlower() == null ? mug.removeDirt() : mug.removeFlower();
                    if (toDrop != null && !world.isRemote) {
                        player.entityDropItem(toDrop, 0.5F);
                    }
                }
            } else if (isDirt(heldItemStack)) {
                mug.addDirt();
                decrStackSize = true;
            } else if (isFlower(heldItemStack)) {
                if (mug.hasDirt() && mug.getFlower() == null) {
                    mug.setFlower(heldItemStack.splitStack(1));
                }
            } else if (heldItemStack.hasCapability(FLUID_HANDLER_CAPABILITY, null)) {
                IFluidHandler handler = heldItemStack.getCapability(FLUID_HANDLER_CAPABILITY, null);
                IFluidTankProperties[] props = handler.getTankProperties();
                for (IFluidTankProperties prop : props) {
                    if (mug.hasFluid() && prop.getContents() == null) {
                        mug.drainFluid(heldItemStack);
                        break;
                    } else if (!mug.hasFluid() && !mug.hasDirt() && prop.getContents() != null) {
                        mug.addFluid(heldItemStack);
                        break;
                    }
                }
            } else {
                EnumDyeColor colorOfItem = getColorFromItem(heldItemStack);
                if (colorOfItem != null && colorOfItem != currentColor) {
                    mug.recolor(colorOfItem);
                    decrStackSize = true;
                }
            }
        }

        world.notifyBlockUpdate(pos, state, world.getBlockState(pos), 3);
        if (decrStackSize && !player.isCreative()) {
            heldItemStack.stackSize--;
        }
        return true;
    }

    private boolean isDirt(ItemStack item) {
        if (item == null) {
            return false;
        }
        for (int id : OreDictionary.getOreIDs(item)) {
            String name = OreDictionary.getOreName(id);
            if (name.equals("dirt")) {
                return true;
            }
        }
        return false;
    }

    private boolean isFlower(ItemStack item) {
        if (item == null) {
            return false;
        }
        Block block = Block.getBlockFromItem(item.getItem());
        return block instanceof BlockBush;
    }

    private EnumDyeColor getColorFromItem(ItemStack item) {
        if (item == null) {
            return null;
        }
        for (int id : OreDictionary.getOreIDs(item)) {
            String name = OreDictionary.getOreName(id);
            if (name.startsWith("dye")) {
                String colorStr = name.replaceFirst("dye", "");
                for (EnumDyeColor color : EnumDyeColor.values()) {
                    // For some reason, Mojang decided that Light Gray should be called Silver in the codebase.
                    // How annoying.
                    String colorName = color == EnumDyeColor.SILVER ? "LightGray" : color.getUnlocalizedName();
                    if (colorName.equalsIgnoreCase(colorStr)) {
                        return color;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Nonnull
    @Override
    public TileEntity createTileEntity(@Nonnull World world, @Nonnull IBlockState state) {
        return new TileEntityMug();
    }

    @Nonnull
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING, COLOR);
    }

    @Nonnull
    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState();
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return 0;
    }

    @Nonnull
    @Override
    public IBlockState getActualState(@Nonnull IBlockState state, IBlockAccess world, BlockPos pos) {
        TileEntity te = WorldUtil.getTileEntitySafely(world, pos);
        if (te instanceof TileEntityMug) {
            TileEntityMug mug = (TileEntityMug) te;
            return state
              .withProperty(COLOR, mug.getColor())
              .withProperty(FACING, mug.getFacing());
        }
        return state;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Nonnull
    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return AABB;
    }
}
