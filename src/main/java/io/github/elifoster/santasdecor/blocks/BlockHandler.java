package io.github.elifoster.santasdecor.blocks;

import io.github.elifoster.santasdecor.Config;
import io.github.elifoster.santasdecor.blocks.mug.BlockMug;
import io.github.elifoster.santasdecor.blocks.mug.TileEntityMug;
import io.github.elifoster.santasdecor.blocks.mug.TileEntityMugRenderer;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static io.github.elifoster.santasdecor.SantasDecor.MODID;

public class BlockHandler {
    public static Block BRICK;
    public static Block CHISELED;
    public static Block COBBLESTONE;
    public static Block PAVER;
    public static Block PLANK;
    public static Block QUARTZ;
    public static Block CHISELED_QUARTZ;
    public static Block STONE;
    public static Block GLASS;

    public static Block MUG;

    public static final Map<Family, Block> logs = new HashMap<>();
    public static final Map<Family, Block> quartzPillars = new HashMap<>();

    private static Block setup(Block base, String name, Function<Block, ItemBlock> itemBlockFunction) {
        base.setUnlocalizedName(MODID + ":" + name);
        base.setRegistryName(MODID, name);
        Block registered = GameRegistry.register(base);
        ItemBlock itemBlock = itemBlockFunction.apply(registered);
        itemBlock.setRegistryName(MODID, name);
        GameRegistry.register(itemBlock);
        return registered;
    }

    private static Block setup(Block base, String name) {
        return setup(base, name, ItemBlockFamily::new);
    }

    /**
     * Registers and initializes all blocks.
     */
    // TODO: Register blocks in the OreDict.
    public static void registerBlocks() {
        BRICK = setup(new BlockDecorStandard(), "brick");
        CHISELED = setup(new BlockDecorStandard(), "chiseled");
        COBBLESTONE = setup(new BlockDecorStandard(), "cobblestone");
        PAVER = setup(new BlockDecorStandard(), "paver");
        PLANK = setup(new BlockDecorStandard(), "plank");
        QUARTZ = setup(new BlockDecorStandard(true), "quartz");
        CHISELED_QUARTZ = setup(new BlockDecorStandard(true), "chiseled_quartz");
        STONE = setup(new BlockDecorStandard(), "stone");
        GLASS = setup(new BlockGlass(), "glass");

        if (Config.enableCeramicMug) {
            MUG = setup(new BlockMug(), "mug", ItemBlock::new);
            GameRegistry.registerTileEntity(TileEntityMug.class, MODID + ":mug");
        }

        for (Family family : Family.LOOKUP) {
            if (family.isEnabled()) {
                Block log = setup(new BlockDecorRotatable(Material.WOOD), "log_" + family.getName(), ItemBlock::new);
                logs.put(family, log);
                if (family.canHaveQuartz()) {
                    Block quartz = setup(new BlockDecorRotatable(Material.ROCK), "pillar_" + family.getName(), ItemBlock::new);
                    quartzPillars.put(family, quartz);
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public static void registerModels() {
        registerModel((BlockDecorStandard) BRICK);
        registerModel((BlockDecorStandard) CHISELED);
        registerModel((BlockDecorStandard) COBBLESTONE);
        registerModel((BlockDecorStandard) PAVER);
        registerModel((BlockDecorStandard) PLANK);
        registerModel((BlockDecorStandard) STONE);
        registerModel((BlockDecorStandard) QUARTZ);
        registerModel((BlockDecorStandard) CHISELED_QUARTZ);

        registerModel(Item.getItemFromBlock(GLASS), BlockGlass.FAMILY);
        for (Block log : logs.values()) {
            ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(log), 0, new ModelResourceLocation(log.getRegistryName(), "inventory"));
        }
        for (Block log : quartzPillars.values()) {
            ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(log), 0, new ModelResourceLocation(log.getRegistryName(), "inventory"));
        }
        if (Config.enableCeramicMug) {
            ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(MUG), 0, new ModelResourceLocation(MUG.getRegistryName(), "inventory"));
        }
    }

    @SideOnly(Side.CLIENT)
    public static void registerTESRs() {
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityMug.class, new TileEntityMugRenderer());
    }

    private static void registerModel(BlockDecorStandard block) {
        registerModel(Item.getItemFromBlock(block), block.family);
    }

    private static void registerModel(Item item, PropertyEnum<Family> familyEnum) {
        for (Family family : familyEnum.getAllowedValues()) {
            ModelLoader.setCustomModelResourceLocation(item, family.ordinal(), new ModelResourceLocation(item.getRegistryName(), familyEnum.getName() + "=" + family.getName()));
        }
    }

    /**
     * Registers and initializes all block and item recipes.
     */
    public static void registerRecipes() {
        for (Family family : Family.LOOKUP) {
            if (family.isEnabled()) {
                addAllRecipesForFamily(family);
            }
        }

        if (Config.enableCeramicMug) {
            GameRegistry.addRecipe(new ShapedOreRecipe(MUG,
              "X X",
              " X ",
              'X', Blocks.HARDENED_CLAY));
        }
    }

    // TODO: OreDict
    // TODO: Nicer OO.
    private static void addAllRecipesForFamily(Family family) {
        int meta = family.ordinal();
        Object resource = family.getCraftingResource();
        int balancedOutput = family.getBalancedOutput();
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BRICK, 4, meta),
          "XX",
          "XX",
          'X', new ItemStack(STONE, 1, meta)));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(CHISELED, 4, meta),
          "XX",
          "XX",
          'X', new ItemStack(BRICK, 1, family.ordinal())));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(COBBLESTONE, balancedOutput, meta),
          "XXX",
          "XZX",
          "XXX",
          'X', Blocks.COBBLESTONE,
          'Z', resource));
        GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(PAVER, 1, meta), new ItemStack(BRICK, 1, meta)));
        GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(PLANK, 4, meta), new ItemStack(logs.get(family))));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(QUARTZ, balancedOutput, meta),
          "XXX",
          "XZX",
          "XXX",
          'X', Blocks.QUARTZ_BLOCK,
          'Z', resource));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(CHISELED_QUARTZ, 4, meta),
          "XX",
          "XX",
          'X', new ItemStack(QUARTZ, 1, meta)));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(STONE, balancedOutput, meta),
          "XXX",
          "XZX",
          "XXX",
          'X', Blocks.STONE,
          'Z', resource));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(GLASS, 1, meta),
          "XXX",
          "XZX",
          "XXX",
          'X', Blocks.GLASS,
          'Z', resource));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(logs.get(family), balancedOutput),
          "XXX",
          "XZX",
          "XXX",
          'X', Blocks.LOG,
          'Z', resource));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(quartzPillars.get(family), 2),
          "X",
          "X",
          'X', new ItemStack(QUARTZ, 1, meta)));
    }
}
