package cc.hyperium.installer.api;

import cc.hyperium.installer.InstallerMain;
import cc.hyperium.installer.api.callbacks.AbstractCallback;
import cc.hyperium.installer.api.callbacks.ErrorCallback;
import cc.hyperium.installer.api.callbacks.Phrase;
import cc.hyperium.installer.api.callbacks.StatusCallback;
import cc.hyperium.installer.api.entities.InstallerConfig;
import cc.hyperium.installer.api.entities.internal.AddonManifest;
import cc.hyperium.installer.api.entities.internal.AddonManifestParser;
import cc.hyperium.installer.utils.JsonHolder;
import cc.hyperium.utils.DownloadTask;
import cc.hyperium.utils.InstallerUtils;
import com.google.common.io.Files;
import com.google.gson.JsonArray;
import com.google.gson.JsonPrimitive;
import me.cubxity.asm.LaunchWrapperPatcher;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.time.Instant;
import java.util.*;
import java.util.function.Consumer;
import java.util.jar.JarFile;

/*
 * Created by Cubxity on 06/07/2018
 */
public class Installer {
    public static final int API_VERSION = 3;

    private final InstallerConfig config;

    private Consumer<AbstractCallback> callback;

    private int code = 1;

    public Installer(InstallerConfig config, Consumer<AbstractCallback> callback) {
        this.config = config;
        this.callback = callback;
    }

