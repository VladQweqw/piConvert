import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.formdev.flatlaf.IntelliJTheme;



public class App {
    private static final JFrame frame = new JFrame("piConvert");
    private static final String[] available_extensions = {
            "jpg",
            "png",
            "gif",
            "bmp",
            "tiff",
            "tif",
            "wbmp",
            "jp2",
            "j2k",
            "pbm",
            "pgm",
            "ppm"
    };
    private final String version = "1.0";
    private static String filePath = "";
    private JLabel ImagePreview = new JLabel();
    private String selectedExtension = available_extensions[0];
    private JButton selectedExtensionElement = new JButton("no file");

    private static void init() {
        try {
            InputStream mediumFontStream = App.class.getResourceAsStream("/fonts/Poppins-Medium.ttf");
            Font poppinsMedium = Font.createFont(Font.TRUETYPE_FONT, mediumFontStream).deriveFont(14f);
            UIManager.put("defaultFont", poppinsMedium);
            UIManager.put("Label.font", poppinsMedium);
            UIManager.put("Button.font", poppinsMedium);

            System.out.println("Font loaded succesfully!");
        } catch (IOException | FontFormatException e) {
            System.out.println("Error getting the font");
            throw new RuntimeException(e);
        }

        try {
            IntelliJTheme.setup(
                    App.class.getResourceAsStream("/themes/piConvert_theme.theme.json")
            );

            System.out.println("Theme loaded succesfully!");
        } catch(Exception e) {
            System.out.println("Failed to load theme");
        }

        try {
            ImageIcon icon = new ImageIcon(App.class.getResource("/images/logo.png"));
            frame.setIconImage(icon.getImage());

            System.out.println("Logo loaded succesfully!");
        } catch(Exception e) {
            System.out.println("Failed to load logo");
        }

    }

    public JButton FileExplorer() {
        JButton btn = new JButton("Select from PC");

        btn.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();

            FileNameExtensionFilter imageFilter = new FileNameExtensionFilter(
                    "Image Files",
                    "jpg",
                    "png",
                    "gif",
                    "bmp",
                    "tiff",
                    "tif",
                    "wbmp",
                    "jp2",
                    "j2k",
                    "pbm",
                    "pgm",
                    "ppm"
            );

            fileChooser.setFileFilter(imageFilter);

            int result = fileChooser.showOpenDialog(null);

            if(result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                this.filePath = selectedFile.getAbsolutePath();

                UpdateImage();
            }else {
                this.filePath = "";
            }

        });

        return btn;

    }

    public void UpdateImage() {

        try {
            File file = new File(this.filePath);

            Image image = ImageIO.read(file);
            Image resizedImaged = image.getScaledInstance(380, 200, Image.SCALE_SMOOTH);

            ImagePreview.setIcon(
                    new ImageIcon(resizedImaged)
            );

        } catch(IOException e) {
            System.out.println("Couldn't scale image");
        }


    }

    public BufferedImage convertImageType(BufferedImage inputImage, int targetType) {
        boolean isPng = getExtension(new File(this.filePath).getName()).equals("png");
        System.out.println(isPng);
        if(isPng) {
            targetType = BufferedImage.TYPE_INT_RGB;
        }

        BufferedImage outputImage = new BufferedImage(inputImage.getWidth(), inputImage.getHeight(), targetType);
        Graphics2D g2d = outputImage.createGraphics();

        if(isPng) {
            g2d.setColor(Color.WHITE); // Set background to white
            g2d.fillRect(0, 0, outputImage.getWidth(), outputImage.getHeight());

        }

        g2d.drawImage(inputImage, 0, 0, null);
        g2d.dispose();

        return outputImage;
    }

    public String removeExtension(String path) {
        for(int i = path.length() - 1; i > 0; i--) {
            if(path.charAt(i) == '.') {
                return path.substring(0, i);
            }
        }

        return path;
    }

    public String getExtension(String path) {
        for(int i = 0; i < path.length(); i++) {
            if(path.charAt(i) == '.') {
                return path.substring(i + 1);
            }
        }

        return path;
    }

    public JButton ConvertButton() {
        JButton convert = new JButton("Convert");
        JFileChooser chooser = new JFileChooser();

        convert.addActionListener(e -> {
            try {
                File inputFile = new File(this.filePath);
                String save_path =
                        inputFile.getParent() + File.separator;

                BufferedImage inputImage = ImageIO.read(inputFile);
                BufferedImage convertedImage = convertImageType(inputImage, BufferedImage.TYPE_INT_ARGB);

                // set the default names
                chooser.setCurrentDirectory(inputFile.getParentFile());
                chooser.setSelectedFile(
                        new File(save_path + removeExtension(inputFile.getName()) + "." + this.selectedExtension)
                );

                int result = chooser.showSaveDialog(null);

                if(result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = chooser.getSelectedFile();
                    save_path += selectedFile.getName();
                }

                ImageIO.write(convertedImage, selectedExtension, new File(save_path));
                System.out.println("Image converted success!");

            } catch(IOException ex) {
                System.out.println("Couldn't convert image");
            }

        });

        return convert;
    }

    public JComboBox ExtensionsList() {
        JComboBox extensionList = new JComboBox(available_extensions);

        extensionList.addActionListener(e -> {
            this.selectedExtension = (String) extensionList.getSelectedItem();

            System.out.println("Selected: " + this.selectedExtension);
        });

        return extensionList;
    }

    public JPanel ConversionButtons() {
        // create a wrapper
        JPanel wrapper = new JPanel();
        wrapper.setPreferredSize(new Dimension(380, 50));

        // initial extension
        selectedExtensionElement.setEnabled(false);
        wrapper.add(selectedExtensionElement);

        // arrow
        JLabel arrow = new JLabel("--->");
        wrapper.add(arrow);

        // extensions list
        wrapper.add(ExtensionsList());

        return wrapper;
    }

    public App() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        // preview image
        ImagePreview.setIcon(new ImageIcon(App.class.getResource("/images/no-image.png")));
        ImagePreview.setPreferredSize(new Dimension(380, 200));
        ImagePreview.setHorizontalAlignment(SwingUtilities.CENTER);
        panel.add(ImagePreview);

        // files exporer
        panel.add(FileExplorer());

        panel.add(ConversionButtons());

        // convert button
        panel.add(ConvertButton());

        JLabel version = new JLabel("Version v." + this.version);
        version.setPreferredSize(new Dimension(380, 30));
        version.setHorizontalAlignment(SwingUtilities.CENTER);
        panel.add(version);

        frame.setContentPane(panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 450);
        frame.setVisible(true);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        init();

        if(args.length > 0) {
            filePath = args[0];
            System.out.println(filePath);
        }

        SwingUtilities.invokeLater(App::new);
    }
}