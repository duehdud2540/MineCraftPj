package com.example.examplemod;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;

public class BankerMenu extends AbstractContainerMenu {

    // NPC가 가지고 있을 3x3 입금용 가방 (데이터 보관용)
    public final SimpleContainer depositContainer;

    public BankerMenu(int containerId, Inventory playerInventory) {
        super(ExampleMod.BANKER_MENU.get(), containerId);

        // 9칸짜리 빈 가방 생성
        this.depositContainer = new SimpleContainer(9);

        // 은행원의 3x3 입금 슬롯 배치
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 3; ++col) {
                this.addSlot(new Slot(this.depositContainer, col + row * 3, 62 + col * 18, 17 + row * 18));
            }
        }

        // 플레이어의 인벤토리 27칸 배치
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }

        // 플레이어의 핫바 9칸 배치
        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
        }
    }

    // Shift+클릭으로 아이템 빠르게 옮기는 기능
    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    // 플레이어가 이 메뉴를 열 자격이 있는지 확인
    @Override
    public boolean stillValid(Player player) {
        return this.depositContainer.stillValid(player);
    }

    //플레이어가 실수로 창을 닫았을 때, 올려둔 동전을 바닥에 떨어뜨려주는 안전장치
    @Override
    public void removed(Player player) {
        super.removed(player);
        this.clearContainer(player, this.depositContainer);
    }
}