package com.example.examplemod;

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

public class ReturnScrollItem extends Item {
    public ReturnScrollItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);

        // 서버 측에서만 실행
        if (!level.isClientSide) {
            if (player instanceof ServerPlayer serverPlayer) {
                ServerLevel serverLevel = (ServerLevel) level;
                BlockPos targetPos = new BlockPos(-67, 71 , 7);

                //플레이어 텔레포트
                serverPlayer.teleportTo(serverLevel,
                        targetPos.getX() + 0.5, targetPos.getY(), targetPos.getZ() + 0.5,
                        serverPlayer.getYRot(), serverPlayer.getXRot());

                //효과음 재생 (엔더맨 텔레포트 소리)
                level.playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0F, 1.0F);

                //메시지 띄우기
                player.displayClientMessage(Component.literal("마을로 귀환했습니다!"), true);

                //아이템 소모 (서바이벌 모드일 때만)
                if (!player.getAbilities().instabuild) {
                    itemstack.shrink(1);
                }
            }
        }

        return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
    }
}