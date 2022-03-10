/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.anthonyponte.jbillservice.view;

import java.awt.Color;
import java.awt.Dimension;
import javax.swing.GroupLayout;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.WindowConstants;

/**
 *
 * @author anthony
 */
public class MainFrame extends JFrame {

    /**
     * Creates new form MainFrame
     */
    public MainFrame() {
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

        dpane = new JDesktopPane();
        mbar = new JMenuBar();
        menuMain = new JMenu();
        menuEntrar = new JMenuItem();
        menuNuevo = new JMenu();
        miComunicacionBaja = new JMenuItem();
        menuVer = new JMenu();
        miComunicacionesBaja = new JMenuItem();
        menuBillService = new JMenu();
        miSummary = new JMenuItem();
        menuSalir = new JMenuItem();

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("JBillService");
        setMinimumSize(new Dimension(1024, 768));

        dpane.setBackground(new Color(250, 202, 222));

        GroupLayout dpaneLayout = new GroupLayout(dpane);
        dpane.setLayout(dpaneLayout);
        dpaneLayout.setHorizontalGroup(dpaneLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGap(0, 1024, Short.MAX_VALUE)
        );
        dpaneLayout.setVerticalGroup(dpaneLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGap(0, 745, Short.MAX_VALUE)
        );

        menuMain.setText("Menu");

        menuEntrar.setText("Entrar");
        menuMain.add(menuEntrar);

        menuNuevo.setText("Nuevo");
        menuNuevo.setEnabled(false);

        miComunicacionBaja.setText("Comunicacion de baja");
        miComunicacionBaja.setEnabled(false);
        menuNuevo.add(miComunicacionBaja);

        menuMain.add(menuNuevo);

        menuVer.setText("Ver");
        menuVer.setEnabled(false);

        miComunicacionesBaja.setText("Comunicaciones de baja");
        miComunicacionesBaja.setEnabled(false);
        menuVer.add(miComunicacionesBaja);

        menuMain.add(menuVer);

        menuBillService.setText("BillService");
        menuBillService.setEnabled(false);

        miSummary.setText("Summary");
        miSummary.setEnabled(false);
        menuBillService.add(miSummary);

        menuMain.add(menuBillService);

        menuSalir.setText("Salir");
        menuMain.add(menuSalir);

        mbar.add(menuMain);

        setJMenuBar(mbar);

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(dpane)
                .addGap(0, 0, 0))
        );
        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(dpane)
                .addGap(0, 0, 0))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public JDesktopPane dpane;
    public JMenuBar mbar;
    public JMenu menuBillService;
    public JMenuItem menuEntrar;
    public JMenu menuMain;
    public JMenu menuNuevo;
    public JMenuItem menuSalir;
    public JMenu menuVer;
    public JMenuItem miComunicacionBaja;
    public JMenuItem miComunicacionesBaja;
    public JMenuItem miSummary;
    // End of variables declaration//GEN-END:variables
}
