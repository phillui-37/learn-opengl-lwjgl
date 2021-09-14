package start_1

import CommonUtil
import commonInterface.ILesson
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWFramebufferSizeCallbackI
import org.lwjgl.glfw.GLFWKeyCallbackI
import org.lwjgl.opengl.GL11
import org.lwjgl.system.MemoryUtil.NULL

object HelloTriangle_4: ILesson {
    override val width = 1024
    override val height = 768
    override val keyCb = GLFWKeyCallbackI { window, key, scancode, action, mods ->
        if (key == GLFW_KEY_ESCAPE && action == GLFW_PRESS)
            glfwSetWindowShouldClose(window, true)
    }
    override val frameBufferSizeCb = GLFWFramebufferSizeCallbackI { window, width, height ->
        GL11.glViewport(0, 0, width, height)
    }
    override var window: Long = NULL

    override fun init() {
        window = CommonUtil.commonInit(width, height)

        glfwSetFramebufferSizeCallback(window, frameBufferSizeCb)

        glfwSetKeyCallback(window, keyCb)

        glfwShowWindow(window)
    }

    override fun loop() {

    }
}