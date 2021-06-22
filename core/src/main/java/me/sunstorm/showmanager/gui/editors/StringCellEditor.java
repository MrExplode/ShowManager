package me.sunstorm.showmanager.gui.editors;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;


public class StringCellEditor extends AbstractCellEditor implements TableCellEditor, ActionListener, FocusListener {
    private final JTextField textField;
    private int row = 0;
    private int column = 0;
    private JTable table;
    
    public StringCellEditor(JTextField field, JTable table) {
        this.table = table;
        this.textField = field;
        this.textField.addActionListener(this);
    }

    @Override
    public Object getCellEditorValue() {
        return textField.getText();
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        this.row = row;
        this.column = column;
        this.table = table;
        return textField;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        this.table.setValueAt(textField.getText(), row, column);
        stopCellEditing();
    }

    @Override
    public void focusGained(FocusEvent e) {
        //unused event
    }

    @Override
    public void focusLost(FocusEvent e) {
        this.table.setValueAt(textField.getText(), row, column);
        stopCellEditing();
    }

}
