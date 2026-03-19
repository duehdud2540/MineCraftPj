package com.example.examplemod;

import com.example.examplemod.blocks.FlagBlock;
import com.example.examplemod.blocks.ReinforcedGlassBlock;
import com.example.examplemod.entity.*;
import com.example.examplemod.items.ReturnScrollItem;
import com.example.examplemod.items.SpawnScrollItem;
import com.example.examplemod.renderer.*;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.*;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@Mod(ExampleMod.MODID)
public class ExampleMod {
    public static final String MODID = "mc_merchant";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static int clientBalance = 0;

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
//item 등록부
    public static final DeferredHolder<Item, Item> RETURN_SCROLL = ITEMS.register("return_scroll", () -> new ReturnScrollItem(new Item.Properties().stacksTo(16)));
    public static final DeferredHolder<Item, Item> COIN_1 = ITEMS.register("coin_1", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> COIN_5 = ITEMS.register("coin_5", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> COIN_10 = ITEMS.register("coin_10", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> COIN_50 = ITEMS.register("coin_50", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> COIN_100 = ITEMS.register("coin_100", () -> new Item(new Item.Properties()));

    public static final DeferredItem<SpawnScrollItem> SPAWN_SCROLL = ITEMS.register("spawn_scroll", () -> new SpawnScrollItem(new Item.Properties().stacksTo(16)));

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MODID);

    public static final DeferredBlock<Block> FLAG_BLOCK = BLOCKS.register("flag_block", () -> new FlagBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PURPLE).strength(2.0f).noOcclusion()));
    public static final DeferredItem<Item> FLAG_BLOCK_ITEM = ITEMS.register("flag_block", () -> new BlockItem(FLAG_BLOCK.get(), new Item.Properties()));

    public static final DeferredBlock<Block> REINFORCED_GLASS = BLOCKS.register("reinforced_glass",
            () -> new ReinforcedGlassBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.NONE)
                    .strength(6000000.0F, 6000000.0F)
                    .noOcclusion()
                    .isValidSpawn((state, getter, pos, type) -> false)
                    .isRedstoneConductor((state, getter, pos) -> false)
                    .isSuffocating((state, getter, pos) -> false)
                    .isViewBlocking((state, getter, pos) -> false)
            ));
    public static final DeferredItem<Item> REINFORCED_GLASS_ITEM = ITEMS.register("reinforced_glass", () -> new BlockItem(REINFORCED_GLASS.get(), new Item.Properties()));
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> COIN_TAB = CREATIVE_MODE_TABS.register("coin_tab", () -> CreativeModeTab.builder().title(Component.literal("동전")).icon(() -> new ItemStack(COIN_100.get()))
            .displayItems((parameters, output) -> {
                output.accept(COIN_10.get());
                output.accept(COIN_50.get());
                output.accept(COIN_100.get());
                output.accept(COIN_5.get());
                output.accept(COIN_1.get());
            }).build());

    //entity 등록부
    public static final Supplier<AttachmentType<Integer>> BANK_BALANCE = ATTACHMENT_TYPES.register("bank_balance", () -> AttachmentType.builder(() -> 0).serialize(Codec.INT).copyOnDeath().build());
    public static final Supplier<AttachmentType<List<GlobalPos>>> FLAG_LIST = ATTACHMENT_TYPES.register("flag_list",
            () -> AttachmentType.builder(() -> (List<GlobalPos>) new ArrayList<GlobalPos>())
                    .serialize(Codec.list(GlobalPos.CODEC))
                    .copyOnDeath()
                    .build());
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<List<String>>> FLAG_NAMES = ATTACHMENT_TYPES.register("flag_names", () -> AttachmentType.builder(() -> (List<String>) new ArrayList<String>()).serialize(Codec.list(Codec.STRING)).copyOnDeath().build());

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FlagBlockEntity>> FLAG_BLOCK_ENTITY = BLOCK_ENTITIES.register("flag_block_entity", () -> BlockEntityType.Builder.of(FlagBlockEntity::new, FLAG_BLOCK.get()).build(null));

    public static final DeferredHolder<EntityType<?>, EntityType<ScarecrowMerchant>> MERCHANT = ENTITY_TYPES.register("merchant", () -> EntityType.Builder.of(ScarecrowMerchant::new, MobCategory.CREATURE).sized(0.6f, 1.95f).build("merchant"));
    public static final DeferredHolder<EntityType<?>, EntityType<BankerEntity>> BANKER = ENTITY_TYPES.register("banker", () -> EntityType.Builder.of(BankerEntity::new, MobCategory.CREATURE).sized(0.6f, 1.95f).build("banker"));
    public static final DeferredHolder<EntityType<?>, EntityType<PotionMerchantEntity>> POTION_MERCHANT = ENTITY_TYPES.register("potion_merchant", () -> EntityType.Builder.of(PotionMerchantEntity::new, MobCategory.CREATURE).sized(0.6f, 1.95f).build("potion_merchant"));
    public static final DeferredHolder<EntityType<?>, EntityType<FishermanMerchant>> FISHERMAN_MERCHANT = ENTITY_TYPES.register("fisherman_merchant", () -> EntityType.Builder.of(FishermanMerchant::new, MobCategory.CREATURE).sized(0.6f, 1.95f).build("fisherman_merchant"));
    public static final DeferredHolder<EntityType<?>, EntityType<FarmerMerchant>> FARMER_MERCHANT = ENTITY_TYPES.register("farmer_merchant", () -> EntityType.Builder.of(FarmerMerchant::new, MobCategory.CREATURE).sized(0.6f, 1.95f).build("farmer_merchant"));

    public static final DeferredBlock<Block> EXAMPLE_BLOCK = BLOCKS.registerSimpleBlock("example_block", BlockBehaviour.Properties.of().mapColor(MapColor.STONE));
    public static final DeferredItem<BlockItem> EXAMPLE_BLOCK_ITEM = ITEMS.registerSimpleBlockItem("example_block", EXAMPLE_BLOCK);
    public static final DeferredItem<Item> EXAMPLE_ITEM = ITEMS.registerSimpleItem("example_item", new Item.Properties().food(new FoodProperties.Builder().alwaysEdible().nutrition(1).saturationModifier(2f).build()));

    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, MODID);
    public static final DeferredHolder<MenuType<?>, MenuType<BankerMenu>> BANKER_MENU = MENUS.register("banker_menu", () -> new MenuType<>(BankerMenu::new, FeatureFlags.DEFAULT_FLAGS));

    @SubscribeEvent
    public void onPlayerLoginCombined(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            Level level = player.level();
            List<GlobalPos> savedFlags = new ArrayList<>(player.getData(FLAG_LIST));
            List<String> savedNames = new ArrayList<>(player.getData(FLAG_NAMES));

            if (!savedFlags.isEmpty()) {
                List<GlobalPos> validFlags = new ArrayList<>();
                List<String> validNames = new ArrayList<>();
                boolean isDirty = false;

                for (int i = 0; i < savedFlags.size(); i++) {
                    GlobalPos gpos = savedFlags.get(i);

                    if (level.dimension().equals(gpos.dimension())) {
                        BlockPos pos = gpos.pos();
                        if (level.getBlockState(pos).is(FLAG_BLOCK.get())) {
                            validFlags.add(gpos); // GlobalPos 그대로 추가
                            if (i < savedNames.size()) validNames.add(savedNames.get(i));
                        } else {
                            isDirty = true;
                        }
                    } else {
                        validFlags.add(gpos);
                        if (i < savedNames.size()) validNames.add(savedNames.get(i));
                    }
                }

                if (isDirty) {
                    player.setData(FLAG_LIST, validFlags);
                    player.setData(FLAG_NAMES, validNames);
                    PacketDistributor.sendToPlayer(player, new SyncFlagsPacket(validFlags, validNames));
                    player.sendSystemMessage(Component.literal("§6[시스템] §f유효하지 않은 깃발 데이터를 정리했습니다."));
                }
            }
        }
    }
