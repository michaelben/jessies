package e.edit;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import e.gui.*;
import e.util.*;

/**
 * A simple title-bar with a label and a right-aligned close button.
 */
public class ETitleBar extends JPanel {
    /**
     * Ensures that all title bars correctly reflect the focus ownership of
     * their associated windows.
     */
    static {
        new KeyboardFocusMonitor() {
            public void focusChanged(Component oldOwner, Component newOwner) {
                setActive(oldOwner, false);
                setActive(newOwner, true);
            }
            
            public void setActive(Component c, boolean active) {
                EWindow window = (EWindow) SwingUtilities.getAncestorOfClass(EWindow.class, c);
                if (window == null) {
                    return;
                }
                window.getTitleBar().setActive(active);
                // Make sure the JScrollPane focus ring is redrawn on Mac OS.
                window.repaint();
            }
        };
    }
    
    private EWindow window;
    
    public static final int TITLE_HEIGHT = 20;
    
    private JLabel titleLabel;
    private ECloseButton closeButton;
    private ESwitchButton switchButton;
    
    private JPanel buttonsPanel;
    
    private boolean isActive;
    
    public ETitleBar(String name, EWindow window) {
        this.window = window;
        
        setLayout(new BorderLayout());
        
        titleLabel = new JLabel(" ");
        titleLabel.setFont(UIManager.getFont("TableHeader.font"));
        titleLabel.setOpaque(false);
        
        add(titleLabel, BorderLayout.CENTER);
        
        buttonsPanel = new JPanel(new BorderLayout());
        buttonsPanel.setOpaque(false);
        add(buttonsPanel, BorderLayout.EAST);
        
        this.closeButton = new ECloseButton(window);
        buttonsPanel.add(closeButton, BorderLayout.EAST);
        
        setActive(false);
        setTitle(name);
        initListener();
    }
    
    private void initListener() {
        addMouseListener(new MouseAdapter() {
            /**
             * Gives a window focus when its title-bar is selected.
             */
            public void mousePressed(MouseEvent e) {
                window.requestFocus();
            }
            
            /**
             * Expands this window to maximum size on a double-click.
             */
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    window.expand();
                }
            }
        });
    }
    
    public void setShowSwitchButton(boolean shouldShow) {
        if (switchButton == null && shouldShow) {
            this.switchButton = new ESwitchButton((ETextWindow) window);
            buttonsPanel.add(switchButton, BorderLayout.WEST);
            forceRepaint();
        } else if (switchButton != null && shouldShow == false) {
            buttonsPanel.remove(switchButton);
            this.switchButton = null;
            forceRepaint();
        }
    }
    
    private void forceRepaint() {
        invalidate();
        validate();
        repaint();
    }
    
    public void setTitle(String title) {
        titleLabel.setText(title);
    }
    
    public String getTitle() {
        return titleLabel.getText();
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public void setActive(boolean isActive) {
        this.isActive = isActive;
        updateColors();
    }
    
    private void updateColors() {
        /*
         * Use the JInternalFrame colors to distinguish focused from unfocused
         * windows.
         * 
         * On Mac OS, although you might expect that this gives exactly the
         * result you see with JInternalFrame, it doesn't. As of 1.4.2, Apple
         * invoke apple.laf.AquaImageFactory's drawFrameTitleBackground method
         * rather than using these colors. We could use reflection to do the
         * same, but that seems unnecessarily fragile.
         */
        if (isActive) {
            setBackground(UIManager.getColor("InternalFrame.activeTitleBackground"));
            titleLabel.setForeground(UIManager.getColor("InternalFrame.activeTitleForeground"));
        } else {
            setBackground(UIManager.getColor("InternalFrame.inactiveTitleBackground"));
            titleLabel.setForeground(UIManager.getColor("InternalFrame.inactiveTitleForeground"));
        }
    }
}
