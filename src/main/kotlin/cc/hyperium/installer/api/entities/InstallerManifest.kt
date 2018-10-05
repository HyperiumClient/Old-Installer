package cc.hyperium.installer.api.entities

import java.util.*

/*
 * Created by Cubxity on 06/07/2018
 */
data class InstallerManifest(
        val latest: VersionManifest,
        val latest_beta: VersionManifest,
        val versions: Array<VersionManifest>,
        val addons: MutableList<AddonManifest> = mutableListOf()

) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as InstallerManifest

        if (latest != other.latest) return false
        if (!Arrays.equals(versions, other.versions)) return false
        if (addons != other.addons) return false

        return true
    }

    override fun hashCode(): Int {
        var result = latest.hashCode()
        result = 31 * result + Arrays.hashCode(versions)
        result = 31 * result + addons.hashCode()
        return result
    }

}