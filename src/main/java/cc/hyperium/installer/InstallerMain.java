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
    private InstallerConfig config;
    private JFrame frame;
    private Font title;
    private Font font;

    public static void main(String... args) {
        if (args.length >= 1) {
            boolean local = args[0].equalsIgnoreCase("local");
            INSTANCE.init(local);
        } else {
            // Conventional installation.
            INSTANCE.init(false);
        }
    }

    private void init(boolean local) {
        AtomicBoolean pass = new AtomicBoolean(false);
        Multithreading.runAsync(() -> {
            InstallerUtils.getManifest();
            if (frame != null && frame.getContentPane().getComponents().length == 2)
                next();
            else
                pass.set(true);
        });

        loadPreviousConfig();

        steps.addAll(Arrays.stream(new InstallerStep[]{
                new LoadingStep(),
                new WelcomeScreen(),
                new MethodScreen(),
                new SettingsScreen(),
                local ? null : new VersionScreen(),
                new AddonsScreen(),
                new PrivacyScreen(),
                new InstallingScreen()
        }).filter(Objects::nonNull).collect(Collectors.toList()));
        if (local)
            config.setVersion(new VersionManifest("LOCAL", 0, "cc.hyperium:Hyperium:LOCAL", "", "", 0, 0, false, Installer.API_VERSION));
        try {
            font = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/fonts/segoeuil.ttf")).deriveFont(15f);
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
            font = new Font("Arial", Font.PLAIN, 15);
        }
        title = font.deriveFont(50f);
        SwingUtilities.invokeLater(() -> {
            initFrame();
            if (pass.get())
                next();
        });
    }

    private void initFrame() {
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
}
