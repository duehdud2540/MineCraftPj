package com.example.examplemod;
import com.example.examplemod.ExampleMod;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.ArrayList;
import java.util.List;

public record SyncFlagsPacket(List<BlockPos> flags, List<String> names) implements CustomPacketPayload {
    public static final Type<SyncFlagsPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(ExampleMod.MODID, "sync_flags"));

    // 1.21 네오포지용 스트림 코덱 (좌표 리스트와 이름 리스트를 순서대로 읽고 씀)
    public static final StreamCodec<RegistryFriendlyByteBuf, SyncFlagsPacket> STREAM_CODEC = StreamCodec.ofMember(
            (payload, buffer) -> {
                // 1. 좌표 리스트 전송
                buffer.writeCollection(payload.flags, (buf, pos) -> buf.writeBlockPos(pos));
                // 2. 이름 리스트 전송 (문자열)
                buffer.writeCollection(payload.names, (buf, name) -> buf.writeUtf(name));
            },
            buffer -> new SyncFlagsPacket(
                    buffer.readCollection(ArrayList::new, buf -> buf.readBlockPos()), // 1. 좌표 읽기
                    buffer.readCollection(ArrayList::new, buf -> buf.readUtf())       // 2. 이름 읽기
            )
    );

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }
    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            // 클라이언트 사이드 플레이어의 데이터를 서버에서 받은 리스트로 덮어쓰기!
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null) {
                mc.player.setData(ExampleMod.FLAG_LIST, this.flags);
                mc.player.setData(ExampleMod.FLAG_NAMES, this.names);
                // ExampleMod.LOGGER.info("클라이언트 깃발 데이터 동기화 완료!"); // 디버그용
            }
        });
    }
}