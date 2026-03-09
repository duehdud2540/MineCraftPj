package com.example.examplemod; // 본인의 패키지 이름 그대로 두세요!

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.ItemCost; // <- 최신 버전용 추가!
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.sounds.SoundEvents;

public class ScarecrowMerchant extends AbstractVillager {

    public ScarecrowMerchant(EntityType<? extends AbstractVillager> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void registerGoals() {
        // 텅~ (움직이지 않음)
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (this.isAlive() && this.getTradingPlayer() == null) {

            // 1. 거래 시작하는 '끄덕' 소리 재생 (진짜 상인 느낌 나게!)
            if (hand == InteractionHand.MAIN_HAND) {
                this.playSound(SoundEvents.VILLAGER_TRADE, 1.0F, 1.0F);
            }

            // 2. 🌟 핵심: 거래창은 반드시 '서버(Server)' 쪽에서 열어야 합니다.
            // (만약 isClientSide 부분에 빨간 줄이 뜨면 isClientSide() 처럼 뒤에 괄호를 붙여주세요!)
            if (!this.level().isClientSide) {
                this.setTradingPlayer(player); // "이 플레이어랑 거래할게!" 라고 지정
                this.openTradingScreen(player, this.getDisplayName(), 1); // 상점 UI 띄우기! (레벨 1)
            }

            // "내가 우클릭 이벤트를 성공적으로 처리했어!" 라고 게임에 보고함
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }

        return super.mobInteract(player, hand);
    }

    // 🌟 최신 버전 필수 추가 사항: 경험치 보상 로직
    @Override
    protected void rewardTradeXp(MerchantOffer offer) {
        // 허수아비 상인이므로 경험치를 안 받습니다. 비워둡니다!
    }
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return null;
    }
    @Override
    protected void updateTrades() {
        MerchantOffers offers = this.getOffers();

        // 🌟 최신 버전 문법: 플레이어가 내는 물건은 ItemCost로 적어야 합니다!
        offers.add(new MerchantOffer(
                new ItemCost(Items.DIRT, 1), // 살 때 내는 물건 (흙 1개)
                new ItemStack(Items.DIAMOND, 1), // 받을 물건 (다이아몬드 1개)
                100, 0, 0.05f
        ));
    }
}