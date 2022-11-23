package it.unibo.oop.reactivegui03;

import java.awt.Dimension;
import java.awt.Toolkit;

import java.lang.reflect.InvocationTargetException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * Third experiment with reactive gui.
 */

public final class AnotherConcurrentGUI extends JFrame {
    private static final long serialVersionUID = 1L;
    private final JLabel display = new JLabel();
    private final JButton upButton = new JButton("Up");
    private final JButton downButton = new JButton("Down");
    private final JButton stopButton = new JButton("Stop");
    private final int wi = 5;
    private final long time = 10000;

    public AnotherConcurrentGUI() {
        super();
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize((int) (screenSize.getWidth() / wi), (int) (screenSize.getHeight() / wi));
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        final JPanel panel = new JPanel();
        panel.add(upButton);
        panel.add(display);
        panel.add(stopButton);
        panel.add(downButton);
        this.getContentPane().add(panel);
        this.setVisible(true);

        final Agent agent = new Agent();
        new Thread(agent).start();
        stopButton.addActionListener((e) -> agent.stopCounting());
        upButton.addActionListener((e) -> agent.upCounting());
        downButton.addActionListener((e) -> agent.downCounting());
        new Thread(() -> {
            try {
                Thread.sleep(time);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            agent.stopCounting();
            upButton.setEnabled(false);
            downButton.setEnabled(false);
            stopButton.setEnabled(false);
        }).start();
    }

    private class Agent implements Runnable {
        private volatile boolean stop;
        private volatile boolean up = true;
        private int counter;

        @Override
        public void run() {

            while (!this.stop) {
                try {
                    final var nextText = Integer.toString(this.counter);
                    SwingUtilities.invokeAndWait(() -> AnotherConcurrentGUI.this.display.setText(nextText));
                    if (up) {
                        this.counter++;
                    } else {
                        this.counter--;
                    }
                    Thread.sleep(100);

                } catch (InvocationTargetException | InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }

        public void stopCounting() {
            this.stop = true;
            upButton.setEnabled(false);
            downButton.setEnabled(false);
        }

        public void upCounting() {
            this.up = true;
        }

        public void downCounting() {
            this.up = false;
        }
    }
}
