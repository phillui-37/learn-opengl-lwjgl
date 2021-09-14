package commonInterface

abstract class TShader {
    // abstract part
    abstract val vertices: Array<Float>

    // concrete part
    fun getVertexShader(filename: String): String? {
        return CommonUtil.getShaderFileContent(if (filename.endsWith(".vert")) filename else "$filename.vert")
    }

    fun getFragmentShader(filename: String): String? {
        return CommonUtil.getShaderFileContent(if (filename.endsWith(".frag")) filename else "$filename.frag")
    }
}