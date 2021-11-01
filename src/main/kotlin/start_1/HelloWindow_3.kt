package start_1

import common.CommonUtil
import common.trait.ILesson
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWFramebufferSizeCallbackI
import org.lwjgl.glfw.GLFWKeyCallbackI
import org.lwjgl.opengl.GL11.glClearColor
import org.lwjgl.opengl.GL11.glViewport
import org.lwjgl.system.MemoryUtil.NULL

object HelloWindow_3 : ILesson {
    override val width = 1024
    override val height = 768
    override var window: Long = NULL

    override val keyCb = GLFWKeyCallbackI { window, key, scancode, action, mods ->
        if (key == GLFW_KEY_ESCAPE && action == GLFW_PRESS)
            glfwSetWindowShouldClose(window, true)
    }

    override val frameBufferSizeCb = GLFWFramebufferSizeCallbackI { window, width, height ->
        glViewport(0, 0, width, height)
    }

    override fun init() {
        glfwSetFramebufferSizeCallback(window, frameBufferSizeCb)

        glfwSetKeyCallback(window, keyCb)

        glfwShowWindow(window)
    }

    override fun loop() {
        CommonUtil.commonLoop(
            window,
            {
                glClearColor(.2f, .3f, .3f, 1f)
            }
        ) {}
    }
}