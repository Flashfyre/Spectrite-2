package com.flashfyre.spectrite.client.render.entity;

import com.flashfyre.spectrite.entity.EntityTypes;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.minecraft.client.render.entity.IronGolemEntityRenderer;

public class EntityRenderers
{
    public static void initEntityRenderers()
    {
        EntityRendererRegistry.register(EntityTypes.SPECTRITE_GOLEM, IronGolemEntityRenderer::new);
        EntityRendererRegistry.register(EntityTypes.SPECTRITE_BOMB, FlyingItemEntityRenderer::new);
    }
}
