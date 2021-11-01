package start_1

import common.CommonUtil
import common.DefaultValue
import common.Shader
import common.trait.*
import fp.TMaybe
import fp.TMaybe.Companion.maybe
import fp.TNone
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWFramebufferSizeCallbackI
import org.lwjgl.glfw.GLFWKeyCallbackI
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL15
import org.lwjgl.opengl.GL15.glGenBuffers
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30
import org.lwjgl.opengl.GL30.*
import org.lwjgl.stb.STBImage.*
import org.lwjgl.system.MemoryUtil.NULL

object Texture2_6: IShader, ILesson {
    override val width: Int = DefaultValue.WIDTH
    override val height: Int = DefaultValue.HEIGHT
    override val keyCb: GLFWKeyCallbackI = DefaultValue.KEYBOARD_CALLBACK
    override val frameBufferSizeCb: GLFWFramebufferSizeCallbackI = DefaultValue.FRAME_BUFFER_SIZE_CALLBACK
    override var window: Long = NULL

    private lateinit var shader: Shader

    private var textures: IntArray = IntArray(2)

    override fun init() {
        glfwSetFramebufferSizeCallback(window, frameBufferSizeCb)
        glfwSetKeyCallback(window, keyCb)

        shader = Shader("start_1/texture", "start_1/texture2")
        buffers = initBuffers()

        getTexture()

        shader.use()
        shader.setInt("texture1", 0)
        shader.setInt("texture2", 1)

        glfwShowWindow(window)
    }

    private fun getTexture() {
        textures[0] = glGenTextures()
        val w = IntArray(1)
        val h = IntArray(1)
        val nrChannels = IntArray(1)
        stbi_set_flip_vertically_on_load(true)

        glBindTexture(GL_TEXTURE_2D, textures[0])

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)

        val data = stbi_load("texture/container.jpg", w, h, nrChannels, 0) ?: throw RuntimeException("Cannot decode container.jpg")
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, w[0], h[0], 0, GL_RGB, GL_UNSIGNED_BYTE, data)
        glGenerateMipmap(GL_TEXTURE_2D)
        stbi_image_free(data)

        textures[1] = glGenTextures()
        glBindTexture(GL_TEXTURE_2D, textures[1])

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)

        val data2 = stbi_load("texture/awesomeface.png", w, h, nrChannels, 0) ?: throw RuntimeException("Cannot decode awesomeface.png")
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, w[0], h[0], 0, GL_RGBA, GL_UNSIGNED_BYTE, data2)
        glGenerateMipmap(GL_TEXTURE_2D)
        stbi_image_free(data2)

    }

    override fun loop() {
        CommonUtil.commonLoop(
            window
        ) {
            glClearColor(.2f,.3f,.3f,1f)

            glActiveTexture(GL_TEXTURE0)
            glBindTexture(GL_TEXTURE_2D, textures[0])
            glActiveTexture(GL_TEXTURE1)
            glBindTexture(GL_TEXTURE_2D, textures[1])

            shader.use()

            buffers[0].VAO.consume(::glBindVertexArray)
            glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0)
        }
    }

    override val vertices: FloatArray = floatArrayOf(
        // coor         // color    // texture coor
         .5f,  .5f, 0f, 1f, 0f, 0f, 1f, 1f, // top right
         .5f, -.5f, 0f, 0f, 1f, 0f, 1f, 0f, // bot right
        -.5f, -.5f, 0f, 0f, 0f, 1f, 0f, 0f, // bot left
        -.5f,  .5f, 0f, 1f, 1f, 0f, 0f, 1f, // top left
    )
    override var programRef: ProgramRef = 0
    override var buffers: Array<InitBufferResult> = arrayOf()
    override var shaders: Array<ShaderRef> = arrayOf()
    override var indices: TMaybe<IntArray> = intArrayOf(
        0, 1, 3, // first triangle
        1, 2, 3, // second triangle
    ).maybe()

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

        // position
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 8 * CommonUtil.TypeSize.FLOAT.size, 0L)
        glEnableVertexAttribArray(0)
        // color
        glVertexAttribPointer(
            1,
            3,
            GL_FLOAT,
            false,
            8 * CommonUtil.TypeSize.FLOAT.size,
            3L * CommonUtil.TypeSize.FLOAT.size
        )
        glEnableVertexAttribArray(1)
        // texture coor
        glVertexAttribPointer(
            2,
            2,
            GL_FLOAT,
            false,
            8 * CommonUtil.TypeSize.FLOAT.size,
            6L * CommonUtil.TypeSize.FLOAT.size
        )
        glEnableVertexAttribArray(2)

        glBindBuffer(GL_ARRAY_BUFFER, 0)
        glBindVertexArray(0)

        return arrayOf(
            InitBufferResult(
                VBO[0].maybe(),
                VAO[0].maybe(),
                EBO[0].maybe()
            )
        )
    }

    override fun cleanUp() {
        CommonUtil.delBuffer(buffers)
        glDeleteProgram(shader.id)
    }
}