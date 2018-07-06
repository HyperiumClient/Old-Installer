package cc.hyperium.installer.api.entities

import com.google.gson.annotations.SerializedName

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
)