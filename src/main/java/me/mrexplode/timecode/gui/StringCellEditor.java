package me.mrexplode.timecode.gui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;


public class StringCellEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {

    private static final long serialVersionUID = 2237451524496529015L;
    private JTextField textField;
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
    public void actionPerformed(ActionEvent e) {
        this.table.setValueAt(textField.getText(), row, column);
        stopCellEditing();
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        this.row = row;
        this.column = column;
        this.table = table;
        return textField;
    }

}
