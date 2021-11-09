package light_2

import common.*
import common.trait.ILesson
import fp.curry2
import fp.tuple
import org.joml.Math.toRadians
import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.BufferUtils
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWCursorPosCallbackI
import org.lwjgl.glfw.GLFWFramebufferSizeCallbackI
import org.lwjgl.glfw.GLFWKeyCallbackI
import org.lwjgl.glfw.GLFWScrollCallbackI
import org.lwjgl.opengl.GL11.GL_DEPTH_TEST
import org.lwjgl.opengl.GL11.glEnable
import org.lwjgl.opengl.GL15
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30.*
import org.lwjgl.system.MemoryUtil.NULL


/**
 * phong lighting model
 *
 * ambient: background light
 * diffuse: lighting on obj
 * specular: light source spot
 *
 * combine 3 params = light result
 *
 *
 * including viewer coordinate: specular lighting
 * light -> obj -> viewer
 */
object BasicLight_2 : ILesson {
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
    override var window: Long = NULL

    private val vertices = floatArrayOf(
        -0.5f, -0.5f, -0.5f, 0.0f, 0.0f, -1.0f,
        0.5f, -0.5f, -0.5f, 0.0f, 0.0f, -1.0f,
        0.5f, 0.5f, -0.5f, 0.0f, 0.0f, -1.0f,
        0.5f, 0.5f, -0.5f, 0.0f, 0.0f, -1.0f,
        -0.5f, 0.5f, -0.5f, 0.0f, 0.0f, -1.0f,
        -0.5f, -0.5f, -0.5f, 0.0f, 0.0f, -1.0f,

        -0.5f, -0.5f, 0.5f, 0.0f, 0.0f, 1.0f,
        0.5f, -0.5f, 0.5f, 0.0f, 0.0f, 1.0f,
        0.5f, 0.5f, 0.5f, 0.0f, 0.0f, 1.0f,
        0.5f, 0.5f, 0.5f, 0.0f, 0.0f, 1.0f,
        -0.5f, 0.5f, 0.5f, 0.0f, 0.0f, 1.0f,
        -0.5f, -0.5f, 0.5f, 0.0f, 0.0f, 1.0f,

        -0.5f, 0.5f, 0.5f, -1.0f, 0.0f, 0.0f,
        -0.5f, 0.5f, -0.5f, -1.0f, 0.0f, 0.0f,
        -0.5f, -0.5f, -0.5f, -1.0f, 0.0f, 0.0f,
        -0.5f, -0.5f, -0.5f, -1.0f, 0.0f, 0.0f,
        -0.5f, -0.5f, 0.5f, -1.0f, 0.0f, 0.0f,
        -0.5f, 0.5f, 0.5f, -1.0f, 0.0f, 0.0f,

        0.5f, 0.5f, 0.5f, 1.0f, 0.0f, 0.0f,
        0.5f, 0.5f, -0.5f, 1.0f, 0.0f, 0.0f,
        0.5f, -0.5f, -0.5f, 1.0f, 0.0f, 0.0f,
        0.5f, -0.5f, -0.5f, 1.0f, 0.0f, 0.0f,
        0.5f, -0.5f, 0.5f, 1.0f, 0.0f, 0.0f,
        0.5f, 0.5f, 0.5f, 1.0f, 0.0f, 0.0f,

        -0.5f, -0.5f, -0.5f, 0.0f, -1.0f, 0.0f,
        0.5f, -0.5f, -0.5f, 0.0f, -1.0f, 0.0f,
        0.5f, -0.5f, 0.5f, 0.0f, -1.0f, 0.0f,
        0.5f, -0.5f, 0.5f, 0.0f, -1.0f, 0.0f,
        -0.5f, -0.5f, 0.5f, 0.0f, -1.0f, 0.0f,
        -0.5f, -0.5f, -0.5f, 0.0f, -1.0f, 0.0f,

        -0.5f, 0.5f, -0.5f, 0.0f, 1.0f, 0.0f,
        0.5f, 0.5f, -0.5f, 0.0f, 1.0f, 0.0f,
        0.5f, 0.5f, 0.5f, 0.0f, 1.0f, 0.0f,
        0.5f, 0.5f, 0.5f, 0.0f, 1.0f, 0.0f,
        -0.5f, 0.5f, 0.5f, 0.0f, 1.0f, 0.0f,
        -0.5f, 0.5f, -0.5f, 0.0f, 1.0f, 0.0f
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

    override fun init() {
        glfwSetFramebufferSizeCallback(window, frameBufferSizeCb)
        glfwSetCursorPosCallback(window, mouseCallback)
        glfwSetScrollCallback(window, scrollCb)
        glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED)
        glEnable(GL_DEPTH_TEST)

        shaders.add(Shader("light_2/basic_light"))
        shaders.add(Shader("light_2/light"))

        val vboBuffer = BufferUtils.createIntBuffer(1)
        val cubeVaoBuffer = BufferUtils.createIntBuffer(1)
        glGenVertexArrays(cubeVaoBuffer)
        glGenBuffers(vboBuffer)

        vbo = vboBuffer.get()
        cubeVao = cubeVaoBuffer.get()

        glBindBuffer(GL_ARRAY_BUFFER, vbo)
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW)
        glBindVertexArray(cubeVao)

