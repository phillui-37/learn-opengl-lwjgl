import commonInterface.InitBufferResult
import commonInterface.getFragmentShaderContent
import commonInterface.getVertexShaderContent
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL20.*
import org.lwjgl.opengl.GL30
import org.lwjgl.opengl.GL30.glDeleteVertexArrays
import org.lwjgl.system.MemoryUtil.NULL

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

            glfwSwapBuffers(window)
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
            glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE)
        }

        val window = glfwCreateWindow(width, height, "LWJGL test", NULL, NULL)
        if (window == NULL)
            throw RuntimeException("Cannot create window")
        glfwMakeContextCurrent(window)
        GL.createCapabilities()

        return window
    }

    fun getShaderFileContent(filename: String): String? {
        return javaClass.classLoader
            ?.getResourceAsStream(filename)
            ?.buffered()
            ?.readAllBytes()
            ?.decodeToString()
    }

    enum class TypeSize(val size: Int) {
        FLOAT(4),
        DOUBLE(8)
    }

    fun initVertexShader(filename: String): Int {
        val vertShader = glCreateShader(GL_VERTEX_SHADER)
        val shaderContent = getVertexShaderContent(filename) ?: throw RuntimeException("vertex shader not found")
        glShaderSource(vertShader, shaderContent)
        glCompileShader(vertShader)
        val success = IntArray(1)
        glGetShaderiv(vertShader, GL_COMPILE_STATUS, success)
        if(success[0] != GL_TRUE)
            throw RuntimeException("VERTEX_SHADER::COMPILE::FAIL")
        return vertShader
    }

    fun initFragmentShader(filename: String): Int {
        val fragShader = glCreateShader(GL_FRAGMENT_SHADER)
        val shaderContent = getFragmentShaderContent(filename) ?: throw RuntimeException("fragment shader not found")
        glShaderSource(fragShader, shaderContent)
        glCompileShader(fragShader)
        val success = IntArray(1)
        GL30.glGetShaderiv(fragShader, GL_COMPILE_STATUS, success)
        if (success[0] != GL_TRUE)
            throw RuntimeException("FRAGMENT_SHADER::COMPILE::FAIL")
        return fragShader
    }

    fun delBuffer(buffers: Array<InitBufferResult>) {
        buffers.forEach {
            it.VAO.consume(::glDeleteVertexArrays)
            it.VBO.consume(::glDeleteBuffers)
            it.EBO.consume(::glDeleteBuffers)
        }
    }
}