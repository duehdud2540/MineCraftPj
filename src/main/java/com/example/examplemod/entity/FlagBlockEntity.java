package com.example.examplemod.entity;

import com.example.examplemod.ExampleMod;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class FlagBlockEntity extends BlockEntity {
    // 깃발마다 고유한 이름을 가질 수 있도록 변수 선언
    private String flagName = "기본 깃발";

    public FlagBlockEntity(BlockPos pos, BlockState state) {
        super(ExampleMod.FLAG_BLOCK_ENTITY.get(), pos, state);
    }

    public void setFlagName(String name) {
        this.flagName = name;
        this.setChanged(); // 데이터가 바뀌었음을 서버에 알림
    }

    public String getFlagName() {
        return this.flagName;
    }
}