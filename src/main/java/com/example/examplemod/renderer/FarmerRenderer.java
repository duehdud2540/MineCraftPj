package com.example.examplemod.renderer;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.entity.FarmerMerchant;
import net.minecraft.client.model.VillagerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FarmerRenderer extends MobRenderer<FarmerMerchant, VillagerModel<FarmerMerchant>> {
    //몸
    private static final ResourceLocation VILLAGER_BASE = ResourceLocation.withDefaultNamespace("textures/entity/villager/villager.png");
    //옷
    private static final ResourceLocation FARMER_CLOTHES = ResourceLocation.withDefaultNamespace("textures/entity/villager/profession/farmer.png");

    public FarmerRenderer(EntityRendererProvider.Context context) {
        super(context, new VillagerModel<>(context.bakeLayer(ModelLayers.VILLAGER)), 0.5f);

        this.addLayer(new RenderLayer<FarmerMerchant, VillagerModel<FarmerMerchant>>(this) {
            @Override
            public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, FarmerMerchant entity, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {

                renderColoredCutoutModel(this.getParentModel(), FARMER_CLOTHES, poseStack, buffer, packedLight, entity, -1);
            }
        });
    }

    @Override
    public ResourceLocation getTextureLocation(FarmerMerchant entity) {
        return VILLAGER_BASE;
    }
}