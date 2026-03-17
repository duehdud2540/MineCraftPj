package com.example.examplemod;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.PacketDistributor;

// [필수] 서버가 이 클래스 자체를 로드하지 않도록 차단!
@OnlyIn(Dist.CLIENT)
public class FlagNameScreen extends Screen {
    private final BlockPos pos;
    private EditBox nameInput;

    public FlagNameScreen(BlockPos pos) {
        super(Component.literal("깃발 이름 설정"));
        this.pos = pos;
    }

    @Override
    protected void init() {
        // 입력창 생성
        this.nameInput = new EditBox(this.font, this.width / 2 - 100, this.height / 2 - 10, 200, 20, Component.literal("이름 입력"));
        this.nameInput.setMaxLength(10);
        this.addRenderableWidget(this.nameInput);

        // 완료 버튼
        this.addRenderableWidget(Button.builder(Component.literal("완료"), (btn) -> {
            PacketDistributor.sendToServer(new ChangeFlagNamePacket(pos, this.nameInput.getValue()));
            this.onClose();
        }).bounds(this.width / 2 - 100, this.height / 2 + 20, 200, 20).build());

        // 입력창에 포커스 주기 (바로 타이핑 가능하게)
        this.setInitialFocus(this.nameInput);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics, mouseX, mouseY, partialTick);
        graphics.drawCenteredString(this.font, "변경할 깃발 이름을 입력하세요", this.width / 2, this.height / 2 - 40, 0xFFFFFF);
        super.render(graphics, mouseX, mouseY, partialTick);
    }
}