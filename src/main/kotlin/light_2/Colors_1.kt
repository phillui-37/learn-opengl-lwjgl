package light_2

import common.trait.*
import common.*
import fp.*
import org.lwjgl.glfw.*
import org.lwjgl.opengl.*
import org.lwjgl.system.MemoryUtil.NULL

object Colors_1: IShader2, ILesson, ITexture, IMouseCb, IScrollCb {
    var lastFrame = 0f
    var deltaTime = 0f

    var firstMouse = true
    var lastX = width / 2f
    var lastY = height / 2f
    val camera = Camera(Vector3f(0f, 0f, 3f))

    override val vertices: FloatArray = floatArrayOf(

    )
    override var programRef: ProgramRef = 0
    override lateinit var buffers: Array<InitBufferResult>
    override lateinit var shaders: Array<ShaderRef>
    override var indices: TMaybe<IntArray> = TNone()
    override fun initBuffers() {}
    override fun cleanUp() {}
    override lateinit var shader: Shader

    override val width: Int = DefaultValue.WIDTH
    override val height: Int = DefaultValue.HEIGHT
    override val keyCb: GLFWKeyCallbackI = GLFWKeyCallbackI { window, key, scancode, action, mods ->
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
    override val frameBufferSizeCb: GLFWFramebufferSizeCallbackI = DefaultValue.FRAME_BUFFER_SIZE_CALLBACK
    override var window: Long = NULL
    override fun init() {

    }
    override fun loop() {

    }
    override fun getTexture(): IntArray {}
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
}