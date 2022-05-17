package start_1

import common.*
import common.trait.*
import fp.TMaybe
import fp.TMaybe.Companion.maybe
import fp.TNone
import fp.curry2
import org.joml.Math
import org.joml.Math.toRadians
import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.BufferUtils
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWCursorPosCallbackI
import org.lwjgl.glfw.GLFWKeyCallbackI
import org.lwjgl.glfw.GLFWScrollCallbackI
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL11.GL_DEPTH_TEST
import org.lwjgl.opengl.GL11.glEnable
import org.lwjgl.opengl.GL13
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30
import org.lwjgl.stb.STBImage
import org.lwjgl.system.MemoryUtil.NULL

object Camera_Keyboard_Ex_9 : IShader3, ILesson, ITexture, IMouseCb, IScrollCb, ILessonPostInit, ILessonCleanUp {
    override val width = DefaultValue.WIDTH
    override val height = DefaultValue.HEIGHT
    override val keyCb = GLFWKeyCallbackI { window, key, scancode, action, mods ->
        val fn = curry2(camera::processKeyboardFPS) // ex1
        val fn2: (Float) -> Unit
        when (key) {
            GLFW_KEY_ESCAPE -> {
                glfwSetWindowShouldClose(window, true)
                fn2 = {}
            }
            GLFW_KEY_W -> fn2 = fn(CameraMovement.FORWARD)
            GLFW_KEY_S -> fn2 = fn(CameraMovement.BACKWARD)
            GLFW_KEY_A -> fn2 = fn(CameraMovement.LEFT)
            GLFW_KEY_D -> fn2 = fn(CameraMovement.RIGHT)
            else -> fn2 = fn(CameraMovement.STAY)
        }
        fn2(deltaTime)
    }
    override val frameBufferSizeCb = DefaultValue.FRAME_BUFFER_SIZE_CALLBACK
    override var window: Long = NULL
    var lastFrame = 0f
    var deltaTime = 0f

    var firstMouse = true
    var lastX = width / 2f
    var lastY = height / 2f
    val camera = Camera(Vector3f(0f, 0f, 3f))

    override fun init() {
        glfwSetFramebufferSizeCallback(window, frameBufferSizeCb)
        glfwSetKeyCallback(window, keyCb)
        glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED)
        glfwSetCursorPosCallback(window, mouseCallback)
        glfwSetScrollCallback(window, scrollCb)
    }

    override fun postInit() {
        shader.use()
        shader.setInt("texture1", 0)
        shader.setInt("texture2", 1)
        glEnable(GL_DEPTH_TEST)

        glfwShowWindow(window)
    }

    override fun loop() {
        while (!glfwWindowShouldClose(window)) {
            val currentFrame = glfwGetTime().toFloat()
            deltaTime = currentFrame - lastFrame
            lastFrame = currentFrame

            GL30.glClearColor(.2f, .3f, .3f, 1f)
            GL20.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT) // clear the framebuffer


            GL30.glActiveTexture(GL13.GL_TEXTURE0)
            GL30.glBindTexture(GL30.GL_TEXTURE_2D, textures[0])
            GL30.glActiveTexture(GL13.GL_TEXTURE1)
            GL30.glBindTexture(GL30.GL_TEXTURE_2D, textures[1])

            shader.use()

            val projection = Matrix4f()
                .perspective(toRadians(camera.zoom), width.toFloat() / height.toFloat(), .1f, 100f)
            shader.setMat4("projection", projection)

            /**
             * look at Matrix
             * [R.x R.y R.z 0]   [1 0 0 -P.x]
             * [U.x U.y U.z 0] * [0 1 0 -P.y]
             * [D.x D.y D.z 0]   [0 0 1 -P.z]
             * [0   0   0   1]   [0 0 0  1]
             *
             * R: right vector(X)
             * U: up vector(Y)
             * D: direction vector(Z), convention OpenGL cam point to +Z but focus to -Z, so the vector is calculated by `camera position - camera target`
             * P: Camera position vector in global space
             *
             * cal flow: P -> D -> R -> U
             */
            val viewM = camera.getViewMatrixCameraEx()
            shader.setMat4("view", viewM)

            buffers[0].VAO.consume(GL30::glBindVertexArray)
            cubePositions.forEachIndexed { idx, it ->
                val model = Matrix4f()
                    .translate(it)
                    .rotate(Math.toRadians(20f * idx), Vector3f(1f, .3f, .5f))
                shader.setMat4("model", model)

                GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 36)
            }

            GLFW.glfwSwapBuffers(window)
            GLFW.glfwPollEvents()
        }
    }

    override lateinit var shader: Shader
    override val vertices = floatArrayOf(
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

    private val cubePositions = arrayOf(
        Vector3f(0.0f, 0.0f, 0.0f),
        Vector3f(2.0f, 5.0f, -15.0f),
        Vector3f(-1.5f, -2.2f, -2.5f),
        Vector3f(-3.8f, -2.0f, -12.3f),
        Vector3f(2.4f, -0.4f, -3.5f),
        Vector3f(-1.7f, 3.0f, -7.5f),
        Vector3f(1.3f, -2.0f, -2.5f),
        Vector3f(1.5f, 2.0f, -2.5f),
        Vector3f(1.5f, 0.2f, -1.5f),
        Vector3f(-1.3f, 1.0f, -1.5f)
    )

    override fun initBuffers(): Array<InitBufferResult> {
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

        return arrayOf(
            InitBufferResult(
                vaoVal.maybe(),
                vboVal.maybe(),
                TNone()
            )
        )
    }

    override fun cleanUp() {
        CommonUtil.delBuffer(buffers)
        GL30.glDeleteProgram(shader.id)
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

    override lateinit var textures: IntArray
    override val mouseCallback = GLFWCursorPosCallbackI { window, xpos, ypos ->
        if (firstMouse) {
            lastX = xpos.toFloat()
            lastY = ypos.toFloat()
            firstMouse = false
        }

        val xoffset = xpos - lastX
        val yoffset = lastY - ypos // y coor go from bot to top
        lastX = xpos.toFloat()
        lastY = ypos.toFloat()

        camera.processMouseMovement(xoffset.toFloat(), yoffset.toFloat())
    }
    override val scrollCb = GLFWScrollCallbackI { window, xoffset, yoffset ->
        camera.processMouseScroll(yoffset.toFloat())
    }
    override val fragmentShaderPath = "start_1/camera.frag"
    override val vertexShaderPath = "start_1/camera.vert"
}