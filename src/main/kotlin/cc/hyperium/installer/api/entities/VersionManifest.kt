package cc.hyperium.installer.api.entities

import com.google.gson.annotations.SerializedName

/*
 * Created by Cubxity on 06/07/2018
 */
data class VersionManifest(
        val name: String,
        @SerializedName("release-id")
        val id: Int?,
        @SerializedName("artifact-name")
        val artifactId: String,
        val url: String,
        val sha256: String,
        val sha1: String,
        val size: Long,
        val path: String,
        @SerializedName("tweak-class")
        val tweaker: String,
        @SerializedName("install-min")
        val targetInstaller: Int
)
