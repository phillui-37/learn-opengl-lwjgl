package common

import org.joml.Math.*
import org.joml.Matrix4f
import org.joml.Vector3f

enum class CameraMovement {
    FORWARD, BACKWARD, LEFT, RIGHT
}

class Camera {
    companion object {
        private const val YAW = -90f
        private const val PITCH = 0f
        private const val SPEED = 250f
        private const val SENSITIVITY = .1f
        private const val ZOOM = 45f
    }

    var position: Vector3f
        private set
    var front: Vector3f
        private set
    lateinit var up: Vector3f
        private set
    lateinit var right: Vector3f
        private set
    var worldUp: Vector3f
        private set

    /**
     * using Euler angle
     */
    var yaw: Float
        private set
    var pitch: Float
        private set
    var movementSpeed: Float
        private set
    var mouseSensitivity: Float
        private set
    var zoom: Float
        private set

    constructor(position: Vector3f=Vector3f(0f, 0f, 0f), up: Vector3f=Vector3f(0f, 1f, 0f), yaw: Float=YAW, pitch: Float= PITCH) {
        front = Vector3f(0f,0f,-1f)
        movementSpeed = SPEED
        mouseSensitivity = SENSITIVITY
        zoom = ZOOM
        this.position = position
        this.yaw = yaw
        this.worldUp = up
        this.pitch = pitch
        updateCameraVectors()
    }

    constructor(posX: Float, posY: Float, posZ: Float, upX: Float, upY: Float, upZ: Float, yaw: Float, pitch: Float): this(Vector3f(posX, posY, posZ), Vector3f(upX, upY,upZ), yaw, pitch)


    fun getViewMatrix() = Matrix4f().lookAt(position, position.add(front, Vector3f()), up)

    fun processKeyboard(direction: CameraMovement, deltaTime: Float) {
        val velocity = movementSpeed * deltaTime
        when (direction) {
            CameraMovement.FORWARD -> position.add(front.mul(velocity, Vector3f()))
            CameraMovement.BACKWARD -> position.sub(front.mul(velocity, Vector3f()))
            CameraMovement.LEFT -> position.sub(right.mul(velocity, Vector3f()))
            CameraMovement.RIGHT -> position.add(right.mul(velocity, Vector3f()))
        }
    }

    fun processMouseMovement(xoffset: Float, yoffset: Float, constrainPitch: Boolean = true) {
        yaw += xoffset * mouseSensitivity
        pitch += yoffset * mouseSensitivity

        if (constrainPitch)
            pitch = max(-89f, min(89f, pitch))

        updateCameraVectors()
    }

    fun processMouseScroll(yoffset: Float) {
        zoom = max(1f, min(45f, zoom - yoffset))
    }


    private fun updateCameraVectors() {
        front = Vector3f(
            cos(toRadians(yaw)) * cos(toRadians(pitch)),
            sin(toRadians(pitch)),
            sin(toRadians(yaw)) * cos(toRadians(pitch))
        ).normalize()
        right = front.cross(worldUp, Vector3f()).normalize(Vector3f())
        up = right.cross(right, Vector3f()).normalize(Vector3f())
    }
}