// 아이템버스 등록부
    public ExampleMod(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);
        CREATIVE_MODE_TABS.register(modEventBus);
        ENTITY_TYPES.register(modEventBus);
        modEventBus.addListener(ExampleMod::onAttributeCreate);
        modEventBus.addListener(ExampleMod::onRegisterRenderers);
        MENUS.register(modEventBus);
        BLOCK_ENTITIES.register(modEventBus);
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        ATTACHMENT_TYPES.register(modEventBus);
        NeoForge.EVENT_BUS.register(this);
        modEventBus.addListener(this::addCreative);
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("HELLO FROM COMMON SETUP");
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
            event.accept(EXAMPLE_BLOCK_ITEM);
        }
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("HELLO from server starting");
    }

    @SubscribeEvent
    public void onCommandsRegister(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("addmoney").then(Commands.argument("target", EntityArgument.player()).then(Commands.argument("amount", IntegerArgumentType.integer(1)).executes(context -> {
            ServerPlayer admin = context.getSource().getPlayerOrException();
            ServerPlayer targetPlayer = EntityArgument.getPlayer(context, "target");
            int amount = IntegerArgumentType.getInteger(context, "amount");
            int currentBalance = targetPlayer.getData(BANK_BALANCE);
            int newBalance = currentBalance + amount;
            targetPlayer.setData(BANK_BALANCE, newBalance);
            PacketDistributor.sendToPlayer(targetPlayer, new SyncBalancePacket(newBalance));
            admin.sendSystemMessage(Component.literal("§e[은행] §f" + targetPlayer.getName().getString() + "님에게 " + amount + "G 입금 완료!"));
            targetPlayer.sendSystemMessage(Component.literal("§e[은행] §f계좌에 " + amount + "G가 입금되었습니다!"));
            return 1;
        }))));
    }

    public static void onAttributeCreate(EntityAttributeCreationEvent event) {
        event.put(MERCHANT.get(), Villager.createAttributes().build());
        event.put(BANKER.get(), BankerEntity.createAttributes().build());
        event.put(POTION_MERCHANT.get(), Villager.createAttributes().build());
        event.put(FISHERMAN_MERCHANT.get(), Villager.createAttributes().build());
        event.put(FARMER_MERCHANT.get(), Villager.createAttributes().build());
    }

    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(MERCHANT.get(), ScarecrowRenderer::new);
        event.registerEntityRenderer(BANKER.get(),BankerRenderer::new);
        event.registerEntityRenderer(POTION_MERCHANT.get(), PotionMerchantRenderer::new);
        event.registerEntityRenderer(FARMER_MERCHANT.get(), FarmerRenderer::new);
        event.registerEntityRenderer(FISHERMAN_MERCHANT.get(), FishermanRenderer::new);
    }


