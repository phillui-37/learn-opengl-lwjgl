package light_2

import common.*
import common.trait.ILesson
import fp.curry2
import fp.tuple
import org.joml.Math
import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.BufferUtils
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFWCursorPosCallbackI
import org.lwjgl.glfw.GLFWFramebufferSizeCallbackI
import org.lwjgl.glfw.GLFWKeyCallbackI
import org.lwjgl.glfw.GLFWScrollCallbackI
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL13
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30
import org.lwjgl.system.MemoryUtil

// point light, attenuation
// F~att~ = (K~c~ + K~l~ * d + K~q~ * d^2^)^-1^
// F~att~ = intensity
// K... = constant

object LightingCasters_5_2 : ILesson {
    override val width: Int = DefaultValue.WIDTH
    override val height: Int = DefaultValue.HEIGHT
    override val keyCb: GLFWKeyCallbackI = GLFWKeyCallbackI { window, key, scancode, action, mods ->
        val fn = curry2(camera::processKeyboard)
        when (key) {
            GLFW.GLFW_KEY_ESCAPE -> {
                GLFW.glfwSetWindowShouldClose(window, true)
                ({})
            }
            GLFW.GLFW_KEY_W -> fn(CameraMovement.FORWARD)
            GLFW.GLFW_KEY_S -> fn(CameraMovement.BACKWARD)
            GLFW.GLFW_KEY_A -> fn(CameraMovement.LEFT)
            GLFW.GLFW_KEY_D -> fn(CameraMovement.RIGHT)
            else -> fn(CameraMovement.STAY)
        }(deltaTime)
    }
    override val frameBufferSizeCb: GLFWFramebufferSizeCallbackI = DefaultValue.FRAME_BUFFER_SIZE_CALLBACK
    override var window: Long = MemoryUtil.NULL

    private val vertices = floatArrayOf(
        // positions          // normals           // texture coords
        -0.5f, -0.5f, -0.5f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f,
        0.5f, -0.5f, -0.5f, 0.0f, 0.0f, -1.0f, 1.0f, 0.0f,
        0.5f, 0.5f, -0.5f, 0.0f, 0.0f, -1.0f, 1.0f, 1.0f,
        0.5f, 0.5f, -0.5f, 0.0f, 0.0f, -1.0f, 1.0f, 1.0f,
        -0.5f, 0.5f, -0.5f, 0.0f, 0.0f, -1.0f, 0.0f, 1.0f,
        -0.5f, -0.5f, -0.5f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f,

        -0.5f, -0.5f, 0.5f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f,
        0.5f, -0.5f, 0.5f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f,
        0.5f, 0.5f, 0.5f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f,
        0.5f, 0.5f, 0.5f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f,
        -0.5f, 0.5f, 0.5f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f,
        -0.5f, -0.5f, 0.5f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f,

        -0.5f, 0.5f, 0.5f, -1.0f, 0.0f, 0.0f, 1.0f, 0.0f,
        -0.5f, 0.5f, -0.5f, -1.0f, 0.0f, 0.0f, 1.0f, 1.0f,
        -0.5f, -0.5f, -0.5f, -1.0f, 0.0f, 0.0f, 0.0f, 1.0f,
        -0.5f, -0.5f, -0.5f, -1.0f, 0.0f, 0.0f, 0.0f, 1.0f,
        -0.5f, -0.5f, 0.5f, -1.0f, 0.0f, 0.0f, 0.0f, 0.0f,
        -0.5f, 0.5f, 0.5f, -1.0f, 0.0f, 0.0f, 1.0f, 0.0f,

        0.5f, 0.5f, 0.5f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f,
        0.5f, 0.5f, -0.5f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f,
        0.5f, -0.5f, -0.5f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f,
        0.5f, -0.5f, -0.5f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f,
        0.5f, -0.5f, 0.5f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f,
        0.5f, 0.5f, 0.5f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f,

        -0.5f, -0.5f, -0.5f, 0.0f, -1.0f, 0.0f, 0.0f, 1.0f,
        0.5f, -0.5f, -0.5f, 0.0f, -1.0f, 0.0f, 1.0f, 1.0f,
        0.5f, -0.5f, 0.5f, 0.0f, -1.0f, 0.0f, 1.0f, 0.0f,
        0.5f, -0.5f, 0.5f, 0.0f, -1.0f, 0.0f, 1.0f, 0.0f,
        -0.5f, -0.5f, 0.5f, 0.0f, -1.0f, 0.0f, 0.0f, 0.0f,
        -0.5f, -0.5f, -0.5f, 0.0f, -1.0f, 0.0f, 0.0f, 1.0f,

        -0.5f, 0.5f, -0.5f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f,
        0.5f, 0.5f, -0.5f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f,
        0.5f, 0.5f, 0.5f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f,
        0.5f, 0.5f, 0.5f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f,
        -0.5f, 0.5f, 0.5f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f,
        -0.5f, 0.5f, -0.5f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f
    )

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

