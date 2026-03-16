package com.example.examplemod;

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
                new ItemCost(Items.COAL_BLOCK, 10),
                new ItemStack(ExampleMod.COIN_1.get(), 1),
                99999, 0, 0.00f // 최대 거래 횟수, 경험치, 가격 변동 계수 //석탄
        ));
        this.getOffers().add(new MerchantOffer(
            new ItemCost(Items.COPPER_BLOCK, 5),
                new ItemStack(ExampleMod.COIN_1.get(), 1),
                99999, 0, 0.00f // 최대 거래 횟수, 경험치, 가격 변동 계수 // 구블
        ));
        this.getOffers().add(new MerchantOffer(
                new ItemCost(Items.NETHERITE_INGOT, 1),
                new ItemStack(ExampleMod.COIN_5.get(), 1),
                99999, 0, 0.00f // 최대 거래 횟수, 경험치, 가격 변동 계수 // 네더라이트
        ));

        //판매


        this.getOffers().add(new MerchantOffer(
                new ItemCost(ExampleMod.COIN_5.get(), 1),
                new ItemStack(Items.DIAMOND, 1),
                99999, 0, 0.00f // 최대 거래 횟수, 경험치, 가격 변동 계수 다이아 -> 5원
        ));
        this.getOffers().add(new MerchantOffer(
                new ItemCost(ExampleMod.COIN_1.get(), 1),
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
                new ItemCost(ExampleMod.COIN_50.get(), 1),
                new ItemStack(Items.NETHERITE_INGOT, 1),
                99999, 0, 0.00f // 최대 거래 횟수, 경험치, 가격 변동 계수 // 구블
        ));



        // 여기까지


    }
}