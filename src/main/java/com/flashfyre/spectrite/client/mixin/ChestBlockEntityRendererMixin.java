package com.flashfyre.spectrite.client.mixin;

import com.flashfyre.spectrite.Spectrite;
import com.flashfyre.spectrite.block.entity.SpectriteChestBlockEntity;
import com.flashfyre.spectrite.client.util.SpectriteEntityRenderUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.block.entity.ChestBlockEntityRenderer;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Environment(EnvType.CLIENT)
@Mixin(ChestBlockEntityRenderer.class)
public class ChestBlockEntityRendererMixin
{
    @ModifyVariable(method = "render(Lnet/minecraft/block/entity/BlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II)V",
            at = @At(value = "STORE", target = "Lnet/minecraft/client/render/TexturedRenderLayers;getChestTexture(Lnet/minecraft/block/entity/BlockEntity;Lnet/minecraft/block/enums/ChestType;Z)Lnet/minecraft/client/util/SpriteIdentifier;"))
    private SpriteIdentifier spectrite$modifyVariableRenderGetChestTexture(SpriteIdentifier spriteIdentifier, BlockEntity entity)
    {
        if (entity instanceof SpectriteChestBlockEntity)
        {
            final String baseTexturePath = spriteIdentifier.getTextureId().getPath();
            final String variantSuffix = baseTexturePath.substring(baseTexturePath.lastIndexOf('/') + 1).replace("normal", "");
            SpectriteEntityRenderUtils.CURRENT_RENDERING_SPECTRITE_COMPATIBLE_ENTITY_ID = new Identifier("chest" + variantSuffix);
            SpectriteEntityRenderUtils.getOrGenerateSpectriteEntityTexture(null,
                    new Identifier(spriteIdentifier.getTextureId().getNamespace(), "textures/" + spriteIdentifier.getTextureId().getPath() + ".png"), null,
                    "textures/entity/chest/spectrite" + variantSuffix + ".png");
            return new SpriteIdentifier(spriteIdentifier.getAtlasId(), Spectrite.getId("entity/chest/spectrite" + variantSuffix));
        }
        return spriteIdentifier;
    }
}
