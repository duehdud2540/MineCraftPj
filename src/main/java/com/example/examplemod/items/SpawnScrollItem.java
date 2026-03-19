package com.example.examplemod.items;

import com.example.examplemod.ExampleMod;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.List;

public class SpawnScrollItem extends Item {
    public SpawnScrollItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        List<GlobalPos> flags = player.getData(ExampleMod.FLAG_LIST);
        if (flags.isEmpty()) {
            if (level.isClientSide) {
                player.sendSystemMessage(Component.literal("§c[오류] §f설치된 깃발이 없습니다!"));
            }
            return InteractionResultHolder.fail(stack);
        }
        if (level.isClientSide) {
            openScrollGui();
        }
        if (!level.isClientSide) {
            if (!player.getAbilities().instabuild) {
            }
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    @OnlyIn(Dist.CLIENT)
    private void openScrollGui() {
        net.minecraft.client.Minecraft.getInstance().setScreen(new com.example.examplemod.FlagTeleportScreen());
    }
}