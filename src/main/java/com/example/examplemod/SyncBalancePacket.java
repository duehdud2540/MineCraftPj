package com.example.examplemod; // 패키지는 적절히 설정

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record SyncBalancePacket(int balance) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SyncBalancePacket> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(ExampleMod.MODID, "sync_balance"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncBalancePacket> STREAM_CODEC = StreamCodec.ofMember(
            SyncBalancePacket::write,
            SyncBalancePacket::new
    );

    public SyncBalancePacket(RegistryFriendlyByteBuf buffer) {
        this(buffer.readInt());
    }

    public void write(RegistryFriendlyByteBuf buffer) {
        buffer.writeInt(balance);
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
