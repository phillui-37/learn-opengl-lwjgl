package common.trait

import common.CommonUtil
import common.Shader
import fp.TMaybe

typealias ProgramRef = Int

interface IShader {
    val vertices: FloatArray
    var programRef: ProgramRef
    var buffers: Array<InitBufferResult>
    var shaders: Array<ShaderRef>
    var indices: TMaybe<IntArray>
    fun initBuffers(): Array<InitBufferResult>
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

fun getVertexShaderContent(filename: String): String? {
    return CommonUtil.getShaderFileContent(if (filename.endsWith(".vert")) filename else "$filename.vert")
}

fun getFragmentShaderContent(filename: String): String? {
    return CommonUtil.getShaderFileContent(if (filename.endsWith(".frag")) filename else "$filename.frag")
}

interface IShader2: IShader {
    var shader: Shader
}

interface IShader3: IShader2 {
    val fragmentShaderPath: String
    val vertexShaderPath: String
}