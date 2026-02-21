package com.example.examplemod.entity;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class MerchantNPC extends AbstractVillager {

    public MerchantNPC(EntityType<? extends AbstractVillager> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void updateTrades(ServerLevel level) {
        this.fillTrades();
    }

    protected void fillTrades() {
        MerchantOffers merchantoffers = this.getOffers();
        merchantoffers.clear();

        merchantoffers.add(new MerchantOffer(
                new ItemCost(Items.EMERALD, 1),
                new ItemStack(Items.DIAMOND, 1),
                12, 2, 0.05f));

        merchantoffers.add(new MerchantOffer(
                new ItemCost(Items.DIAMOND, 3),
                new ItemStack(Items.EMERALD, 5),
                8, 3, 0.05f));
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (this.isAlive() && !this.isTrading() && !this.isBaby()) {
            if (hand == InteractionHand.MAIN_HAND) {
                if (!this.level().isClientSide()) {
                    this.updateTrades((ServerLevel) this.level());
                    this.setTradingPlayer(player);
                    this.openTradingScreen(player, this.getDisplayName(), 1);
                }
                return InteractionResult.SUCCESS;
            }
        }
        return super.mobInteract(player, hand);
    }

    @Override
    protected void rewardTradeXp(MerchantOffer offer) {
        // 이 부분이 추가되어야 에러가 사라집니다!
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob other) {
        return null;
    }
}