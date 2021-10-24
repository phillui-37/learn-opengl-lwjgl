package start_1

import common.CommonUtil
import common.DefaultValue
import common.Shader
import common.trait.*
import fp.TMaybe
import fp.TMaybe.Companion.maybe
import fp.TNone
import org.joml.Math
import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.BufferUtils
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFWFramebufferSizeCallbackI
import org.lwjgl.glfw.GLFWKeyCallbackI
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL13
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30
import org.lwjgl.stb.STBImage
import org.lwjgl.system.MemoryUtil.NULL

object Coordinate_8 : IShader2, ILesson, ITexture {
    override val width: Int = DefaultValue.WIDTH
    override val height: Int = DefaultValue.HEIGHT
    override val keyCb: GLFWKeyCallbackI = DefaultValue.KEYBOARD_CALLBACK
    override val frameBufferSizeCb: GLFWFramebufferSizeCallbackI = DefaultValue.FRAME_BUFFER_SIZE_CALLBACK
    override var window: Long = NULL

    override lateinit var shader: Shader
    override lateinit var textures: IntArray

    private val cubePositions = arrayOf(
        Vector3f(0f, 0f, 0f),
        Vector3f(2f, 5f, -15f),
        Vector3f(-1.5f, -2.2f, -2.5f),
        Vector3f(-3.8f, -2f, -12.3f),
        Vector3f(2.4f, -.4f, -3.5f),
        Vector3f(-1.7f, 3f, -7.5f),
        Vector3f(1.3f, -2f, -2.5f),
        Vector3f(1.5f, 2f, -2.5f),
        Vector3f(1.5f, .2f, -1.5f),
        Vector3f(-1.3f, 1f, -1.5f)
    )

    override fun init() {
        window = CommonUtil.commonInit(width, height)
        GLFW.glfwSetFramebufferSizeCallback(window, frameBufferSizeCb)
        GLFW.glfwSetKeyCallback(window, keyCb)


        shader = Shader("start_1/coor")
        initBuffers()

        textures = getTexture()

        shader.use()
        shader.setInt("texture1", 0)
        shader.setInt("texture2", 1)
        GL11.glEnable(GL11.GL_DEPTH_TEST)


        GLFW.glfwShowWindow(window)
    }

