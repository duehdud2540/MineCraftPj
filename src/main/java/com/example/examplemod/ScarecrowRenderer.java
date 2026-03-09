package com.example.examplemod; // 본인의 패키지 이름인지 확인!

import net.minecraft.client.model.VillagerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class ScarecrowRenderer extends MobRenderer<ScarecrowMerchant, VillagerModel<ScarecrowMerchant>> {

    // 주민 텍스처 파일 경로 (1.21 버전 이상이라면 ResourceLocation.parse(...) 를 쓰세요!)
    private static final ResourceLocation TEXTURE = ResourceLocation.parse("minecraft:textures/entity/villager/villager.png");
    // 만약 위 줄에서 parse에 빨간 줄이 뜬다면 (1.20 버전), 아래 줄로 바꿔주세요:
    // private static final ResourceLocation TEXTURE = new ResourceLocation("minecraft", "textures/entity/villager/villager.png");

    public ScarecrowRenderer(EntityRendererProvider.Context context) {
        // 주민의 3D 뼈대(Model)를 빌려옵니다. 그림자 크기는 0.5f
        super(context, new VillagerModel<>(context.bakeLayer(ModelLayers.VILLAGER)), 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(ScarecrowMerchant entity) {
        return TEXTURE;
    }
}