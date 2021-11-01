package start_1

import common.CommonUtil
import common.trait.*
import fp.TMaybe
import fp.TMaybe.Companion.maybe
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWFramebufferSizeCallbackI
import org.lwjgl.glfw.GLFWKeyCallbackI
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL30.*
import org.lwjgl.system.MemoryUtil.NULL

object HelloRectangle_4: IShader, ILesson {
    override val width = 1024
    override val height = 768
    override val keyCb = GLFWKeyCallbackI { window, key, scancode, action, mods ->
        if (key == GLFW.GLFW_KEY_ESCAPE && action == GLFW.GLFW_PRESS)
            GLFW.glfwSetWindowShouldClose(window, true)
    }

    override val frameBufferSizeCb = GLFWFramebufferSizeCallbackI { window, width, height ->
        GL11.glViewport(0, 0, width, height)
    }
    override var window: Long = NULL

    override fun init() {
        window = CommonUtil.commonInit(width, height)

        glfwSetFramebufferSizeCallback(window, frameBufferSizeCb)
        glfwSetKeyCallback(window, keyCb)

        loadShaders(ShaderFileSrc("start_1/rectangle", "start_1/rectangle"))
        buffers = initBuffers()

        glfwShowWindow(window)
    }

    override fun loop() {
        CommonUtil.commonLoop(
            window,
            {}
        ) {
            glClearColor(.2f, .3f, .3f, 1f)
            glUseProgram(programRef)
            glBindVertexArray(buffers[0].VAO.get())
            glDrawElements(GL_TRIANGLES,6, GL_UNSIGNED_INT, 0)
        }
    }

    override val vertices = floatArrayOf(
        .5f,.5f,0f,
        .5f, -.5f, 0f,
        -.5f, -.5f, 0f,
        -.5f, .5f, 0f,
    )
    override var programRef: ProgramRef = 0
    override var buffers: Array<InitBufferResult> = emptyArray()
    override var shaders: Array<ShaderRef> = emptyArray()
    override var indices: TMaybe<IntArray> = intArrayOf(
        0,1,3,
        1,2,3
    ).maybe()

    private fun loadShaders(vararg files: ShaderFileSrc) {
        val vertShader = glCreateShader(GL_VERTEX_SHADER)
        var shaderContent = getVertexShaderContent(files[0].vertex) ?: throw RuntimeException("vertex shader not found")
        glShaderSource(vertShader, shaderContent)
        glCompileShader(vertShader)
        var success = IntArray(1)
        glGetShaderiv(vertShader, GL_COMPILE_STATUS, success)
        if (success[0] != GL_TRUE) {
            throw RuntimeException("VERTEX_SHADER::COMPILE::FAIL")
        }

        val fragShader = glCreateShader(GL_FRAGMENT_SHADER)
        shaderContent = getFragmentShaderContent(files[0].fragment) ?: throw RuntimeException("fragment shader not found")
        glShaderSource(fragShader, shaderContent)
        glCompileShader(fragShader)
        success = IntArray(1)
        glGetShaderiv(fragShader, GL_COMPILE_STATUS, success)
        if (success[0] != GL_TRUE) {
            throw RuntimeException("FRAGMENT_SHADER::COMPILE::FAIL")
        }

        programRef = glCreateProgram()
        glAttachShader(programRef, vertShader)
        glAttachShader(programRef, fragShader)
        glLinkProgram(programRef)
        success = IntArray(1)
        glGetProgramiv(programRef, GL_LINK_STATUS, success)
        if (success[0] != GL_TRUE) {
            throw RuntimeException("PROGRAM::LINK::FAIL")
        }

        shaders = arrayOf(
            ShaderRef(vertShader, fragShader)
        )
    }

    override fun initBuffers(): Array<InitBufferResult> {
        val VAO = IntArray(1)
        val VBO = IntArray(1)
        val EBO = IntArray(1)
        glGenVertexArrays(VAO)
        glGenBuffers(VBO)
        glGenBuffers(EBO)
        glBindVertexArray(VAO[0])

        glBindBuffer(GL_ARRAY_BUFFER, VBO[0])
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW)

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, EBO[0])
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices.get(), GL_STATIC_DRAW)

        glVertexAttribPointer(0, 3, GL_FLOAT, false, 3 * CommonUtil.TypeSize.FLOAT.size, 0L)
        glEnableVertexAttribArray(0)

        glBindBuffer(GL_ARRAY_BUFFER, 0)
        glBindVertexArray(0)

//        glPolygonMode(GL_FRONT_AND_BACK, GL_FILL)
        glPolygonMode(GL_FRONT_AND_BACK, GL_LINE)

        return arrayOf(
            InitBufferResult(
                VBO[0].maybe(),
                VAO[0].maybe(),
                EBO[0].maybe()
            )
        )
    }

    override fun cleanUp() {
        buffers.forEach {
            it.VAO.consume(::glDeleteVertexArrays)
            it.VBO.consume(::glDeleteBuffers)
            it.EBO.consume(::glDeleteBuffers)
        }

        glDeleteProgram(programRef)
    }
}