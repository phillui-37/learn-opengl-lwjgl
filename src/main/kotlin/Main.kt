import org.lwjgl.Version
import org.lwjgl.glfw.Callbacks.glfwFreeCallbacks
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.glfw.GLFWFramebufferSizeCallbackI
import org.lwjgl.glfw.GLFWKeyCallbackI
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11.*
import org.lwjgl.system.MemoryStack.stackPush
import org.lwjgl.system.MemoryUtil.NULL


private var window: Long = NULL

private const val WIDTH = 1024
private const val HEIGHT = 768

fun main() {
    println("LWJGL ${Version.getVersion()}")
    init()
    loop()

    glfwFreeCallbacks(window);
    glfwDestroyWindow(window);

    glfwTerminate();
    glfwSetErrorCallback(null)?.free();
}

private fun init() {
    GLFWErrorCallback.createPrint(System.err).set()
    if (!glfwInit())
        throw IllegalStateException("GLFW::init::FAILED")
    glfwDefaultWindowHints()

    window = glfwCreateWindow(WIDTH, HEIGHT, "LWJGL test", NULL, NULL)
    if (window==NULL)
        throw RuntimeException("Cannot create window")
    glfwMakeContextCurrent(window)
    glfwSetFramebufferSizeCallback(window, frameBufferSizeCb)

    glfwSetKeyCallback(window, keyCb)

    glfwShowWindow(window)
}

private fun loop() {
    // This line is critical for LWJGL's interoperation with GLFW's
    // OpenGL context, or any context that is managed externally.
    // LWJGL detects the context that is current in the current thread,
    // creates the GLCapabilities instance and makes the OpenGL
    // bindings available for use.
    GL.createCapabilities();

    // Set the clear color
    // Set the clear color
    glClearColor(.2f, .3f, .3f, 1f)

    // Run the rendering loop until the user has attempted to close
    // the window or has pressed the ESCAPE key.

    // Run the rendering loop until the user has attempted to close
    // the window or has pressed the ESCAPE key.
    while (!glfwWindowShouldClose(window)) {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT) // clear the framebuffer


        glfwSwapBuffers(window) // swap the color buffers

        // Poll for window events. The key callback above will only be
        // invoked during this call.
        glfwPollEvents()
    }
}

val keyCb = GLFWKeyCallbackI { window, key, scancode, action, mods ->
    if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
        glfwSetWindowShouldClose(window, true)
}

val frameBufferSizeCb = GLFWFramebufferSizeCallbackI { window, width, height ->
    glViewport(0,0,width,height)
}