package common

import fp.TMaybe.Companion.maybe
import org.lwjgl.opengl.GL20.*

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


    fun use() = glUseProgram(id)

    fun setBool(name: String, value: Boolean) = glUniform1i(glGetUniformLocation(id, name), if (value) 1 else 0)

    fun setInt(name: String, value: Int) = glUniform1i(glGetUniformLocation(id, name), value)

    fun setFloat(name: String, value: Float) = glUniform1f(glGetUniformLocation(id, name), value)
}