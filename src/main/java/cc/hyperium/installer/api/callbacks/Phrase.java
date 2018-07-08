package cc.hyperium.installer.api.callbacks;

/*
 * Created by Cubxity on 08/07/2018
 */
public enum Phrase {

    /**
     * Pre checks before installation process
     */
    PRE,

    /**
     * When it's copying '1.8.9' version
     */
    COPY_VERSION,

    /**
     * Copies the jar installer is running from (LOCAL)
     */
    COPY_LOCAL,

    /**
     * When it's downloading client
     */
    DOWNLOAD_CLIENT,

    /**
     * Client integrity verification
     */
    VERIFY_CLIENT,

    /**
     * When it's downloading components such as Optifine and adddons
     */
    DOWNLOAD_COMPONENTS,

    /**
     * Addon integrity verification
     */
    VERIFY_COMPONENTS,

    /**
     * When it's patching optifine, only when user has selected it
     */
    PATCH_OPTIFINE,

    /**
     * When it's creating launcher profile
     */
    CREATE_PROFILE,


    /**
     * When installation has finished
     */
    DONE
}
