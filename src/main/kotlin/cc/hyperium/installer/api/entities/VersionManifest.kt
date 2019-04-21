package cc.hyperium.installer.api.entities

import com.google.gson.annotations.SerializedName

data class
        VersionManifest(
        @SerializedName("build")
        val name: String,
        val id: Int,
        val url: String,
        val sha256: String,
        val sha1: String,
        val size: Long,
        val time: Long,
        val beta: Boolean,
        @SerializedName("installer_target")
        val targetInstaller: Int
)