        glVertexAttribPointer(0, 3, GL_FLOAT, false, 6 * Float.SIZE_BYTES, 0)
        glEnableVertexAttribArray(0)

        glVertexAttribPointer(1, 3, GL_FLOAT, false, 6 * Float.SIZE_BYTES, 3L * Float.SIZE_BYTES)
        glEnableVertexAttribArray(1)

        val lightCubeVaoBuffer = BufferUtils.createIntBuffer(1)
        glGenVertexArrays(lightCubeVaoBuffer)
        lightCubeVao = lightCubeVaoBuffer.get()
        glBindVertexArray(lightCubeVao)
        glBindBuffer(GL_ARRAY_BUFFER, vbo)
        GL20.glVertexAttribPointer(0, 3, GL_FLOAT, false, 6 * Float.SIZE_BYTES, 0)
        glEnableVertexAttribArray(0)
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
            glfwGetKey(window, GLFW_KEY_ESCAPE) tuple { glfwSetWindowShouldClose(window, true) },
            glfwGetKey(window, GLFW_KEY_W) tuple { camera.processKeyboard(CameraMovement.FORWARD, deltaTime) },
            glfwGetKey(window, GLFW_KEY_S) tuple { camera.processKeyboard(CameraMovement.BACKWARD, deltaTime) },
            glfwGetKey(window, GLFW_KEY_A) tuple { camera.processKeyboard(CameraMovement.LEFT, deltaTime) },
            glfwGetKey(window, GLFW_KEY_D) tuple { camera.processKeyboard(CameraMovement.RIGHT, deltaTime) }
        )
            .forEach { if (it._1 == GLFW_PRESS) it._2() }
    }

    override fun loop() {
        var currentFrame: Float
        val lightingShader = shaders[0]
        val lightCubeShader = shaders[1]
        while (!glfwWindowShouldClose(window)) {
            currentFrame = glfwGetTime().toFloat()
            deltaTime = currentFrame - lastFrame
            lastFrame = currentFrame

            processInput(window)

            glClearColor(.1f, .1f, .1f, 1f)
            glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)


            val projection = Matrix4f()
                .perspective(toRadians(camera.zoom), (width / height).toFloat(), .1f, 100f)
            val view = camera.getViewMatrix()
            with(lightingShader) {
                use()
                setVec3("objectColor", 1f, .5f, .31f)
                setVec3("lightColor", 1f, 1f, 1f)
                setVec3("lightPos", lightPos)
                setVec3("viewPos", camera.position)
                setMat4("projection", projection)
                setMat4("view", view)
                setMat4("model", Matrix4f())
            }

            glBindVertexArray(cubeVao)
            glDrawArrays(GL_TRIANGLES, 0, 36)


            val model = Matrix4f()
                .translate(lightPos, Matrix4f())
                .scale(Vector3f(.2f))
            with(lightCubeShader) {
                use()
                setMat4("projection", projection)
                setMat4("view", view)
                setMat4("model", model)
            }

            glBindVertexArray(lightCubeVao)
            glDrawArrays(GL_TRIANGLES, 0, 36)

            glfwSwapBuffers(window)
            glfwPollEvents()
        }

        glDeleteVertexArrays(cubeVao)
        glDeleteVertexArrays(lightCubeVao)
        glDeleteBuffers(vbo)
    }
}