package common

import common.trait.InitBufferResult
import common.trait.getFragmentShaderContent
import common.trait.getVertexShaderContent
import fp.notNull
import org.lwjgl.BufferUtils
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL20.*
import org.lwjgl.opengl.GL30.glDeleteVertexArrays
import org.lwjgl.opengl.GL30.glGenerateMipmap
import org.lwjgl.opengl.GLUtil
import org.lwjgl.stb.STBImage.*
import org.lwjgl.system.MemoryUtil.NULL
import org.lwjgl.system.Platform
import java.nio.ByteBuffer

object CommonUtil {
    fun commonLoop(
        window: Long,
        fnOnce: () -> Unit,
        fnLoop: () -> Unit
    ) {
        fnOnce()
        while (!glfwWindowShouldClose(window)) {
            glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT) // clear the framebuffer

            fnLoop()

            glfwSwapBuffers(window)
            glfwPollEvents()
        }
    }

    fun commonLoop(
        window: Long,
        fnLoop: () -> Unit
    ) {
        commonLoop(window, {}, fnLoop)
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
        if (Platform.get() == Platform.MACOSX) {
            glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE)
        }

        val window = glfwCreateWindow(width, height, "LWJGL test", NULL, NULL)
        if (window == NULL)
            throw RuntimeException("Cannot create window")
        glfwMakeContextCurrent(window)
        GL.createCapabilities()
        GLUtil.setupDebugMessageCallback(System.err)

        return window
    }

    fun getShaderFileContent(filename: String): String? {
        return javaClass.classLoader
            ?.getResourceAsStream(filename)
            ?.buffered()
            ?.readAllBytes()
            ?.decodeToString()
    }

    fun getTextureFileContent(filename: String): ByteBuffer? {
        val data = javaClass.classLoader
            ?.getResourceAsStream("texture/$filename")
            ?.buffered()
            ?.readAllBytes()
        return data?.let(ByteBuffer::wrap)

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
            throw RuntimeException("VERTEX_SHADER::COMPILE::FAIL, code: ${success[0]}")
        return vertShader
    }

    fun initFragmentShader(filename: String): Int {
        val fragShader = glCreateShader(GL_FRAGMENT_SHADER)
        val shaderContent = getFragmentShaderContent(filename) ?: throw RuntimeException("fragment shader not found")
        glShaderSource(fragShader, shaderContent)
        glCompileShader(fragShader)
        val success = IntArray(1)
        glGetShaderiv(fragShader, GL_COMPILE_STATUS, success)
        if (success[0] != GL_TRUE)
            throw RuntimeException("FRAGMENT_SHADER::COMPILE::FAIL, code: ${success[0]}")
        return fragShader
    }

    fun delBuffer(buffers: Array<InitBufferResult>) {
        buffers.forEach {
            it.VAO.consume(::glDeleteVertexArrays)
            it.VBO.consume(::glDeleteBuffers)
            it.EBO.consume(::glDeleteBuffers)
        }
    }

    fun loadTexture(path: String): Int {
        val textureIdBuffer = BufferUtils.createIntBuffer(1)
        glGenTextures(textureIdBuffer)
        val textureId = textureIdBuffer.get()
        stbi_set_flip_vertically_on_load(true)

        val width = BufferUtils.createIntBuffer(1)
        val height = BufferUtils.createIntBuffer(1)
        val nrComponents = BufferUtils.createIntBuffer(1)
        val data = stbi_load(path, width, height, nrComponents, 0)
        data.notNull {
            val format = when (val n = nrComponents.get()) {
                1 -> GL_RED
                3 -> GL_RGB
                4 -> GL_RGBA
                else -> throw RuntimeException("Invalid nrComponents value $n")
            }
            glBindTexture(GL_TEXTURE_2D, textureId)
            GL11.glTexImage2D(GL_TEXTURE_2D, 0, format, width.get(), height.get(), 0, format, GL_UNSIGNED_BYTE, it)
            glGenerateMipmap(GL_TEXTURE_2D)

            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
        }
        stbi_image_free(data)
        return textureId
    }
}