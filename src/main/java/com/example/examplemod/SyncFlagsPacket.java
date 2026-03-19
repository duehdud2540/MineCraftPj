package com.example.examplemod;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.core.GlobalPos; // BlockPos 대신 GlobalPos 임포트
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.ArrayList;
import java.util.List;

//레코드 파라미터를 List<GlobalPos>로 변경
public record SyncFlagsPacket(List<GlobalPos> flags, List<String> names) implements CustomPacketPayload {
    public static final Type<SyncFlagsPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(ExampleMod.MODID, "sync_flags"));

    // 스트림 코덱 수정 (GlobalPos.STREAM_CODEC 사용)
    public static final StreamCodec<RegistryFriendlyByteBuf, SyncFlagsPacket> STREAM_CODEC = StreamCodec.composite(
            GlobalPos.STREAM_CODEC.apply(ByteBufCodecs.list()), SyncFlagsPacket::flags,
            ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list()), SyncFlagsPacket::names,
            SyncFlagsPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null) {
                // 3. 클라이언트 데이터 시트에 GlobalPos 리스트 저장
                mc.player.setData(ExampleMod.FLAG_LIST, this.flags);
                mc.player.setData(ExampleMod.FLAG_NAMES, this.names);
            }
        });
    }
}