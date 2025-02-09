package ewewukek.musketmod;

import ewewukek.musketmod.networking.ModPackets;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class ClientSetup implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.INSTANCE.register(MusketMod.BULLET_ENTITY_TYPE, (ctx) -> new BulletRenderer(ctx));

        ClampedItemPropertyFunction loaded = (stack, world, player, seed) -> {
            return GunItem.isLoaded(stack) ? 1 : 0;
        };
        FabricModelPredicateProviderRegistry.register(Items.MUSKET, new ResourceLocation("loaded"), loaded);
        FabricModelPredicateProviderRegistry.register(Items.RIFLE, new ResourceLocation("loaded"), loaded);
        FabricModelPredicateProviderRegistry.register(Items.BLUNDERBUSS, new ResourceLocation("loaded"), loaded);
        FabricModelPredicateProviderRegistry.register(Items.MUSKET_WITH_BAYONET, new ResourceLocation("loaded"), loaded);
        FabricModelPredicateProviderRegistry.register(Items.PISTOL, new ResourceLocation("loaded"), loaded);

        ClientPlayNetworking.registerGlobalReceiver(MusketMod.SMOKE_EFFECT_PACKET_ID, (client, handler, buf, responseSender) -> {
            ClientLevel world = handler.getLevel();
            Vec3 origin = new Vec3(buf.readFloat(), buf.readFloat(), buf.readFloat());
            Vec3 direction = new Vec3(buf.readFloat(), buf.readFloat(), buf.readFloat());
            GunItem.fireParticles(world, origin, direction);
        });
        ClientPlayNetworking.registerGlobalReceiver(ModPackets.CLIENT_PLAY_MUSKET_SOUND, (client, handler, buf, responseSender) -> {
            int soundIndex = buf.readInt();
            client.execute(() ->  {
                Entity entity = client.player;
                SoundEvent sound = Sounds.soundList.get(soundIndex);
                entity.playSound(sound, 0.8f, 1);
            });
        });

    }
}
