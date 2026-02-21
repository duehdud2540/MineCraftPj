package com.example.examplemod.item;

import com.example.examplemod.ExampleMod;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class MerchantNPCSpawnEggItem extends Item {

    public MerchantNPCSpawnEggItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        if (!(level instanceof ServerLevel serverLevel)) {
            return InteractionResult.PASS;
        }
        BlockPos pos = context.getClickedPos().above();
        BlockState state = level.getBlockState(context.getClickedPos());
        if (!state.getCollisionShape(level, context.getClickedPos()).isEmpty()) {
            pos = pos.above();
        }
        ExampleMod.MERCHANT_NPC.get().spawn(serverLevel, pos, EntitySpawnReason.SPAWN_ITEM_USE);
        if (context.getPlayer() != null && !context.getPlayer().getAbilities().instabuild) {
            context.getItemInHand().shrink(1);
        }
        return InteractionResult.CONSUME;
    }
}
