package common.trait

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

interface ILessonPostInit {
    fun postInit()
}

interface ILessonCleanUp {
    fun cleanUp()
}