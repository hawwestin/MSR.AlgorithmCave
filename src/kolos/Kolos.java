/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kolos;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author michal
 */
public class Kolos extends JFrame {
    
    private static JPanel mainPanel;
    private static Wegier wegier;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        new Kolos();
    }
    
    public Kolos() {
        super();
        setTitle("Algorytm wegierski");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        wegier = new Wegier();
        setContentPane(mainPanel);
        mainPanel.add(wegier);
        mainPanel.revalidate();
        mainPanel.repaint();
        
        pack();
        setVisible(true);
        setSize(new Dimension(600, 600));
        
    }
    
}
