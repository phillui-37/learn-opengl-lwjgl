package light_2

import common.*
import common.trait.*
import fp.TMaybe
import fp.TMaybe.Companion.maybe
import fp.TNone
import fp.curry2
import org.joml.Math.toRadians
import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.BufferUtils
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWCursorPosCallbackI
import org.lwjgl.glfw.GLFWFramebufferSizeCallbackI
import org.lwjgl.glfw.GLFWKeyCallbackI
import org.lwjgl.glfw.GLFWScrollCallbackI
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL30
import org.lwjgl.system.MemoryUtil.NULL

object Colors_1 : IShader3, ILesson, IMouseCb, IScrollCb, ILessonPostInit, ILessonCleanUp {
    override val width: Int = DefaultValue.WIDTH
    override val height: Int = DefaultValue.HEIGHT
    var lastFrame = 0f
    var deltaTime = 0f

    var firstMouse = true
    var lastX = width / 2f
    var lastY = height / 2f
    val camera = Camera(Vector3f(0f, 0f, 3f))

    private val lightPos = Vector3f(1.2f, 1f, 2f)

    override val vertices: FloatArray = floatArrayOf(
        -0.5f, -0.5f, -0.5f,
        0.5f, -0.5f, -0.5f,
        0.5f, 0.5f, -0.5f,
        0.5f, 0.5f, -0.5f,
        -0.5f, 0.5f, -0.5f,
        -0.5f, -0.5f, -0.5f,

        -0.5f, -0.5f, 0.5f,
        0.5f, -0.5f, 0.5f,
        0.5f, 0.5f, 0.5f,
        0.5f, 0.5f, 0.5f,
        -0.5f, 0.5f, 0.5f,
        -0.5f, -0.5f, 0.5f,

        -0.5f, 0.5f, 0.5f,
        -0.5f, 0.5f, -0.5f,
        -0.5f, -0.5f, -0.5f,
        -0.5f, -0.5f, -0.5f,
        -0.5f, -0.5f, 0.5f,
        -0.5f, 0.5f, 0.5f,

        0.5f, 0.5f, 0.5f,
        0.5f, 0.5f, -0.5f,
        0.5f, -0.5f, -0.5f,
        0.5f, -0.5f, -0.5f,
        0.5f, -0.5f, 0.5f,
        0.5f, 0.5f, 0.5f,

        -0.5f, -0.5f, -0.5f,
        0.5f, -0.5f, -0.5f,
        0.5f, -0.5f, 0.5f,
        0.5f, -0.5f, 0.5f,
        -0.5f, -0.5f, 0.5f,
        -0.5f, -0.5f, -0.5f,

        -0.5f, 0.5f, -0.5f,
        0.5f, 0.5f, -0.5f,
        0.5f, 0.5f, 0.5f,
        0.5f, 0.5f, 0.5f,
        -0.5f, 0.5f, 0.5f,
        -0.5f, 0.5f, -0.5f,
    )
    override var programRef: ProgramRef = 0
    override lateinit var buffers: Array<InitBufferResult>
    override lateinit var shaders: Array<ShaderRef>
    override var indices: TMaybe<IntArray> = TNone()
    override fun initBuffers(): Array<InitBufferResult> {
        val cubeVAO = BufferUtils.createIntBuffer(1)
        val VBO = BufferUtils.createIntBuffer(1)
        GL30.glGenVertexArrays(cubeVAO)
        GL30.glGenBuffers(VBO)

        val cubeVaoVal = cubeVAO.get()
        val vboVal = VBO.get()

        GL30.glBindVertexArray(cubeVaoVal)

        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, vboVal)
        GL30.glBufferData(GL30.GL_ARRAY_BUFFER, vertices, GL30.GL_STATIC_DRAW)

        // cube vao position
        GL30.glVertexAttribPointer(0, 3, GL30.GL_FLOAT, false, 3 * Float.SIZE_BYTES, 0L)
        GL30.glEnableVertexAttribArray(0)
        
        val lightVAO = BufferUtils.createIntBuffer(1)
        GL30.glGenVertexArrays(lightVAO)
        val lightVaoVal = lightVAO.get()
        GL30.glBindVertexArray(lightVaoVal)
        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, vboVal)
        // light src vao position
        GL30.glVertexAttribPointer(
            0,
            3,
            GL30.GL_FLOAT,
            false,
            3 * Float.SIZE_BYTES,
            0L
        )
        GL30.glEnableVertexAttribArray(0)

        return arrayOf(
            InitBufferResult(
                cubeVaoVal.maybe(),
                vboVal.maybe(),
                TNone()
            ),
            InitBufferResult(
                lightVaoVal.maybe(),
                vboVal.maybe(),
                TNone()
            )
        )
    }
    override fun cleanUp() {
        CommonUtil.delBuffer(buffers)
        GL30.glDeleteProgram(shader.id)
    }

    override val fragmentShaderPath: String = "light_2/colors"
    override val vertexShaderPath: String = "light_2/colors"
    override lateinit var shader: Shader
    private lateinit var lightShader: Shader

    override val keyCb: GLFWKeyCallbackI = GLFWKeyCallbackI { window, key, scancode, action, mods ->
        val fn = curry2(camera::processKeyboard)
        when (key) {
            GLFW_KEY_ESCAPE -> {
                glfwSetWindowShouldClose(window, true)
                ({})
            }
            GLFW_KEY_W -> fn(CameraMovement.FORWARD)
            GLFW_KEY_S -> fn(CameraMovement.BACKWARD)
            GLFW_KEY_A -> fn(CameraMovement.LEFT)
            GLFW_KEY_D -> fn(CameraMovement.RIGHT)
            else -> fn(CameraMovement.STAY)
        }(deltaTime)
    }
    override val frameBufferSizeCb: GLFWFramebufferSizeCallbackI = DefaultValue.FRAME_BUFFER_SIZE_CALLBACK
    override var window: Long = NULL
    override fun init() {
        glfwSetFramebufferSizeCallback(window, frameBufferSizeCb)
        glfwSetKeyCallback(window, keyCb)
        glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED)
        glfwSetCursorPosCallback(window, mouseCallback)
        glfwSetScrollCallback(window, scrollCb)

        lightShader = Shader("light_2/light")
    }

    override fun loop() {
        CommonUtil.commonLoop(window) {
            val now = glfwGetTime().toFloat()
            deltaTime = now - lastFrame
            lastFrame = now

            shader.use()
            shader.setVec3("objectColor", 1f, .5f, .31f)
            shader.setVec3("lightColor", 1f, 1f, 1f)

            val projection = Matrix4f()
                .perspective(toRadians(camera.zoom), width/height.toFloat(), .1f, 100f)
            val view = camera.getViewMatrix()

            shader.setMat4("projection", projection)
            shader.setMat4("view", view)

            val model = Matrix4f()
            shader.setMat4("model", model)

            GL30.glBindVertexArray(buffers[0].VAO.get())
            GL30.glDrawArrays(GL11.GL_TRIANGLES, 0, 36)

            with(lightShader) {
                use()
                setMat4("projection", projection)
                setMat4("view", view)
                val model2 = model
                    .translate(lightPos, Matrix4f())
                    .scale(Vector3f(.2f))
                setMat4("model", model2)
            }
            GL30.glBindVertexArray(buffers[1].VAO.get())
            GL30.glDrawArrays(GL11.GL_TRIANGLES, 0, 36)
        }
    }

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

    override fun postInit() {
        GL11.glEnable(GL11.GL_DEPTH_TEST)

        glfwShowWindow(window)
    }
}