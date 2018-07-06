package cc.hyperium.installer.api.entities

import cc.hyperium.utils.InstallerUtils

data class InstallerConfig(
        var version: VersionManifest = InstallerUtils.getManifest().versions[InstallerUtils.getManifest().versions.size - 1],
        var localJre: Boolean = true,
        var dir: String = InstallerUtils.getMinecraftDir().absolutePath,
        var wam: Int = 1,
        var components: Array<ComponentManifest> = emptyArray()
)