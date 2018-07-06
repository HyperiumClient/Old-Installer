package cc.hyperium.installer.api;

import cc.hyperium.installer.api.entities.InstallerConfig;

/*
 * Created by Cubxity on 06/07/2018
 */
public class Installer {
    public static final int API_VERSION = 1;

    private InstallerConfig config;

    public Installer(InstallerConfig config) {
        this.config = config;
    }


}