    private val mouseCallback = GLFWCursorPosCallbackI { window, xpos, ypos ->
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
    private val scrollCb = GLFWScrollCallbackI { window, xoffset, yoffset ->
        camera.processMouseScroll(yoffset.toFloat())
    }

    private val shaders = mutableListOf<Shader>()
    private var vbo = 0
    private var cubeVao = 0
    private var lightCubeVao = 0
    private var diffuseMap = 0
    private var specularMap = 0

    override fun init() {
        GLFW.glfwSetFramebufferSizeCallback(window, frameBufferSizeCb)
        GLFW.glfwSetCursorPosCallback(window, mouseCallback)
        GLFW.glfwSetScrollCallback(window, scrollCb)
        GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED)
        GL11.glEnable(GL11.GL_DEPTH_TEST)

        shaders.add(Shader("light_2/light_caster", "light_2/light_caster_2"))
        shaders.add(Shader("light_2/light"))

        val vboBuffer = BufferUtils.createIntBuffer(1)
        val cubeVaoBuffer = BufferUtils.createIntBuffer(1)
        GL30.glGenVertexArrays(cubeVaoBuffer)
        GL30.glGenBuffers(vboBuffer)

        vbo = vboBuffer.get()
        cubeVao = cubeVaoBuffer.get()

        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, vbo)
        GL30.glBufferData(GL30.GL_ARRAY_BUFFER, vertices, GL30.GL_STATIC_DRAW)
        GL30.glBindVertexArray(cubeVao)

        GL30.glVertexAttribPointer(0, 3, GL30.GL_FLOAT, false, 8 * Float.SIZE_BYTES, 0)
        GL30.glEnableVertexAttribArray(0)

        GL30.glVertexAttribPointer(1, 3, GL30.GL_FLOAT, false, 8 * Float.SIZE_BYTES, 3L * Float.SIZE_BYTES)
        GL30.glEnableVertexAttribArray(1)

        GL30.glVertexAttribPointer(2, 2, GL30.GL_FLOAT, false, 8 * Float.SIZE_BYTES, 6L * Float.SIZE_BYTES)
        GL30.glEnableVertexAttribArray(2)

