package common

import fp.Tuple3
import org.joml.Vector2f
import org.joml.Vector3f
import org.lwjgl.BufferUtils
import org.lwjgl.assimp.*
import org.lwjgl.assimp.Assimp.*
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL11.glDeleteTextures
import org.lwjgl.opengl.GL11.glGenTextures
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30
import org.lwjgl.opengl.GL30.GL_RG
import org.lwjgl.stb.STBImage.stbi_image_free
import org.lwjgl.stb.STBImage.stbi_load
import java.nio.ByteBuffer
import java.nio.IntBuffer

class Model(path: String, val gamma: Boolean) {
    private val meshes: MutableList<Mesh> = mutableListOf()
    private var directory: String = ""
    private val texturesLoaded: MutableMap<String, Texture> = mutableMapOf()

    init {
        loadModel(path)
    }
    constructor(path: String): this(path, false)

    private fun loadModel(path: String) {
        val scene = aiImportFile(path, aiProcess_Triangulate or aiProcess_FlipUVs or aiProcess_CalcTangentSpace)
        if (scene == null || (scene.mFlags() and AI_SCENE_FLAGS_INCOMPLETE) == AI_SCENE_FLAGS_INCOMPLETE || scene.mRootNode() == null) {
            throw RuntimeException("ERROR:ASSIMP::${aiGetErrorString()}")
        }
        directory = path.substring(0, path.indexOfLast { it == '/' })
        processNode(scene.mRootNode()!!, scene)
        aiReleaseImport(scene)
    }

    private fun processNode(node: AINode, scene: AIScene) {
        for (i in 0 until node.mNumMeshes()) {
            val mesh = AIMesh.create(scene.mMeshes()!!.get(node.mMeshes()!!.get(i)))
            meshes.add(processMesh(mesh, scene))
        }

        for (i in 0 until node.mNumChildren()) {
            processNode(AINode.create(node.mChildren()!!.get(i)), scene)
        }
    }

    private fun processMesh(mesh: AIMesh, scene: AIScene): Mesh {
        val vertices = mutableListOf<Vertex>()
        val indices = mutableListOf<Int>()
        val textures = mutableListOf<Texture>()

        for (i in 0 until mesh.mNumVertices()) {
            val (posX,posY,posZ) = mesh.mVertices().run { Tuple3(x(), y(), x()) }
            val (norX, norY, norZ) = mesh.mNormals()!!.run {Tuple3(x(), y(), z())}
            val texCoords = mesh.mTextureCoords(0)?.run {
                Vector2f(x(), y())
            } ?: Vector2f(0f)
            vertices.add(Vertex(Vector3f(posX, posY, posZ), Vector3f(norX, norY, norZ), texCoords))
        }

        for (i in 0 until mesh.mNumFaces()) {
            val face = mesh.mFaces().get(i)
            for (j in 0 until face.mNumIndices())
                indices.add(face.mIndices().get(j))
        }

        val material = AIMaterial.create(scene.mMaterials()!!.get(mesh.mMaterialIndex()))
        val diffuseMaps = loadMaterialTextures(material, aiTextureType_DIFFUSE, "texture_diffuse")
        textures.addAll(diffuseMaps)
        val specularMaps = loadMaterialTextures(material, aiTextureType_SPECULAR, "texture_specular")
        textures.addAll(specularMaps)
        val normalMaps = loadMaterialTextures(material, aiTextureType_HEIGHT, "texture_normal")
        textures.addAll(normalMaps)
        val heightMaps = loadMaterialTextures(material, aiTextureType_AMBIENT, "texture_height")
        textures.addAll(heightMaps)

        return Mesh(vertices, indices, textures)
    }

    private fun loadMaterialTextures(mat: AIMaterial, type: Int, typeName: String): List<Texture> {
        val ret = mutableListOf<Texture>()
        for (i in 0 until aiGetMaterialTextureCount(mat, type)) {
            val str = AIString.calloc()
            aiGetMaterialTexture(mat, type, i, str, null as IntBuffer?, null, null, null, null, null)
            val path = str.dataString()
            if(path == null || path.length <= 0) {
                continue
            }
            if (!texturesLoaded.containsKey(path)) {
                val texture = Texture(TextureFromFile(str.dataString(), directory), typeName, path)
                texturesLoaded[path] = texture
                ret.add(texture)
            } else {
                ret.add(texturesLoaded[path]!!)
            }
        }
        return ret
    }

    fun draw(shader: Shader) {
        meshes.forEach { it.draw(shader) }
    }

    fun delete() {
        meshes.forEach {it.delete()}
        texturesLoaded.values.forEach { glDeleteTextures(it.id) }
        meshes.clear()
        texturesLoaded.clear()
    }
}

fun TextureFromFile(path: String, directory: String, gamma: Boolean = false): Int {
    val filename = "$directory/$path"
    val textureIdBuffer = BufferUtils.createIntBuffer(1)
    glGenTextures(textureIdBuffer)
    val textureId = textureIdBuffer.get()

    val width = BufferUtils.createIntBuffer(1)
    val height = BufferUtils.createIntBuffer(1)
    val nrComponents = BufferUtils.createIntBuffer(1)
    val data = stbi_load(filename, width, height, nrComponents, 0)
    if (data != null) {
        val format = when (val n = nrComponents.get()) {
            1 -> GL20.GL_RED
            2 -> GL_RG
            3 -> GL20.GL_RGB
            4 -> GL20.GL_RGBA
            else -> throw RuntimeException("Invalid nrComponents value $n")
        }
        GL20.glBindTexture(GL20.GL_TEXTURE_2D, textureId)
        GL11.glTexImage2D(GL20.GL_TEXTURE_2D, 0, format, width.get(), height.get(), 0, format, GL20.GL_UNSIGNED_BYTE, data)
        GL30.glGenerateMipmap(GL20.GL_TEXTURE_2D)

        GL20.glTexParameteri(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_WRAP_S, GL20.GL_REPEAT)
        GL20.glTexParameteri(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_WRAP_T, GL20.GL_REPEAT)
        GL20.glTexParameteri(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_MIN_FILTER, GL20.GL_LINEAR_MIPMAP_LINEAR)
        GL20.glTexParameteri(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_MAG_FILTER, GL20.GL_LINEAR)
        stbi_image_free(data as ByteBuffer?)
    } else {
        stbi_image_free(data as ByteBuffer?)
        throw RuntimeException("Cannot load texture from $filename")
    }
    return textureId
}