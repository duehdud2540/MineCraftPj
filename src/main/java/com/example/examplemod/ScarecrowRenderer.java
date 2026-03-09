package com.example.examplemod;

import net.minecraft.client.model.VillagerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

// 0.0.1에서 썼던 주민 모델로 다시 복구!
public class ScarecrowRenderer extends MobRenderer<ScarecrowMerchant, VillagerModel<ScarecrowMerchant>> {

    // 마인크래프트 기본 주민 텍스처 경로입니다. (나중에 텍스처만 바꿔도 느낌 확 살아요!)
    private static final ResourceLocation VILLAGER_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/villager/villager.png");

    public ScarecrowRenderer(EntityRendererProvider.Context context) {
        // 주민 모델(VillagerModel)과 그 뼈대(ModelLayers.VILLAGER)를 사용합니다.
        super(context, new VillagerModel<>(context.bakeLayer(ModelLayers.VILLAGER)), 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(ScarecrowMerchant entity) {
        return VILLAGER_LOCATION;
    }
}