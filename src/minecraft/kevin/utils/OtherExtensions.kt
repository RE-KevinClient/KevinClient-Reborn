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
package kevin.utils

import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.util.AxisAlignedBB
import net.minecraft.util.Vec3

fun Vec3.multiply(value: Double): Vec3 {
    return Vec3(this.xCoord * value, this.yCoord * value, this.zCoord * value)
}

fun AxisAlignedBB.getLookingTargetRange(thePlayer: EntityPlayerSP, rotation: Rotation? = null, range: Double=6.0): Double {
    val eyes = thePlayer.getPositionEyes(1F)
    val movingObj = this.calculateIntercept(eyes, (rotation ?: RotationUtils.bestServerRotation()).toDirection().multiply(range).add(eyes)) ?: return Double.MAX_VALUE
    return movingObj.hitVec.distanceTo(eyes)
}

fun AxisAlignedBB.expands(v: Double, modifyYDown: Boolean=true, modifyYUp: Boolean=true): AxisAlignedBB {
    return AxisAlignedBB(this.minX - v, this.minY - (if (modifyYDown) v else 0.0), this.minZ - v, this.maxX + v, this.maxY + (if (modifyYUp) v else 0.0), this.maxZ + v)
}