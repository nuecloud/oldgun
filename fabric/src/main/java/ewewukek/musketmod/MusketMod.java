package ewewukek.musketmod;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import ewewukek.musketmod.networking.ModPackets;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Unit;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.phys.Vec3;

import static net.minecraft.world.item.CreativeModeTabs.COMBAT;

public class MusketMod implements ModInitializer {
    public static final String MODID = "musketmod";
    public static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("musketmod.txt");

    public static final EntityType<BulletEntity> BULLET_ENTITY_TYPE = FabricEntityTypeBuilder.<BulletEntity>create(MobCategory.MISC, BulletEntity::new)
            .dimensions(EntityDimensions.fixed(0.5F, 0.5F))
            .trackRangeBlocks(64).trackedUpdateRate(5)
            .forceTrackedVelocityUpdates(false)
            .build();

    @Override
    public void onInitialize() {
        Config.reload();

        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(MODID, "musket"), Items.MUSKET);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(MODID, "musket_with_bayonet"), Items.MUSKET_WITH_BAYONET);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(MODID, "pistol"), Items.PISTOL);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(MODID, "rifle"), Items.RIFLE);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(MODID, "blunderbuss"), Items.BLUNDERBUSS);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(MODID, "cartridge"), Items.CARTRIDGE);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(MODID, "cleaning_rod"), Items.CLEANING_ROD);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(MODID, "dirty_cleaning_rod"), Items.DIRTY_CLEANING_ROD);

        Registry.register(BuiltInRegistries.ENTITY_TYPE, new ResourceLocation(MODID, "bullet"), BULLET_ENTITY_TYPE);

        Registry.register(BuiltInRegistries.SOUND_EVENT, Sounds.MUSKET_LOAD_0.getLocation(), Sounds.MUSKET_LOAD_0);
        Registry.register(BuiltInRegistries.SOUND_EVENT, Sounds.MUSKET_LOAD_1.getLocation(), Sounds.MUSKET_LOAD_1);
        Registry.register(BuiltInRegistries.SOUND_EVENT, Sounds.MUSKET_LOAD_2.getLocation(), Sounds.MUSKET_LOAD_2);
        Registry.register(BuiltInRegistries.SOUND_EVENT, Sounds.MUSKET_READY.getLocation(), Sounds.MUSKET_READY);
        Registry.register(BuiltInRegistries.SOUND_EVENT, Sounds.MUSKET_FIRE.getLocation(), Sounds.MUSKET_FIRE);
        Registry.register(BuiltInRegistries.SOUND_EVENT, Sounds.PISTOL_FIRE.getLocation(), Sounds.PISTOL_FIRE);
        Sounds.addSoundsToList();

        ItemGroupEvents.modifyEntriesEvent(COMBAT).register(content -> {
            content.accept(Items.CARTRIDGE);
            content.accept(Items.MUSKET_WITH_BAYONET);
            content.accept(Items.MUSKET);
            content.accept(Items.PISTOL);
            content.accept(Items.BLUNDERBUSS);
            content.accept(Items.RIFLE);
            content.accept(Items.CLEANING_ROD);
            content.accept(Items.DIRTY_CLEANING_ROD);
        });
        //ModPackets.registerS2CPackets();

        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(new IdentifiableResourceReloadListener() {
            @Override
            public ResourceLocation getFabricId() {
                return new ResourceLocation(MODID, "reload");
            }

            @Override
            public CompletableFuture<Void> reload(PreparationBarrier stage, ResourceManager resourceManager,
                ProfilerFiller preparationsProfiler, ProfilerFiller reloadProfiler, Executor backgroundExecutor,
                Executor gameExecutor) {

                return stage.wait(Unit.INSTANCE).thenRunAsync(() -> {
                    Config.reload();
                }, gameExecutor);
            }
        });
    }

    public static final ResourceLocation SMOKE_EFFECT_PACKET_ID = new ResourceLocation(MODID, "smoke_effect");

    public static void sendSmokeEffect(LivingEntity shooter, Vec3 origin, Vec3 direction) {
        FriendlyByteBuf buf = PacketByteBufs.create();
        buf.writeFloat((float)origin.x);
        buf.writeFloat((float)origin.y);
        buf.writeFloat((float)origin.z);
        buf.writeFloat((float)direction.x);
        buf.writeFloat((float)direction.y);
        buf.writeFloat((float)direction.z);
        BlockPos blockPos = BlockPos.containing(origin.x, origin.y, origin.z);
        for (ServerPlayer serverPlayer : PlayerLookup.tracking((ServerLevel)shooter.level(), blockPos)) {
            ServerPlayNetworking.send(serverPlayer, SMOKE_EFFECT_PACKET_ID, buf);
        }

    }
}
