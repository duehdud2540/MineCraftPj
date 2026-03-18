package com.example.examplemod.entity;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.blocks.FlagBlock;
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
import net.minecraft.world.damagesource.DamageSource;
import com.example.examplemod.ExampleMod;

public class ScarecrowMerchant extends AbstractVillager {

    public ScarecrowMerchant(EntityType<? extends AbstractVillager> entityType, Level level) {
        super(entityType, level);
        this.fireImmune();
        //불에안탐
    }

    @Override
    protected void registerGoals() {
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (this.isAlive() && this.getTradingPlayer() == null) {

            // 1. 거래 시작하는 '끄덕' 소리 재생
            if (hand == InteractionHand.MAIN_HAND) {
                this.playSound(SoundEvents.VILLAGER_TRADE, 1.0F, 1.0F);
            }


            if (!this.level().isClientSide) {
                this.setTradingPlayer(player);
                this.openTradingScreen(player, this.getDisplayName(), 1); // 상점 UI 띄우기
            }

            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }

        return super.mobInteract(player, hand);
    }


    @Override
    protected void rewardTradeXp(MerchantOffer offer) {

    }
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return null;
    }
    @Override
    protected void updateTrades() {
        MerchantOffers offers = this.getOffers();


        //거래 품목 코드
        this.getOffers().add(new MerchantOffer(
                new ItemCost(Items.DIAMOND, 1), // 입금 1
                new ItemStack(ExampleMod.COIN_1.get(), 1), // 출금
                99999, 0, 0.00f // 최대 거래 횟수, 경험치, 가격 변동 계수 다이아 5개 1원
        ));
        this.getOffers().add(new MerchantOffer(
                new ItemCost(Items.IRON_INGOT, 10), // 입금 1
                new ItemStack(ExampleMod.COIN_1.get(), 1), // 출금
                99999, 0, 0.00f // 최대 거래 횟수, 경험치, 가격 변동 계수 // 철 1원
        ));
        this.getOffers().add(new MerchantOffer(
                new ItemCost(Items.GOLD_INGOT, 10), // 입금 1
                new ItemStack(ExampleMod.COIN_1.get(), 1), // 출금
                99999, 0, 0.00f // 최대 거래 횟수, 경험치, 가격 변동 계수 //골드
        ));
        this.getOffers().add(new MerchantOffer(
                new ItemCost(Items.COAL_BLOCK, 4),
                new ItemStack(ExampleMod.COIN_1.get(), 1),
                99999, 0, 0.00f // 최대 거래 횟수, 경험치, 가격 변동 계수 //석탄 블럭
        ));
        this.getOffers().add(new MerchantOffer(
            new ItemCost(Items.COPPER_BLOCK, 5),
                new ItemStack(ExampleMod.COIN_1.get(), 1),
                99999, 0, 0.00f // 최대 거래 횟수, 경험치, 가격 변동 계수 // 구블
        ));
        this.getOffers().add(new MerchantOffer(
                new ItemCost(Items.NETHERITE_INGOT, 1),
                new ItemStack(ExampleMod.COIN_50.get(), 1),
                99999, 0, 0.00f // 최대 거래 횟수, 경험치, 가격 변동 계수 // 네더라이트
        ));



        //판매
      /*  this.getOffers().add(new MerchantOffer(
                new ItemCost(ExampleMod.FLAG_BLOCK, 1),
                new ItemStack(ExampleMod.COIN_50.get(), 1),
                99999, 0, 0.00f // 최대 거래 횟수, 경험치, 가격 변동 계수 // 네더라이트
        ));
        this.getOffers().add(new MerchantOffer(
                new ItemCost(ExampleMod.SPAWN_SCROLL.get(), 1),
                new ItemStack(ExampleMod.COIN_5.get(), 1),
                99999, 0, 0.00f // 최대 거래 횟수, 경험치, 가격 변동 계수 // 네더라이트
        ));*/
        this.getOffers().add(new MerchantOffer(
                new ItemCost(ExampleMod.COIN_10.get(), 3),
                new ItemStack(ExampleMod.RETURN_SCROLL, 1),
                99999, 0, 0.00f // 최대 거래 횟수, 경험치, 가격 변동 계수 // 네더라이트
        ));
        this.getOffers().add(new MerchantOffer(
                new ItemCost(ExampleMod.COIN_5.get(), 1),
                new ItemStack(Items.DIAMOND, 1),
                99999, 0, 0.00f // 최대 거래 횟수, 경험치, 가격 변동 계수 다이아 -> 5원
        ));
        this.getOffers().add(new MerchantOffer(
                new ItemCost(ExampleMod.COIN_10.get(), 1),
                new ItemStack(Items.IRON_INGOT, 1),
                99999, 0, 0.00f // 최대 거래 횟수, 경험치, 가격 변동 계수 철
        ));
        this.getOffers().add(new MerchantOffer(
                new ItemCost(ExampleMod.COIN_1.get(), 1),
                new ItemStack(Items.GOLD_INGOT, 1),
                99999, 0, 0.00f // 최대 거래 횟수, 경험치, 가격 변동 계수 // 금
        ));
        this.getOffers().add(new MerchantOffer(
                new ItemCost(ExampleMod.COIN_1.get(), 1),
                new ItemStack(Items.COAL_BLOCK, 1),
                99999, 0, 0.00f // 최대 거래 횟수, 경험치, 가격 변동 계수 //석탄
        ));
        this.getOffers().add(new MerchantOffer(
                new ItemCost(ExampleMod.COIN_1.get(), 1),
                new ItemStack(Items.COPPER_BLOCK, 1),
                99999, 0, 0.00f // 최대 거래 횟수, 경험치, 가격 변동 계수 // 구블
        ));
        this.getOffers().add(new MerchantOffer(
                new ItemCost(ExampleMod.COIN_100.get(), 1),
                new ItemStack(Items.NETHERITE_INGOT, 1),
                99999, 0, 0.00f // 최대 거래 횟수, 경험치, 가격 변동 계수 // 구블
        ));



}

        // 여기까지

        public boolean removeWhenFarAway(double distanceToClosestPlayer) {
            // 플레이어와의 거리가 아무리 멀어도 절대 삭제하지 않음
            return false;
        }

        // 이름표없어도 사라지지않음
        @Override
        public boolean requiresCustomPersistence() {
            return true;
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


    }