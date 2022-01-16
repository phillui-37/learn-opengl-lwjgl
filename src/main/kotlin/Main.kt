import common.CommonUtil
import common.Shader
import common.trait.*
import light_2.*
import org.lwjgl.Version
import org.lwjgl.glfw.Callbacks.glfwFreeCallbacks
import org.lwjgl.glfw.GLFW.*
import start_1.*

enum class Start1(val lesson: ILesson) {
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

enum class Light2(val lesson: ILesson) {
    COLORS(Colors_1),
    BASIC_LIGHT(BasicLight_2),
    MATERIALS(Materials_3),
    LIGHTING_MAPS(LightingMaps_4),
    LIGHTING_CASTER_PARALLEL_LIGHT(LightingCasters_5_1),
    LIGHTING_CASTER_POINT_LIGHT(LightingCasters_5_2),
    LIGHTING_CASTER_SPOTLIGHT(LightingCasters_5_3),
    LIGHTING_CASTER_SPOTLIGHT_OUTER(LightingCasters_5_4),
}

fun main() {
    println("LWJGL ${Version.getVersion()}\nDir: ${System.getProperty("user.dir")}")
    val lesson = Light2.LIGHTING_CASTER_SPOTLIGHT_OUTER.lesson

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