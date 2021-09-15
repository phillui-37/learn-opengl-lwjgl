package commonInterface

import CommonUtil
import fp.TMaybe

typealias ProgramRef = Int

interface IShader {
    val vertices: FloatArray
    var programRef: ProgramRef
    var buffers: Array<InitBufferResult>
    var shaders: Array<ShaderRef>
    fun loadShaders(vararg files: ShaderFileSrc)
    fun initBuffers()
    fun cleanUp()
}

data class InitBufferResult(
    val VBO: TMaybe<Int>,
    val VAO: TMaybe<Int>,
    val EBO: TMaybe<Int>,
)

data class ShaderFileSrc(
    val vertex: String,
    val fragment: String,
)

data class ShaderRef(
    val vertex: Int,
    val fragment: Int,
)

fun IShader.getVertexShaderContent(filename: String): String? {
    return CommonUtil.getShaderFileContent(if (filename.endsWith(".vert")) filename else "$filename.vert")
}

fun IShader.getFragmentShaderContent(filename: String): String? {
    return CommonUtil.getShaderFileContent(if (filename.endsWith(".frag")) filename else "$filename.frag")
}