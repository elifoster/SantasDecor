package io.github.elifoster.santasdecor.blocks.mug;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import org.lwjgl.opengl.GL11;

public class TileEntityMugRenderer extends TileEntitySpecialRenderer<TileEntityMug> {
    private static final ResourceLocation DIRT_TEXTURE = new ResourceLocation("minecraft:blocks/dirt");

    @Override
    public void renderTileEntityAt(TileEntityMug te, double x, double y, double z, float partialTicks, int destroyStage) {
        Minecraft mc = Minecraft.getMinecraft();

        GlStateManager.enableLighting();
        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5, y + 0.5, z + 0.5);
        GlStateManager.scale(1, 1, 1);

        // TODO: Fluid

        bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        TextureMap blocksMap = mc.getTextureMapBlocks();
        TextureAtlasSprite textureToRenderFlat = null;
        float height = 0;
        if (te.hasDirt()) {
            textureToRenderFlat = blocksMap.getTextureExtry(DIRT_TEXTURE.toString());
            height = 6F / 16F;
        } else if (te.hasFluid()) {
            FluidStack fluid = te.getTank().getFluid();
            assert fluid != null;
            textureToRenderFlat = blocksMap.getTextureExtry(fluid.getFluid().getStill(fluid).toString());
            if (te.getRenderFrame() % 2 == 0) {
                textureToRenderFlat.updateAnimation();
            }
            height = 4.5F / 16F;
        }

        if (textureToRenderFlat != null) {
            GlStateManager.disableBlend();
            GlStateManager.rotate(180F, 1F, 0F, 0F);
            GlStateManager.translate(-3.75F / 16F, height, -3.75F / 16F);
            renderFlatTexture(textureToRenderFlat);
            GlStateManager.translate(3.75F / 16F, -height, 3.75F / 16F);
            GlStateManager.rotate(180F, 1, 0, 0);
            GlStateManager.enableBlend();

            EnumFacing facing = te.getFacing();
            float angle = 0F;
            switch (facing) {
                case SOUTH: {
                    angle = 180F;
                    break;
                }
                case EAST: {
                    angle = 90F;
                    break;
                }
                case WEST: {
                    angle = 270F;
                    break;
                }
                default:
                    break;
            }
            GlStateManager.rotate(angle, 0, 1, 0);
            GlStateManager.scale(0.75D, 0.75D, 0.75D);
            ItemStack flower = te.getFlower();
            if (flower != null) {
                mc.getRenderItem().renderItem(flower, ItemCameraTransforms.TransformType.NONE);
            }
        }

        GlStateManager.popMatrix();

        te.incrementRenderFrame();
    }

    private void renderFlatTexture(TextureAtlasSprite texture) {
        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer buffer = tessellator.getBuffer();
        float maxU = texture.getMaxU();
        float maxV = texture.getMaxV();
        float minV = texture.getMinV();
        float minU = texture.getMinU();

        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        buffer.pos(0.125D, 0, 0.125D).tex(maxU, maxV).endVertex();
        buffer.pos(0.35D, 0, 0.125D).tex(minU, maxV).endVertex();
        buffer.pos(0.35D, 0, 0.35D).tex(minU, minV).endVertex();
        buffer.pos(0.125D, 0, 0.35D).tex(maxU, minV).endVertex();
        tessellator.draw();
    }
}