        val lightCubeVaoBuffer = BufferUtils.createIntBuffer(1)
        GL30.glGenVertexArrays(lightCubeVaoBuffer)
        lightCubeVao = lightCubeVaoBuffer.get()
        GL30.glBindVertexArray(lightCubeVao)
        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, vbo)
        GL20.glVertexAttribPointer(0, 3, GL30.GL_FLOAT, false, 8 * Float.SIZE_BYTES, 0)
        GL30.glEnableVertexAttribArray(0)

        diffuseMap = CommonUtil.loadTexture("texture/container2.png")
        specularMap = CommonUtil.loadTexture("texture/container2_specular.png")

        shaders[0].use()
        shaders[0].setInt("material.diffuse", 0)
        shaders[0].setInt("material.specular", 1)
    }

    private var deltaTime = 0f
    private var lastFrame = 0f
    private val camera = Camera(Vector3f(0f, 0f, 3f))
    private var lastX = width / 2f
    private var lastY = height / 2f
    private val lightPos = Vector3f(1.2f, 1f, 2f)
    private var firstMouse = true

    private fun processInput(window: Long) {
        arrayOf(
            GLFW.glfwGetKey(window, GLFW.GLFW_KEY_ESCAPE) tuple { GLFW.glfwSetWindowShouldClose(window, true) },
            GLFW.glfwGetKey(window, GLFW.GLFW_KEY_W) tuple {
                camera.processKeyboard(
                    CameraMovement.FORWARD,
                    deltaTime
                )
            },
            GLFW.glfwGetKey(window, GLFW.GLFW_KEY_S) tuple {
                camera.processKeyboard(
                    CameraMovement.BACKWARD,
                    deltaTime
                )
            },
            GLFW.glfwGetKey(window, GLFW.GLFW_KEY_A) tuple { camera.processKeyboard(CameraMovement.LEFT, deltaTime) },
            GLFW.glfwGetKey(window, GLFW.GLFW_KEY_D) tuple { camera.processKeyboard(CameraMovement.RIGHT, deltaTime) }
        )
            .forEach { if (it._1 == GLFW.GLFW_PRESS) it._2() }
    }

    override fun loop() {
        var currentFrame: Float
        val lightingShader = shaders[0]
        val lightCubeShader = shaders[1]
        while (!GLFW.glfwWindowShouldClose(window)) {
            currentFrame = GLFW.glfwGetTime().toFloat()
            deltaTime = currentFrame - lastFrame
            lastFrame = currentFrame

            processInput(window)

            GL30.glClearColor(.1f, .1f, .1f, 1f)
            GL30.glClear(GL30.GL_COLOR_BUFFER_BIT or GL30.GL_DEPTH_BUFFER_BIT)


            val projection = Matrix4f()
                .perspective(Math.toRadians(camera.zoom), (width / height).toFloat(), .1f, 100f)
            val view = camera.getViewMatrix()
            val diffuseColor = Vector3f(.5f, .5f, .5f)
            val ambientColor = Vector3f(.2f, .2f, .2f)
            with(lightingShader) {
                use()
                setFloat("light.constant", 1f)
                setFloat("light.linear", .09f)
                setFloat("light.quadratic", .032f)
                setFloat("material.shininess", 32f)
                setVec3("light.ambient", ambientColor)
                setVec3("light.diffuse", diffuseColor)
                setVec3("light.specular", 1f, 1f, 1f)
                setVec3("light.position", lightPos)
                setVec3("viewPos", camera.position)
                setMat4("projection", projection)
                setMat4("view", view)
                setMat4("model", Matrix4f())
            }

            GL13.glActiveTexture(GL13.GL_TEXTURE0)
            GL13.glBindTexture(GL13.GL_TEXTURE_2D, diffuseMap)

            GL13.glActiveTexture(GL13.GL_TEXTURE1)
            GL13.glBindTexture(GL13.GL_TEXTURE_2D, specularMap)

            GL30.glBindVertexArray(cubeVao)
            cubePositions.forEachIndexed { idx, pos ->
                val model = Matrix4f()
                    .translate(pos)
                    .rotate(Math.toRadians(20f * idx), Vector3f(1f,.3f, .5f))
                lightingShader.setMat4("model", model)

                GL30.glDrawArrays(GL30.GL_TRIANGLES, 0, 36)
            }


            val model = Matrix4f()
                .translate(lightPos, Matrix4f())
                .scale(Vector3f(.2f))
            with(lightCubeShader) {
                use()
                setMat4("projection", projection)
                setMat4("view", view)
                setMat4("model", model)
            }

            GL30.glBindVertexArray(lightCubeVao)
            GL30.glDrawArrays(GL30.GL_TRIANGLES, 0, 36)

            GLFW.glfwSwapBuffers(window)
            GLFW.glfwPollEvents()
        }

        GL30.glDeleteVertexArrays(cubeVao)
        GL30.glDeleteVertexArrays(lightCubeVao)
        GL30.glDeleteBuffers(vbo)
    }
}
