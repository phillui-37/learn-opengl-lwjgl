import commonInterface.ILesson
import commonInterface.IShader
import org.lwjgl.Version
import org.lwjgl.glfw.Callbacks.glfwFreeCallbacks
import org.lwjgl.glfw.GLFW.*
import start_1.ColorTriangle_5
import start_1.HelloRectangle_4
import start_1.HelloTriangle_4
import start_1.HelloWindow_3

enum class Lesson(val lesson: ILesson) {
    // Getting started
    HELLO_WINDOW(HelloWindow_3),
    HELLO_TRIANGLE(HelloTriangle_4),
    HELLO_RECTANGLE(HelloRectangle_4),
    COLOR_TRIANGLE(ColorTriangle_5)
}

fun main() {
    println("LWJGL ${Version.getVersion()}")
    val lesson = Lesson.COLOR_TRIANGLE.lesson

    lesson.init()
    lesson.loop()
    if (lesson is IShader)
        lesson.cleanUp()

    glfwFreeCallbacks(lesson.window)
    glfwDestroyWindow(lesson.window)

    glfwTerminate()
    glfwSetErrorCallback(null)?.free()
}