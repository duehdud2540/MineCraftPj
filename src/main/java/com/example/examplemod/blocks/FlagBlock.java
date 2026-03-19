package com.example.examplemod.blocks;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.FlagNameScreen;
import com.example.examplemod.SyncFlagsPacket; // 패킷 클래스 위치에 맞춰 수정
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
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
import org.openjdk.nashorn.internal.objects.Global;

import java.util.ArrayList;
import java.util.List;

public class FlagBlock extends Block {

    public FlagBlock(Properties properties) {
        super(properties.noOcclusion());
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        if (!level.isClientSide && placer instanceof Player player) {
            // GlobalPos로 현재 위치와 차원을 묶어서 생성
            GlobalPos newFlagPos = GlobalPos.of(level.dimension(), pos);

            List<GlobalPos> currentFlags = new ArrayList<>(player.getData(ExampleMod.FLAG_LIST));
            List<String> currentNames = new ArrayList<>(player.getData(ExampleMod.FLAG_NAMES));

            if (currentFlags.size() >= 4) {
                player.sendSystemMessage(Component.literal("§c[오류] §f깃발은 인당 4개까지만 가능합니다!"));
                level.destroyBlock(pos, true);
                return;
            }

            currentFlags.add(newFlagPos); // 이제 좌표+차원이 같이 들어감!
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
                // 서버의 모든 플레이어를 확인
                for (ServerPlayer player : level.getServer().getPlayerList().getPlayers()) {
                    List<GlobalPos> currentFlags = new ArrayList<>(player.getData(ExampleMod.FLAG_LIST));
                    List<String> currentNames = new ArrayList<>(player.getData(ExampleMod.FLAG_NAMES));

                    //리스트를 돌면서 '좌표'와 '차원'이 모두 일치하는 인덱스를 찾음
                    int index = -1;
                    for (int i = 0; i < currentFlags.size(); i++) {
                        GlobalPos gpos = currentFlags.get(i);
                        // 부서진 위치(pos)와 차원(level.dimension())이 저장된 데이터와 일치하는지 확인
                        if (gpos.pos().equals(pos) && gpos.dimension().equals(level.dimension())) {
                            index = i;
                            break;
                        }
                    }

                    //일치하는 깃발을 찾았다면 삭제 프로세스 진행
                    if (index != -1) {
                        currentFlags.remove(index);
                        if (index < currentNames.size()) {
                            currentNames.remove(index);
                        }

                        // 서버 데이터 갱신
                        player.setData(ExampleMod.FLAG_LIST, currentFlags);
                        player.setData(ExampleMod.FLAG_NAMES, currentNames);

                        // 클라이언트에게 동기화 패킷 전송
                        PacketDistributor.sendToPlayer(player, new SyncFlagsPacket(currentFlags, currentNames));

                        // 주인에게 알림
                        player.sendSystemMessage(Component.literal("§c[알림] §f설치했던 깃발이 파괴되었습니다! (남은 개수: " + currentFlags.size() + "/4)"));

                        // 주인을 찾았으니 루프 종료
                        break;
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