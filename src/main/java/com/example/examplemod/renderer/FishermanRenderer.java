package com.example.examplemod.renderer;

import com.example.examplemod.entity.FishermanMerchant;
import net.minecraft.client.model.VillagerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.MultiBufferSource;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FishermanRenderer extends MobRenderer<FishermanMerchant, VillagerModel<FishermanMerchant>> {


    private static final ResourceLocation VILLAGER_BASE = ResourceLocation.withDefaultNamespace("textures/entity/villager/villager.png");

    private static final ResourceLocation FISHERMAN_CLOTHES = ResourceLocation.withDefaultNamespace("textures/entity/villager/profession/fisherman.png");

    public FishermanRenderer(EntityRendererProvider.Context context) {
        super(context, new VillagerModel<>(context.bakeLayer(ModelLayers.VILLAGER)), 0.5f);


        this.addLayer(new RenderLayer<FishermanMerchant, VillagerModel<FishermanMerchant>>(this) {
            @Override
            public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, FishermanMerchant entity, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
                // 몸체 모델 위에 어부 옷 텍스처를 겹쳐서 그림
                renderColoredCutoutModel(this.getParentModel(), FISHERMAN_CLOTHES, poseStack, buffer, packedLight, entity, -1);
            }
        });
    }

    @Override
    public ResourceLocation getTextureLocation(FishermanMerchant entity) {
        // 기본 바탕은 주민 피부색 텍스처를 반환
        return VILLAGER_BASE;
    }
}