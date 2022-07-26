package com.timo.firstmod.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.timo.firstmod.FirstMod;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class LightningBlockScreen extends AbstractContainerScreen<LightningBlockMenu> {
	private static final ResourceLocation TEXTURE = new ResourceLocation(FirstMod.MODID, "textures/gui/lightning_block_gui.png");

	//MENU RENDERING
	
	public LightningBlockScreen(LightningBlockMenu menu, Inventory inv, Component component) {
		super(menu, inv, component);
	}

	@Override
    protected void renderBg(PoseStack pPoseStack, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        blit(pPoseStack, x, y, 0, 0, imageWidth, imageHeight);
        
        if(menu.isCrafting()) {
            blit(pPoseStack, x + 73, y + 35, 176, 0, menu.getScaledProgress(), 18);    //startx, starty (where to render),    startx, starty (where is what to render),     lengthx, lengthy (how much to render)
        }
    }

    @Override
    public void render(PoseStack pPoseStack, int mouseX, int mouseY, float delta) {
        renderBackground(pPoseStack);
        super.render(pPoseStack, mouseX, mouseY, delta);
        renderTooltip(pPoseStack, mouseX, mouseY);
    }
}
