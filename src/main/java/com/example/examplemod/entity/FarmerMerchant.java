package com.example.examplemod.entity;

import com.example.examplemod.ExampleMod;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.trading.ItemCost; // <- 최신 버전용 추가!
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import com.example.examplemod.ExampleMod;

public class FarmerMerchant extends AbstractVillager {

    public FarmerMerchant(EntityType<? extends AbstractVillager> entityType, Level level) {
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
        Ingredient allWools = Ingredient.of(ItemTags.WOOL);
                this.getOffers().add(new MerchantOffer(
                        new ItemCost(allWools.getItems()[0].getItem(), 20), // 입금 1
                        new ItemStack(ExampleMod.COIN_1.get(), 1), // 출금
                        99999, 0, 0.00f // 최대 거래 횟수, 경험치, 가격 변동 계수 다이아 5개 1원
                ));
        this.getOffers().add(new MerchantOffer(
                new ItemCost(Items.COOKED_BEEF, 16), // 입금 1
                new ItemStack(ExampleMod.COIN_1.get(), 1), // 출금
                99999, 0, 0.00f // 최대 거래 횟수, 경험치, 가격 변동 계수 다이아 5개 1원
        ));
        this.getOffers().add(new MerchantOffer(
                new ItemCost(Items.COOKED_CHICKEN, 16), // 입금 1
                new ItemStack(ExampleMod.COIN_1.get(), 1), // 출금
                99999, 0, 0.00f // 최대 거래 횟수, 경험치, 가격 변동 계수 다이아 5개 1원
        ));

        this.getOffers().add(new MerchantOffer(
                new ItemCost(Items.COOKED_PORKCHOP, 16), // 입금 1
                new ItemStack(ExampleMod.COIN_1.get(), 1), // 출금
                99999, 0, 0.00f // 최대 거래 횟수, 경험치, 가격 변동 계수 다이아 5개 1원
        ));
        this.getOffers().add(new MerchantOffer(
                new ItemCost(Items.COOKED_MUTTON, 20), // 입금 1
                new ItemStack(ExampleMod.COIN_1.get(), 1), // 출금
                99999, 0, 0.00f // 최대 거래 횟수, 경험치, 가격 변동 계수 다이아 5개 1원
        ));
        this.getOffers().add(new MerchantOffer(
                new ItemCost(Items.HAY_BLOCK, 4), // 입금 1
                new ItemStack(ExampleMod.COIN_1.get(), 1), // 출금
                99999, 0, 0.00f // 최대 거래 횟수, 경험치, 가격 변동 계수 다이아 5개 1원
        ));
        this.getOffers().add(new MerchantOffer(
                new ItemCost(Items.BEETROOT, 32), // 입금 1
                new ItemStack(ExampleMod.COIN_1.get(), 1), // 출금
                99999, 0, 0.00f // 최대 거래 횟수, 경험치, 가격 변동 계수 다이아 5개 1원
        ));
        this.getOffers().add(new MerchantOffer(
                new ItemCost(Items.MELON, 16), // 입금 1
                new ItemStack(ExampleMod.COIN_1.get(), 1), // 출금
                99999, 0, 0.00f // 최대 거래 횟수, 경험치, 가격 변동 계수 다이아 5개 1원
        ));
        this.getOffers().add(new MerchantOffer(
                new ItemCost(Items.PUMPKIN_PIE, 4), // 입금 1
                new ItemStack(ExampleMod.COIN_1.get(), 1), // 출금
                99999, 0, 0.00f // 최대 거래 횟수, 경험치, 가격 변동 계수 다이아 5개 1원
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