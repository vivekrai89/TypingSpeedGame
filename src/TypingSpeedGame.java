import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class TypingSpeedGame extends JFrame {

    private JTextPane sentencePane;  // Allows colored text
    private JTextField typingField;
    private JLabel timerLabel, wpmLabel;
    private JButton startButton, newLineButton;

    // Array of sentences for random selection
    private String[] sentences = {
            "The quick brown fox jumps over the lazy dog.",
            "Typing fast requires practice and patience.",
            "Java programming is fun and powerful.",
            "Swing makes GUI development easy.",
            "Practice makes perfect in every skill."
    };
    private String sentence; // Current sentence

    private long startTime;
    private Timer timer;

    public TypingSpeedGame() {
        setTitle("Typing Speed Game");
        setSize(750, 350);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        setLocationRelativeTo(null);  // Center window

        // Sentence display with colors
        sentencePane = new JTextPane();
        sentencePane.setFont(new Font("Arial", Font.BOLD, 16));
        sentencePane.setEditable(false);
        sentencePane.setBackground(Color.LIGHT_GRAY);
        sentencePane.setBorder(new EmptyBorder(10,10,10,10));
        add(sentencePane, BorderLayout.NORTH);

        // Typing field
        typingField = new JTextField();
        typingField.setFont(new Font("Arial", Font.PLAIN, 16));
        typingField.setEnabled(false);
        add(typingField, BorderLayout.CENTER);

        // Timer and WPM panel
        JPanel statsPanel = new JPanel(new GridLayout(1, 2, 20, 10));
        timerLabel = new JLabel("Time: 0s", SwingConstants.CENTER);
        timerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        timerLabel.setOpaque(true);
        timerLabel.setBackground(Color.YELLOW);

        wpmLabel = new JLabel("WPM: 0", SwingConstants.CENTER);
        wpmLabel.setFont(new Font("Arial", Font.BOLD, 16));
        wpmLabel.setOpaque(true);
        wpmLabel.setBackground(Color.CYAN);

        statsPanel.add(timerLabel);
        statsPanel.add(wpmLabel);
        statsPanel.setBorder(new EmptyBorder(10,10,10,10));
        add(statsPanel, BorderLayout.EAST);

        // Panel for buttons
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 20, 10));
        startButton = new JButton("Start");
        startButton.setFont(new Font("Arial", Font.BOLD, 16));

        newLineButton = new JButton("New Line");
        newLineButton.setFont(new Font("Arial", Font.BOLD, 16));
        newLineButton.setEnabled(false); // only enabled after start

        buttonPanel.add(startButton);
        buttonPanel.add(newLineButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Button actions
        startButton.addActionListener(e -> startGame());
        newLineButton.addActionListener(e -> startNewLine());

        setVisible(true);
    }

    private void startGame() {
        pickRandomSentence();
        typingField.setEnabled(true);
        typingField.setText("");
        typingField.requestFocus();
        startButton.setEnabled(false);
        newLineButton.setEnabled(true);

        startTime = System.currentTimeMillis();
        timerLabel.setText("Time: 0s");
        wpmLabel.setText("WPM: 0");

        typingField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                updateTextColors();
                updateWPM();
            }
        });

        timer = new Timer(1000, e -> {
            long seconds = (System.currentTimeMillis() - startTime) / 1000;
            timerLabel.setText("Time: " + seconds + "s");
        });
        timer.start();
    }

    private void startNewLine() {
        if (timer != null) timer.stop(); // stop current timer
        pickRandomSentence();
        typingField.setEnabled(true);
        typingField.setText("");
        typingField.requestFocus();

        startTime = System.currentTimeMillis();
        timerLabel.setText("Time: 0s");
        wpmLabel.setText("WPM: 0");

        // Timer restarts
        timer = new Timer(1000, e -> {
            long seconds = (System.currentTimeMillis() - startTime) / 1000;
            timerLabel.setText("Time: " + seconds + "s");
        });
        timer.start();
    }

    private void pickRandomSentence() {
        int index = (int) (Math.random() * sentences.length);
        sentence = sentences[index];
        sentencePane.setText(sentence);
    }

    private void updateTextColors() {
        String typed = typingField.getText();
        StyledDocument doc = sentencePane.getStyledDocument();
        StyleContext sc = StyleContext.getDefaultStyleContext();
        AttributeSet green = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, Color.GREEN);
        AttributeSet red = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, Color.RED);
        AttributeSet black = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, Color.BLACK);

        doc.setCharacterAttributes(0, sentence.length(), black, true);

        int minLength = Math.min(typed.length(), sentence.length());
        for (int i = 0; i < minLength; i++) {
            if (typed.charAt(i) == sentence.charAt(i)) {
                doc.setCharacterAttributes(i, 1, green, false);
            } else {
                doc.setCharacterAttributes(i, 1, red, false);
            }
        }

        // Stop timer if sentence complete
        if (typed.equals(sentence)) {
            timer.stop();
            typingField.setEnabled(false);
            startButton.setEnabled(true);
        }
    }

    private void updateWPM() {
        String typed = typingField.getText();
        long elapsedSec = (System.currentTimeMillis() - startTime) / 1000;
        if (elapsedSec == 0) return;

        String[] typedWords = typed.split(" ");
        String[] sentenceWords = sentence.split(" ");
        int correct = 0;
        for (int i = 0; i < Math.min(typedWords.length, sentenceWords.length); i++) {
            if (typedWords[i].equals(sentenceWords[i])) correct++;
        }

        double wpm = (correct / (double) elapsedSec) * 60;
        wpmLabel.setText(String.format("WPM: %.2f", wpm));
    }

    public static void main(String[] args) {
        new TypingSpeedGame();
    }
}
