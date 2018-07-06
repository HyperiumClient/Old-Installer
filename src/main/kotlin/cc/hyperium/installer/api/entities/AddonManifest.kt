package cc.hyperium.installer.api.entities

/*
 * Created by Cubxity on 06/07/2018
 */
class AddonManifest(
        name: String,
        val description: String,
        val version: String,
        val author: String,
        val verified: Boolean,
        val sha256: String,
        val url: String,
        val depends: Array<String>
) : ComponentManifest(name)