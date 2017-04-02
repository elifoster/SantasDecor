package io.github.elifoster.santasdecor.blocks.mug;

import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static net.minecraftforge.fluids.Fluid.BUCKET_VOLUME;
import static net.minecraftforge.fluids.capability.CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY;

public class TileEntityMug extends TileEntity {
    @Nonnull
    private EnumFacing facing = EnumFacing.NORTH;
    private boolean hasDirt;
    @Nullable
    private ItemStack flower;
    @Nonnull
    private FluidTank tank = new FluidTank(BUCKET_VOLUME);
    @Nonnull
    private EnumDyeColor color = EnumDyeColor.WHITE;
    private int renderFrame = 0;

    /**
     * @return The current color of this mug.
     */
    @Nonnull
    EnumDyeColor getColor() {
        return color;
    }

    /**
     * Sets this mug's current color to the provided color.
     */
    void recolor(@Nonnull EnumDyeColor color) {
        this.color = color;
    }

    /**
     * @return Whether there is dirt in this mug. This does not necessarily mean there is a flower.
     *         A mug with dirt can have a flower, but a mug cannot have a flower without dirt.
     */
    boolean hasDirt() {
        return hasDirt;
    }

    /**
     * Sets this mug to have dirt inside of it.
     */
    void addDirt() {
        hasDirt = true;
    }

    /**
     * Removes the dirt from the mug. If it has a flower, it also removes that. This shouldn't happen unless there's
     * some weirdness going on, or someone is doing some mod compatibility.
     * @return {@inheritDoc {@link #removeFlower()}}
     */
    @Nullable
    ItemStack removeDirt() {
        hasDirt = false;
        return removeFlower();
    }

    /**
     * Removes the flower from the mug.
     * @return The flower that was contained in the mug.
     */
    @Nullable
    ItemStack removeFlower() {
        ItemStack oldFlower = flower == null ? null : flower.copy();
        flower = null;
        return oldFlower;
    }

    /**
     * @return The current flower in this mug. Null if there is no flower.
     */
    @Nullable
    ItemStack getFlower() {
        return flower;
    }

    /**
     * Sets the flower stored in the mug to the provided ItemStack.
     */
    void setFlower(@Nullable ItemStack flower) {
        this.flower = flower;
    }

    /**
     * @return The current direction that the mug is facing. Can only be NORTH, EAST, SOUTH, or WEST.
     */
    @Nonnull
    EnumFacing getFacing() {
        return facing;
    }

    /**
     * Rotates this mug's facing value around the Y axis.
     */
    void rotateBlock() {
        facing = facing.rotateY();
    }

    /**
     * @return Whether there is a fluid in this mug.
     */
    boolean hasFluid() {
        return tank.getFluidAmount() > 0;
    }

    /**
     * Adds the fluid from the provided ItemStack fluid container to this mug. This does <i>not</i> do safety checks
     * for whether there is already a fluid, or if there is already dirt/flower in it.
     */
    void addFluid(@Nonnull ItemStack container) {
        if (container.hasCapability(FLUID_HANDLER_CAPABILITY, null)) {
            IFluidHandler handler = container.getCapability(FLUID_HANDLER_CAPABILITY, null);
            tank.fill(handler.drain(tank.getCapacity(), true), true);
        }
    }

    /**
     * Drains the current fluid from the mug into the provided ItemStack fluid container. This does <i>not</i> do safety
     * checks for whether there actually is a fluid in this mug already.
     */
    void drainFluid(@Nonnull ItemStack into) {
        if (into.hasCapability(FLUID_HANDLER_CAPABILITY, null)) {
            IFluidHandler handler = into.getCapability(FLUID_HANDLER_CAPABILITY, null);
            tank.drain(handler.fill(tank.getFluid(), true), true);
        }
    }

    @Nonnull
    FluidTank getTank() {
        return tank;
    }

    int getRenderFrame() {
        return renderFrame;
    }

    void incrementRenderFrame() {
        renderFrame++;
        if (renderFrame == Integer.MAX_VALUE) {
            renderFrame = 0;
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        // Side note: I really wish we could store this as a string.
        recolor(EnumDyeColor.byMetadata(compound.getInteger("Color")));
        hasDirt = compound.getBoolean("HasDirt");
        if (compound.hasKey("Flower")) {
            setFlower(ItemStack.func_77949_a(compound.getCompoundTag("Flower")));
        }
        EnumFacing facingFromNBT = EnumFacing.byName(compound.getString("Facing"));
        facing = facingFromNBT == null ? EnumFacing.NORTH : facingFromNBT;
        tank.readFromNBT(compound.getCompoundTag("Fluid"));
    }

    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound = super.writeToNBT(compound);
        compound.setInteger("Color", getColor().getMetadata());
        compound.setBoolean("HasDirt", hasDirt());
        ItemStack flower = getFlower();
        if (flower != null) {
            compound.setTag("Flower", flower.writeToNBT(new NBTTagCompound()));
        }
        compound.setString("Facing", getFacing().getName());
        compound.setTag("Fluid", tank.writeToNBT(new NBTTagCompound()));
        return compound;
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(getPos(), 0, getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        readFromNBT(pkt.getNbtCompound());
    }

    @Nonnull
    @Override
    public NBTTagCompound getUpdateTag() {
        return writeToNBT(new NBTTagCompound());
    }
}
