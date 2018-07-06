package cc.hyperium.installer;

import cc.hyperium.installer.api.entities.InstallerConfig;
import cc.hyperium.installer.steps.InstallerStep;
import cc.hyperium.installer.steps.SettingsScreen;
import cc.hyperium.installer.steps.VersionScreen;
import cc.hyperium.installer.steps.WelcomeScreen;
import cc.hyperium.utils.Colors;
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
import java.util.Queue;

/*
 * Created by Cubxity on 05/07/2018
 */
public class InstallerMain {
    public static final InstallerMain INSTANCE = new InstallerMain();

    private Queue<InstallerStep> steps = new ArrayDeque<>();
    private Logger logger = LoggerFactory.getLogger("Installer");
    private InstallerConfig config;
    private JFrame frame;
    private Font title;
    private Font font;

    private void init() {
        logger.info("Loading previous settings...");
        File prev = new File(System.getProperty("user.home"), "hinstaller-state.json");
        if (prev.exists())
            try {
                config = new Gson().fromJson(new String(Files.readAllBytes(prev.toPath()), Charset.defaultCharset()), InstallerConfig.class);
            } catch (Exception ex) {
                logger.error("Failed to load previous installer config", ex);
                config = new InstallerConfig();
            }
        else
            config = new InstallerConfig();

        logger.info("Starting installer...");
        steps.addAll(Arrays.asList(
                new WelcomeScreen(),
                new SettingsScreen(),
                new VersionScreen()
        ));
        try {
            font = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/fonts/segoeuil.ttf")).deriveFont(15f);
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
            font = new Font("Arial", Font.PLAIN, 15); //Fallback
        }
        title = font.deriveFont(50f);
        logger.info("Initialing frame...");
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(new MetalLookAndFeel());
            } catch (Exception ignored) {
            }
            frame = new JFrame("Hyperium Installer");
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
        });
    }

    public static void main(String... args) {
        INSTANCE.init();
    }

    public void next() {
        if (steps.isEmpty()) return;
        InstallerStep step = steps.poll();
        SwingUtilities.invokeLater(() -> {
            frame.getContentPane().removeAll();
            step.modifyFrame(frame);
            step.addComponents(frame.getContentPane());
            frame.repaint();
        });
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
}