    override fun getTexture(): IntArray {
        val textures = IntArray(2)
        val w = IntArray(1)
        val h = IntArray(1)
        val nrChannels = IntArray(1)
        STBImage.stbi_set_flip_vertically_on_load(true)

        textures[0] = GL30.glGenTextures()
        GL30.glBindTexture(GL30.GL_TEXTURE_2D, textures[0])

        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_WRAP_S, GL30.GL_REPEAT)
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_WRAP_T, GL30.GL_REPEAT)
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MIN_FILTER, GL30.GL_LINEAR)
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MAG_FILTER, GL30.GL_LINEAR)

        var data = STBImage.stbi_load("texture/container.jpg", w, h, nrChannels, 0)
            ?: throw RuntimeException("Cannot decode container.jpg")
        GL30.glTexImage2D(GL30.GL_TEXTURE_2D, 0, GL30.GL_RGB, w[0], h[0], 0, GL30.GL_RGB, GL30.GL_UNSIGNED_BYTE, data)
        GL30.glGenerateMipmap(GL30.GL_TEXTURE_2D)
        STBImage.stbi_image_free(data)

        textures[1] = GL30.glGenTextures()
        GL30.glBindTexture(GL30.GL_TEXTURE_2D, textures[1])

        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_WRAP_S, GL30.GL_REPEAT)
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_WRAP_T, GL30.GL_REPEAT)
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MIN_FILTER, GL30.GL_LINEAR)
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MAG_FILTER, GL30.GL_LINEAR)

        data = STBImage.stbi_load("texture/awesomeface.png", w, h, nrChannels, 0)
            ?: throw RuntimeException("Cannot decode awesomeface.png")
        GL30.glTexImage2D(
            GL30.GL_TEXTURE_2D,
            0,
            GL30.GL_RGB,
            w[0],
            h[0],
            0,
            GL30.GL_RGBA,
            GL30.GL_UNSIGNED_BYTE,
            data
        )
        GL30.glGenerateMipmap(GL30.GL_TEXTURE_2D)
        STBImage.stbi_image_free(data)

        return textures
    }

    override fun loop() {
        CommonUtil.commonLoop(
            window
        ) {
            GL30.glClearColor(.2f, .3f, .3f, 1f)

            GL30.glActiveTexture(GL13.GL_TEXTURE0)
            GL30.glBindTexture(GL30.GL_TEXTURE_2D, textures[0])
            GL30.glActiveTexture(GL13.GL_TEXTURE1)
            GL30.glBindTexture(GL30.GL_TEXTURE_2D, textures[1])

            shader.use()
            val projM = Matrix4f()  // global -> view
                .perspective(
                    Math.toRadians(45f),
                    width.toFloat() / height.toFloat(),
                    .1f, 100f
                )
            val viewM = Matrix4f() // view -> clip
                .translate(Vector3f(0f, 0f, -3f)) // +ve: towards camera
            shader.setMat4("projection", projM)
            shader.setMat4("view", viewM)

            buffers[0].VAO.consume(GL30::glBindVertexArray)
            cubePositions.forEachIndexed { idx, it ->
                val model = Matrix4f()
                    .translate(it)
                    .rotate(Math.toRadians(20f * idx), Vector3f(1f, .3f, .5f))
                shader.setMat4("model", model)

                GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 36)
            }
        }
    }

    // x,y,z,tex.x,tex.y
    override val vertices: FloatArray = floatArrayOf(
        -0.5f, -0.5f, -0.5f, 0.0f, 0.0f,
        0.5f, -0.5f, -0.5f, 1.0f, 0.0f,
        0.5f, 0.5f, -0.5f, 1.0f, 1.0f,
        0.5f, 0.5f, -0.5f, 1.0f, 1.0f,
        -0.5f, 0.5f, -0.5f, 0.0f, 1.0f,
        -0.5f, -0.5f, -0.5f, 0.0f, 0.0f,

        -0.5f, -0.5f, 0.5f, 0.0f, 0.0f,
        0.5f, -0.5f, 0.5f, 1.0f, 0.0f,
        0.5f, 0.5f, 0.5f, 1.0f, 1.0f,
        0.5f, 0.5f, 0.5f, 1.0f, 1.0f,
        -0.5f, 0.5f, 0.5f, 0.0f, 1.0f,
        -0.5f, -0.5f, 0.5f, 0.0f, 0.0f,

        -0.5f, 0.5f, 0.5f, 1.0f, 0.0f,
        -0.5f, 0.5f, -0.5f, 1.0f, 1.0f,
        -0.5f, -0.5f, -0.5f, 0.0f, 1.0f,
        -0.5f, -0.5f, -0.5f, 0.0f, 1.0f,
        -0.5f, -0.5f, 0.5f, 0.0f, 0.0f,
        -0.5f, 0.5f, 0.5f, 1.0f, 0.0f,

        0.5f, 0.5f, 0.5f, 1.0f, 0.0f,
        0.5f, 0.5f, -0.5f, 1.0f, 1.0f,
        0.5f, -0.5f, -0.5f, 0.0f, 1.0f,
        0.5f, -0.5f, -0.5f, 0.0f, 1.0f,
        0.5f, -0.5f, 0.5f, 0.0f, 0.0f,
        0.5f, 0.5f, 0.5f, 1.0f, 0.0f,

        -0.5f, -0.5f, -0.5f, 0.0f, 1.0f,
        0.5f, -0.5f, -0.5f, 1.0f, 1.0f,
        0.5f, -0.5f, 0.5f, 1.0f, 0.0f,
        0.5f, -0.5f, 0.5f, 1.0f, 0.0f,
        -0.5f, -0.5f, 0.5f, 0.0f, 0.0f,
        -0.5f, -0.5f, -0.5f, 0.0f, 1.0f,

        -0.5f, 0.5f, -0.5f, 0.0f, 1.0f,
        0.5f, 0.5f, -0.5f, 1.0f, 1.0f,
        0.5f, 0.5f, 0.5f, 1.0f, 0.0f,
        0.5f, 0.5f, 0.5f, 1.0f, 0.0f,
        -0.5f, 0.5f, 0.5f, 0.0f, 0.0f,
        -0.5f, 0.5f, -0.5f, 0.0f, 1.0f
    )
    override var programRef: ProgramRef = 0
    override var buffers: Array<InitBufferResult> = arrayOf()
    override var shaders: Array<ShaderRef> = arrayOf()
    override var indices: TMaybe<IntArray> = TNone()

    override fun initBuffers() {
        val VAO = BufferUtils.createIntBuffer(1)
        val VBO = BufferUtils.createIntBuffer(1)
        GL30.glGenVertexArrays(VAO)
        GL30.glGenBuffers(VBO)

        val vaoVal = VAO.get()
        val vboVal = VBO.get()
        GL30.glBindVertexArray(vaoVal)

        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, vboVal)
        GL30.glBufferData(GL30.GL_ARRAY_BUFFER, vertices, GL30.GL_STATIC_DRAW)

        // position
        GL30.glVertexAttribPointer(0, 3, GL30.GL_FLOAT, false, 5 * Float.SIZE_BYTES, 0L)
        GL30.glEnableVertexAttribArray(0)
        // texture coor
        GL30.glVertexAttribPointer(
            1,
            2,
            GL30.GL_FLOAT,
            false,
            5 * Float.SIZE_BYTES,
            3L * Float.SIZE_BYTES
        )
        GL30.glEnableVertexAttribArray(1)

        buffers = arrayOf(
            InitBufferResult(
                vboVal.maybe(),
                vaoVal.maybe(),
                TNone()
            )
        )
    }

    override fun cleanUp() {
        CommonUtil.delBuffer(buffers)
        GL30.glDeleteProgram(shader.id)
    }
}