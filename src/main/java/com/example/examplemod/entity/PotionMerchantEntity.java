package com.example.examplemod.entity;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffects;
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
import com.example.examplemod.ExampleMod;

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
            // 비교를 위해 원본 아이템의 이름을 가져옴
            String targetName = result.getHoverName().getString();

            // 1. 버프 부여 로직
            int addedDuration = 6000;
            net.minecraft.core.Holder<net.minecraft.world.effect.MobEffect> effectType = null;

            if (targetName.contains("야간 투시")) effectType = MobEffects.NIGHT_VISION;
            else if (targetName.contains("수중 호흡")) effectType = MobEffects.WATER_BREATHING;
            else if (targetName.contains("재생")) effectType = MobEffects.REGENERATION;
            else if (targetName.contains("신속")) effectType = MobEffects.MOVEMENT_SPEED;
            else if (targetName.contains("화염 저항")) effectType = MobEffects.FIRE_RESISTANCE;

            if (effectType != null) {
                net.minecraft.world.effect.MobEffectInstance current = player.getEffect(effectType);
                int newDuration = (current != null) ? current.getDuration() + addedDuration : addedDuration;
                player.addEffect(new net.minecraft.world.effect.MobEffectInstance(effectType, newDuration, 0));
                player.playSound(SoundEvents.PLAYER_LEVELUP, 1.0F, 1.2F);
            }

            // 커서에 잡힌 아이템 삭제
            player.containerMenu.setCarried(ItemStack.EMPTY);

            // 인벤토리 전체를 검사해서 쉬프트 클릭으로 들어간 아이템 삭제
            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                ItemStack stack = player.getInventory().getItem(i);
                // 이름이 똑같은 아이템이 인벤토리에 있다면 삭제 (우리 상점 전용 포션만 지우기 위해)
                if (!stack.isEmpty() && stack.getHoverName().getString().equals(targetName)) {
                    stack.setCount(0);
                }
            }

            // 서버 사이드 동기화
            if (player instanceof ServerPlayer sPlayer) {
                sPlayer.containerMenu.broadcastFullState();
                // 슬롯 업데이트 패킷을 보내서 클라이언트 화면에서도 확실히 지움
                sPlayer.connection.send(new ClientboundContainerSetSlotPacket(0, -1, -1, ItemStack.EMPTY));
                sPlayer.inventoryMenu.resumeRemoteUpdates();
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

        //화염저항
        ItemStack fire_resistance = new ItemStack(net.minecraft.world.item.Items.POTION);
        fire_resistance.set(net.minecraft.core.component.DataComponents.CUSTOM_NAME, net.minecraft.network.chat.Component.literal("§b즉시 화염 저항 부여 (5분)"));
        offers.add(new MerchantOffer( new ItemCost(ExampleMod.COIN_5.get(), 1),fire_resistance,maxUses, 0, 0.00f));

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