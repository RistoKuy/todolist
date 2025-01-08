/*
 * Author: Aristo Baadi
 * GitHub: https://github.com/RistoKuy
 * 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package todolist;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.util.ArrayDeque;
import java.util.Deque;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

/**
 * A simple To-Do List application with GUI.
 * Allows users to add, check/uncheck, delete, and clear to-do items and groups.
 * Maintains a history of the last 5 actions performed.
 * 
 * @version 1.0
 */
public class Todolist {
    private static JFrame frame;
    private static DefaultListModel<String> listModel;
    private static JList<String> todoList;
    private static Deque<String> history;
    private static JComboBox<String> groupComboBox;
    private static DefaultComboBoxModel<String> groupModel;

    /**
     * The main method to initialize and display the To-Do List application.
     * 
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            frame = new JFrame("To-Do List App");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(400, 300);

            listModel = new DefaultListModel<>();
            todoList = new JList<>(listModel);
            history = new ArrayDeque<>();
            groupModel = new DefaultComboBoxModel<>();
            groupComboBox = new JComboBox<>(groupModel);
            groupModel.addElement("Default");

            JPanel panel = new JPanel();
            panel.setLayout(new BorderLayout());
            panel.setBackground(Color.LIGHT_GRAY); // Set background color

            JButton addButton = new JButton("Add To-Do List");
            addButton.setToolTipText("Click to add a new to-do list item"); // Add tooltip
            addButton.addActionListener(e -> {
                String name = JOptionPane.showInputDialog(frame, "Enter To-Do List Name:");
                if (name != null && !name.trim().isEmpty()) {
                    String sanitizedInput = sanitizeInput(name);
                    String group = (String) groupComboBox.getSelectedItem();
                    listModel.addElement(group + ": " + sanitizedInput);
                    addToHistory("Added: " + sanitizedInput + " to group " + group);
                }
            });

            JButton checkButton = new JButton("Check/Uncheck");
            checkButton.setToolTipText("Click to check/uncheck the selected item"); // Add tooltip
            checkButton.addActionListener(e -> {
                int[] selectedIndices = todoList.getSelectedIndices();
                if (selectedIndices.length > 0) {
                    for (int index : selectedIndices) {
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
            topPanel.setBackground(Color.DARK_GRAY); // Set background color
            topPanel.add(addButton, BorderLayout.WEST);
            topPanel.add(groupComboBox, BorderLayout.EAST);

            JButton addGroupButton = new JButton("Add Group");
            addGroupButton.setToolTipText("Click to add a new group"); // Add tooltip
            addGroupButton.addActionListener(e -> {
                String groupName = JOptionPane.showInputDialog(frame, "Enter Group Name:");
                if (groupName != null && !groupName.trim().isEmpty()) {
                    String sanitizedGroupName = sanitizeInput(groupName);
                    groupModel.addElement(sanitizedGroupName);
                }
            });
            topPanel.add(addGroupButton, BorderLayout.CENTER);

            panel.add(topPanel, BorderLayout.NORTH);
            todoList.setFont(new Font("Arial", Font.PLAIN, 14)); // Set font for to-do list items
            panel.add(new JScrollPane(todoList), BorderLayout.CENTER);

            JButton deleteButton = new JButton("Delete Selected");
            deleteButton.setToolTipText("Click to delete the selected item or group"); // Add tooltip
            deleteButton.addActionListener(e -> {
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
            });

            JButton clearButton = new JButton("Clear");
            clearButton.setToolTipText("Click to clear all items and groups"); // Add tooltip
            clearButton.addActionListener(e -> {
                listModel.clear();
                groupModel.removeAllElements();
                groupModel.addElement("Default");
                addToHistory("Cleared all items and groups");
            });

            JButton historyButton = new JButton("View History");
            historyButton.setToolTipText("Click to view the last 5 actions"); // Add tooltip
            historyButton.addActionListener(e -> {
                StringBuilder historyText = new StringBuilder("Last 5 Actions:\n");
                for (String action : history) {
                    historyText.append(action).append("\n");
                }
                JOptionPane.showMessageDialog(frame, historyText.toString(), "History", JOptionPane.INFORMATION_MESSAGE);
            });

            JPanel bottomPanel = new JPanel();
            bottomPanel.setLayout(new BorderLayout());
            bottomPanel.setBackground(Color.DARK_GRAY); // Set background color
            bottomPanel.add(checkButton, BorderLayout.WEST);
            bottomPanel.add(clearButton, BorderLayout.CENTER); // Add clear button in the middle
            bottomPanel.add(deleteButton, BorderLayout.EAST);
            bottomPanel.add(historyButton, BorderLayout.NORTH); // Add history button at the top of the bottom panel

            panel.add(bottomPanel, BorderLayout.SOUTH);

            frame.add(panel);
            frame.setVisible(true);
        });
    }

    /**
     * Adds an action to the history stack.
     * Maintains a maximum of 5 actions in the history.
     * 
     * @param action the action to be added to the history
     */
    private static void addToHistory(String action) {
        if (history.size() == 5) {
            history.removeFirst();
        }
        history.addLast(action);
    }

    private static String sanitizeInput(String input) {
        return input.replaceAll("[^a-zA-Z0-9\\s]", "").trim();
    }
}
