import commonInterface.ILesson
import org.lwjgl.Version
import org.lwjgl.glfw.Callbacks.glfwFreeCallbacks
import org.lwjgl.glfw.GLFW.*
import start_1.HelloWindow_3

enum class Lesson(val lesson: ILesson) {
    // Getting started
    HELLO_WINDOW(HelloWindow_3)
}

fun main() {
    println("LWJGL ${Version.getVersion()}")
    val lesson = Lesson.HELLO_WINDOW.lesson

    lesson.init()
    lesson.loop()

    glfwFreeCallbacks(lesson.window)
    glfwDestroyWindow(lesson.window)

    glfwTerminate()
    glfwSetErrorCallback(null)?.free()
}