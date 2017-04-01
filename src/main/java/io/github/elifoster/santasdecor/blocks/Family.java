package io.github.elifoster.santasdecor.blocks;

import io.github.elifoster.santasdecor.Config;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.util.IStringSerializable;

import java.util.Locale;
import java.util.function.Supplier;

public enum Family implements IStringSerializable {
    // TODO: OreDict
    BLAZE(Items.BLAZE_ROD),
    BONE(Items.BONE, false),
    BURNT(Items.COAL),
    CRYING(Items.GHAST_TEAR, () -> Config.cryingBalance ? 12 : 8),
    ENDER(Items.ENDER_PEARL, () -> Config.enderBalance ? 24 : 8),
    FLESH(Items.ROTTEN_FLESH, false),
    ICE(Blocks.ICE),
    LEATHER(Items.LEATHER, false),
    PORKED(Items.PORKCHOP),
    SLIME(Items.SLIME_BALL),
    SNOW(Items.SNOWBALL, false);

    private final boolean canHaveQuartz;
    private final Object craftingResource;
    private final Supplier<Integer> balancedOutputSupplier;

    public static final Family[] LOOKUP = new Family[values().length];

    static {
        for (Family family : values()) {
            LOOKUP[family.ordinal()] = family;
        }
    }

    Family(Object craftingResource) {
        this(craftingResource, () -> 8);
    }

    Family(Object craftingResource, boolean canHaveQuartz) {
        this(craftingResource, canHaveQuartz, () -> 8);
    }

    Family(Object craftingResource, Supplier<Integer> balancedOutputSupplier) {
        this(craftingResource, true, balancedOutputSupplier);
    }

    Family(Object craftingResource, boolean canHaveQuartz, Supplier<Integer> balancedOutputSupplier) {
        this.canHaveQuartz = canHaveQuartz;
        this.craftingResource = craftingResource;
        this.balancedOutputSupplier = balancedOutputSupplier;
    }

    public boolean isEnabled() {
        switch (this) {
            case BLAZE: return Config.enableBlaze;
            case BONE: return Config.enableBone;
            case BURNT: return Config.enableBurnt;
            case CRYING: return Config.enableCrying;
            case ENDER: return Config.enableEnder;
            case FLESH: return Config.enableFlesh;
            case ICE: return Config.enableIce;
            case LEATHER: return Config.enableLeather;
            case PORKED: return Config.enablePorked;
            case SLIME: return Config.enableSlime;
            case SNOW: return Config.enableSnow;
        }
        return false;
    }

    public boolean canHaveQuartz() {
        return canHaveQuartz;
    }

    public Object getCraftingResource() {
        return craftingResource;
    }

    public int getBalancedOutput() {
        return balancedOutputSupplier.get();
    }

    @Override
    public String getName() {
        return toString().toLowerCase(Locale.US);
    }
}
