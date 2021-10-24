package common.trait

import org.lwjgl.glfw.GLFWCursorPosCallbackI

interface IMouseCb {
    val mouseCallback: GLFWCursorPosCallbackI
}