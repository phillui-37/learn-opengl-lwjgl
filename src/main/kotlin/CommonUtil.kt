import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL11.*
import org.lwjgl.system.MemoryUtil.NULL
import start_1.HelloWindow_3

object CommonUtil {
    fun commonLoop(
        window: Long,
        fnOnce: () -> Unit,
        fnLoop: () -> Unit
    ) {
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities()
        fnOnce()
        while (!glfwWindowShouldClose(window)) {
            glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT) // clear the framebuffer

            fnLoop()

            glfwSwapBuffers(HelloWindow_3.window)
            glfwPollEvents()
        }
    }

    fun commonInit(
        width: Int,
        height: Int,
    ): Long {
        GLFWErrorCallback.createPrint(System.err).set()
        if (!glfwInit())
            throw IllegalStateException("GLFW::init::FAILED")

        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3)
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3)
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE)
        if (System.getProperty("os.name").toLowerCase().contains("mac")) {
            glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL11.GL_TRUE)
        }

        val window = glfwCreateWindow(width, height, "LWJGL test", NULL, NULL)
        if (window == NULL)
            throw RuntimeException("Cannot create window")
        glfwMakeContextCurrent(window)

        return window
    }

    fun getShaderFileContent(filename: String): String? {
        return javaClass.classLoader
            ?.getResourceAsStream(filename)
            ?.buffered()
            ?.readAllBytes()
            ?.decodeToString()
    }
}