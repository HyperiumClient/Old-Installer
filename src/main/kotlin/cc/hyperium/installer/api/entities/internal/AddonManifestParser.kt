package cc.hyperium.installer.api.entities.internal

import com.google.common.io.Files
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.io.*
import java.nio.charset.Charset
import java.util.jar.JarFile

class AddonManifestParser {
    private var json: JsonObject? = null

    private val gson = Gson()

    constructor(jar: JarFile) {

        var jarInputStream: InputStream? = null
        try {
            val entry = jar.getEntry("addon.json")

            val jsonFile = File.createTempFile("json", "tmp")
            jsonFile.deleteOnExit()

            jarInputStream = jar.getInputStream(entry)
            val os = FileOutputStream(jsonFile)
            copyInputStream(jarInputStream, os)

            val contents = Files.toString(jsonFile, Charset.defaultCharset())

            val parser = JsonParser()
            val json = parser.parse(contents).asJsonObject

            if (!json.has("version") && !json.has("name") && !json.has("mainClass")) {
                throw AddonLoadException("Invalid addon manifest ( Must include name, verson and mainClass)")
            }
            this.json = json
        } catch (e: Exception) {
            e.printStackTrace()
            throw AddonLoadException("Exception reading manifest")
        } finally {
            jarInputStream?.close()
            jar.close()
        }
    }

    constructor(contents: String) {
        val parser = JsonParser()
        val json = parser.parse(contents).asJsonObject

        if (!json.has("version") && !json.has("name") && !json.has("mainClass")) {
            throw AddonLoadException("Invalid addon manifest (Must include name, version and mainClass)")
        }
        this.json = json
    }

    fun getAddonManifest(): AddonManifest {
        return gson.fromJson(json, AddonManifest::class.java)
    }

    override fun toString(): String {
        return json!!.toString()
    }

    private fun copyInputStream(input: InputStream, output: OutputStream): Int {
        val count = copyLarge(input, output, ByteArray(1024 * 4))
        return if (count > Integer.MAX_VALUE) {
            -1
        } else count.toInt()
    }

    @Throws(IOException::class)
    private fun copyLarge(input: InputStream, output: OutputStream, buffer: ByteArray): Long {
        var count: Long = 0
        var n = 0
        while ({ n = input.read(buffer); n }() != -1) {
            output.write(buffer, 0, n)
            count += n.toLong()
        }
        return count
    }
}
