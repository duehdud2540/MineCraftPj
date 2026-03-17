package com.example.examplemod;

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
        // 8블록 이내의 플레이어를 쳐다봄
        this.goalSelector.addGoal(0, new net.minecraft.world.entity.ai.goal.LookAtPlayerGoal(this, Player.class, 2.0F));
    }
    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        // 서버 측에서만 실행
        if (!this.level().isClientSide && player instanceof ServerPlayer serverPlayer) {

            serverPlayer.openMenu(new SimpleMenuProvider(
                    (id, inventory, p) -> new BankerMenu(id, inventory),
                    Component.literal("은행 창구") // UI 상단에 뜰 제목
            ));
            return InteractionResult.SUCCESS;
        }
        // 클라이언트 측에서는 팔 흔드는 모션 등 시각적 효과만 처리
        return InteractionResult.sidedSuccess(this.level().isClientSide);
    }

    // 모든데미지 무력화
    @Override
    public boolean hurt(DamageSource source, float amount) {
        // 절대 데미지나(kill) 공허같은 데미지는 무조건 들어오게만들었음
        if (source.is(net.minecraft.world.damagesource.DamageTypes.FELL_OUT_OF_WORLD)) {
            return true;
        }

        // 공격자가 플레이어이고 크리에이티브 모드인 경우 죽일 수 있게 허용함
        if (source.getEntity() instanceof Player player && player.isCreative()) {
            return true;
        }
        return false;
    }


    // 밀려나지않음
    @Override
    public boolean isPushable() {
        return false;
    }

    // 몬스터 인식불가
    @Override
    public boolean canBeSeenAsEnemy() {
        return false;
    }

    // 유체화
    @Override
    public boolean isPickable() {
        return true; // 우클릭 상호작용을 위해 true 유지
    }
    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        // 플레이어와의 거리가 아무리 멀어도 절대 삭제하지 않음
        return false;
    }

    // 이름표없어도 사라지지않음
    @Override
    public boolean requiresCustomPersistence() {
        return true;
    }
}