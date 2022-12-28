/*
 * This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package kevin.module.modules.movement

import kevin.event.*
import kevin.module.*
import kevin.utils.MovementUtils
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.network.play.server.S27PacketExplosion
import net.minecraft.util.BlockPos
import net.minecraft.util.EnumFacing

@Suppress("unused_parameter")
class LongJump : Module("LongJump", "Allows you to jump further.", category = ModuleCategory.MOVEMENT) {
    private val modeValue = ListValue("Mode", arrayOf("NCP", "AACv1", "AACv2", "AACv3", "Mineplex", "Mineplex2", "Mineplex3", "Redesky", "BlocksMCBlockOver", "Buzz", "ExplosionBoost"), "NCP")
    private val ncpBoostValue = FloatValue("NCPBoost", 4.25f, 1f, 10f)
    private val autoJumpValue = BooleanValue("AutoJump", false)
    private val explosionBoostHigh = FloatValue("ExplosionBoostHigh",0.00F,0.01F,1F)
    private val explosionBoostLong = FloatValue("ExplosionBoostLong",0.25F,0.01F,1F)
    private var jumped = false
    private var canBoost = false
    private var teleported = false
    private var canMineplexBoost = false
    private var explosion = false

    override fun onEnable() {
        if (modeValue equal "Buzz") {
            mc.netHandler.addToSendQueue(C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 3.42, mc.thePlayer.posZ, false))
            mc.netHandler.addToSendQueue(C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 1E-12, mc.thePlayer.posZ, false))
            mc.netHandler.addToSendQueue(C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, true))
            mc.thePlayer.jump()
        }
    }
    override fun onDisable() {

    }

    @EventTarget
    fun onUpdate(event: UpdateEvent?) {
        val thePlayer = mc.thePlayer ?: return

        if (jumped) {
            val mode = modeValue.get()

            if (thePlayer.onGround || thePlayer.capabilities.isFlying) {
                jumped = false
                canMineplexBoost = false

                if (mode.equals("NCP", ignoreCase = true)) {
                    thePlayer.motionX = 0.0
                    thePlayer.motionZ = 0.0
                }
                return
            }
            run {
                when (mode.lowercase()) {
                    "ncp" -> {
                        MovementUtils.strafe(MovementUtils.speed * if (canBoost) ncpBoostValue.get() else 1f)
                        canBoost = false
                    }
                    "aacv1" -> {
                        thePlayer.motionY += 0.05999
                        MovementUtils.strafe(MovementUtils.speed * 1.08f)
                    }
                    "aacv2", "mineplex3" -> {
                        thePlayer.jumpMovementFactor = 0.09f
                        thePlayer.motionY += 0.0132099999999999999999999999999
                        thePlayer.jumpMovementFactor = 0.08f
                        MovementUtils.strafe()
                    }
                    "aacv3" -> {
                        if (thePlayer.fallDistance > 0.5f && !teleported) {
                            val value = 3.0
                            val horizontalFacing = thePlayer.horizontalFacing
                            var x = 0.0
                            var z = 0.0

                            when {
                                horizontalFacing==EnumFacing.NORTH -> z = -value
                                horizontalFacing==EnumFacing.EAST -> x = +value
                                horizontalFacing==EnumFacing.SOUTH -> z = +value
                                horizontalFacing==EnumFacing.WEST -> x = -value
                                else -> {
                                }
                            }

                            thePlayer.setPosition(thePlayer.posX + x, thePlayer.posY, thePlayer.posZ + z)
                            teleported = true
                        }
                    }
                    "mineplex" -> {
                        thePlayer.motionY += 0.0132099999999999999999999999999
                        thePlayer.jumpMovementFactor = 0.08f
                        MovementUtils.strafe()
                    }
                    "mineplex2" -> {
                        if (!canMineplexBoost)
                            return@run

                        thePlayer.jumpMovementFactor = 0.1f
                        if (thePlayer.fallDistance > 1.5f) {
                            thePlayer.jumpMovementFactor = 0f
                            thePlayer.motionY = (-10f).toDouble()
                        }

                        MovementUtils.strafe()
                    }
                    "redesky" -> {
                        thePlayer.jumpMovementFactor = 0.15f
                        thePlayer.motionY += 0.05f
                    }
                    "blocksmcblockover" -> {
                        if (mc.theWorld.isBlockFullCube(BlockPos(mc.thePlayer.posX, mc.thePlayer.posY + 2.1, mc.thePlayer.posZ))) {
                            canBoost = true
                            mc.timer.timerSpeed = 0.2F
                            return
                        } else if (canBoost && thePlayer.onGround) {
                            thePlayer.motionY = 0.2
                            mc.timer.timerSpeed = 1F
                            MovementUtils.strafe(5.0f)
                            canBoost = false
                            return
                        }
                    }
                    "buzz" -> {
                        if (mc.thePlayer.hurtTime > 8) {
                            MovementUtils.strafe(ncpBoostValue.get())
                            mc.thePlayer.motionY += 0.42
                        }
                    }
                }
            }
        }
        if (autoJumpValue.get() && thePlayer.onGround && MovementUtils.isMoving) {
            jumped = true
            thePlayer.jump()
        }
        if (modeValue.get().equals("ExplosionBoost",true)){
            if (explosion){
                mc.thePlayer.motionX *= 1F + explosionBoostLong.get()
                mc.thePlayer.motionY *= 1F + explosionBoostHigh.get()
                mc.thePlayer.motionZ *= 1F + explosionBoostLong.get()
                explosion = false
            }
        }
    }

    @EventTarget
    fun onMove(event: MoveEvent) {
        val thePlayer = mc.thePlayer ?: return
        val mode = modeValue.get()

        if (mode.equals("mineplex3", ignoreCase = true)) {
            if (thePlayer.fallDistance != 0.0f)
                thePlayer.motionY += 0.037
        } else if (mode.equals("ncp", ignoreCase = true) && !MovementUtils.isMoving && jumped) {
            thePlayer.motionX = 0.0
            thePlayer.motionZ = 0.0
            event.zeroXZ()
        }
    }

    @EventTarget(ignoreCondition = true)
    fun onJump(event: JumpEvent) {
        jumped = true
        canBoost = true
        teleported = false

        if (state) {
            when (modeValue.get().lowercase()) {
                "mineplex" -> event.motion = event.motion * 4.08f
                "mineplex2" -> {
                    if (mc.thePlayer!!.isCollidedHorizontally) {
                        event.motion = 2.31f
                        canMineplexBoost = true
                        mc.thePlayer!!.onGround = false
                    }
                }
            }
        }
    }

    @EventTarget
    fun onPacket(event: PacketEvent){
        if (event.packet is S27PacketExplosion)
            if (event.packet.func_149149_c() != 0F ||
                event.packet.func_149144_d() != 0F ||
                event.packet.func_149147_e() != 0F) explosion = true
    }

    override val tag: String
        get() = modeValue.get()
}