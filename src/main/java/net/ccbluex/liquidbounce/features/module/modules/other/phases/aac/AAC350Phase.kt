package net.ccbluex.liquidbounce.features.module.modules.other.phases.aac

import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.modules.other.phases.PhaseMode
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.ccbluex.liquidbounce.utils.block.BlockUtils
import net.ccbluex.liquidbounce.utils.timer.tickTimer
import net.minecraft.block.Block
import net.minecraft.block.BlockAir
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.util.MathHelper
import kotlin.math.cos
import kotlin.math.sin

class AAC350Phase : PhaseMode("AAC3.5.0") {
    private val tickTimer = tickTimer()
    override fun onEnable() {
        tickTimer.reset()
    }
    override fun onUpdate(event: UpdateEvent) {
        val isInsideBlock = BlockUtils.collideBlockIntersects(mc.thePlayer.entityBoundingBox) { block: Block? -> block !is BlockAir }
        if(isInsideBlock) {
            mc.thePlayer.noClip = true
            mc.thePlayer.motionY = 0.0
            mc.thePlayer.onGround = true
        }

        tickTimer.update()

        if (!tickTimer.hasTimePassed(2) || !mc.thePlayer.isCollidedHorizontally || !(!isInsideBlock || mc.thePlayer.isSneaking)) return
        val yaw = Math.toRadians(mc.thePlayer.rotationYaw.toDouble())
        val oldX = mc.thePlayer.posX
        val oldZ = mc.thePlayer.posZ
        val x = -sin(yaw)
        val z = cos(yaw)
        mc.thePlayer.setPosition(oldX + x, mc.thePlayer.posY, oldZ + z)
        tickTimer.reset()
    }
    override fun onPacket(event: PacketEvent) {
        val packet = event.packet
        if (packet is C03PacketPlayer) {
            val yaw = MovementUtils.direction.toFloat()
            packet.x = packet.x - MathHelper.sin(yaw) * 0.00000001
            packet.z = packet.z + MathHelper.cos(yaw) * 0.00000001
        }
    }
}