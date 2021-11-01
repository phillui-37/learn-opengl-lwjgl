import common.CommonUtil
import common.Shader
import common.trait.*
import org.lwjgl.Version
import org.lwjgl.glfw.Callbacks.glfwFreeCallbacks
import org.lwjgl.glfw.GLFW.*
import start_1.*

enum class Lesson(val lesson: ILesson) {
    // Getting started
    HELLO_WINDOW(HelloWindow_3),
    HELLO_TRIANGLE(HelloTriangle_4),
    HELLO_RECTANGLE(HelloRectangle_4),
    COLOR_TRIANGLE(ColorTriangle_5),
    TEXTURE(Texture_6),
    TEXTURE2(Texture2_6),
    TRANSFORMATION(Transformation_7),
    COORDINATE(Coordinate_8),
    CAMERA1(Camera_9),
    CAMERA2(Camera_Keyboard_9),
    CAMERA_EX(Camera_Keyboard_Ex_9),
}

fun main() {
    println("LWJGL ${Version.getVersion()}\nDir: ${System.getProperty("user.dir")}")
    val lesson = Lesson.CAMERA_EX.lesson

    // init
    lesson.window = CommonUtil.commonInit(lesson.width, lesson.height)
    lesson.init()
    if (lesson is IShader3) {
        lesson.shader = Shader(lesson.vertexShaderPath, lesson.fragmentShaderPath)
        lesson.buffers = lesson.initBuffers()
    }
    if (lesson is ITexture) {
        lesson.textures = lesson.getTexture()
    }
    if (lesson is ILessonPostInit) lesson.postInit()

    // loop
    lesson.loop()
    if (lesson is IShader)
        lesson.cleanUp()

    glfwFreeCallbacks(lesson.window)
    glfwDestroyWindow(lesson.window)

    glfwTerminate()
    glfwSetErrorCallback(null)?.free()
}