package com.example.examplemod.items;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class  ReturnScrollItem extends Item {
    public ReturnScrollItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);

        if (!level.isClientSide) {
            if (player instanceof ServerPlayer serverPlayer) {
                ServerLevel overworld = serverPlayer.getServer().getLevel(Level.OVERWORLD);
                if (overworld != null) {
                    BlockPos targetPos = new BlockPos(104, 70, -236);

                    serverPlayer.teleportTo(overworld,
                            targetPos.getX() + 0.5, targetPos.getY(), targetPos.getZ() + 0.5,
                            serverPlayer.getYRot(), serverPlayer.getXRot());

                    overworld.playSound(null, targetPos.getX(), targetPos.getY(), targetPos.getZ(),
                            SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0F, 1.0F);

                    player.displayClientMessage(Component.literal("§a상점으로 귀환했습니다!"), true);

                    if (!player.getAbilities().instabuild) {
                        itemstack.shrink(1);
                    }
                }
            }
        }

        return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
    }
}