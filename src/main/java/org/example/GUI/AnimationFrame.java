package org.example.GUI;

import javax.swing.*;

public class AnimationFrame extends JFrame {
    private AnimationPanel animationPanel;

    public AnimationFrame(int numberOfServers) {
        setTitle("Queue Simulation Animation");
        setSize(1400, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        animationPanel = new AnimationPanel(numberOfServers);
        add(animationPanel);
        setVisible(true);

    }

    public AnimationPanel getAnimationPanel() {
        return animationPanel;
    }
}
