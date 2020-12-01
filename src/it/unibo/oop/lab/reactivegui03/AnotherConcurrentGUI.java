package it.unibo.oop.lab.reactivegui03;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.JButton;
import javax.swing.JLabel;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.lang.reflect.InvocationTargetException;

public class AnotherConcurrentGUI extends JFrame {
    
    private static final long serialVersionUID = -8710276539980695794L;
    private static final int PROPORTION = 4;
    private static final int SECONDS_TO_MILLIS = 1000;
    private static final long TIME_LIMIT = 10;
    
    private final JPanel panel = new JPanel();
    private final JLabel count = new JLabel();
    private final JButton up = new JButton("Up");
    private final JButton down = new JButton("Down");
    private final JButton stop = new JButton("Stop");
    private final Agent agent = new Agent();

    public AnotherConcurrentGUI() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize((int) screenSize.getWidth() / PROPORTION, (int) screenSize.getHeight() / PROPORTION);
        this.panel.setLayout(new FlowLayout());
        this.up.setEnabled(false);
        
        new Thread(agent).start();
        /*
         * Adding action listeners
         */
        up.addActionListener(e -> {
            agent.changeDirection();
            up.setEnabled(false);
            down.setEnabled(true);
        });
        down.addActionListener(e -> {
            agent.changeDirection();
            down.setEnabled(false);
            up.setEnabled(true);
        });
        stop.addActionListener(e -> {
            agent.stopCounting();
            up.setEnabled(false);
            down.setEnabled(false);
            stop.setEnabled(false);
        });
        
        /*
         * Creating timer
         */
        final Timer timer = new Timer();
        new Thread(timer).start();
        
        /*
         * Adding components to the frame
         */
        panel.add(count);
        panel.add(up);
        panel.add(down);
        panel.add(stop);
        this.getContentPane().add(panel);
        this.setVisible(true);
        
    }
    
    public class Agent implements Runnable {
        
        private volatile boolean stop;
        private volatile boolean goingUp = true;
        private volatile int counter;
        
        public void run() {
            while(!this.stop) {
                try {
                    SwingUtilities.invokeAndWait(() -> count.setText(Integer.toString(this.counter)));
                    this.counter = this.goingUp ? this.counter + 1 : this.counter - 1;
                    Thread.sleep(100);
                } catch(InvocationTargetException | InterruptedException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        }
        
        public void stopCounting() {
            this.stop = true;
        }
        
        public void changeDirection() {
            this.goingUp = !this.goingUp;
        }
        
    }
    
    public class Timer implements Runnable {
        
        private volatile int secondsPassed;
        
        public void run() {
            while(!timeLimitReached()) {
                try {
                    Thread.sleep(SECONDS_TO_MILLIS);
                    this.secondsPassed++;
                } catch(InterruptedException ex) {
                    System.out.println(ex.getMessage());
                }
            }
            agent.stopCounting();
            up.setEnabled(false);
            down.setEnabled(false);
            stop.setEnabled(false);
        }
        
        public boolean timeLimitReached() {
            return this.secondsPassed >= TIME_LIMIT;
        }
    }
}