//공용 버스패킷

    @EventBusSubscriber(modid = ExampleMod.MODID, bus = EventBusSubscriber.Bus.MOD)
    public static class CommonModEvents {
        @SubscribeEvent
        public static void registerPayloads(final RegisterPayloadHandlersEvent event) {
            final PayloadRegistrar registrar = event.registrar(MODID).versioned("1.0.0");

            registrar.playToClient(SyncFlagsPacket.TYPE, SyncFlagsPacket.STREAM_CODEC, SyncFlagsPacket::handle);
            registrar.playToServer(ChangeFlagNamePacket.TYPE, ChangeFlagNamePacket.STREAM_CODEC, ChangeFlagNamePacket::handle);
            registrar.playToServer(TeleportPacket.TYPE, TeleportPacket.STREAM_CODEC, (payload, context) -> context.enqueueWork(() -> {
                if (!(context.player() instanceof ServerPlayer player)) return;

                // 1. 스크롤 확인
                ItemStack scroll = player.getInventory().items.stream()
                        .filter(stack -> stack.is(SPAWN_SCROLL.get())).findFirst().orElse(ItemStack.EMPTY);

                // 2. 권한 체크
                if (scroll.isEmpty() && !player.getAbilities().instabuild) {
                    player.sendSystemMessage(Component.literal("§c[오류] §f스폰 스크롤이 부족합니다!"));
                    return;
                }

                // 3. 차원 이동 및 텔레포트
                GlobalPos target = payload.targetGlobalPos(); // 패킷에서 GlobalPos를 가져온다고 가정
                var targetLevel = player.server.getLevel(target.dimension());

                if (targetLevel != null) {
                    BlockPos p = target.pos();
                    player.teleportTo(targetLevel, p.getX() + 0.5, p.getY() + 1.0, p.getZ() + 0.5, player.getYRot(), player.getXRot());

                    // 4. 아이템 소모
                    if (!player.getAbilities().instabuild) scroll.shrink(1);

                    player.sendSystemMessage(Component.literal("§a[이동] §f차원을 넘어 이동했습니다!"));
                }
            }));
            registrar.playToClient(SyncBalancePacket.TYPE, SyncBalancePacket.STREAM_CODEC, (payload, context) -> context.enqueueWork(() -> {
                ExampleMod.clientBalance = payload.balance();
            }));
            registrar.playToServer(BankActionPacket.TYPE, BankActionPacket.STREAM_CODEC, (payload, context) -> context.enqueueWork(() -> {
                if (context.player() instanceof ServerPlayer player && player.containerMenu instanceof BankerMenu menu) {
                    int currentBalance = player.getData(BANK_BALANCE);
                    if (payload.action() == 0) {
                        int totalDeposit = 0;
                        for (int i = 0; i < 9; i++) {
                            ItemStack stack = menu.depositContainer.getItem(i);
                            int coinValue = 0;
                            if (stack.is(COIN_100.get())) coinValue = 100;
                            else if (stack.is(COIN_50.get())) coinValue = 50;
                            else if (stack.is(COIN_10.get())) coinValue = 10;
                            else if (stack.is(COIN_5.get())) coinValue = 5;
                            else if (stack.is(COIN_1.get())) coinValue = 1;

                            if (coinValue > 0) {
                                totalDeposit += coinValue * stack.getCount();
                                stack.setCount(0);
                            }
                        }
                        if (totalDeposit > 0) {
                            player.setData(BANK_BALANCE, currentBalance + totalDeposit);
                            PacketDistributor.sendToPlayer(player, new SyncBalancePacket(currentBalance + totalDeposit));
                        }
                    } else if (payload.action() == 1) {
                        int amount = payload.amount();
                        if (currentBalance >= amount) {
                            player.setData(BANK_BALANCE, currentBalance - amount);
                            PacketDistributor.sendToPlayer(player, new SyncBalancePacket(currentBalance - amount));
                            ItemStack coinItem = ItemStack.EMPTY;
                            if (amount == 100) coinItem = new ItemStack(COIN_100.get());
                            else if (amount == 500) coinItem = new ItemStack(COIN_100.get(), 5);
                            else if (amount == 50) coinItem = new ItemStack(COIN_50.get());
                            else if (amount == 10) coinItem = new ItemStack(COIN_10.get());
                            else if (amount == 5) coinItem = new ItemStack(COIN_5.get());
                            else if (amount == 1) coinItem = new ItemStack(COIN_1.get());
                            player.getInventory().placeItemBackInInventory(coinItem);
                        }
                    }
                }
            }));
        }
    }
    @EventBusSubscriber(modid = ExampleMod.MODID, bus = EventBusSubscriber.Bus.GAME)
    public class PlayerDataEvents {

        @SubscribeEvent
        public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
            if (event.getEntity() instanceof ServerPlayer serverPlayer) {
                // 1. 서버에 저장된 데이터를 가져옴
                List<GlobalPos> flags = serverPlayer.getData(ExampleMod.FLAG_LIST);

                List<String> names = serverPlayer.getData(ExampleMod.FLAG_NAMES);
                int balance = serverPlayer.getData(ExampleMod.BANK_BALANCE);

                PacketDistributor.sendToPlayer(serverPlayer, new SyncFlagsPacket(flags, names));
                PacketDistributor.sendToPlayer(serverPlayer, new SyncBalancePacket(balance));

                System.out.println(serverPlayer.getName().getString() + "님의 데이터를 클라이언트로 동기화했습니다.");
            }
        }
    }
    @SubscribeEvent
    public  void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            // 서버가 저장하고 있는 깃발 데이터를 가져옴
            List<GlobalPos> currentFlags = player.getData(ExampleMod.FLAG_LIST);
            List<String> currentNames = player.getData(ExampleMod.FLAG_NAMES);

            // 차원을 이동한 직후의 클라이언트에게 데이터를 다시 전송
            PacketDistributor.sendToPlayer(player, new SyncFlagsPacket(currentFlags, currentNames));
        }
    }
