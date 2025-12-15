package main;

import javax.swing.*;
import java.awt.*;

public class StatusBoard extends JPanel {

    private final DefaultListModel<String> model = new DefaultListModel<>();
    private final JList<String> list = new JList<>(model);

    public StatusBoard() {
        setLayout(new BorderLayout());
        setBackground(Color.DARK_GRAY);

        JLabel title = new JLabel("Status Board");
        title.setForeground(Color.WHITE);
        title.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
        add(title, BorderLayout.NORTH);

        list.setBackground(new Color(30, 30, 30));
        list.setForeground(Color.WHITE);
        list.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scroll = new JScrollPane(list);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        add(scroll, BorderLayout.CENTER);

        // Optional sizing
        setPreferredSize(new Dimension(320, 0));
    }

    public void log(String msg) {
        model.addElement(msg);
        int last = model.size() - 1;
        if (last >= 0) list.ensureIndexIsVisible(last);
    }

    public void clear() {
        model.clear();
    }
}
