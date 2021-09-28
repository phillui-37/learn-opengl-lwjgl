package start_1

import CommonUtil.delBuffer
import CommonUtil.initFragmentShader
import CommonUtil.initVertexShader
import commonInterface.*
import fp.TMaybe
import fp.TMaybe.Companion.maybe
import fp.TNone
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL20.*
import org.lwjgl.opengl.GL30
import org.lwjgl.opengl.GL30.glBindVertexArray
import org.lwjgl.system.MemoryUtil.NULL

object ColorTriangle_5: IShader, ILesson {
    override val width = DefaultValue.WIDTH
    override val height = DefaultValue.HEIGHT
    override val keyCb = DefaultValue.KEYBOARD_CALLBACK

    override val frameBufferSizeCb = DefaultValue.FRAME_BUFFER_SIZE_CALLBACK
    override var window: Long = NULL

    override fun init() {
        window = CommonUtil.commonInit(width, height)

        glfwSetFramebufferSizeCallback(window, frameBufferSizeCb)
        glfwSetKeyCallback(window, keyCb)

        loadShaders(ShaderFileSrc("start_1/color_triangle", "start_1/color_triangle"))
        initBuffers()

        glfwShowWindow(window)
    }

    override fun loop() {
        CommonUtil.commonLoop(
            window,
            {}
        ) {
            glClearColor(.2f,.3f,.3f,1f)
            glUseProgram(programRef)
            buffers[0].VAO.consume(::glBindVertexArray)
            glDrawArrays(GL_TRIANGLES, 0, 3)
        }
    }

    override val vertices = floatArrayOf(
        -.5f, -.5f, 0f,
        .5f, -.5f, 0f,
        0f, .5f, 0f
    )
    override var programRef = 0
    override var buffers: Array<InitBufferResult> = emptyArray()
    override var shaders: Array<ShaderRef> = emptyArray()
    override var indices: TMaybe<IntArray> = TNone()

    override fun loadShaders(vararg files: ShaderFileSrc) {
        val vert = initVertexShader(files[0].vertex)
        val frag = initFragmentShader(files[0].fragment)
        programRef = glCreateProgram()
        glAttachShader(programRef, vert)
        glAttachShader(programRef, frag)
        glLinkProgram(programRef)
        val success = IntArray(1)
        glGetProgramiv(programRef, GL_LINK_STATUS, success)
        if (success[0] != GL_TRUE) {
            throw RuntimeException("PROGRAM::LINK::FAIL")
        }

        shaders = arrayOf(
            ShaderRef(vert, frag)
        )
    }

    override fun initBuffers() {
        val VAO = IntArray(1)
        val VBO = IntArray(1)
        GL30.glGenVertexArrays(VAO)
        glGenBuffers(VBO)
        glBindVertexArray(VAO[0])

        glBindBuffer(GL_ARRAY_BUFFER, VBO[0])
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW)
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 3 * CommonUtil.TypeSize.FLOAT.size, 0L)
        glEnableVertexAttribArray(0)
        glBindBuffer(GL_ARRAY_BUFFER, 0)
        glBindVertexArray(0)

        buffers = arrayOf(
            InitBufferResult(
                VBO[0].maybe(),
                VAO[0].maybe(),
                TNone()
            )
        )
    }

    override fun cleanUp() {
        delBuffer(buffers)
        glDeleteProgram(programRef)
    }
}