/* ===================================================
 * JFreeSVG : an SVG library for the Java(tm) platform
 * ===================================================
 * 
 * (C)opyright 2013-2015, by Object Refinery Limited.  All rights reserved.
 *
 * Project Info:  http://www.jfree.org/jfreesvg/index.html
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * [Oracle and Java are registered trademarks of Oracle and/or its affiliates. 
 * Other names may be trademarks of their respective owners.]
 * 
 * If you do not wish to be bound by the terms of the AGPL, an alternative
 * commercial license can be purchased.  For details, please see visit the
 * JFreeSVG home page:
 * 
 * http://www.jfree.org/jfreesvg
 */

package org.jfree.graphics2d.demo;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import org.jfree.graphics2d.svg.SVGGraphics2D;
import org.jfree.graphics2d.svg.SVGUtils;

/**
 * This demo shows how to export a Swing UI to SVG.
 */
public class SwingUIToSVGDemo extends JFrame implements ActionListener {
    
    public SwingUIToSVGDemo(String title) {
        super(title);
        add(createContent());
    }
    
    private JComponent createContent() {
        JPanel content = new JPanel(new BorderLayout());
        JTabbedPane tabs = new JTabbedPane();
        tabs.add("Tab 1", new JButton("First Tab"));
        tabs.add("Tab 2", new JButton("Second Tab"));
        JButton button = new JButton("Save to SVG");
        button.addActionListener(this);
        content.add(tabs);
        content.add(button, BorderLayout.SOUTH);
        return content;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        JComponent c = (JComponent) getContentPane().getComponent(0);
        SVGGraphics2D g2 = new SVGGraphics2D(c.getWidth(), c.getHeight());
        c.paint(g2);
        File f = new File("SwingUIToSVGDemo.svg");
        try {
            SVGUtils.writeToSVG(f, g2.getSVGElement());
        } catch (IOException ex) {
            System.err.println(ex);
        }
    }

    public static void main(String[] args) {
        try {
            for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // just take the default look and feel
        }

        SwingUIToSVGDemo app = new SwingUIToSVGDemo("SwingUIToSVGDemo.java");
        app.pack();
        app.setVisible(true);
        
    }

}
