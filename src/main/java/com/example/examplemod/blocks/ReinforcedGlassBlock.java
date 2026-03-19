package com.example.examplemod.blocks;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;

public class ReinforcedGlassBlock extends Block {
    public ReinforcedGlassBlock(Properties properties) {
        super(properties);
    }


    @Override
    public PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.NORMAL;
    }
}