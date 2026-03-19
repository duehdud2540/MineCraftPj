package com.example.examplemod;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn; // 필수 임포트
import net.neoforged.neoforge.network.PacketDistributor;
import java.util.List;

// [핵심] 서버야, 여기는 화면 그리는 곳이니까 아예 쳐다보지도 마!
@OnlyIn(Dist.CLIENT)
public class FlagTeleportScreen extends Screen {
    public FlagTeleportScreen() {
        super(Component.literal("깃발 이동"));
    }

    @Override
    protected void init() {
        if (this.minecraft == null || this.minecraft.player == null) return;
        Player player = this.minecraft.player;

        List<GlobalPos> flags = player.getData(ExampleMod.FLAG_LIST);
        List<String> names = player.getData(ExampleMod.FLAG_NAMES);

        for (int i = 0; i < flags.size(); i++) {
            GlobalPos pos = flags.get(i);
            String flagName = (i < names.size()) ? names.get(i) : pos.pos().toShortString();
            String buttonText = (i + 1) + "번 지점: " + flagName;
            int index = i;
            int x = (this.width / 2) + (index % 2 == 0 ? -105 : 5);
            int y = (this.height / 2) + (index < 2 ? -40 : 5);

            this.addRenderableWidget(Button.builder(
                            Component.literal(buttonText),
                            (btn) -> {
                                teleportToFlag(index);
                            })
                    .bounds(x, y, 100, 35)
                    .build());
        }

        this.addRenderableWidget(Button.builder(Component.literal("창 닫기"), (btn) -> this.onClose())
                .bounds(this.width / 2 - 50, this.height / 2 + 50, 100, 20).build());
    }

    private void teleportToFlag(int index) {
        if (this.minecraft == null || this.minecraft.player == null) return;
        List<GlobalPos> flags = this.minecraft.player.getData(ExampleMod.FLAG_LIST);
        if (index >= 0 && index < flags.size()) {
            GlobalPos targetPos = flags.get(index);
            PacketDistributor.sendToServer(new TeleportPacket(targetPos));
            this.onClose();
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        int flagIndex = -1;
        if (keyCode >= 49 && keyCode <= 52) flagIndex = keyCode - 49;
        else if (keyCode >= 321 && keyCode <= 324) flagIndex = keyCode - 321;

        if (flagIndex != -1) {
            teleportToFlag(flagIndex);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics, mouseX, mouseY, partialTick); // 배경 렌더링 추가
        graphics.drawCenteredString(this.font, "§l[ 깃발 텔레포트 ]", this.width / 2, this.height / 2 - 70, 0xFFFFFF);
        graphics.drawCenteredString(this.font, "§7(숫자키 1~4를 눌러 바로 이동 가능)", this.width / 2, this.height / 2 - 58, 0xAAAAAA);
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}