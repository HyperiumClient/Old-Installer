package cc.hyperium.installer;

import cc.hyperium.installer.api.Installer;
import cc.hyperium.installer.api.entities.InstallerConfig;
import cc.hyperium.installer.api.entities.VersionManifest;
import cc.hyperium.installer.steps.*;
import cc.hyperium.utils.Colors;
import cc.hyperium.utils.InstallerUtils;
import cc.hyperium.utils.Multithreading;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.plaf.metal.MetalLookAndFeel;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class InstallerMain {
    public static final InstallerMain INSTANCE = new InstallerMain();
    private final Queue<InstallerStep> steps = new ArrayDeque<>();
    private final Logger logger = LoggerFactory.getLogger("Installer");
    private final StringBuilder log = new StringBuilder();
    public String launchCommand = "";
    private InstallerConfig config;
    private JFrame frame;
    private Font title;
    private Font font;

    public static void main(String... args) {
        if (args.length >= 1) {
            boolean local = args[0].equalsIgnoreCase("local");

            boolean forward = args[0].equalsIgnoreCase("fw");
            StringBuilder fwCmd = new StringBuilder(); // for fast install being called by auto updater
            if (args.length >= 2)
                for (int i = 1; i < args.length; i++)
                    fwCmd.append(args[i]).append(" ");
            if (forward) {
                // Installer has been called from the client.
                INSTANCE.logger.info("LAUNCH COMMAND: " + fwCmd);
                INSTANCE.setLaunchCommand(fwCmd.toString());
                INSTANCE.fastInstall(true);
            } else {
                // Installer has been called from the command line.
                INSTANCE.init(local);
            }
        } else {
            // Conventional installation.
            INSTANCE.init(false);
        }
    }

    private void fastInstall(boolean local) {
        loadPreviousConfig();

        if (local) {
            config.setVersion(new VersionManifest("LOCAL", 0, "cc.hyperium:Hyperium:LOCAL", "", "", 0, 0, false, Installer.API_VERSION));
        } else {
            config.setVersion(InstallerUtils.getManifest().getLatest());
        }
        try {
            font = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/fonts/segoeuil.ttf")).deriveFont(15f);
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
            font = new Font("Arial", Font.PLAIN, 15); //Fallback
        }

        System.out.println("Config INFO: ");
        System.out.println("Version: " + INSTANCE.getConfig().getVersion());

        title = font.deriveFont(50f);

        // Start installing phase immediately.
        logger.info("Beginning installation...");
        initFrame();
        renderStep(new InstallingScreen());
    }

    public void launchMinecraft() {
        Thread launchThread = new Thread(() -> {
            try {
                Runtime.getRuntime().exec(INSTANCE.getLaunchCommand());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        launchThread.start();
        System.exit(0);
    }

    public String getLaunchCommand() {
        return launchCommand;
    }

    public void setLaunchCommand(String launchCommand) {
        this.launchCommand = launchCommand;
    }

    private void init(boolean local) {
        final PrintStream ps = System.out;
        final PrintStream logStream = new PrintStream(new OutputStream() {
            @Override
            public void write(int b) {
                ps.write(b);
                log.append((char) b);
            }
        });
        System.setOut(logStream);
        System.setErr(logStream);

        AtomicBoolean pass = new AtomicBoolean(false);
        Multithreading.runAsync(() -> {
            InstallerUtils.getManifest();
            if (frame != null && frame.getContentPane().getComponents().length == 2)
                next();
            else
                pass.set(true);
        });

        loadPreviousConfig();

        logger.debug("Local = {}", local);
        logger.info("Starting installer...");
        steps.addAll(Arrays.stream(new InstallerStep[]{
                new LoadingStep(),
                new WelcomeScreen(),
                new MethodScreen(),
                new SettingsScreen(),
                local ? null : new VersionScreen(),
                new AddonsScreen(),
                new TOSScreen(),
                new PrivacyScreen(),
                new InstallingScreen()
        }).filter(Objects::nonNull).collect(Collectors.toList()));
        if (local)
            config.setVersion(new VersionManifest("LOCAL", 0, "cc.hyperium:Hyperium:LOCAL", "", "", 0, 0, false, Installer.API_VERSION));
        try {
            font = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/fonts/segoeuil.ttf")).deriveFont(15f);
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
            font = new Font("Arial", Font.PLAIN, 15); //Fallback
        }
        title = font.deriveFont(50f);
        logger.info("Initializing frame...");
        SwingUtilities.invokeLater(() -> {
            initFrame();
            if (pass.get())
                next();
        });
    }

    private void initFrame() {
        logger.info("Initialing frame...");
        try {
            UIManager.setLookAndFeel(new MetalLookAndFeel());
        } catch (Exception ignored) {
        }
        frame = new JFrame("HyperiumJailbreak Installer");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        try {
            frame.setIconImage(ImageIO.read(getClass().getResourceAsStream("/icons/hyperium.png")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        frame.setUndecorated(true);

        frame.getContentPane().setBackground(Colors.DARK);
        frame.getContentPane().setLayout(null);
        frame.pack();

        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setSize(dim.width / 2, dim.height / 2);
        frame.setLocation(dim.width / 4, dim.height / 4);

        frame.setVisible(true);

        next();
    }


    public void next() {
        if (steps.isEmpty()) return;
        renderStep(steps.poll());
    }

    private void renderStep(InstallerStep installerStep) {
        SwingUtilities.invokeLater(() -> {
            frame.getContentPane().removeAll();
            installerStep.modifyFrame(frame);
            installerStep.addComponents(frame.getContentPane());
            frame.repaint();
        });
    }

    private void loadPreviousConfig() {
        logger.info("Loading previous settings...");
        File prev = new File(System.getProperty("user.home"), "hinstaller-state.json");
        if (prev.exists()) {
            try {
                config = new Gson().fromJson(new String(Files.readAllBytes(prev.toPath()), Charset.defaultCharset()), InstallerConfig.class);
            } catch (Exception ex) {
                logger.error("Failed to load previous installer config", ex);
                config = new InstallerConfig();
            }
        } else {
            config = new InstallerConfig();
        }
    }

    public JFrame getFrame() {
        return frame;
    }

    public Font getFont() {
        return font;
    }

    public Font getTitle() {
        return title;
    }

    public InstallerConfig getConfig() {
        return config;
    }

    public Logger getLogger() {
        return logger;
    }

    public StringBuilder getLog() {
        return log;
    }
}
