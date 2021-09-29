package common

import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWFramebufferSizeCallbackI
import org.lwjgl.glfw.GLFWKeyCallbackI
import org.lwjgl.opengl.GL11.glViewport

object DefaultValue {
    const val WIDTH=1024
    const val HEIGHT=768
    val KEYBOARD_CALLBACK = GLFWKeyCallbackI { window, key, scancode, action, mods ->
        if (key == GLFW_KEY_ESCAPE && action == GLFW_PRESS)
            glfwSetWindowShouldClose(window, true)
    }
    val FRAME_BUFFER_SIZE_CALLBACK = GLFWFramebufferSizeCallbackI {window, width, height ->
        glViewport(0,0,width,height)
    }

}