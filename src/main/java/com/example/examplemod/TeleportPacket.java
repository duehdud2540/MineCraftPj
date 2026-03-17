package com.example.examplemod;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record TeleportPacket(BlockPos targetPos) implements CustomPacketPayload {
    public static final Type<TeleportPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(ExampleMod.MODID, "teleport_packet"));

    public static final StreamCodec<RegistryFriendlyByteBuf, TeleportPacket> STREAM_CODEC = StreamCodec.ofMember(
            (payload, buffer) -> buffer.writeBlockPos(payload.targetPos()),
            buffer -> new TeleportPacket(buffer.readBlockPos())
    );

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }
}