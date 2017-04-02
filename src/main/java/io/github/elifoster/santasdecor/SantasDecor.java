package io.github.elifoster.santasdecor;

import io.github.elifoster.santasdecor.blocks.BlockHandler;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;

import static io.github.elifoster.santasdecor.SantasDecor.MODID;

@Mod(
  modid = MODID,
  name = "Santa's Decor",
  version = "2.0.0"
)
public class SantasDecor {
    public static final String MODID = "santasdecor";

    public static final CreativeTabs CREATIVE_TAB = new SantasDecorTab("Santa's Decor");

    @Mod.EventHandler
    void onPreInitializationPhase(FMLPreInitializationEvent event) {
        Config.load(event);
        BlockHandler.registerBlocks();
        if (event.getSide() == Side.CLIENT) {
            BlockHandler.registerModels();
        }
    }

    @Mod.EventHandler
    void onInitializationPhase(FMLInitializationEvent event) {
        BlockHandler.registerRecipes();
        if (event.getSide() == Side.CLIENT) {
            BlockHandler.registerTESRs();
        }
    }
}
