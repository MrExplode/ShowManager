package me.mrexplode.timecode.gui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;


public class SelectionCellEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {

    private static final long serialVersionUID = -4589883770575558611L;
    
    private JComboBox<?> comboBox;
    private JTable table;
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
        System.out.println(e.getActionCommand());
        if (e.getActionCommand().equals("comboBoxChanged")) {
            table.setValueAt(comboBox.getSelectedItem(), row, column);
            fireEditingStopped();
        }
    }

}