    public void install() {
        Phrase phrase = Phrase.PRE;
        String sep = File.separator;

        try {
            System.setProperty("http.agent", "Mozilla/5.0 Hyperium Installer");
            callback.accept(new StatusCallback(phrase, "Running pre checks", null));
            File mc = new File(config.getDir());
            if (!mc.exists() || mc.isFile()) {
                callback.accept(new ErrorCallback(new IllegalStateException("Invalid Minecraft directory"), phrase, "Invalid Minecraft directory"));
                return;
            }

            boolean mmc = new File(mc, "multimc.cfg").exists();

            InstallerMain.INSTANCE.getLogger().debug("MC Dir = {}", mc.getAbsolutePath());
            InstallerMain.INSTANCE.getLogger().debug("MultiMC = {}", mmc);

            File versions = new File(mc, mmc ? "instances" : "versions");
            File origin = mmc ? new File(mc, "libraries" + sep + "com" + sep + "mojang" + sep + "minecraft" + sep + "1.8.9") : new File(versions, "1.8.9");
            File originJson = new File(origin, "1.8.9.json");
            File originJar = new File(origin, mmc ? "minecraft-1.8.9-client.jar" : "1.8.9.jar");

            if (mmc ? (!origin.exists() || !originJar.exists()) : (!origin.exists() || !originJson.exists() || !originJar.exists())) {
                callback.accept(new ErrorCallback(new IllegalStateException("Version '1.8.9' does not exist"), phrase, "Plesae run Minecrft 1.8.9 then try again"));
                return;
            }

            File target = new File(versions, "Hyperium 1.8.9");

            File libraries = new File(mc, "libraries");
            if (!mmc && target.exists())
                try {
                    callback.accept(new StatusCallback(phrase, "Deleting previous files", null));
                    FileUtils.deleteDirectory(target);

                } catch (Exception ex) {
                    callback.accept(new ErrorCallback(ex, phrase, "Failed to delete the old files, is the game running?"));
                    return;
                }


            if (config.getVersion().getName().equals("LOCAL")) {
                phrase = Phrase.COPY_VERSION;
                File local;

                local = new File(InstallerMain.class.getProtectionDomain().getCodeSource().getLocation().toURI());
                InstallerMain.INSTANCE.getLogger().debug("Local = {}", local.getAbsolutePath());
                callback.accept(new StatusCallback(phrase, "Copying local jar", local));
                try {
                    File localLib = new File(libraries, "cc" + sep + "hyperium" + sep + "Hyperium" + sep + config.getVersion().getName() + sep + "Hyperium-" + config.getVersion().getName() + ".jar");
                    localLib.getParentFile().mkdirs();
                    Files.copy(local, new File(localLib.getParent(), localLib.getName()));
                } catch (IOException e) {
                    callback.accept(new ErrorCallback(e, phrase, "Failed to copy local jar: " + e.getMessage()));
                    return;
                }
            } else {
                try {
                    File hyperium = new File(mc, "libraries" + sep + "cc" + sep + "hyperium" + sep + "Hyperium");
                    if (hyperium.exists())
                        FileUtils.deleteDirectory(hyperium);
                } catch (IOException ex) {
                    callback.accept(new ErrorCallback(ex, phrase, "Failed to delete old Hyperium libraries"));
                }
                phrase = Phrase.DOWNLOAD_CLIENT;
                File downloaded;
                try {
                    File dest = new File(libraries, "cc" + sep + "hyperium" + sep + "Hyperium" + sep + config.getVersion().getName() + sep + "Hyperium-" + config.getVersion().getName() + ".jar");
                    ;
                    File dir = dest.getParentFile();
                    InstallerMain.INSTANCE.getLogger().debug("Target directory: {}", dir.getAbsolutePath());
                    dir.mkdirs();

                    DownloadTask dl = new DownloadTask(config.getVersion().getUrl(), dir.getAbsolutePath());
                    dl.addPropertyChangeListener(evt -> {
                        if (evt.getNewValue() instanceof Integer)
                            callback.accept(new StatusCallback(Phrase.DOWNLOAD_CLIENT, evt.getNewValue() + "% Downloaded", null));
                    });
                    dl.execute();
                    dl.get();
                    downloaded = new File(dir, dl.getFileName());
                    InstallerMain.INSTANCE.getLogger().debug("Downloaded file: {}", downloaded.getAbsolutePath());
                } catch (Exception ex) {
                    callback.accept(new ErrorCallback(ex, phrase, "Failed to download the client: " + ex.getMessage()));
                    return;
                }

                phrase = Phrase.VERIFY_CLIENT;

                callback.accept(new StatusCallback(phrase, "Verifying client", null));
                String hash;
                hash = InstallerUtils.toHex(InstallerUtils.checksum(downloaded, "SHA-256")).toLowerCase();
                InstallerMain.INSTANCE.getLogger().debug("SHA256 Hash = {}, Expected {}", hash, config.getVersion().getSha256());
                if (!hash.equals(config.getVersion().getSha256())) {
                    callback.accept(new ErrorCallback(new IllegalStateException("SHA256 Hash does not match"), phrase, "Failed to verify the downloaded file, please try again."));
                    return;
                }
                hash = InstallerUtils.toHex(InstallerUtils.checksum(downloaded, "SHA1")).toLowerCase();
                InstallerMain.INSTANCE.getLogger().debug("SHA1 Hash = {}, Expected {}", hash, config.getVersion().getSha1());
                if (!hash.equals(config.getVersion().getSha1())) {
                    callback.accept(new ErrorCallback(new IllegalStateException("SHA1 Hash does not match"), phrase, "Failed to verify the downloaded file, please try again."));
                    return;
                }
            }

            File optifine = null;
            boolean of = config.getComponents().stream().anyMatch(c -> c.equals("Optifine"));
            if (of) {
                phrase = Phrase.DOWNLOAD_COMPONENTS;
                callback.accept(new StatusCallback(phrase, "Downloading Optifine", null));
                File tmpDir = java.nio.file.Files.createTempDirectory("Hyperium").toFile();
                try {
                    DownloadTask dl = new DownloadTask("https://raw.githubusercontent.com/HyperiumClient/Hyperium-Repo/master/files/mods/OptiFine_1.8.9_HD_U_I7.jar", tmpDir.getAbsolutePath());
                    dl.addPropertyChangeListener(evt -> {
                        if (evt.getNewValue() instanceof Integer)
                            callback.accept(new StatusCallback(Phrase.DOWNLOAD_COMPONENTS, "Downloading Optifine (" + evt.getNewValue() + "%)", null));
                    });
                    dl.execute();
                    dl.get();
                    optifine = new File(tmpDir, dl.getFileName());
                } catch (Exception ex) {
                    callback.accept(new ErrorCallback(ex, phrase, "Failed to download Optifine: " + ex.getMessage()));
                }
            }

            File targetJson = new File(target, "Hyperium 1.8.9.json");
            File targetJar = new File(target, "Hyperium 1.8.9.jar");
            target.mkdir();
            if (!mmc) {
                phrase = Phrase.COPY_VERSION;
                try {
                    FileUtils.copyFile(originJson, targetJson);
                    FileUtils.copyFile(originJar, targetJar);
                } catch (IOException ex) {
                    callback.accept(new ErrorCallback(ex, phrase, "Failed to copy files: " + ex.getMessage()));
                    return;
                }
            }

            if (of) {
                try {
                    phrase = Phrase.PATCH_OPTIFINE;
                    callback.accept(new StatusCallback(phrase, "Patching Optifine", null));
                    File optifineLibDir = new File(libraries, sep + "optifine" + sep + "OptiFine" + sep + "1.8.9_HD_U_I7");
                    optifineLibDir.mkdirs();
                    File optifineLib = new File(optifineLibDir, "OptiFine-1.8.9_HD_U_I7.jar");

                    Class<?> patcher = InstallerUtils.loadClass(optifine.toURI().toURL(), "optifine.Patcher");
                    Method main = patcher.getMethod("main", String[].class);
                    main.invoke(null, new Object[]{new String[]{originJar.getAbsolutePath(), optifine.getAbsolutePath(), optifineLib.getAbsolutePath()}});
                } catch (Exception ex) {
                    callback.accept(new ErrorCallback(ex, phrase, "Failed to patch Optifine: " + ex.getMessage()));
                    return;
                }
                optifine.delete();
            }

            phrase = Phrase.DOWNLOAD_COMPONENTS;
            Map<File, AddonManifest> installedAddons = new HashMap<>();
            File addonsDir = new File(mmc ? target : mc, (mmc ? ".minecraft" + sep : "") + "addons");
            if (addonsDir.exists()) {
                File[] files = addonsDir.listFiles((dir, name) -> name.endsWith(".jar"));
                if (files != null)
                    for (File a : files) {
                        try {
                            installedAddons.put(a, new AddonManifestParser(new JarFile(a)).getAddonManifest());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
            } else addonsDir.mkdirs();
            try {
                List<cc.hyperium.installer.api.entities.AddonManifest> addons = InstallerUtils.getManifest().getAddons();
                for (String cm : config.getComponents()) {
                    if (cm.equals("Optifine")) continue;
                    Optional<cc.hyperium.installer.api.entities.AddonManifest> oa = addons.stream().filter(a -> a.getName().equals(cm)).findFirst();
                    if (!oa.isPresent()) {
                        InstallerMain.INSTANCE.getLogger().warn("Addon not found: {}", cm);
                        continue;
                    }
                    cc.hyperium.installer.api.entities.AddonManifest addon = oa.get();
                    installedAddons.forEach((f, m) -> {
                        if (m.getName() != null)
                            if (m.getName().equals(addon.getName()))
                                if (!f.delete())
                                    InstallerMain.INSTANCE.getLogger().warn("Failed to delete {}", f.getAbsolutePath());
                    });
                    try {
                        phrase = Phrase.DOWNLOAD_COMPONENTS;
                        InstallerMain.INSTANCE.getLogger().info("Downloading {}", addon.getUrl());
                        DownloadTask dl = new DownloadTask(addon.getUrl(), addonsDir.getAbsolutePath());
                        dl.addPropertyChangeListener(evt -> {
                            if (evt.getNewValue() instanceof Integer)
                                callback.accept(new StatusCallback(Phrase.DOWNLOAD_COMPONENTS, "Downloading " + addon.getName() + " (" + evt.getNewValue() + "%)", addon));
                        });
                        dl.execute();
                        dl.get();
                        phrase = Phrase.VERIFY_COMPONENTS;
                        if (!InstallerUtils.toHex(InstallerUtils.checksum(new File(addonsDir, dl.getFileName()), "SHA-256")).equalsIgnoreCase(addon.getSha256())) {
                            callback.accept(new ErrorCallback(new IllegalStateException("Component's checksum does not match"), phrase, addon.getName() + "'s checksum does not match"));
                            return;
                        }
                    } catch (Exception ex) {
                        callback.accept(new ErrorCallback(ex, phrase, "Failed to download " + addon.getName() + ", " + ex.getMessage()));
                        return;
                    }
                }
            } catch (Exception ex) {
                callback.accept(new ErrorCallback(ex, phrase));
                return;
            }

            File launchWrapper = new File(libraries, "net" + sep + "minecraft" + sep + "launchwrapper" + sep + "1.7" + sep + "launchwrapper-1.7.jar");
            launchWrapper.getParentFile().mkdirs();
            if (!launchWrapper.exists()) {
                phrase = Phrase.DOWNLOAD_LAUNCHER;
                try {
                    DownloadTask dl = new DownloadTask("https://libraries.minecraft.net/net/minecraft/launchwrapper/1.7/launchwrapper-1.7.jar", launchWrapper.getParentFile().getAbsolutePath());
                    dl.addPropertyChangeListener(evt -> {
                        if (evt.getNewValue() instanceof Integer)
                            callback.accept(new StatusCallback(Phrase.DOWNLOAD_COMPONENTS, "Downloading LaunchWrapper (" + evt.getNewValue() + "%)", null));
                    });
                    dl.execute();
                    dl.get();
                } catch (Exception ex) {
                    callback.accept(new ErrorCallback(ex, phrase, "Failed to download LaunchWrapper, " + ex.getMessage()));
                    return;
                }
            }

            File customLaunchWrapper = new File(launchWrapper.getParentFile().getParentFile(), "Hyperium" + sep + "launchwrapper-Hyperium.jar");
            customLaunchWrapper.getParentFile().mkdir();
            phrase = Phrase.PATCH_LAUNCHER;
            try {
                LaunchWrapperPatcher.patch(launchWrapper, "cc/hyperium/utils/CrashHandler", "handle", "(Ljava/lang/Exception;)V", customLaunchWrapper);
            } catch (Exception ex) {
                callback.accept(new ErrorCallback(ex, phrase, "Failed to patch LaunchWrapper: " + ex.getMessage()));
                return;
            }

            phrase = Phrase.CREATE_PROFILE;
            callback.accept(new StatusCallback(phrase, "Creating profile", null));
            if (mmc) {
                File cfg = new File(target, "instance.cfg");
                Properties prop = new Properties();
                if (cfg.exists())
                    try {
                        prop.load(new FileReader(cfg));
                    } catch (IOException ex) {
                        callback.accept(new ErrorCallback(ex, phrase, "Failed to read instance.cfg"));
                        return;
                    }
                prop.setProperty("name", "Hyperium 1.8.9");
                prop.setProperty("InstanceType", "OneSix");
                prop.setProperty("MaxMemAlloc", String.valueOf(config.getWam() * 1024));
                prop.setProperty("MinMemAlloc", "512");
                prop.setProperty("iconKey", "hyperium");
                try {
                    prop.store(new FileWriter(cfg), null);
                } catch (IOException ex) {
                    callback.accept(new ErrorCallback(ex, phrase, "Failed to write instance.cfg"));
                    return;
                }

                File pack = new File(target, "mmc-pack.json");
                JsonHolder packJson;
                try {
                    packJson = pack.exists() ? new JsonHolder(Files.asCharSource(pack, Charset.defaultCharset()).read()) : new JsonHolder();
                } catch (IOException ex) {
                    callback.accept(new ErrorCallback(ex, phrase, "Failed to read mmc-pack.json"));
                    return;
                }
                JsonArray components = new JsonArray();
                components.add(
                        new JsonHolder()
                                .put("cachedName", "LWJGL 2")
                                .put("cachedVersion", "2.9.4-nightly-20150209")
                                .put("cachedVolatile", true)
                                .put("dependencyOnly", true)
                                .put("uid", "org.lwjgl")
                                .put("version", "2.9.4-nightly-20150209")
                                .getObject()
                );
                JsonArray cr = new JsonArray();
                cr.add(
                        new JsonHolder()
                                .put("suggests", "2.9.4-nightly-20150209")
                                .put("uid", "org.lwjgl")
                                .getObject()
                );
                components.add(
                        new JsonHolder()
                                .put("cachedName", "Minecraft")
                                .put("cachedRequires", cr)
                                .put("cachedVersion", "1.8.9")
                                .put("important", true)
                                .put("uid", "net.minecraft")
                                .put("version", "1.8.9")
                                .getObject()
                );
                components.add(
                        new JsonHolder()
                                .put("uid", "cc.hyperium")
                                .put("cachedName", "Hyperium")
                                .put("cachedVersion", "0.17")
                                .getObject()
                );
                packJson.put("components", components);
                packJson.put("formatVersion", 1);
                try {
                    Files.asCharSink(pack, Charset.defaultCharset()).write(packJson.toString());
                } catch (IOException ex) {
                    callback.accept(new ErrorCallback(ex, phrase, "Failed to write mmc-pack.json"));
                    return;
                }

                File patches = new File(target, "patches");
                patches.mkdir();

                JsonHolder hyperiumJson = new JsonHolder();
                JsonArray tweakers = new JsonArray();
                tweakers.add(new JsonPrimitive("cc.hyperium.launch.LaunchTweaker"));
                hyperiumJson.put("+tweakers", tweakers);

                JsonArray libs = new JsonArray();
                libs.add(
                        new JsonHolder()
                                .put("name", "cc.hyperium:Hyperium:" + config.getVersion().getName())
                                .put("MMC-hint", "local")
                                .getObject()
                );
                libs.add(
                        new JsonHolder()
                                .put("name", "net.minecraft:launchwrapper:Hyperium")
                                .put("MMC-hint", "local")
                                .getObject()
                );
                if (of)
                    libs.add(
                            new JsonHolder()
                                    .put("name", "optifine:OptiFine:1.8.9_HD_U_I7")
                                    .put("MMC-hint", "local")
                                    .getObject()
                    );
                hyperiumJson.put("libraries", libs);
                hyperiumJson.put("mainClass", "net.minecraft.launchwrapper.Launch");
                hyperiumJson.put("name", "Hyperium");
                hyperiumJson.put("version", config.getVersion().getName());
                hyperiumJson.put("uid", "cc.hyperium");
                hyperiumJson.put("formatVersion", 1);

                try {
                    Files.asCharSink(new File(patches, "cc.hyperium.json"), Charset.defaultCharset()).write(hyperiumJson.toString());
                } catch (IOException ex) {
                    callback.accept(new ErrorCallback(ex, phrase, "Failed to write cc.hyperium.json"));
                    return;
                }

                File icons = new File(mc, "icons");
                icons.mkdir();
                try {
                    Files.write(Base64.getDecoder().decode(InstallerUtils.ICON_BASE64.replace("data:image/png;base64,", "")), new File(icons, "hyperium.png"));
                } catch (IOException ex) {
                    callback.accept(new ErrorCallback(ex, phrase, "Failed to add hyperium icon"));
                    return;
                }

            } else {
                JsonHolder json;
                JsonHolder launcherProfiles;
                try {
                    json = new JsonHolder(Files.asCharSource(targetJson, Charset.defaultCharset()).read());
                    launcherProfiles = new JsonHolder(Files.asCharSource(new File(mc, "launcher_profiles.json"), Charset.defaultCharset()).read());
                } catch (IOException ex) {
                    callback.accept(new ErrorCallback(ex, phrase, "Failed to read profile"));
                    return;
                }
                JsonHolder lib = new JsonHolder();
                lib.put("name", "cc.hyperium:Hyperium:" + config.getVersion().getName());
                JsonArray libs = json.optJSONArray("libraries");
                libs.add(lib.getObject());
                libs.add(new JsonHolder().put("name", "net.minecraft:launchwrapper:Hyperium").getObject());
                if (of)
                    libs.add(new JsonHolder().put("name", "optifine:OptiFine:1.8.9_HD_U_I7").getObject());
                json.put("libraries", libs);
                json.put("id", "Hyperium 1.8.9");
                json.put("mainClass", "net.minecraft.launchwrapper.Launch");
                json.put("minecraftArguments", json.optString("minecraftArguments") + " --tweakClass=cc.hyperium.launch.HyperiumTweaker");

                JsonHolder profiles = launcherProfiles.optJSONObject("profiles");
                Instant instant = Instant.ofEpochMilli(System.currentTimeMillis());
                String installedUUID = UUID.randomUUID().toString();
                for (String key : profiles.getKeys()) {
                    if (profiles.optJSONObject(key).has("name"))
                        if (profiles.optJSONObject(key).optString("name").equals("Hyperium 1.8.9"))
                            installedUUID = key;
                }
                JsonHolder profile = new JsonHolder()
                        .put("name", "Hyperium 1.8.9")
                        .put("type", "custom")
                        .put("created", instant.toString())
                        .put("lastUsed", instant.toString())
                        .put("lastVersionId", "Hyperium 1.8.9")
                        .put("javaArgs", "-Xms512M -Xmx" + config.getWam() + "G")
                        .put("icon", InstallerUtils.ICON_BASE64);
                if (config.getLocalJre())
                    if (System.getProperty("java.version").startsWith("1.8"))
                        if (System.getProperty("sun.arch.data.model", "").equalsIgnoreCase("64")) {
                            File file = new File(System.getProperty("java.home"), "bin" + sep + "java" + (InstallerUtils.getOS() == InstallerUtils.OSType.Windows ? "w.exe" : ""));
                            if (file.exists())
                                profile.put("javaDir", file.getAbsolutePath());
                            else
                                InstallerMain.INSTANCE.getLogger().debug("Local JRE path does not exist, path = {}", file.getAbsolutePath());
                        }
                profiles.put(installedUUID, profile);
                launcherProfiles.put("profiles", profiles);

                try {
                    Files.asCharSink(targetJson, Charset.defaultCharset()).write(json.toString());
                    Files.asCharSink(new File(mc, "launcher_profiles.json"), Charset.defaultCharset()).write(launcherProfiles.toString());
                } catch (IOException ex) {
                    callback.accept(new ErrorCallback(ex, phrase, "Failed to write profile"));
                    return;
                }
            }

            phrase = Phrase.DONE;
            callback.accept(new StatusCallback(phrase, "Installation success! Launch from your Minecraft launcher.", null));
            code = 0;
        } catch (Exception ex) {
            callback.accept(new ErrorCallback(ex, phrase));
        }
    }

    public int getCode() {
        return code;
    }

    public void setCallback(Consumer<AbstractCallback> callback) {
        this.callback = callback;
    }
}
