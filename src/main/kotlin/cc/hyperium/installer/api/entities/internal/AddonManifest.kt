package cc.hyperium.installer.api.entities.internal

class AddonManifest {
    var name: String? = null

    var version: String? = null

    val desc: String? = null

    val mainClass: String? = null

    /**
     * If the addon includes and Mixins you
     * can add the config(s) here which will
     * automatically at them to the environment
     *
     * @return mixin configs
     */
    val mixinConfigs: List<String>? = null

    val tweakerClass: String? = null

    /**
     * An array containing all the names of
     * the dependencies. The dependencies
     * should be loaded before this addon.
     *
     * @return the names of the dependencies
     */
    val dependencies: List<String> = ArrayList()

    /**
     * If the addon includes a {@link net.minecraft.launchwrapper.IClassTransformer}
     * you can specify it here.
     */
    val transformerClass: String? = null

    /**
     * Class where the config for the addon is
     */
    val overlay: String? = null

    val author: String? = null
}