//클라이언트 버스
    @EventBusSubscriber(modid = ExampleMod.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            LOGGER.info("HELLO FROM CLIENT SETUP");
            ItemBlockRenderTypes.setRenderLayer(ExampleMod.REINFORCED_GLASS.get(), RenderType.translucent());
        }

        @SubscribeEvent
        public static void registerScreens(RegisterMenuScreensEvent event) {
            event.register(ExampleMod.BANKER_MENU.get(), BankerScreen::new);
        }
    }

//클라이언트 버스 패킷
    @EventBusSubscriber(modid = ExampleMod.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.GAME)
    public static class ClientGameEvents {
        @SubscribeEvent
        public static void onRenderGui(ScreenEvent.Render.Post event) {
            if (event.getScreen() instanceof InventoryScreen) {
                GuiGraphics graphics = event.getGuiGraphics();
                Font font = Minecraft.getInstance().font;

                // ExampleMod.clientBalance 사용으로 충돌 방지
                String balanceText = ExampleMod.clientBalance + " Won";
                int screenWidth = graphics.guiWidth();
                int boxWidth = 20 + font.width(balanceText) + 10;
                int boxHeight = 24;
                int startX = screenWidth - boxWidth - 10;
                int startY = 10;

                graphics.fill(startX, startY, startX + boxWidth, startY + boxHeight, 0x80000000);
                int borderColor = 0xFF555555;
                graphics.fill(startX, startY, startX + boxWidth, startY + 1, borderColor);
                graphics.fill(startX, startY + boxHeight - 1, startX + boxWidth, startY + boxHeight, borderColor);
                graphics.fill(startX, startY, startX + 1, startY + boxHeight, borderColor);
                graphics.fill(startX + boxWidth - 1, startY, startX + boxWidth, startY + boxHeight, borderColor);
                graphics.renderItem(new ItemStack(COIN_100.get()), startX + 4, startY + 4);
                graphics.drawString(font, balanceText, startX + 24, startY + 8, 0xFFD700, true);
            }
        }
    }
}