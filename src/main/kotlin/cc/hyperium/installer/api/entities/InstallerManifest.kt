package cc.hyperium.installer.api.entities

import com.google.gson.annotations.SerializedName
import java.util.Arrays

/*
 * Created by Cubxity on 06/07/2018
 */
data class InstallerManifest(
        @SerializedName("latest-supported")
        val latestSupported: Int,
        @SerializedName("latest-stable")
        val latestStable: String,
        @SerializedName("latest-dev")
        val latestDev: String,
        val versions: Array<VersionManifest>,
        val addons: Array<AddonManifest>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as InstallerManifest

        if (latestSupported != other.latestSupported) return false
        if (latestStable != other.latestStable) return false
        if (latestDev != other.latestDev) return false
        if (!Arrays.equals(versions, other.versions)) return false
        if (!Arrays.equals(addons, other.addons)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = latestSupported
        result = 31 * result + latestStable.hashCode()
        result = 31 * result + latestDev.hashCode()
        result = 31 * result + Arrays.hashCode(versions)
        result = 31 * result + Arrays.hashCode(addons)
        return result
    }
}