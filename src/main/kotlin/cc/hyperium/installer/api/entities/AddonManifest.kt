package cc.hyperium.installer.api.entities

import java.util.Arrays

class AddonManifest(
        val name: String,
        val description: String,
        val version: String,
        val author: String,
        val verified: Boolean,
        val sha256: String,
        val url: String,
        val depends: Array<String>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as AddonManifest

        if (name != other.name) return false
        if (description != other.description) return false
        if (version != other.version) return false
        if (author != other.author) return false
        if (verified != other.verified) return false
        if (sha256 != other.sha256) return false
        if (url != other.url) return false
        if (!Arrays.equals(depends, other.depends)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + version.hashCode()
        result = 31 * result + author.hashCode()
        result = 31 * result + verified.hashCode()
        result = 31 * result + sha256.hashCode()
        result = 31 * result + url.hashCode()
        result = 31 * result + Arrays.hashCode(depends)
        return result
    }
}
