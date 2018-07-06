package cc.hyperium.utils;

import cc.hyperium.installer.api.entities.InstallerManifest;
import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;

import java.io.File;
import java.util.Locale;

/*
 * Created by Cubxity on 06/07/2018
 */
public class InstallerUtils {
    private static HttpClient client = HttpClients.createDefault();
    private static InstallerManifest manifest;
    private static OSType os;

    public static File getMinecraftDir() {
        switch (getOS()) {
            case Linux:
                return new File(System.getProperty("user.home"), ".minecraft");
            case Windows:
                return new File(System.getenv("APPDATA"), ".minecraft");
            case MacOS:
                return new File(System.getProperty("user.home") + "/Library/Application Support", "minecraft");
            default:
                return new File(System.getProperty("user.home"), ".minecraft");
        }
    }

    public static OSType getOS() {
        if (os == null) {
            String OS = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
            if ((OS.contains("mac")) || (OS.contains("darwin")))
                os = OSType.MacOS;
            else if (OS.contains("win"))
                os = OSType.Windows;
            else if (OS.contains("nux"))
                os = OSType.Linux;
            else
                os = OSType.Other;
        }
        return os;
    }

    public enum OSType {
        Windows, MacOS, Linux, Other
    }

    public static JsonHolder get(String url) {
        try {
            return new JsonHolder(IOUtils.toString(client.execute(new HttpGet(url)).getEntity().getContent()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new JsonHolder();
    }

    public static InstallerManifest getManifest() {
        if (manifest == null)
            manifest = new Gson().fromJson(get("https://raw.githubusercontent.com/HyperiumClient/Hyperium-Repo/master/installer/versions.json").getObject(), InstallerManifest.class);
        return manifest;
    }
}
