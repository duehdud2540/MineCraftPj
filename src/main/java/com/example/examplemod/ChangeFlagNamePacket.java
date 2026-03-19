package com.example.examplemod;

import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.ArrayList;
import java.util.List;

public record ChangeFlagNamePacket(BlockPos pos, String newName) implements CustomPacketPayload {
    public static final Type<ChangeFlagNamePacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(ExampleMod.MODID, "change_flag_name"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ChangeFlagNamePacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, ChangeFlagNamePacket::pos,
            ByteBufCodecs.STRING_UTF8, ChangeFlagNamePacket::newName,
            ChangeFlagNamePacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

    // 서버에서 실행될 로직
    public static void handle(ChangeFlagNamePacket payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            List<GlobalPos> flags = player.getData(ExampleMod.FLAG_LIST);
            List<String> names = new ArrayList<>(player.getData(ExampleMod.FLAG_NAMES));

            int index = flags.indexOf(payload.pos());
            if (index != -1) {
                // 해당 인덱스의 이름을 새 이름으로 교체!
                while (names.size() <= index) names.add("지점 " + (names.size() + 1));
                names.set(index, payload.newName());

                player.setData(ExampleMod.FLAG_NAMES, names);
                // 다시 클라이언트들에게 동기화 전송 (아까 만든 SyncFlagsPacket 활용)
                PacketDistributor.sendToPlayer((ServerPlayer) player, new SyncFlagsPacket(flags, names));
                player.sendSystemMessage(Component.literal("§a[성공] §f깃발 이름이 '" + payload.newName() + "'(으)로 변경되었습니다."));
            }
        });
    }
}