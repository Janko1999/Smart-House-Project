/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package alarmjanko;


import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 *
 * @author Ideapad S340
 */
public class Error extends JFrame{
    JLabel text;
    JButton okButton;

    public Error(String text) {
        this.text = new JLabel(text,SwingConstants.CENTER);
        this.okButton = new JButton("OK");
        this.setSize(400, 200);
        this.init();
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
        this.setVisible(true);
    }

    private void init() {
        this.text.setFont(new Font("Times New Roman", Font.BOLD, 18));
        this.okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Error.this.dispose();
             }
        });
        //this.okButton.setSize(20, 20);
        JPanel pom = new JPanel();
        pom.add(this.okButton);
        JPanel p = new JPanel(new GridLayout(2,1));
        p.add(this.text);
        p.add(pom);
        this.add(p);
        
     }
    
    
}

    

