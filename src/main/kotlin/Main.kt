import common.trait.ILesson
import common.trait.IShader
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
}

fun main() {
    println("LWJGL ${Version.getVersion()}\nDir: ${System.getProperty("user.dir")}")
    val lesson = Lesson.COORDINATE.lesson

    lesson.init()
    lesson.loop()
    if (lesson is IShader)
        lesson.cleanUp()

    glfwFreeCallbacks(lesson.window)
    glfwDestroyWindow(lesson.window)

    glfwTerminate()
    glfwSetErrorCallback(null)?.free()
}