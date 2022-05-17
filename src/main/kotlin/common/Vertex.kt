package common

import org.joml.Vector2f
import org.joml.Vector3f

data class Vertex(
    val Position: Vector3f,
    val Normal: Vector3f,
    val TexCoords: Vector2f
) {
    fun flatten(): FloatArray {
        return floatArrayOf(
            Position.x,
            Position.y,
            Position.z,
            Normal.x,
            Normal.y,
            Normal.z,
            TexCoords.x,
            TexCoords.y,
        )
    }

    companion object {
        fun sizeof() = (3+3+2) * Float.SIZE_BYTES
    }
}