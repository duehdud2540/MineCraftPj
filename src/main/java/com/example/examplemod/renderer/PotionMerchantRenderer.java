package com.example.examplemod;

import net.minecraft.client.model.WitchModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class PotionMerchantRenderer extends MobRenderer<PotionMerchantEntity, WitchModel<PotionMerchantEntity>> {

    private static final ResourceLocation WITCH_LOCATION =
            ResourceLocation.withDefaultNamespace("textures/entity/witch.png");

    public PotionMerchantRenderer(EntityRendererProvider.Context context) {
        super(context, new WitchModel<>(context.bakeLayer(ModelLayers.WITCH)), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(PotionMerchantEntity entity) {
        return WITCH_LOCATION;
    }
}