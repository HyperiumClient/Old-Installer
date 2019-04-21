package cc.hyperium.installer.api.entities.internal

class AddonManifest {
    var name: String? = null

    var version: String? = null

    val desc: String? = null

    val mainClass: String? = null

    val mixinConfigs: List<String>? = null

    val tweakerClass: String? = null

    val dependencies: List<String> = ArrayList()

    val transformerClass: String? = null

    val overlay: String? = null

    val author: String? = null
}
