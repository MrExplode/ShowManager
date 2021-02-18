package me.mrexplode.showmanager.gui.editors;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.AbstractCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;


public class SelectionCellEditor extends AbstractCellEditor implements TableCellEditor, ActionListener, FocusListener {
    private final JComboBox<?> comboBox;
    private final JTable table;
    private int row = 0;
    private int column = 0;
    
    public SelectionCellEditor(JComboBox<?> box, JTable table) {
        this.comboBox = box;
        this.table = table;
        this.comboBox.addActionListener(this);
    }

    @Override
    public Object getCellEditorValue() {
        return comboBox.getSelectedItem();
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        this.row = row;
        this.column = column;
        return comboBox;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("comboBoxChanged")) {
            table.setValueAt(comboBox.getSelectedItem(), row, column);
            stopCellEditing();
        }
    }

    @Override
    public void focusGained(FocusEvent e) {
        //unused event
    }

    @Override
    public void focusLost(FocusEvent e) {
        table.setValueAt(comboBox.getSelectedItem(), row, column);
        stopCellEditing();
    }

}
