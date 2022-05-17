package common

import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL15
import org.lwjgl.opengl.GL30.*
import org.lwjgl.system.MemoryUtil
import java.nio.FloatBuffer
import java.nio.IntBuffer

class Mesh(val vertices: List<Vertex>, val indices: List<Int>, val textures: List<Texture>) {
    init {
        setupMesh()
    }

    private var vao = 0
    private var vbo = 0
    private var ebo = 0

    private fun setupMesh() {
        val vboBuffer = BufferUtils.createIntBuffer(1)
        val vaoBuffer = BufferUtils.createIntBuffer(1)
        val eboBuffer = BufferUtils.createIntBuffer(1)

        glGenVertexArrays(vaoBuffer)
        glGenBuffers(vboBuffer)
        glGenBuffers(eboBuffer)

        vao = vaoBuffer.get()
        vbo = vboBuffer.get()
        ebo = eboBuffer.get()

        glBindVertexArray(vao)
        glBindBuffer(GL_ARRAY_BUFFER, vbo)
        val vertexData = getVertexData()
        glBufferData(GL_ARRAY_BUFFER, vertexData, GL_STATIC_DRAW)

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo)
        val indicesData = getIndicesData()
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesData, GL_STATIC_DRAW)

        glEnableVertexAttribArray(0)
        glVertexAttribPointer(0, 3, GL_FLOAT, false, Vertex.sizeof(), 0)

        glEnableVertexAttribArray(1)
        glVertexAttribPointer(1, 3, GL_FLOAT, false, Vertex.sizeof(), 3L * Float.SIZE_BYTES)

        glEnableVertexAttribArray(2)
        glVertexAttribPointer(2, 2, GL_FLOAT, false, Vertex.sizeof(), 6L * Float.SIZE_BYTES)

        glBindVertexArray(0)
        MemoryUtil.memFree(vertexData)
        MemoryUtil.memFree(indicesData)
    }

    private fun getIndicesData(): IntBuffer {
        val size = indices.size
        val buffer = MemoryUtil.memAllocInt(size)

        for (i in 0 until size) {
            buffer.put(i, indices[i])
        }
        return buffer
    }

    private fun getVertexData(): FloatBuffer {
        val offset = Vertex.sizeof() / Float.SIZE_BYTES
        val buffer = MemoryUtil.memAllocFloat(vertices.size * offset)

        for (i in vertices.indices) {
            val vertex = vertices[i]
            val idx = i * offset
            vertex.Position.get(idx, buffer)
            vertex.Normal.get(idx+3, buffer)
            vertex.TexCoords.get(idx+6, buffer)
        }
        return buffer
    }

    fun draw(shader: Shader) {
        var diffuseNr = 1
        var specularNr = 1
        var normalNr = 1
        var heightNr = 1
        textures.forEachIndexed { index, texture ->
            glActiveTexture(GL_TEXTURE0 + index)
            val name = texture.type
            val number = when (name) {
                "texture_diffuse" -> diffuseNr++.toString()
                "texture_specular" -> specularNr++.toString()
                "texture_normal" -> normalNr++.toString()
                "texture_height" -> heightNr++.toString()
                else -> null
            }

            shader.setInt("${name}${number}", index)
            glBindTexture(GL_TEXTURE_2D, texture.id)
        }

        glActiveTexture(GL_TEXTURE0)

        // draw mesh
        glBindVertexArray(vao)
        glDrawElements(GL_TRIANGLES, indices.size, GL_UNSIGNED_INT, 0)
        glBindVertexArray(0)
    }

    fun delete() {
        glDeleteVertexArrays(vao)
        GL15.glDeleteBuffers(vbo)
        GL15.glDeleteBuffers(vbo)
    }
}