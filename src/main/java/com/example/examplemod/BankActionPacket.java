package com.example.examplemod;
import com.example.examplemod.ExampleMod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record BankActionPacket(int action, int amount) implements CustomPacketPayload {


    public static final CustomPacketPayload.Type<BankActionPacket> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(ExampleMod.MODID, "bank_action"));

    public static final StreamCodec<RegistryFriendlyByteBuf, BankActionPacket> STREAM_CODEC = StreamCodec.ofMember(
            BankActionPacket::write,
            BankActionPacket::new
    );

    public BankActionPacket(RegistryFriendlyByteBuf buffer) {
        this(buffer.readInt(), buffer.readInt());
    }

    public void write(RegistryFriendlyByteBuf buffer) {
        buffer.writeInt(action);
        buffer.writeInt(amount);
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}