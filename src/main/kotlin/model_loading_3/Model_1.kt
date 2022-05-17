package model_loading_3

import common.*
import common.trait.ILesson
import common.trait.ILessonCleanUp
import fp.curry2
import fp.tuple
import org.joml.Math.toRadians
import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFW.glfwGetTime
import org.lwjgl.glfw.GLFWCursorPosCallbackI
import org.lwjgl.glfw.GLFWKeyCallbackI
import org.lwjgl.glfw.GLFWScrollCallbackI
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL30
import org.lwjgl.stb.STBImage.stbi_set_flip_vertically_on_load
import org.lwjgl.system.MemoryUtil

object Model_1: ILesson, ILessonCleanUp {
    override val width = 1024
    override val height = 768
    private val camera = Camera(Vector3f(0f, 0f, 3f))
    private var deltaTime = 0f
    private var lastFrame = 0f
    private var firstMouse = true
    private var lastX = 0f
    private var lastY = 0f
    private var shader: Shader? = null
    private var model: Model? = null

    override val keyCb = GLFWKeyCallbackI { window, key, scancode, action, mods ->
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

    override val frameBufferSizeCb = DefaultValue.FRAME_BUFFER_SIZE_CALLBACK
    override var window= MemoryUtil.NULL

    override fun init() {
        GLFW.glfwSetFramebufferSizeCallback(window, frameBufferSizeCb)
        GLFW.glfwSetCursorPosCallback(window, mouseCallback)
        GLFW.glfwSetScrollCallback(window, scrollCb)
        GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED)
        GL11.glEnable(GL11.GL_DEPTH_TEST)

        stbi_set_flip_vertically_on_load(true)

        shader = Shader("model_loading_3/model_loading")
        model = Model("texture/backpack.obj")

        // draw in wireframe
        // glPolygonMode(GL_FRONT_AND_BACK, GL_LINE)
    }

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
            GLFW.glfwGetKey(window, GLFW.GLFW_KEY_A) tuple { camera.processKeyboard(CameraMovement.LEFT,
                deltaTime
            ) },
            GLFW.glfwGetKey(window, GLFW.GLFW_KEY_D) tuple { camera.processKeyboard(CameraMovement.RIGHT,
                deltaTime
            ) }
        )
            .forEach { if (it._1 == GLFW.GLFW_PRESS) it._2() }
    }

    override fun loop() {
        while (!GLFW.glfwWindowShouldClose(window)) {
            val currentFrame = glfwGetTime().toFloat()
            deltaTime = currentFrame - lastFrame
            lastFrame = currentFrame

            processInput(window)

            GL30.glClearColor(.1f, .1f, .1f, 1f)
            GL30.glClear(GL30.GL_COLOR_BUFFER_BIT or GL30.GL_DEPTH_BUFFER_BIT)

            shader?.use()

            val projection = Matrix4f()
                .perspective(toRadians(camera.zoom), width.toFloat() / height.toFloat(), .1f, 100f)
            val view = camera.getViewMatrix()

            shader?.setMat4("projection", projection)
            shader?.setMat4("view", view)

            val _model = Matrix4f()
                .translate(Vector3f(0f))
                .scale(Vector3f())
            shader?.setMat4("model", _model)
            model?.draw(shader!!)

            GLFW.glfwSwapBuffers(window)
            GLFW.glfwPollEvents()
        }
    }

    override fun cleanUp() {
        model?.delete()
    }
}