package common

import fp.TMaybe.Companion.maybe
import org.joml.*
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL20.*
import java.nio.FloatBuffer

class Shader(vertexPath: String, fragmentPath: String) {

    var id: Int
        private set

    init {
        val vertShader = CommonUtil.initVertexShader(vertexPath)
        val fragShader = CommonUtil.initFragmentShader(fragmentPath)

        id = glCreateProgram()
        glAttachShader(id, vertShader)
        glAttachShader(id, fragShader)
        glLinkProgram(id)
        val success = IntArray(1)
        glGetProgramiv(id, GL_LINK_STATUS, success)
        if (success[0] != GL_TRUE) {
            throw RuntimeException("PROGRAM::LINK::FAIL")
        }
        glDeleteShader(vertShader)
        glDeleteShader(fragShader)
    }

    constructor(filePath: String) : this(filePath, filePath)


    fun use() = glUseProgram(id)

    fun setBool(name: String, value: Boolean) = glUniform1i(glGetUniformLocation(id, name), if (value) 1 else 0)

    fun setInt(name: String, value: Int) = glUniform1i(glGetUniformLocation(id, name), value)

    fun setFloat(name: String, value: Float) = glUniform1f(glGetUniformLocation(id, name), value)

    fun setVec2(name: String, value: Vector2f) =
        glUniform2fv(glGetUniformLocation(id, name), BufferUtils.createFloatBuffer(2).also { value.get(it) })

    fun setVec2(name: String, x: Float, y: Float) = glUniform2f(glGetUniformLocation(id, name), x, y)

    fun setVec3(name: String, value: Vector3f) =
        glUniform3fv(glGetUniformLocation(id, name), BufferUtils.createFloatBuffer(3).also { value.get(it) })

    fun setVec3(name: String, x: Float, y: Float, z: Float) = glUniform3f(glGetUniformLocation(id, name), x, y, z)

    fun setVec4(name: String, value: Vector4f) =
        glUniform4fv(glGetUniformLocation(id, name), BufferUtils.createFloatBuffer(4).also { value.get(it) })

    fun setVec4(name: String, x: Float, y: Float, z: Float, w: Float) =
        glUniform4f(glGetUniformLocation(id, name), x, y, z, w)

    fun setMat2(name: String, mat: Matrix2f) = glUniformMatrix2fv(glGetUniformLocation(id, name), false, BufferUtils.createFloatBuffer(4).also { mat.get(it) })

    fun setMat3(name: String, mat: Matrix3f) = glUniformMatrix3fv(glGetUniformLocation(id, name), false, BufferUtils.createFloatBuffer(9).also { mat.get(it) })

    fun setMat4(name: String, mat: Matrix4f) = glUniformMatrix4fv(glGetUniformLocation(id, name), false, BufferUtils.createFloatBuffer(16).also { mat.get(it) })
}