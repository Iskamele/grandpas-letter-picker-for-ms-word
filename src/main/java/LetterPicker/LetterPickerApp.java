package LetterPicker;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.font.TextAttribute;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class LetterPickerApp {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Initial Picker");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new BorderLayout());
            frame.add(new AlphabetPanel(), BorderLayout.CENTER);
            frame.add(new ControlPanel(), BorderLayout.SOUTH);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    static class AlphabetPanel extends JPanel {
        static int fontSize = 24; // Default font size

        public AlphabetPanel() {
            setLayout(new GridLayout(0, 5)); // Layout with 5 columns
            Font customFont = loadCustomFont(fontSize); // Load font

            for (int i = 1040; i <= 1071; i++) {
                char c = (char) i;
                JButton button = new JButton(String.valueOf(c));
                button.setFont(customFont);
                button.addActionListener(e -> copyToClipboard(button.getText(), fontSize));
                add(button);
            }
        }

        private void copyToClipboard(String text, int fontSize) {
            String htmlText = "<html><body><span style='font-family:Barocco Floral Initial;font-size:" + fontSize + "pt;'>"
                    + text
                    + "</span></body></html>";
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(new HtmlSelection(htmlText), null);
//            JOptionPane.showMessageDialog(this, text + " скопирована в буфер обмена");
        }

        private Font loadCustomFont(int size) {
            Font font = new Font("Barocco Floral Initial", Font.PLAIN, size);
            Map<TextAttribute, Object> attributes = new HashMap<>();
            attributes.put(TextAttribute.SIZE, 70);
            return font.deriveFont(attributes);
        }

        static void setFontSize(int newSize) {
            fontSize = newSize;
        }
    }

    static class ControlPanel extends JPanel {
        public ControlPanel() {
            Integer[] sizes = {12, 14, 16, 18, 20, 22, 24, 26, 28, 30};
            JComboBox<Integer> sizePicker = new JComboBox<>(sizes);
            sizePicker.setSelectedItem(24); // Default size
            sizePicker.addActionListener(e -> AlphabetPanel.setFontSize((Integer) sizePicker.getSelectedItem()));

            JTextField customSizeField = new JTextField(5);
            customSizeField.getDocument().addDocumentListener(new DocumentListener() {
                public void changedUpdate(DocumentEvent e) {
                    updateSize();
                }

                public void removeUpdate(DocumentEvent e) {
                    updateSize();
                }

                public void insertUpdate(DocumentEvent e) {
                    updateSize();
                }

                private void updateSize() {
                    SwingUtilities.invokeLater(() -> {
                        try {
                            int customSize = Integer.parseInt(customSizeField.getText());
                            AlphabetPanel.setFontSize(customSize);
                        } catch (NumberFormatException ex) {
                            // Handle exception if the input is not a valid number
                        }
                    });
                }
            });

            add(new JLabel("Выбор размера шрифта:"));
            add(sizePicker);
            add(new JLabel("Ввод размера шрифта:"));
            add(customSizeField);
        }
    }

    private static class HtmlSelection implements Transferable {
        private static final DataFlavor HTML_FLAVOR = new DataFlavor("text/html; class=java.lang.String; charset=UTF-8", null);
        private final String html;

        public HtmlSelection(String html) {
            this.html = html;
        }

        @Override
        public DataFlavor[] getTransferDataFlavors() {
            return new DataFlavor[]{HTML_FLAVOR, DataFlavor.stringFlavor};
        }

        @Override
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return HTML_FLAVOR.equals(flavor) || DataFlavor.stringFlavor.equals(flavor);
        }

        @Override
        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
            if (HTML_FLAVOR.equals(flavor)) {
                return html;
            } else if (DataFlavor.stringFlavor.equals(flavor)) {
                return html.replaceAll("<[^>]+>", "");
            } else {
                throw new UnsupportedFlavorException(flavor);
            }
        }
    }
}
