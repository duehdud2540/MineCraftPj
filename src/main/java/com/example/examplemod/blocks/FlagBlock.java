package com.example.examplemod.blocks;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.FlagNameScreen;
import com.example.examplemod.SyncFlagsPacket; // 패킷 클래스 위치에 맞춰 수정
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class FlagBlock extends Block {

    public FlagBlock(Properties properties) {
        super(properties.noOcclusion());
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        if (!level.isClientSide && placer instanceof Player player) {
            // 가변 리스트로 복사
            List<BlockPos> currentFlags = new ArrayList<>(player.getData(ExampleMod.FLAG_LIST));
            List<String> currentNames = new ArrayList<>(player.getData(ExampleMod.FLAG_NAMES));

            if (currentFlags.size() >= 4) {
                player.sendSystemMessage(Component.literal("§c[오류] §f깃발은 인당 최대 4개까지만 설치할 수 있습니다!"));
                level.destroyBlock(pos, true);
                return;
            }

            currentFlags.add(pos);
            player.setData(ExampleMod.FLAG_LIST, currentFlags);
            player.setData(ExampleMod.FLAG_NAMES, currentNames);
            PacketDistributor.sendToPlayer((ServerPlayer) player, new SyncFlagsPacket(currentFlags, currentNames));

            player.sendSystemMessage(Component.literal("§e[깃발] §f설치 완료! (현재: " + currentFlags.size() + "/4)"));
        }
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.isClientSide) {
            openFlagScreen(pos);
        }
        return InteractionResult.SUCCESS;
    }

    @net.neoforged.api.distmarker.OnlyIn(net.neoforged.api.distmarker.Dist.CLIENT)
    private void openFlagScreen(BlockPos pos) {
        net.minecraft.client.Minecraft.getInstance().setScreen(new com.example.examplemod.FlagNameScreen(pos));
    }
    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            if (!level.isClientSide) {
                ServerPlayer player = (ServerPlayer) level.getNearestPlayer(pos.getX(), pos.getY(), pos.getZ(), 16, false);

                if (player != null) {
                    List<BlockPos> currentFlags = new ArrayList<>(player.getData(ExampleMod.FLAG_LIST));
                    List<String> currentNames = new ArrayList<>(player.getData(ExampleMod.FLAG_NAMES));

                    if (currentFlags.contains(pos)) {
                        int index = currentFlags.indexOf(pos);
                        currentFlags.remove(index);

                        if (index < currentNames.size()) {
                            currentNames.remove(index);
                        }

                        // [핵심] 서버 데이터 갱신
                        player.setData(ExampleMod.FLAG_LIST, currentFlags);
                        player.setData(ExampleMod.FLAG_NAMES, currentNames);
                        PacketDistributor.sendToPlayer(player, new SyncFlagsPacket(currentFlags, currentNames));

                        player.sendSystemMessage(Component.literal("§c[깃발] §f제거 완료 (남은 개수: " + currentFlags.size() + "/4)"));
                    }
                }
            }
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }
    private int getFlagIndex(Player player, BlockPos pos) {
        return player.getData(ExampleMod.FLAG_LIST).indexOf(pos);
    }
}