package com.example.examplemod;

import net.minecraft.world.item.*;
import org.slf4j.Logger;
//내가 추가함
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.flag.FeatureFlags;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.attachment.AttachmentType;
import com.mojang.serialization.Codec;
import java.util.function.Supplier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.client.renderer.entity.VillagerRenderer;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.fml.common.Mod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.neoforged.neoforge.client.event.ScreenEvent; // 만약 NeoForge라면 이 줄을 쓰세요
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;

import net.minecraft.commands.Commands;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.minecraft.commands.arguments.EntityArgument;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.PacketDistributor;

//여기까지
import com.mojang.logging.LogUtils;

import net.minecraft.world.item.BlockItem;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraft.client.Minecraft;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(ExampleMod.MODID)
public class ExampleMod {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "mc_merchant";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();
    // Create a Deferred Register to hold Blocks which will all be registered under the "examplemod" namespace
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    // Create a Deferred Register to hold Items which will all be registered under the "examplemod" namespace
    //아랫부분 변경됨
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    public static final DeferredHolder<Item, Item> RETURN_SCROLL = ITEMS.register("return_scroll",
            () -> new ReturnScrollItem(new Item.Properties().stacksTo(16))); // 최대 16개까지 겹칠 수 있음
    public static final DeferredHolder<Item, Item> COIN_1 = ITEMS.register("coin_1", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> COIN_5 = ITEMS.register("coin_5", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> COIN_10 = ITEMS.register("coin_10", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> COIN_50 = ITEMS.register("coin_50", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> COIN_100 = ITEMS.register("coin_100", () -> new Item(new Item.Properties()));
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> COIN_TAB = CREATIVE_MODE_TABS.register("coin_tab", () -> CreativeModeTab.builder().title(Component.literal("동전")).icon(() -> new ItemStack(COIN_100.get())) // 아이콘은 100원
            .displayItems((parameters, output) -> {
                output.accept(COIN_10.get());
                output.accept(COIN_50.get());
                output.accept(COIN_100.get());
                output.accept(COIN_5.get());
                output.accept(COIN_1.get());
            }).build());
    public static final Supplier<AttachmentType<Integer>> BANK_BALANCE = ATTACHMENT_TYPES.register(
            "bank_balance",
            () -> AttachmentType.builder(() -> 0) // 기본값: 처음 접속하면 0원
                    .serialize(Codec.INT)         // 저장 방식: 정수(Integer) 형태로 월드 파일에 영구 저장
                    .copyOnDeath()                // 옵션: 플레이어가 죽어서 리스폰해도 돈을 유지함!
                    .build()
    );

    // 상인(merchant) 등록하기
    public static final DeferredHolder<EntityType<?>, EntityType<ScarecrowMerchant>> MERCHANT = ENTITY_TYPES.register("merchant", () -> EntityType.Builder.of(ScarecrowMerchant::new, MobCategory.CREATURE).sized(0.6f, 1.95f).build("merchant"));
    // Create a Deferred Register to hold CreativeModeTabs which will all be registered under the "examplemod" namespace
    public static final DeferredHolder<EntityType<?>, EntityType<BankerEntity>> BANKER =
            ENTITY_TYPES.register("banker", () -> EntityType.Builder.of(BankerEntity::new, MobCategory.CREATURE)
                    .sized(0.6f, 1.95f).build("banker"));


    // Creates a new Block with the id "examplemod:example_block", combining the namespace and path
    public static final DeferredBlock<Block> EXAMPLE_BLOCK = BLOCKS.registerSimpleBlock("example_block", BlockBehaviour.Properties.of().mapColor(MapColor.STONE));
    // Creates a new BlockItem with the id "examplemod:example_block", combining the namespace and path
    public static final DeferredItem<BlockItem> EXAMPLE_BLOCK_ITEM = ITEMS.registerSimpleBlockItem("example_block", EXAMPLE_BLOCK);

    // Creates a new food item with the id "examplemod:example_id", nutrition 1 and saturation 2
    public static final DeferredItem<Item> EXAMPLE_ITEM = ITEMS.registerSimpleItem("example_item", new Item.Properties().food(new FoodProperties.Builder().alwaysEdible().nutrition(1).saturationModifier(2f).build()));
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, MODID);
    public static final DeferredHolder<MenuType<?>, MenuType<BankerMenu>> BANKER_MENU = MENUS.register(
            "banker_menu",
            () -> new MenuType<>(BankerMenu::new, FeatureFlags.DEFAULT_FLAGS)
    );
    // Creates a creative tab with the id "examplemod:example_tab" for the example item, that is placed after the combat tab
    public record SyncBalancePacket(int balance) implements CustomPacketPayload {

        // 우편물의 고유 번호표
        public static final CustomPacketPayload.Type<SyncBalancePacket> TYPE =
                new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(ExampleMod.MODID, "sync_balance"));

        // 우편물을 안전하게 포장하는 도구 (StreamCodec)
        public static final StreamCodec<RegistryFriendlyByteBuf, SyncBalancePacket> STREAM_CODEC = StreamCodec.ofMember(
                SyncBalancePacket::write,
                SyncBalancePacket::new
        );

        // 버퍼에서 데이터 읽기
        public SyncBalancePacket(RegistryFriendlyByteBuf buffer) {
            this(buffer.readInt());
        }

        // 버퍼에 데이터 쓰기
        public void write(RegistryFriendlyByteBuf buffer) {
            buffer.writeInt(balance);
        }

        // 필수 오버라이드
        @Override
        public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }
    @EventBusSubscriber(modid = ExampleMod.MODID, bus = EventBusSubscriber.Bus.GAME)
    public static class ServerGameEvents {
        @SubscribeEvent
        public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
            // 접속한 사람이 서버 플레이어라면
            if (event.getEntity() instanceof ServerPlayer player) {
                //  Attachment에 있는 진짜 잔고를 읽어서
                int currentBalance = player.getData(BANK_BALANCE);

                //  클라이언트로 쏴줌
                PacketDistributor.sendToPlayer(player, new SyncBalancePacket(currentBalance));
            }
        }
    }


    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public ExampleMod(IEventBus modEventBus, ModContainer modContainer) {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);
        CREATIVE_MODE_TABS.register(modEventBus);
//변경됨
        ENTITY_TYPES.register(modEventBus);
        modEventBus.addListener(ExampleMod::onAttributeCreate);
        modEventBus.addListener(ExampleMod::onRegisterRenderers);

        MENUS.register(modEventBus);
//여기까지
        // Register the Deferred Register to the mod event bus so blocks get registered
        BLOCKS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so items get registered
        ITEMS.register(modEventBus);

        // Register the Deferred Register to the mod event bus so tabs get registered
        ATTACHMENT_TYPES.register(modEventBus);

        modEventBus.addListener((RegisterPayloadHandlersEvent event) -> {
            event.registrar(ExampleMod.MODID)
                    // 1. 서버 -> 클라이언트 (잔고 갱신)
                    .playToClient(
                            SyncBalancePacket.TYPE,
                            SyncBalancePacket.STREAM_CODEC,
                            (payload, context) -> context.enqueueWork(() -> {
                                ClientGameEvents.clientBalance = payload.balance();
                            })
                    )
                    // 클라이언트 -> 서버 (입금/출금 요청 처리)
                    .playToServer(
                            BankActionPacket.TYPE,
                            BankActionPacket.STREAM_CODEC,
                            (payload, context) -> context.enqueueWork(() -> {
                                if (context.player() instanceof ServerPlayer player && player.containerMenu instanceof BankerMenu menu) {
                                    int currentBalance = player.getData(BANK_BALANCE);

                                    //동작 0: 입금 버튼을 눌렀을 때
                                    if (payload.action() == 0) {
                                        int totalDeposit = 0;
                                        // 3x3 가방(9칸)을 싹 뒤져서 동전 찾기
                                        for (int i = 0; i < 9; i++) {
                                            ItemStack stack = menu.depositContainer.getItem(i);
                                            int coinValue = 0;
                                            if (stack.is(COIN_100.get())) coinValue = 100;
                                            else if (stack.is(COIN_50.get())) coinValue = 50;
                                            else if (stack.is(COIN_10.get())) coinValue = 10;
                                            else if (stack.is(COIN_5.get())) coinValue = 5;
                                            else if (stack.is(COIN_1.get())) coinValue = 1;

                                            if (coinValue > 0) {
                                                totalDeposit += coinValue * stack.getCount(); // 금액 계산
                                                stack.setCount(0); // 아이템 소멸(입금 완료)
                                            }
                                        }
                                        // 돈이 하나라도 들어왔다면 계좌 갱신
                                        if (totalDeposit > 0) {
                                            player.setData(BANK_BALANCE, currentBalance + totalDeposit);
                                            PacketDistributor.sendToPlayer(player, new SyncBalancePacket(currentBalance + totalDeposit));
                                        }
                                    }

                                    // 출금 버튼을 눌렀을 때
                                    else if (payload.action() == 1) {
                                        int amountToWithdraw = payload.amount();
                                        if (currentBalance >= amountToWithdraw) { // 잔고가 충분한지 확인!
                                            // 잔고 차감
                                            player.setData(BANK_BALANCE, currentBalance - amountToWithdraw);
                                            PacketDistributor.sendToPlayer(player, new SyncBalancePacket(currentBalance - amountToWithdraw));

                                            // 동전 아이템 지급
                                            ItemStack coinItem = ItemStack.EMPTY;
                                            if (amountToWithdraw == 100) coinItem = new ItemStack(COIN_100.get());
                                            else if(amountToWithdraw == 500) coinItem = new ItemStack(COIN_100.get(), 5);
                                            else if (amountToWithdraw == 50) coinItem = new ItemStack(COIN_50.get());
                                            else if (amountToWithdraw == 10) coinItem = new ItemStack(COIN_10.get());
                                            else if (amountToWithdraw == 5) coinItem = new ItemStack(COIN_5.get());
                                            else if (amountToWithdraw == 1) coinItem = new ItemStack(COIN_1.get());

                                            // 플레이어 인벤토리에 넣어주기 (꽉 찼으면 바닥에 떨어짐)
                                            player.getInventory().placeItemBackInInventory(coinItem);
                                        }
                                    }
                                }
                            })
                    );
        });

        // Register ourselves for server and other game events we are interested in.
        // Note that this is necessary if and only if we want *this* class (ExampleMod) to respond directly to events.
        // Do not add this line if there are no @SubscribeEvent-annotated functions in this class, like onServerStarting() below.
        NeoForge.EVENT_BUS.register(this);

        // Register the item to a creative tab
        modEventBus.addListener(this::addCreative);

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP");

        if (Config.LOG_DIRT_BLOCK.getAsBoolean()) {
            LOGGER.info("DIRT BLOCK >> {}", BuiltInRegistries.BLOCK.getKey(Blocks.DIRT));
        }

        LOGGER.info("{}{}", Config.MAGIC_NUMBER_INTRODUCTION.get(), Config.MAGIC_NUMBER.getAsInt());

        Config.ITEM_STRINGS.get().forEach((item) -> LOGGER.info("ITEM >> {}", item));
    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
            event.accept(EXAMPLE_BLOCK_ITEM);
        }
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    @SubscribeEvent
    public void onCommandsRegister(RegisterCommandsEvent event) {
        event.getDispatcher().register(
                Commands.literal("addmoney")
                        // 1. [유저이름] 입력받기 (EntityArgument.player() 사용)
                        .then(Commands.argument("target", EntityArgument.player())
                                // 2. [금액] 입력받기
                                .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                                        .executes(context -> {
                                            // 명령어를 '입력한' 관리자 (메시지 받을 사람)
                                            ServerPlayer admin = context.getSource().getPlayerOrException();

                                            // 명령어로 '지목당한' 대상 플레이어
                                            ServerPlayer targetPlayer = EntityArgument.getPlayer(context, "target");

                                            // 입력한 금액
                                            int amount = IntegerArgumentType.getInteger(context, "amount");

                                            // 대상 플레이어의 계좌 잔액 불러와서 더하기
                                            int currentBalance = targetPlayer.getData(BANK_BALANCE);
                                            int newBalance = currentBalance + amount;
                                            targetPlayer.setData(BANK_BALANCE, newBalance);
                                            PacketDistributor.sendToPlayer(targetPlayer, new SyncBalancePacket(newBalance));

                                            // 관리자에게 완료 메시지 띄우기
                                            admin.sendSystemMessage(Component.literal("§e[은행] §f" + targetPlayer.getName().getString() + "님에게 " + amount + "G 입금 완료! (현재 잔액: " + newBalance + "G)"));

                                            // 돈을 받은 대상 플레이어에게도 알림 보내기 (옵션)
                                            targetPlayer.sendSystemMessage(Component.literal("§e[은행] §f계좌에 " + amount + "G가 입금되었습니다! (현재 잔액: " + newBalance + "G)"));

                                            return 1;
                                        })
                                )
                        )
        );
    }

    public static void onAttributeCreate(EntityAttributeCreationEvent event) {
        event.put(MERCHANT.get(), Villager.createAttributes().build());
        event.put(BANKER.get(), BankerEntity.createAttributes().build());
    }

    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(MERCHANT.get(), ScarecrowRenderer::new);
        event.registerEntityRenderer(BANKER.get(),BankerRenderer::new);
    }

    // 1. MOD 버스용 (초기 설정)
    @EventBusSubscriber(modid = ExampleMod.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            // Some client setup code
            LOGGER.info("HELLO FROM CLIENT SETUP");
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        }
        @SubscribeEvent
        public static void registerScreens(RegisterMenuScreensEvent event) {
            event.register(ExampleMod.BANKER_MENU.get(), BankerScreen::new);
        }
    }

    // 2. GAME 버스용 (인벤토리 잔고 표시)
    @EventBusSubscriber(modid = ExampleMod.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.GAME)
    public static class ClientGameEvents {
        public static int clientBalance = 0;
        @SubscribeEvent
        public static void onRenderGui(ScreenEvent.Render.Post event) {
            // 1. 현재 열린 화면이 '인벤토리'인지 확인
            if (event.getScreen() instanceof InventoryScreen) {

                GuiGraphics graphics = event.getGuiGraphics();
                Font font = Minecraft.getInstance().font;

                // 나중에 실제 데이터와 연동될 텍스트
                String balanceText = clientBalance + " Won";

                // 1. 화면 전체 너비 구하기
                int screenWidth = graphics.guiWidth();

                // 2. 그릴 박스의 크기 계산
                int boxWidth = 20 + font.width(balanceText) + 10;
                int boxHeight = 24;

                // 3. 우측 상단 좌표 설정 (여기서 startX, startY가 만들어집니다)
                int startX = screenWidth - boxWidth - 10;
                int startY = 10;
                // =========================================================

                // 4. 반투명한 검은색 배경 박스 그리기
                graphics.fill(startX, startY, startX + boxWidth, startY + boxHeight, 0x80000000);

                // 테두리 추가
                int borderColor = 0xFF555555;
                graphics.fill(startX, startY, startX + boxWidth, startY + 1, borderColor); // 위
                graphics.fill(startX, startY + boxHeight - 1, startX + boxWidth, startY + boxHeight, borderColor); // 아래
                graphics.fill(startX, startY, startX + 1, startY + boxHeight, borderColor); // 왼쪽
                graphics.fill(startX + boxWidth - 1, startY, startX + boxWidth, startY + boxHeight, borderColor); // 오른쪽

                // 5. 동전 아이콘 그리기
                graphics.renderItem(new ItemStack(COIN_100.get()), startX + 4, startY + 4);

                // 6. 텍스트 그리기
                graphics.drawString(font, balanceText, startX + 24, startY + 8, 0xFFD700, true);

            }
        }
    }
    public record BankActionPacket(int action, int amount) implements CustomPacketPayload {
        // action: 0 = 입금, 1 = 출금
        // amount: 출금할 때 얼마짜리 동전을 뽑을 건지 (100, 50, 10, 5, 1)

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

}