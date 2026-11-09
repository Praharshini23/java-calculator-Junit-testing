package com.calculator;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class Main {
    private static final Color BACKGROUND = new Color(11, 16, 29);
    private static final Color PANEL = new Color(20, 27, 44);
    private static final Color NUMBER_BUTTON = new Color(31, 40, 60);
    private static final Color UTILITY_BUTTON = new Color(42, 52, 73);
    private static final Color ACCENT = new Color(111, 125, 255);
    private static final Color TEXT = new Color(242, 245, 255);
    private static final Color MUTED = new Color(137, 150, 178);

    private final Calculator calculator = new Calculator();
    private final JTextField display = new JTextField("0");
    private final JLabel status = new JLabel("READY  •  STANDARD");
    private double storedValue;
    private String pendingOperation;
    private boolean startNewNumber = true;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main().show());
    }

    private void show() {
        JFrame frame = new JFrame("Calculator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(370, 590));
        frame.setSize(390, 650);
        frame.getContentPane().setBackground(BACKGROUND);
        frame.setLayout(new BorderLayout(0, 18));
        frame.getRootPane().setBorder(BorderFactory.createEmptyBorder(22, 22, 22, 22));

        JPanel top = new JPanel(new BorderLayout(0, 8));
        top.setOpaque(false);
        JLabel title = new JLabel("CALCULATOR");
        title.setFont(new Font("Segoe UI", Font.BOLD, 12));
        title.setForeground(MUTED);
        title.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 0));
        status.setFont(new Font("Segoe UI", Font.BOLD, 10));
        status.setForeground(new Color(129, 218, 190));
        status.setHorizontalAlignment(SwingConstants.RIGHT);
        JPanel heading = new JPanel(new BorderLayout());
        heading.setOpaque(false);
        heading.add(title, BorderLayout.WEST);
        heading.add(status, BorderLayout.EAST);
        top.add(heading, BorderLayout.NORTH);

        display.setEditable(false);
        display.setFocusable(false);
        display.setHorizontalAlignment(SwingConstants.RIGHT);
        display.setFont(new Font("Segoe UI", Font.PLAIN, 48));
        display.setForeground(TEXT);
        display.setBackground(PANEL);
        display.setBorder(BorderFactory.createEmptyBorder(20, 18, 18, 18));
        JPanel displayCard = new RoundedPanel(PANEL, 26);
        displayCard.setLayout(new BorderLayout());
        displayCard.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        displayCard.add(display, BorderLayout.CENTER);
        top.add(displayCard, BorderLayout.CENTER);
        frame.add(top, BorderLayout.NORTH);

        JPanel keys = new JPanel(new GridLayout(5, 4, 11, 11));
        keys.setOpaque(false);
        String[] labels = {"C", "±", "%", "÷", "7", "8", "9", "×",
                "4", "5", "6", "−", "1", "2", "3", "+", "⌫", "0", ".", "="};
        for (String label : labels) {
            Color buttonColor = label.equals("=") ? ACCENT
                    : isOperator(label) ? new Color(51, 61, 91)
                    : isUtility(label) ? UTILITY_BUTTON : NUMBER_BUTTON;
            JButton button = new RoundedButton(label, buttonColor);
            button.setFont(new Font("Segoe UI", label.matches("\\d") ? Font.PLAIN : Font.BOLD,
                    label.matches("\\d") ? 23 : 18));
            button.setForeground(label.equals("=") ? Color.WHITE
                    : isUtility(label) ? new Color(190, 199, 220) : TEXT);
            button.setFocusPainted(false);
            button.setBorderPainted(false);
            button.setContentAreaFilled(false);
            button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            button.setPreferredSize(new Dimension(70, 70));
            button.addActionListener(actionFor(label));
            keys.add(button);
        }
        frame.add(keys, BorderLayout.CENTER);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private ActionListener actionFor(String key) {
        return event -> {
            if (key.matches("\\d")) {
                enterDigit(key);
            } else {
                switch (key) {
                    case "." -> enterDecimal();
                    case "C" -> clear();
                    case "⌫" -> backspace();
                    case "±" -> toggleSign();
                    case "%" -> percent();
                    case "+", "−", "×", "÷" -> chooseOperation(key);
                    case "=" -> calculate();
                    default -> { }
                }
            }
        };
    }

    private boolean isOperator(String key) {
        return key.equals("+") || key.equals("−") || key.equals("×") || key.equals("÷");
    }

    private boolean isUtility(String key) {
        return key.equals("C") || key.equals("±") || key.equals("%") || key.equals("⌫");
    }

    private void enterDigit(String digit) {
        String current = display.getText();
        display.setText(startNewNumber || current.equals("0") ? digit : current + digit);
        startNewNumber = false;
    }

    private void enterDecimal() {
        if (startNewNumber) {
            display.setText("0.");
            startNewNumber = false;
        } else if (!display.getText().contains(".")) {
            display.setText(display.getText() + ".");
        }
    }

    private void chooseOperation(String operation) {
        try {
            storedValue = currentValue();
            pendingOperation = operation;
            startNewNumber = true;
            status.setText("OPERATION  " + operation);
        } catch (NumberFormatException e) {
            showError("Invalid number");
        }
    }

    private void calculate() {
        if (pendingOperation == null) return;
        try {
            double right = currentValue();
            double result = switch (pendingOperation) {
                case "+" -> storedValue + right;
                case "−" -> storedValue - right;
                case "×" -> storedValue * right;
                case "÷" -> divide(storedValue, right);
                default -> right;
            };
            display.setText(format(result));
            pendingOperation = null;
            startNewNumber = true;
            status.setText("RESULT");
        } catch (ArithmeticException | NumberFormatException e) {
            showError(e instanceof ArithmeticException ? e.getMessage() : "Invalid number");
        }
    }

    private double divide(double left, double right) {
        if (right == 0) throw new ArithmeticException("Cannot divide by zero");
        return left / right;
    }

    private double currentValue() {
        return Double.parseDouble(display.getText());
    }

    private String format(double value) {
        if (!Double.isFinite(value)) return "Error";
        if (value == Math.rint(value) && Math.abs(value) < 1e16) return Long.toString((long) value);
        return Double.toString(value);
    }

    private void clear() {
        display.setText("0");
        storedValue = 0;
        pendingOperation = null;
        startNewNumber = true;
        status.setText("READY  •  STANDARD");
    }

    private void backspace() {
        if (startNewNumber) return;
        String value = display.getText();
        display.setText(value.length() <= 1 || (value.length() == 2 && value.startsWith("-"))
                ? "0" : value.substring(0, value.length() - 1));
    }

    private void toggleSign() {
        double value = currentValue();
        display.setText(format(-value));
        startNewNumber = false;
    }

    private void percent() {
        display.setText(format(currentValue() / 100.0));
        startNewNumber = false;
    }

    private void showError(String message) {
        display.setText(message);
        pendingOperation = null;
        startNewNumber = true;
        status.setText("CHECK INPUT");
    }

    private static class RoundedPanel extends JPanel {
        private final Color fill;
        private final int radius;

        RoundedPanel(Color fill, int radius) {
            this.fill = fill;
            this.radius = radius;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(fill);
            g.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            g.dispose();
            super.paintComponent(graphics);
        }
    }

    private static class RoundedButton extends JButton {
        private final Color base;
        private boolean hovered;

        RoundedButton(String text, Color base) {
            super(text);
            this.base = base;
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder());
            addMouseListener(new java.awt.event.MouseAdapter() {
                @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                    hovered = true;
                    repaint();
                }
                @Override public void mouseExited(java.awt.event.MouseEvent e) {
                    hovered = false;
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Color top = hovered ? base.brighter() : base;
            Color bottom = hovered ? base : base.darker();
            g.setPaint(new GradientPaint(0, 0, top, 0, getHeight(), bottom));
            g.fillRoundRect(0, 0, getWidth(), getHeight(), 22, 22);
            g.dispose();
            super.paintComponent(graphics);
        }
    }
}
