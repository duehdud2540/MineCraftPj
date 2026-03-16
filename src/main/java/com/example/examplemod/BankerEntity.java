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

public class BankerEntity extends PathfinderMob {

    public static net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder createAttributes() {
        return net.minecraft.world.entity.Mob.createMobAttributes()
                .add(net.minecraft.world.entity.ai.attributes.Attributes.MAX_HEALTH, 20.0D) // 체력 20 (하트 10개)
                .add(net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED, 0.5D); // 이동 속도
    }
    public BankerEntity(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
    }

    // 2. 상호작용: 플레이어가 은행원을 우클릭했을 때 실행되는 핵심 코드입니다.
    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        // 서버 측에서만 실행 (클라이언트는 화면만 띄우면 됨)
        if (!this.level().isClientSide && player instanceof ServerPlayer serverPlayer) {

            // [핵심 기능] 플레이어에게 BankerMenu(3x3 창구) 화면을 열어줍니다!
            serverPlayer.openMenu(new SimpleMenuProvider(
                    (id, inventory, p) -> new BankerMenu(id, inventory),
                    Component.literal("은행 창구") // UI 상단에 뜰 제목
            ));

            return InteractionResult.SUCCESS;
        }

        // 클라이언트 측에서는 팔 흔드는 모션 등 시각적 효과만 처리
        return InteractionResult.sidedSuccess(this.level().isClientSide);
    }

    public BankerEntity(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
       //불에타지않음
        this.fireImmune();
    }

    // 모든데미지 무력화
    @Override
    public boolean hurt(DamageSource source, float amount) {
        // 어떤 데미지가 들어와도 false를 반환하여 데미지를 입지 않습니다.
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
}