/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package todolist;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Stack;

/**
 *
 * @author Aristo Baadi
 */
public class Todolist {
    private static JFrame frame;
    private static DefaultListModel<String> listModel;
    private static JList<String> todoList;
    private static Stack<String> history;
    private static JComboBox<String> groupComboBox;
    private static DefaultComboBoxModel<String> groupModel;

    public static void main(String[] args) {
        frame = new JFrame("To-Do List App");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);

        listModel = new DefaultListModel<>();
        todoList = new JList<>(listModel);
        history = new Stack<>();
        groupModel = new DefaultComboBoxModel<>();
        groupComboBox = new JComboBox<>(groupModel);
        groupModel.addElement("Default");

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JButton addButton = new JButton("Add To-Do List");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = JOptionPane.showInputDialog(frame, "Enter To-Do List Name:");
                if (name != null && !name.trim().isEmpty()) {
                    String group = (String) groupComboBox.getSelectedItem();
                    listModel.addElement(group + ": " + name);
                    addToHistory("Added: " + name + " to group " + group);
                }
            }
        });

        JButton checkButton = new JButton("Check/Uncheck");
        checkButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int index = todoList.getSelectedIndex();
                if (index != -1) {
                    String item = listModel.getElementAt(index);
                    if (item.startsWith("[Done] ")) {
                        item = item.substring(7);
                        addToHistory("Unchecked: " + item);
                    } else {
                        item = "[Done] " + item;
                        addToHistory("Checked: " + item);
                    }
                    listModel.set(index, item);
                }
            }
        });

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());
        topPanel.add(addButton, BorderLayout.WEST);
        topPanel.add(groupComboBox, BorderLayout.EAST);

        JButton addGroupButton = new JButton("Add Group");
        addGroupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String groupName = JOptionPane.showInputDialog(frame, "Enter Group Name:");
                if (groupName != null && !groupName.trim().isEmpty()) {
                    groupModel.addElement(groupName);
                }
            }
        });
        topPanel.add(addGroupButton, BorderLayout.CENTER);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(todoList), BorderLayout.CENTER);

        JButton deleteButton = new JButton("Delete Selected");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int[] selectedIndices = todoList.getSelectedIndices();
                if (selectedIndices.length > 0) {
                    for (int i = selectedIndices.length - 1; i >= 0; i--) {
                        String item = listModel.getElementAt(selectedIndices[i]);
                        addToHistory("Deleted: " + item);
                        listModel.remove(selectedIndices[i]);
                    }
                } else {
                    String selectedGroup = (String) groupComboBox.getSelectedItem();
                    if (!"Default".equals(selectedGroup)) {
                        groupModel.removeElement(selectedGroup);
                        for (int i = listModel.size() - 1; i >= 0; i--) {
                            if (listModel.getElementAt(i).startsWith(selectedGroup + ": ")) {
                                listModel.remove(i);
                            }
                        }
                        addToHistory("Deleted group: " + selectedGroup);
                    }
                }
            }
        });

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BorderLayout());
        bottomPanel.add(checkButton, BorderLayout.WEST);
        bottomPanel.add(deleteButton, BorderLayout.EAST);

        panel.add(bottomPanel, BorderLayout.SOUTH);

        frame.add(panel);
        frame.setVisible(true);
    }

    private static void addToHistory(String action) {
        if (history.size() == 5) {
            history.remove(0);
        }
        history.push(action);
    }
}
