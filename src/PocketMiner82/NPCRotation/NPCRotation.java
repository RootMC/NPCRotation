package PocketMiner82.NPCRotation;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerMoveEvent;
import cn.nukkit.level.Location;
import cn.nukkit.math.Vector2;
import cn.nukkit.network.protocol.MoveEntityAbsolutePacket;
import cn.nukkit.network.protocol.MovePlayerPacket;
import cn.nukkit.plugin.PluginBase;
import idk.plugin.npc.entities.NPC_Entity;
import idk.plugin.npc.entities.NPC_Human;
import idk.plugin.npc.entities.NPC_Shulker;

public class NPCRotation extends PluginBase implements Listener {

    @Override
    public void onEnable() {
        this.getLogger().info("NPC Rotation enabled.");
        this.getServer().getPluginManager().registerEvents(this, this);
        
        this.saveDefaultConfig();
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerMove(PlayerMoveEvent ev) {
        Player player = ev.getPlayer();
        Location from = ev.getFrom();
        Location to = ev.getTo();
        if (from.distance(to) < 0.1)
            return;
        
        double maxDistance = this.getConfig().getDouble("max-distance");
        
        for (Entity e : player.getLevel().getNearbyEntities(player.getBoundingBox().clone().expand(maxDistance, maxDistance, maxDistance), player)) {
            if(!(e instanceof NPC_Entity) && !(e instanceof NPC_Human))
                continue;
            if(e instanceof NPC_Shulker)
                continue;
            
            double xdiff = player.x - e.x;
            double zdiff = player.z - e.z;
            double angle = Math.atan2(zdiff, xdiff);
            double yaw = ((angle * 180) / Math.PI) - 90;
            
            double ydiff = player.y - e.y;
            Vector2 v = new Vector2(e.x, e.z);
            double dist = v.distance(player.x, player.z);
            angle = Math.atan2(dist, ydiff);
            double pitch = ((angle * 180) / Math.PI) - 90;
            
            if (e instanceof NPC_Human) {
                MovePlayerPacket pk = new MovePlayerPacket();
                pk.eid = e.getId();
                pk.x = (float) e.x;
                pk.y = (float) e.y + e.getEyeHeight();
                pk.z = (float) e.z;
                pk.yaw = (float) yaw;
                pk.pitch = (float) pitch;
                pk.headYaw = (float) yaw;
                pk.onGround = e.onGround;
                player.dataPacket(pk);
            } else {
                MoveEntityAbsolutePacket pk = new MoveEntityAbsolutePacket();
                pk.eid = e.getId();
                pk.x = e.x;
                pk.y = e.y;
                pk.z = e.z;
                pk.yaw = yaw;
                pk.pitch = pitch;
                pk.headYaw = yaw;
                pk.onGround = e.onGround;
                player.dataPacket(pk);
            }
        }
    }
}
