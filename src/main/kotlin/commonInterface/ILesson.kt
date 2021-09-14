package commonInterface

import org.lwjgl.glfw.GLFWFramebufferSizeCallbackI
import org.lwjgl.glfw.GLFWKeyCallbackI

interface ILesson {
    val width: Int
    val height: Int
    val keyCb: GLFWKeyCallbackI
    val frameBufferSizeCb: GLFWFramebufferSizeCallbackI
    var window: Long
    fun init()
    fun loop()
}