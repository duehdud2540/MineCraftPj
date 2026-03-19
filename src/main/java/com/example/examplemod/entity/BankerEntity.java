package com.example.examplemod.entity;

import com.example.examplemod.BankerMenu;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import com.example.examplemod.ExampleMod;

public class BankerEntity extends PathfinderMob {

    public static net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder createAttributes() {
        return net.minecraft.world.entity.Mob.createMobAttributes()
                .add(net.minecraft.world.entity.ai.attributes.Attributes.MAX_HEALTH, 20.0D) // 체력 20 (하트 10개)
                .add(net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED, 0.5D); // 이동 속도
    }

    public BankerEntity(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
        this.fireImmune();
    }
    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new net.minecraft.world.entity.ai.goal.LookAtPlayerGoal(this, Player.class, 2.0F));
    }
    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (!this.level().isClientSide && player instanceof ServerPlayer serverPlayer) {

            serverPlayer.openMenu(new SimpleMenuProvider(
                    (id, inventory, p) -> new BankerMenu(id, inventory),
                    Component.literal("은행 창구") // UI 상단에 뜰 제목
            ));
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.sidedSuccess(this.level().isClientSide);
    }
    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.is(net.minecraft.world.damagesource.DamageTypes.FELL_OUT_OF_WORLD)) {
            return true;
        }
        if (source.getEntity() instanceof Player player && player.isCreative()) {
            return true;
        }
        return false;
    }



    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean canBeSeenAsEnemy() {
        return false;
    }


    @Override
    public boolean isPickable() {
        return true; // 우클릭 상호작용을 위해 true 유지
    }
    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        // 플레이어와의 거리가 아무리 멀어도 절대 삭제하지 않음
        return false;
    }

    @Override
    public boolean requiresCustomPersistence() {
        return true;
    }
}