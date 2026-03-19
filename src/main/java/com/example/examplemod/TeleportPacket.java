package com.example.examplemod;

import net.minecraft.core.GlobalPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record TeleportPacket(GlobalPos targetGlobalPos) implements CustomPacketPayload {

    public static final Type<TeleportPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(ExampleMod.MODID, "teleport_packet"));

    public static final StreamCodec<RegistryFriendlyByteBuf, TeleportPacket> STREAM_CODEC = StreamCodec.composite(
            GlobalPos.STREAM_CODEC, TeleportPacket::targetGlobalPos,
            TeleportPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}