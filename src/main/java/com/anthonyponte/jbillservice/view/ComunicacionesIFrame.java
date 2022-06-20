/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package com.anthonyponte.jbillservice.view;

import java.awt.Color;
import java.awt.Dimension;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.GroupLayout;
import javax.swing.JInternalFrame;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import org.jdesktop.swingx.JXDatePicker;
import org.kordamp.ikonli.remixicon.RemixiconAL;
import org.kordamp.ikonli.swing.FontIcon;

/**
 *
 * @author AnthonyPonte
 */
public class ComunicacionesIFrame extends JInternalFrame {

    /**
     * Creates new form TablaIFrame
     */
    public ComunicacionesIFrame() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        dpMesAno = new JXDatePicker();
        separator = new JSeparator();
        tfFiltrar = new JTextField();
        spltPane = new JSplitPane();
        scrllEncabezado = new JScrollPane();
        tblEncabezado = new JTable();
        scrllDetalle = new JScrollPane();
        tblDetalle = new JTable();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Comunicaciones");
        setFrameIcon(FontIcon.of(RemixiconAL.CALENDAR_2_LINE, 16, Color.decode("#FACADE")));
        setMaximumSize(null);
        setMinimumSize(new Dimension(800, 600));
        setPreferredSize(new Dimension(800, 600));

        dpMesAno.setDate(new Date());
        dpMesAno.setFormats(new SimpleDateFormat("MMMM y"));
        dpMesAno.setMaximumSize(null);
        dpMesAno.setMinimumSize(null);
        dpMesAno.setPreferredSize(new Dimension(125, 30));
        dpMesAno.getEditor().setEditable(false);

        tfFiltrar.setMinimumSize(new Dimension(300, 30));
        tfFiltrar.setPreferredSize(new Dimension(300, 30));
        tfFiltrar.putClientProperty("JTextField.leadingIcon", FontIcon.of(RemixiconAL.FILTER_LINE, 16, Color.decode("#FFFFFF")));
        tfFiltrar.putClientProperty("JTextField.placeholderText", "Filtrar");
        tfFiltrar.putClientProperty("JTextField.showClearButton", true);

        spltPane.setDividerLocation(300);
        spltPane.setDividerSize(6);
        spltPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
        spltPane.setResizeWeight(1.0);

        tblEncabezado.setModel(new DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tblEncabezado.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tblEncabezado.setColumnSelectionAllowed(true);
        tblEncabezado.getTableHeader().setReorderingAllowed(false);
        scrllEncabezado.setViewportView(tblEncabezado);
        tblEncabezado.getColumnModel().getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        spltPane.setLeftComponent(scrllEncabezado);

        tblDetalle.setModel(new DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Tipo Codigo", "Tipo Descripcion", "Serie", "Correlativo", "Motivo"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblDetalle.getTableHeader().setReorderingAllowed(false);
        scrllDetalle.setViewportView(tblDetalle);
        if (tblDetalle.getColumnModel().getColumnCount() > 0) {
            tblDetalle.getColumnModel().getColumn(0).setResizable(false);
            tblDetalle.getColumnModel().getColumn(1).setResizable(false);
            tblDetalle.getColumnModel().getColumn(2).setResizable(false);
            tblDetalle.getColumnModel().getColumn(3).setResizable(false);
            tblDetalle.getColumnModel().getColumn(4).setResizable(false);
        }

        spltPane.setRightComponent(scrllDetalle);

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(tfFiltrar, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(spltPane, GroupLayout.DEFAULT_SIZE, 776, Short.MAX_VALUE)
                    .addComponent(separator)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(dpMesAno, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(dpMesAno, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(separator, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tfFiltrar, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(spltPane, GroupLayout.DEFAULT_SIZE, 471, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public JXDatePicker dpMesAno;
    public JScrollPane scrllDetalle;
    public JScrollPane scrllEncabezado;
    public JSeparator separator;
    public JSplitPane spltPane;
    public JTable tblDetalle;
    public JTable tblEncabezado;
    public JTextField tfFiltrar;
    // End of variables declaration//GEN-END:variables
}