import common.trait.ILesson
import common.trait.IShader
import org.lwjgl.Version
import org.lwjgl.glfw.Callbacks.glfwFreeCallbacks
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GLUtil
import start_1.*

enum class Lesson(val lesson: ILesson) {
    // Getting started
    HELLO_WINDOW(HelloWindow_3),
    HELLO_TRIANGLE(HelloTriangle_4),
    HELLO_RECTANGLE(HelloRectangle_4),
    COLOR_TRIANGLE(ColorTriangle_5),
    TEXTURE(Texture_6),
}

fun main() {
    println("LWJGL ${Version.getVersion()}\nDir: ${System.getProperty("user.dir")}")
    val lesson = Lesson.TEXTURE.lesson

    lesson.init()
    lesson.loop()
    if (lesson is IShader)
        lesson.cleanUp()

    glfwFreeCallbacks(lesson.window)
    glfwDestroyWindow(lesson.window)

    glfwTerminate()
    glfwSetErrorCallback(null)?.free()
}