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
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;

public class PotionMerchantEntity extends AbstractVillager {

    public PotionMerchantEntity(EntityType<? extends AbstractVillager> entityType, Level level) {
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
    public void rewardTradeXp(MerchantOffer offer) {
        if (this.getTradingPlayer() instanceof Player player) {
            ItemStack result = offer.getResult();
            String name = result.getHoverName().getString();

            // 1. 버프 부여 로직
            int addedDuration = 6000;
            net.minecraft.core.Holder<net.minecraft.world.effect.MobEffect> effectType = null;

            if (name.contains("야간 투시")) effectType = net.minecraft.world.effect.MobEffects.NIGHT_VISION;
            else if (name.contains("수중 호흡")) effectType = net.minecraft.world.effect.MobEffects.WATER_BREATHING;
            else if (name.contains("재생")) effectType = net.minecraft.world.effect.MobEffects.REGENERATION;
            else if (name.contains("신속")) effectType = net.minecraft.world.effect.MobEffects.MOVEMENT_SPEED;

            if (effectType != null) {
                net.minecraft.world.effect.MobEffectInstance current = player.getEffect(effectType);
                int newDuration = (current != null) ? current.getDuration() + addedDuration : addedDuration;
                player.addEffect(new net.minecraft.world.effect.MobEffectInstance(effectType, newDuration, 0));
                player.playSound(SoundEvents.PLAYER_LEVELUP, 1.0F, 1.2F);
            }

            // 2. 아이템 삭제 및 잔상 제거 (여기가 핵심!)
            player.getInventory().removeItem(result);
            player.containerMenu.setCarried(ItemStack.EMPTY);

            // sPlayer 선언은 여기서 딱 한 번만!
            if (player instanceof ServerPlayer sPlayer) {
                // 아까 에러 났던 sendAllContents 대신 broadcastFullState 사용
                sPlayer.containerMenu.broadcastFullState();

                // 커서 아이템 강제 삭제 패킷 전송
                sPlayer.connection.send(new ClientboundContainerSetSlotPacket(0, -1, -1, ItemStack.EMPTY));
            }
        }
    }
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return null;
    }

    // 여기까지
    @Override
    protected void updateTrades() {
        MerchantOffers offers = this.getOffers();
        int maxUses = 99999;

        //신속
        ItemStack speedDisplay = new ItemStack(net.minecraft.world.item.Items.POTION);
        speedDisplay.set(net.minecraft.core.component.DataComponents.CUSTOM_NAME, net.minecraft.network.chat.Component.literal("§b즉시 신속 부여 (5분)"));
        offers.add(new MerchantOffer( new ItemCost(ExampleMod.COIN_5.get(), 1),speedDisplay,maxUses, 0, 0.00f));
        //야간투시
        ItemStack nightVision = new ItemStack(Items.POTION);
        nightVision.set(net.minecraft.core.component.DataComponents.CUSTOM_NAME, Component.literal("§9즉시 야간 투시 부여 (5분)"));
        offers.add(new MerchantOffer(new ItemCost(ExampleMod.COIN_5.get(), 1), nightVision, maxUses, 0, 0.0f));

        //수중 호흡
        ItemStack waterBreathing = new ItemStack(Items.POTION);
        waterBreathing.set(net.minecraft.core.component.DataComponents.CUSTOM_NAME, Component.literal("§3즉시 수중 호흡 부여 (5분)"));
        offers.add(new MerchantOffer(new ItemCost(ExampleMod.COIN_5.get(), 1), waterBreathing, maxUses, 0, 0.0f));

        //재생
        ItemStack regen = new ItemStack(Items.POTION);
        regen.set(net.minecraft.core.component.DataComponents.CUSTOM_NAME, Component.literal("§d즉시 재생 부여 (5분)"));
        offers.add(new MerchantOffer(new ItemCost(ExampleMod.COIN_5.get(), 1), regen, maxUses, 0, 0.0f));

    }




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