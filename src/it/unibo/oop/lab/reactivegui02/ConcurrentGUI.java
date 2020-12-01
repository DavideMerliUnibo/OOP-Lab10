package it.unibo.oop.lab.reactivegui02;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ConcurrentGUI extends JFrame{

    private static final long serialVersionUID = 1L;
    private static final int PROPORTION = 4;
    
    final JLabel count = new JLabel("0");
    final JButton up = new JButton("Up");
    final JButton down = new JButton("Down");
    final JButton stop = new JButton("Stop");
    
    public ConcurrentGUI() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Dimension screenSize = new Dimension();
        screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize((int) screenSize.getWidth() / PROPORTION, (int) screenSize.getHeight() / PROPORTION);
        final JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());
        up.setEnabled(false);
        
        final Agent agent = new Agent();
        new Thread(agent).start();
        /*
         * Adding action listeners for the three buttons
         */
        up.addActionListener(new ActionListener() {

            public void actionPerformed(final ActionEvent e) {
                if(agent.isStopped()) {
                    new Thread(agent).start();
                }
                agent.resumeCounting();
                if(!agent.isGoingUp()) {
                    agent.changeDirection();
                }
                up.setEnabled(false);
                down.setEnabled(true);
            }
            
        });
        
        down.addActionListener(new ActionListener() {

            public void actionPerformed(final ActionEvent e) {
                if(agent.isStopped()) {
                    new Thread(agent).start();
                }
                agent.resumeCounting();
                if(agent.isGoingUp()) {
                    agent.changeDirection();
                }
                down.setEnabled(false);
                up.setEnabled(true);
            }
            
        });
        
        stop.addActionListener(new ActionListener() {

            public void actionPerformed(final ActionEvent e) {
                agent.stopCounting();
                up.setEnabled(true);
                down.setEnabled(true);
            }
            
        });
        
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

    /*
     * Nested class Agent
     */
    private class Agent implements Runnable {
        private volatile boolean stop;
        private volatile boolean goingUp;
        private volatile int counter;
        
        public Agent() {
            this.stop = false;
            this.goingUp = true;
            this.counter = 0;
        }
        
        public void run() {
            while(!this.stop) {
                try {
                    count.setText(Integer.toString(this.counter));
                    if(this.goingUp) {
                        this.counter++;
                    }
                    else {
                        this.counter--;
                    }
                    Thread.sleep(100);
                } catch(InterruptedException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        }
        
        public void stopCounting() {
            this.stop = true;
        }
        
        public void resumeCounting() {
            this.stop = false;
        }
        
        public void changeDirection() {
            this.goingUp = !this.goingUp;
        }
        
        public boolean isStopped() {
            return this.stop;
        }
        
        public boolean isGoingUp() {
            return this.goingUp;
        }
    }
}
