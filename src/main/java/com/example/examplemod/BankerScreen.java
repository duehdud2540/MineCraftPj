package com.example.examplemod;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.PacketDistributor;
import net.minecraft.world.item.ItemStack;


public class BankerScreen extends AbstractContainerScreen<BankerMenu> {

    private static final ResourceLocation TEXTURE = ResourceLocation.withDefaultNamespace("textures/gui/container/dispenser.png");

    public BankerScreen(BankerMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void init() {
        super.init();

        int buttonX = this.leftPos + 180;

        // 1. 전액 입금 버튼
        this.addRenderableWidget(Button.builder(Component.literal("전액 입금"), button -> {
            PacketDistributor.sendToServer(new ExampleMod.BankActionPacket(0, 0));
        }).bounds(buttonX, this.topPos + 15, 65, 20).build());

        // 2. 100원 출금 버튼
        this.addRenderableWidget(Button.builder(Component.literal("100원 출금"), button -> {
            PacketDistributor.sendToServer(new ExampleMod.BankActionPacket(1, 100));
        }).bounds(buttonX, this.topPos + 40, 65, 20).build());

        // 3. 50원 출금 버튼
        this.addRenderableWidget(Button.builder(Component.literal("50원 출금"), button -> {
            PacketDistributor.sendToServer(new ExampleMod.BankActionPacket(1, 50));
        }).bounds(buttonX, this.topPos + 65, 65, 20).build());
        // 4. 500원 출금 버튼
        this.addRenderableWidget(Button.builder(Component.literal("500원 출금"), button -> {
            PacketDistributor.sendToServer(new ExampleMod.BankActionPacket(1, 500));
        }).bounds(buttonX, this.topPos + 90, 65, 20).build());
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics, mouseX, mouseY, partialTick); // 어두운 뒷배경 깔기
        super.render(graphics, mouseX, mouseY, partialTick);
        String balanceText = ExampleMod.ClientGameEvents.clientBalance + " G";

        // 1. 박스 크기 계산 (인벤토리랑 똑같은 너비)
        int boxWidth = 20 + this.font.width(balanceText) + 10;
        int boxHeight = 24;

        // 2. 위치 설정 (은행 창구 배경의 오른쪽 위 바깥에 띄우고 싶다면 아래 좌표 사용)
        int startX = this.leftPos + this.imageWidth - boxWidth;
        int startY = this.topPos - 30; // 창구 바로 위쪽에 띄우기

        // 3. 반투명 배경 박스 그리기
        graphics.fill(startX, startY, startX + boxWidth, startY + boxHeight, 0x80000000);

        // 4. 테두리 그리기
        int borderColor = 0xFF555555;
        graphics.fill(startX, startY, startX + boxWidth, startY + 1, borderColor); // 위
        graphics.fill(startX, startY + boxHeight - 1, startX + boxWidth, startY + boxHeight, borderColor); // 아래
        graphics.fill(startX, startY, startX + 1, startY + boxHeight, borderColor); // 왼쪽
        graphics.fill(startX + boxWidth - 1, startY, startX + boxWidth, startY + boxHeight, borderColor); // 오른쪽

        // 5. 동전 아이콘 그리기
        graphics.renderFakeItem(new ItemStack(ExampleMod.COIN_100.get()), startX + 4, startY + 4);

        // 6. 황금색 텍스트 그리기
        graphics.drawString(this.font, balanceText, startX + 24, startY + 8, 0xFFD700, true);

        this.renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        // 화면 정중앙 좌표 계산
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        // 발사기 텍스처 그리기
        graphics.blit(TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        // UI 위에 글씨 쓰기 (기본 은행 제목)
        graphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 4210752, false);
        // 내 인벤토리 제목
        graphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 4210752, false);
    }
}