package com.example.examplemod.renderer;

import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import com.example.examplemod.entity.BankerEntity;
import com.example.examplemod.ExampleMod;


public class BankerRenderer extends MobRenderer<BankerEntity, PlayerModel<BankerEntity>> {

    private static final ResourceLocation BANKER_LOCATION =
            ResourceLocation.fromNamespaceAndPath(ExampleMod.MODID, "textures/entity/banker.png");

    public BankerRenderer(EntityRendererProvider.Context context) {
        super(context, new PlayerModel<>(context.bakeLayer(ModelLayers.PLAYER), false), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(BankerEntity entity) {
        return BANKER_LOCATION;
    }
}