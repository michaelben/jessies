package e.ptextarea;

import java.awt.*;
import java.io.*;
import javax.swing.*;

public class PTextWindow {
    private PTextWindow() { }
    
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Syntax: PTextWindow <filename>");
            System.exit(1);
        }
        final File file = new File(args[0]);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                PTextArea area = new PTextArea();
                area.setText(new String(getFileText(file)));
                PTextStyler styler = getTextStyler(file, area);
                if (styler != null) {
                    area.setTextStyler(styler);
                }
                JFrame frame = new JFrame("PTextArea Test: " + file.getPath());
                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                JScrollPane scroller = new JScrollPane(area);
                frame.getContentPane().add(scroller);
                frame.setSize(new Dimension(600, 600));
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }
    
    private static PTextStyler getTextStyler(File file, PTextArea textArea) {
        String name = file.getName();
        if (name.indexOf('.') != -1) {
            String extension = name.substring(name.lastIndexOf('.') + 1);
            if (extension.equals("cpp") || extension.equals("h")) {
                return new PCPPTextStyler(textArea);
            } else if (extension.equals("c")) {
                return new PCTextStyler(textArea);
            } else if (extension.equals("java")) {
                return new PJavaTextStyler(textArea);
            }
        }
        return null;
    }
    
    private static char[] getFileText(File file) {
        try {
            CharArrayWriter chars = new CharArrayWriter();
            PrintWriter writer = new PrintWriter(chars);
            BufferedReader in = new BufferedReader(new FileReader(file));
            String line;
            while ((line = in.readLine()) != null) {
                writer.println(line);
            }
            in.close();
            writer.close();
            return chars.toCharArray();
        } catch (IOException ex) {
            ex.printStackTrace();
            return new char[0];
        }
    }
}
