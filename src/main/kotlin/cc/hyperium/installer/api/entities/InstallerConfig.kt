package cc.hyperium.installer.api.entities

import cc.hyperium.utils.InstallerUtils

data class InstallerConfig(
        var version: VersionManifest? = null,
        var localJre: Boolean = true,
        var dir: String = InstallerUtils.getMinecraftDir().absolutePath,
        var wam: Int = 1,
        var components: List<String> = ArrayList<String>()
)