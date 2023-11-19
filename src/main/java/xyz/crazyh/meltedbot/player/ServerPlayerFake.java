package xyz.crazyh.meltedbot.player;

import com.mojang.authlib.GameProfile;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.game.ClientboundRotateHeadPacket;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.GameProfileCache;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.phys.Vec3;
import xyz.crazyh.meltedbot.utils.Messenger;

import java.util.concurrent.atomic.AtomicReference;

// Most of this mod is from fabric-carpet
public class ServerPlayerFake extends ServerPlayer {
    public boolean isShadow;
    public Runnable fixStartingPosition = () -> {
    };

    public ServerPlayerFake(MinecraftServer minecraftServer, ServerLevel level, GameProfile gameProfile, boolean isShadow) {
        super(minecraftServer, level, gameProfile);
        this.isShadow = isShadow;
    }

    public ServerPlayerFake(MinecraftServer minecraftServer, ServerLevel level, GameProfile gameProfile) {
        super(minecraftServer, level, gameProfile);
        this.isShadow = false;
    }

    // Returns true if it was successful, false if couldn't spawn due to the player not existing in Mojang servers
    public static ServerPlayerFake createFake(String name, MinecraftServer server, Vec3 pos, double yaw, double pitch, ResourceKey<Level> dimId, GameType gameMode, boolean isFlying) {
        ServerLevel worldIn = server.getLevel(dimId);
        GameProfileCache.setUsesAuthentication(false);
        GameProfile gameProfile;

        // get game profile
        try {
            gameProfile = server.getProfileCache().get(name).orElse(null);
        } finally {
            GameProfileCache.setUsesAuthentication(server.isDedicatedServer() && server.usesAuthentication());
        }
        // maybe will add commands to disable offline player
        if (gameProfile == null) {
            gameProfile = new GameProfile(Player.createPlayerUUID(name), name);
        }
        if (gameProfile.getProperties().containsKey("textures")) {
            AtomicReference<GameProfile> ref = new AtomicReference<>();
            SkullBlockEntity.updateGameprofile(gameProfile, ref::set);
            gameProfile = ref.get();
        }

        // spawn bot entity
        ServerPlayerFake instance = new ServerPlayerFake(server, worldIn, gameProfile);
        instance.fixStartingPosition = () -> instance.moveTo(pos.x, pos.y, pos.z, (float) yaw, (float) pitch);
        // setup bot
        server.getPlayerList().placeNewPlayer(new FakeClientConnection(PacketFlow.SERVERBOUND), instance);
        instance.teleportTo(worldIn, pos.x, pos.y, pos.z, (float) yaw, (float) pitch);
        instance.setHealth(20.0F);
        instance.unsetRemoved();
        instance.maxUpStep = 0.6F;
        instance.gameMode.changeGameModeForPlayer(gameMode);
        // update headings and dim
        server.getPlayerList().broadcastAll(new ClientboundRotateHeadPacket(instance, (byte) (instance.yHeadRot * 256 / 360)));
        server.getPlayerList().broadcastAll(new ClientboundTeleportEntityPacket(instance), dimId);
        // show all model layers (incl. capes)
        // set flying last for cape I guess
        instance.entityData.set(DATA_PLAYER_MODE_CUSTOMISATION, (byte) 0x7f);
        instance.getAbilities().flying = isFlying;

        return instance;
    }

    // no shadow for now
    public static ServerPlayerFake createShadow(MinecraftServer server, ServerPlayer player) {
        return null;
    }

    @Override
    protected void equipEventAndSound(ItemStack itemStack) {
        if (!isUsingItem()) {
            super.equipEventAndSound(itemStack);
        }
    }

    @Override
    public void kill() {
        kill(Messenger.s("Killed"));
    }

    public void kill(Component reason) {
        shakeOff();
        this.server.tell(new TickTask(this.server.getTickCount(), () -> {
            this.connection.onDisconnect(reason);
        }));
    }

    private void shakeOff() {
        if (getVehicle() instanceof Player) stopRiding();
        for (Entity passenger : getIndirectPassengers()) {
            if (passenger instanceof Player) passenger.stopRiding();
        }
    }

    @Override
    public void tick() {
        if (this.getServer().getTickCount() % 10 == 0) {
            this.connection.resetPosition();
            this.getLevel().getChunkSource().move(this);
            hasChangedDimension();
        }

        try {
            super.tick();
            this.doTick();
        } catch (NullPointerException ignored) {
            //do nothing, og comment:
            // happens with that paper port thingy - not sure what that would fix, but hey
            // the game not gonna crash violently.
        }
    }

    @Override
    public void die(DamageSource cause) {
        shakeOff();
        super.die(cause);
        setHealth(20);
        this.foodData = new FoodData();
        kill(this.getCombatTracker().getDeathMessage());
    }

    @Override
    public String getIpAddress() {
        return "127.0.0.1";
    }
}
