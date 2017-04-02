package io.github.elifoster.santasdecor;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class Config {
    public static boolean enableBlaze;
    public static boolean enableBone;
    public static boolean enableBurnt;
    public static boolean enableEnder;
    public static boolean enableFlesh;
    public static boolean enableIce;
    public static boolean enableLeather;
    public static boolean enableSlime;
    public static boolean enableSnow;
    public static boolean enableCrying;
    public static boolean enablePorked;

    public static boolean enableCeramicMug;

    public static boolean enderBalance;
    public static boolean cryingBalance;

    /**
     * Creates, customizes, and saves the configuration file.
     * @param event The FMLPreInitializationEvent that this is called during.
     */
    public static void load(FMLPreInitializationEvent event) {
        Configuration config = new Configuration(event.getSuggestedConfigurationFile());
        config.load();

        //Block Family
        config.addCustomCategoryComment("Block Family", "Disabling these disables everything in that family. For further tweaking, use MineTweaker.");
        enableBlaze = config.get("Block Family", "Toggle the Blaze family", true).getBoolean();
        enableBone = config.get("Block Family", "Toggle the Bone family", true).getBoolean();
        enableBurnt = config.get("Block Family", "Toggle the Burnt family", true).getBoolean();
        enableEnder = config.get("Block Family", "Toggle the Ender family", true).getBoolean();
        enableFlesh = config.get("Block Family", "Toggle the Flesh family", true).getBoolean();
        enableIce = config.get("Block Family", "Toggle the Ice family", true).getBoolean();
        enableLeather = config.get("Block Family", "Toggle the Leather family", true).getBoolean();
        enableSlime = config.get("Block Family", "Toggle the Slime family", true).getBoolean();
        enableSnow = config.get("Block Family", "Toggle the Snow family", true).getBoolean();
        enableCrying = config.get("Block Family", "Toggle the Crying family", true).getBoolean();
        enablePorked = config.get("Block Family", "Toggle the Porked family", true).getBoolean();

        //Other
        config.addCustomCategoryComment("Other blocks", "Toggling blocks that are not considered to be in any block family.");
        enableCeramicMug = config.get("Other blocks", "Toggle the Ceramic Mug", true).getBoolean();

        //Tweaking
        config.addCustomCategoryComment("Tweaking", "Things that can be used to tweak stuff, without enabling/disabling them.");
        enderBalance = config.get("Tweaking", "Ender blocks output 8 like everything else. This should be used in packs where ender pearls are abundant, or farmable.", false).getBoolean(false);
        cryingBalance = config.get("Tweaking", "Crying blocks output 8 like everything else. This should be used in packs where ghast tears are abundant, or farmable.", false).getBoolean(false);

        config.save();
    }
}
