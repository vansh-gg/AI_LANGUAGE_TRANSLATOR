package Sql;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;

import org.json.*;

import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.LiveSpeechRecognizer;

public class TranslatorApp1 extends JFrame {

    private static final long serialVersionUID = 1L;
    private JTextArea inputArea, outputArea;
    private JComboBox<String> fromLang, toLang;
    private JButton translateBtn, voiceBtn, swapBtn, historyBtn;

    public TranslatorApp1() {
        setTitle("AI LANGUAGE TRANSLATOR");
        setSize(750, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(15, 15));
        getContentPane().setBackground(new Color(10, 25, 47));

        // Header
        JLabel title = new JLabel("AI LANGUAGE TRANSLATOR", SwingConstants.CENTER);
        title.setForeground(new Color(0, 198, 255));
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));
        add(title, BorderLayout.NORTH);

        // Input and Output Areas
        inputArea = new JTextArea();
        inputArea.setLineWrap(true);
        inputArea.setWrapStyleWord(true);
        inputArea.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        inputArea.setBackground(new Color(30, 41, 59));
        inputArea.setForeground(Color.WHITE);
        inputArea.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(0, 198, 255), 1),
                "   INPUT TEXT   ",
                TitledBorder.CENTER, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 15),
                new Color(0, 198, 255)));

        outputArea = new JTextArea();
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        outputArea.setBackground(new Color(30, 41, 59));
        outputArea.setForeground(new Color(226, 232, 240));
        outputArea.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(0, 198, 255), 1),
                "   TRANSLATED TEXT   ",
                TitledBorder.CENTER, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 15),
                new Color(0, 198, 255)));

        JPanel textPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        textPanel.setBackground(new Color(10, 25, 47));
        textPanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        textPanel.add(new JScrollPane(inputArea));
        textPanel.add(new JScrollPane(outputArea));
        add(textPanel, BorderLayout.CENTER);

        // Language selectors
        String[] langs = { "English", "Spanish", "French", "Hindi", "Italian",
                "German", "Chinese", "Japanese", "Korean", "Russian" };
        fromLang = new JComboBox<>(langs);
        toLang = new JComboBox<>(langs);
        fromLang.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        toLang.setFont(new Font("Segoe UI", Font.PLAIN, 15));

        JLabel fromLabel = new JLabel("From:");
        fromLabel.setForeground(new Color(173, 216, 230));
        fromLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));

        JLabel toLabel = new JLabel("To:");
        toLabel.setForeground(new Color(173, 216, 230));
        toLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));

        swapBtn = new JButton("Swap");
        swapBtn.setBackground(new Color(0, 198, 255));
        swapBtn.setForeground(Color.BLACK);
        swapBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        swapBtn.addActionListener(e -> swapLanguages());

        // 🎤 Voice Input
        voiceBtn = new JButton("Speak");
        voiceBtn.setBackground(new Color(37, 99, 235));
        voiceBtn.setForeground(Color.WHITE);
        voiceBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        voiceBtn.addActionListener(e -> startVoiceInput());

        // 🚀 Translate Button
        translateBtn = new JButton("Translate");
        translateBtn.setFont(new Font("Segoe UI", Font.BOLD, 18));
        translateBtn.setBackground(new Color(0, 114, 255));
        translateBtn.setForeground(Color.WHITE);
        translateBtn.addActionListener(e -> translateText());

        // 🕒 View History Button
        historyBtn = new JButton("View History");
        historyBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        historyBtn.setBackground(new Color(0, 198, 255));
        historyBtn.addActionListener(e -> showTranslationHistory());

        // Bottom Layout
        JPanel bottomPanel = new JPanel(new GridBagLayout());
        bottomPanel.setBackground(new Color(15, 23, 42));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        gbc.gridx = 0;
        gbc.gridy = 0;
        bottomPanel.add(fromLabel, gbc);
        gbc.gridx = 1;
        bottomPanel.add(fromLang, gbc);
        gbc.gridx = 2;
        bottomPanel.add(swapBtn, gbc);
        gbc.gridx = 3;
        bottomPanel.add(toLabel, gbc);
        gbc.gridx = 4;
        bottomPanel.add(toLang, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        bottomPanel.add(voiceBtn, gbc);
        gbc.gridx = 2;
        gbc.gridwidth = 2;
        bottomPanel.add(translateBtn, gbc);
        gbc.gridx = 4;
        bottomPanel.add(historyBtn, gbc);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    // Swap languages
    private void swapLanguages() {
        int fromIndex = fromLang.getSelectedIndex();
        int toIndex = toLang.getSelectedIndex();
        fromLang.setSelectedIndex(toIndex);
        toLang.setSelectedIndex(fromIndex);
    }

    private void startVoiceInput() {
        new Thread(() -> {
            try {
                Configuration config = new Configuration();
                config.setAcousticModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us");
                config.setDictionaryPath("resource:/edu/cmu/sphinx/models/en-us/cmudict-en-us.dict");
                config.setLanguageModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us.lm.bin");

                LiveSpeechRecognizer recognizer = new LiveSpeechRecognizer(config);
                recognizer.startRecognition(true);

                JOptionPane.showMessageDialog(this, "🎤 Speak now...");
                String result = recognizer.getResult().getHypothesis();
                recognizer.stopRecognition();

                inputArea.setText(result);
                JOptionPane.showMessageDialog(this, "✅ Captured: " + result);

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Voice input error: " + ex.getMessage());
            }
        }).start();
    }

    private void translateText() {
        String text = inputArea.getText().trim();
        if (text.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter some text!");
            return;
        }

        String from = fromLang.getSelectedItem().toString().split(" - ")[0];
        String to = toLang.getSelectedItem().toString().split(" - ")[0];

        try {
            String urlStr = "https://api.mymemory.translated.net/get?q=" +
                    URLEncoder.encode(text, "UTF-8") +
                    "&langpair=" + from + "|" + to;

            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null)
                response.append(line);
            in.close();

            JSONObject json = new JSONObject(response.toString());
            String translated = json.getJSONObject("responseData").getString("translatedText");

            outputArea.setText(translated);

            // 💾 Save translation to DB
            TranslationDatabase.saveTranslation(text, translated);

        } catch (Exception e) {
            outputArea.setText("Error translating: " + e.getMessage());
        }
    }

    // 🧠 Show history from MySQL
    private void showTranslationHistory() {
        java.util.List<String> history = TranslationDatabase.getTranslationHistory();
        if (history.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No translations saved yet!");
            return;
        }

        JTextArea historyArea = new JTextArea(15, 40);
        historyArea.setEditable(false);
        for (String record : history)
            historyArea.append(record + "\n");
        JScrollPane scroll = new JScrollPane(historyArea);

        JOptionPane.showMessageDialog(this, scroll, "Translation History", JOptionPane.INFORMATION_MESSAGE);
    }

    // Main
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TranslatorApp1 app = new TranslatorApp1();
            app.setVisible(true);
        });
    }
}
