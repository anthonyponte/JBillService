/*
 * Copyright (C) 2022 anthony
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.anthonyponte.jbillservice.controller;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.gui.TableFormat;
import ca.odell.glazedlists.swing.AdvancedListSelectionModel;
import ca.odell.glazedlists.swing.AdvancedTableModel;
import ca.odell.glazedlists.swing.DefaultEventSelectionModel;
import static ca.odell.glazedlists.swing.GlazedListsSwing.eventTableModelWithThreadProxyList;
import com.anthonyponte.jbillservice.custom.MyDateFormat;
import com.anthonyponte.jbillservice.custom.MyFileCreator;
import com.anthonyponte.jbillservice.custom.MyTableResize;
import com.anthonyponte.jbillservice.dao.ResumenDiarioDao;
import com.anthonyponte.jbillservice.dao.SummaryDao;
import com.anthonyponte.jbillservice.idao.IResumenDiarioDao;
import com.anthonyponte.jbillservice.idao.ISummaryDao;
import com.anthonyponte.jbillservice.maindoc.SummaryDocuments;
import com.anthonyponte.jbillservice.model.Documento;
import com.anthonyponte.jbillservice.model.DocumentoIdentidad;
import com.anthonyponte.jbillservice.model.Empresa;
import com.anthonyponte.jbillservice.model.Estado;
import com.anthonyponte.jbillservice.model.Impuesto;
import com.anthonyponte.jbillservice.model.Moneda;
import com.anthonyponte.jbillservice.model.Operacion;
import com.anthonyponte.jbillservice.model.OtrosCargos;
import com.anthonyponte.jbillservice.model.Percepcion;
import com.anthonyponte.jbillservice.model.RegimenPercepcion;
import com.anthonyponte.jbillservice.model.ResumenDiario;
import com.anthonyponte.jbillservice.model.ResumenDiarioDetalle;
import com.anthonyponte.jbillservice.model.TipoDocumento;
import com.anthonyponte.jbillservice.view.LoadingDialog;
import com.anthonyponte.jbillservice.view.ResumenDiarioIFrame;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.sql.SQLException;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.jdom2.Document;
import org.xml.sax.SAXException;

/** @author anthony */
public class ResumenDiarioController {
  private final ResumenDiarioIFrame iFrame;
  private final LoadingDialog dialog;
  private Preferences preferences;
  private SummaryDao summaryDao;
  private ResumenDiarioDao resumenDiarioDao;
  private EventList<ResumenDiarioDetalle> eventList;
  private AdvancedTableModel<ResumenDiarioDetalle> tableModel;
  private AdvancedListSelectionModel<ResumenDiarioDetalle> selectionModel;

  public ResumenDiarioController(ResumenDiarioIFrame iFrame, LoadingDialog dialog) {
    this.iFrame = iFrame;
    this.dialog = dialog;
    initComponents();
  }

  void init() {
    iFrame.btnNuevo.addActionListener(
        (ActionEvent arg0) -> {
          iFrame.cbxTipo.setSelectedIndex(0);
          iFrame.dpFechaGeneracion.setDate(new Date());

          dialog.setVisible(true);
          dialog.setLocationRelativeTo(iFrame);

          SwingWorker worker =
              new SwingWorker<Integer, Void>() {
                @Override
                protected Integer doInBackground() throws Exception {
                  int count = 0;
                  try {
                    TipoDocumento tipoDocumento = (TipoDocumento) iFrame.cbxTipo.getSelectedItem();
                    Date fechaGeneracion = iFrame.dpFechaGeneracion.getDate();

                    count = summaryDao.count(tipoDocumento, fechaGeneracion);
                  } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(
                        null,
                        ex.getMessage(),
                        ResumenDiarioController.class.getName(),
                        JOptionPane.ERROR_MESSAGE);
                  }
                  return count;
                }

                @Override
                protected void done() {
                  try {
                    dialog.dispose();

                    iFrame.tabbed.setSelectedIndex(0);

                    Date fechaGeneracion = iFrame.dpFechaGeneracion.getDate();
                    iFrame.tfSerie.setText(MyDateFormat.yyyyMMdd(fechaGeneracion));

                    int count = get();
                    iFrame.tfCorrelativo.setText(String.valueOf(count));

                    iFrame.dpFechaEmision.setEnabled(true);
                    iFrame.dpFechaEmision.setDate(new Date());
                    iFrame.dpFechaEmision.requestFocus();

                    iFrame.cbxEstado.setEnabled(true);
                    iFrame.cbxEstado.setSelectedIndex(0);

                    iFrame.cbxMoneda.setEnabled(true);
                    iFrame.cbxMoneda.setSelectedIndex(0);

                    iFrame.cbxDocumentoTipo.setEnabled(true);
                    iFrame.cbxDocumentoTipo.setSelectedIndex(0);

                    iFrame.tfDocumentoSerie.setEnabled(true);

                    iFrame.tfDocumentoCorrelativo.setEnabled(true);

                    iFrame.cbxDocumentoIdentidadTipo.setEnabled(true);
                    iFrame.cbxDocumentoIdentidadTipo.setSelectedIndex(0);

                    iFrame.tfDocumentoIdentidadNumero.setEnabled(true);

                    iFrame.tfImporteTotal.setEnabled(true);
                    iFrame.tfImporteTotal.setText("0.00");

                    iFrame.tfGravadas.setEnabled(true);
                    iFrame.tfGravadas.setText("0.00");

                    iFrame.tfExoneradas.setEnabled(true);
                    iFrame.tfExoneradas.setText("0.00");

                    iFrame.tfInafectas.setEnabled(true);
                    iFrame.tfInafectas.setText("0.00");

                    iFrame.tfGratuitas.setEnabled(true);
                    iFrame.tfGratuitas.setText("0.00");

                    iFrame.tfExportacion.setEnabled(true);
                    iFrame.tfExportacion.setText("0.00");

                    iFrame.tfOtrosCargos.setEnabled(true);
                    iFrame.tfOtrosCargos.setText("0.00");

                    iFrame.tfIsc.setEnabled(true);
                    iFrame.tfIsc.setText("0.00");

                    iFrame.tfIgv.setEnabled(true);
                    iFrame.tfIgv.setText("0.00");

                    iFrame.tfIsc.setEnabled(true);
                    iFrame.tfIsc.setText("0.00");

                    iFrame.tfOtrosTributos.setEnabled(true);
                    iFrame.tfOtrosTributos.setText("0.00");

                    iFrame.tfBolsasPlasticas.setEnabled(true);
                    iFrame.tfBolsasPlasticas.setText("0.00");

                    iFrame.cbxPercepcionRegimen.setEnabled(true);
                    iFrame.cbxPercepcionRegimen.setSelectedIndex(0);

                    iFrame.tfPercepcionTasa.setEnabled(true);

                    iFrame.tfPercepcionMonto.setEnabled(true);

                    iFrame.tfPercepcionMontoTotal.setEnabled(true);

                    iFrame.tfPercepcionBase.setEnabled(true);

                    iFrame.btnNuevo.setEnabled(false);

                    iFrame.btnGuardar.setEnabled(false);

                    iFrame.btnLimpiar.setEnabled(true);
                  } catch (InterruptedException | ExecutionException ex) {
                    JOptionPane.showMessageDialog(
                        null,
                        ex.getMessage(),
                        ResumenDiarioController.class.getName(),
                        JOptionPane.ERROR_MESSAGE);
                  }
                }
              };

          worker.execute();
        });

    iFrame.btnGuardar.addActionListener(
        (ActionEvent arg0) -> {
          File jks = new File(preferences.get(UsuarioController.FIRMA_JKS, ""));
          if (jks.exists()) {
            int input =
                JOptionPane.showOptionDialog(
                    iFrame,
                    "Seguro que desea guardar esta "
                        + iFrame.cbxTipo.getSelectedItem().toString()
                        + "?",
                    "Guardar",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    new Object[] {"Guardar", "Cancelar"},
                    "Guardar");

            if (input == JOptionPane.YES_OPTION) {

              dialog.setVisible(true);
              dialog.setLocationRelativeTo(iFrame);

              SwingWorker worker =
                  new SwingWorker<ResumenDiario, Void>() {
                    @Override
                    protected ResumenDiario doInBackground() throws Exception {
                      ResumenDiario resumenDiario = null;
                      try {
                        resumenDiario = new ResumenDiario();
                        resumenDiario.setUbl("2.0");
                        resumenDiario.setVersion("1.0");

                        resumenDiario.setTipoDocumento(
                            (TipoDocumento) iFrame.cbxTipo.getSelectedItem());
                        resumenDiario.setSerie(
                            MyDateFormat.yyyyMMdd(iFrame.dpFechaGeneracion.getDate()));
                        resumenDiario.setCorrelativo(
                            Integer.valueOf(iFrame.tfCorrelativo.getText()));

                        resumenDiario.setFechaEmision(iFrame.dpFechaGeneracion.getDate());
                        resumenDiario.setFechaReferencia(iFrame.dpFechaEmision.getDate());

                        Empresa emisor = new Empresa();
                        emisor.setRuc(preferences.get(UsuarioController.RUC, ""));
                        emisor.setTipo(preferences.getInt(UsuarioController.RUC_TIPO, 0));
                        emisor.setRazonSocial(preferences.get(UsuarioController.RAZON_SOCIAL, ""));
                        resumenDiario.setEmisor(emisor);

                        resumenDiario.setResumenDiarioDetalles(eventList);

                        SummaryDocuments summaryDocuments = new SummaryDocuments();
                        Document document = summaryDocuments.getStructure(resumenDiario);

                        File xml =
                            MyFileCreator.create(
                                resumenDiario.getTipoDocumento().getCodigo(),
                                resumenDiario.getSerie(),
                                resumenDiario.getCorrelativo(),
                                document);

                        File sign =
                            MyFileCreator.sign(
                                resumenDiario.getTipoDocumento().getCodigo(),
                                resumenDiario.getSerie(),
                                resumenDiario.getCorrelativo(),
                                xml);

                        File zip =
                            MyFileCreator.compress(
                                resumenDiario.getTipoDocumento().getCodigo(),
                                resumenDiario.getSerie(),
                                resumenDiario.getCorrelativo(),
                                sign);

                        byte[] byteArray = Files.readAllBytes(zip.toPath());
                        resumenDiario.setNombreZip(zip.getName());
                        resumenDiario.setZip(byteArray);
                        int id = summaryDao.create(resumenDiario);
                        System.out.println(".doInBackground() " + id);
                        resumenDiarioDao.create(id, eventList);
                        System.out.println(".doInBackground() " + id);
                        System.out.println(".doInBackground() " + resumenDiario);

                        sign.delete();
                        zip.delete();
                      } catch (IOException
                          | InvalidAlgorithmParameterException
                          | KeyStoreException
                          | NoSuchAlgorithmException
                          | UnrecoverableEntryException
                          | CertificateException
                          | MarshalException
                          | XMLSignatureException
                          | ParserConfigurationException
                          | TransformerException
                          | SAXException
                          | SQLException ex) {
                        cancel(true);

                        JOptionPane.showMessageDialog(
                            null,
                            ex.getMessage(),
                            ResumenDiarioController.class.getName(),
                            JOptionPane.ERROR_MESSAGE);
                      }
                      System.out.println(".doInBackground() " + resumenDiario);
                      return resumenDiario;
                    }

                    @Override
                    protected void done() {
                      dialog.dispose();

                      if (isCancelled()) {
                        start();
                      } else {
                        try {
                          start();
                          ResumenDiario get = get();
                          System.out.println(".done() " + get);
                          JOptionPane.showMessageDialog(
                              iFrame,
                              get.getNombreZip() + " guardado",
                              "Guardado",
                              JOptionPane.INFORMATION_MESSAGE);
                        } catch (InterruptedException | ExecutionException ex) {
                          Logger.getLogger(ResumenDiarioController.class.getName())
                              .log(Level.SEVERE, null, ex);
                        }
                      }
                    }
                  };

              worker.execute();
            }

          } else {
            JOptionPane.showMessageDialog(
                iFrame,
                "No se encuentra el archivo JKS en la ruta "
                    + preferences.get(UsuarioController.FIRMA_JKS, ""),
                ResumenDiarioController.class.getName(),
                JOptionPane.ERROR_MESSAGE);
          }
        });

    iFrame.btnLimpiar.addActionListener(
        (ActionEvent arg0) -> {
          start();
        });

    iFrame.cbxDocumentoTipo.addItemListener(
        (ItemEvent ie) -> {
          if (ie.getStateChange() == ItemEvent.SELECTED) {
            if (iFrame.cbxDocumentoTipo.getSelectedIndex() == 0) {
              iFrame.tbbdDetalle.setEnabledAt(2, false);

              iFrame.cbxDocumentoReferenciaTipo.setEnabled(false);
              iFrame.cbxDocumentoReferenciaTipo.setSelectedIndex(-1);

              iFrame.tfDocumentoReferenciaSerie.setEnabled(false);

              iFrame.tfDocumentoReferenciaCorrelativo.setEnabled(false);
            } else {
              iFrame.tbbdDetalle.setEnabledAt(2, true);

              iFrame.cbxDocumentoReferenciaTipo.setEnabled(true);
              iFrame.cbxDocumentoReferenciaTipo.setSelectedIndex(0);

              iFrame.tfDocumentoReferenciaSerie.setEnabled(true);

              iFrame.tfDocumentoReferenciaCorrelativo.setEnabled(true);
            }
          }
        });

    iFrame.cbxPercepcionRegimen.addItemListener(
        (ItemEvent ie) -> {
          if (ie.getStateChange() == ItemEvent.SELECTED) {
            if (iFrame.cbxDocumentoTipo.getSelectedIndex() == 0) {
              RegimenPercepcion regimenPercepcion =
                  (RegimenPercepcion) iFrame.cbxPercepcionRegimen.getSelectedItem();
              iFrame.tfPercepcionTasa.setText(String.valueOf(regimenPercepcion.getPorcentaje()));
            }
          }
        });

    iFrame.btnAgregar.addActionListener(
        (arg0) -> {
          ResumenDiarioDetalle detalle = new ResumenDiarioDetalle();

          Documento documento = new Documento();
          documento.setSerie(iFrame.tfDocumentoSerie.getText());
          documento.setCorrelativo(Integer.valueOf(iFrame.tfDocumentoCorrelativo.getText()));
          documento.setTipoDocumento((TipoDocumento) iFrame.cbxDocumentoTipo.getSelectedItem());
          detalle.setDocumento(documento);

          if (iFrame.cbxDocumentoIdentidadTipo.getSelectedIndex() >= 0
              && !iFrame.tfDocumentoIdentidadNumero.getText().isEmpty()) {
            Empresa adquiriente = new Empresa();
            adquiriente.setDocumentoIdentidadNumero(
                Integer.valueOf(iFrame.tfDocumentoIdentidadNumero.getText()));
            adquiriente.setDocumentoIdentidad(
                (DocumentoIdentidad) iFrame.cbxDocumentoIdentidadTipo.getSelectedItem());
            detalle.setAdquiriente(adquiriente);
          }

          if (iFrame.cbxDocumentoReferenciaTipo.getSelectedIndex() >= 0
              && !iFrame.tfDocumentoReferenciaSerie.getText().isEmpty()
              && !iFrame.tfDocumentoReferenciaCorrelativo.getText().isEmpty()) {
            Documento documentoReferencia = new Documento();
            documentoReferencia.setSerie(iFrame.tfDocumentoReferenciaSerie.getText());
            documentoReferencia.setCorrelativo(
                Integer.valueOf(iFrame.tfDocumentoReferenciaCorrelativo.getText()));
            documentoReferencia.setTipoDocumento(
                (TipoDocumento) iFrame.cbxDocumentoReferenciaTipo.getSelectedItem());
            detalle.setDocumentoReferencia(documentoReferencia);
          }

          if (iFrame.cbxPercepcionRegimen.getSelectedIndex() >= 0
              && !iFrame.tfPercepcionTasa.getText().isEmpty()
              && !iFrame.tfPercepcionMonto.getText().isEmpty()
              && !iFrame.tfPercepcionMontoTotal.getText().isEmpty()) {
            Percepcion percepcion = new Percepcion();
            percepcion.setRegimenPercepcion(
                (RegimenPercepcion) iFrame.cbxPercepcionRegimen.getSelectedItem());
            percepcion.setMonto(Double.parseDouble(iFrame.tfPercepcionMonto.getText()));
            percepcion.setMontoTotal(Double.parseDouble(iFrame.tfPercepcionMonto.getText()));
            percepcion.setMonto(Double.parseDouble(iFrame.tfPercepcionMontoTotal.getText()));
            percepcion.setBase(Double.parseDouble(iFrame.tfPercepcionBase.getText()));
            detalle.setPercepcion(percepcion);
          }

          detalle.setEstado((Estado) iFrame.cbxEstado.getSelectedItem());

          detalle.setImporteTotal(Double.parseDouble(iFrame.tfImporteTotal.getText()));

          detalle.setMoneda((Moneda) iFrame.cbxMoneda.getSelectedItem());

          if (!iFrame.tfGravadas.getText().equals("0.00")) {
            Operacion gravadas = new Operacion();
            gravadas.setCodigo("01");
            gravadas.setDescripcion("Gravado");
            gravadas.setTotal(Double.parseDouble(iFrame.tfGravadas.getText()));
            detalle.setGravadas(gravadas);
          }

          if (!iFrame.tfExoneradas.getText().equals("0.00")) {
            Operacion exoneradas = new Operacion();
            exoneradas.setCodigo("02");
            exoneradas.setDescripcion("Exonerado");
            exoneradas.setTotal(Double.parseDouble(iFrame.tfExoneradas.getText()));
            detalle.setExoneradas(exoneradas);
          }

          if (!iFrame.tfInafectas.getText().equals("0.00")) {
            Operacion inafectas = new Operacion();
            inafectas.setCodigo("03");
            inafectas.setDescripcion("Inafecto");
            inafectas.setTotal(Double.parseDouble(iFrame.tfInafectas.getText()));
            detalle.setInafectas(inafectas);
          }

          if (!iFrame.tfExportacion.getText().equals("0.00")) {
            Operacion exportacion = new Operacion();
            exportacion.setCodigo("04");
            exportacion.setDescripcion("Exportación");
            exportacion.setTotal(Double.parseDouble(iFrame.tfExportacion.getText()));
            detalle.setExportacion(exportacion);
          }

          if (!iFrame.tfGratuitas.getText().equals("0.00")) {
            Operacion gratuitas = new Operacion();
            gratuitas.setCodigo("05");
            gratuitas.setDescripcion("Gratuitas");
            gratuitas.setTotal(Double.parseDouble(iFrame.tfGratuitas.getText()));
            detalle.setGratuitas(gratuitas);
          }

          if (!iFrame.tfOtrosCargos.getText().equals("0.00")) {
            OtrosCargos otrosCargos = new OtrosCargos();
            otrosCargos.setIndicador(true);
            otrosCargos.setTotal(Double.parseDouble(iFrame.tfOtrosCargos.getText()));
            detalle.setOtrosCargos(otrosCargos);
          }

          Impuesto igv = new Impuesto();
          igv.setTotal(Double.parseDouble(iFrame.tfIgv.getText()));
          igv.setCodigo("1000");
          igv.setDescripcion("IGV Impuesto General a las Ventas");
          igv.setCodigoInternacional("VAT");
          detalle.setIgv(igv);

          if (!iFrame.tfIsc.getText().equals("0.00")) {
            Impuesto isc = new Impuesto();
            isc.setTotal(Double.parseDouble(iFrame.tfIsc.getText()));
            isc.setCodigo("2000");
            isc.setDescripcion("ISC Impuesto Selectivo al Consumo");
            isc.setCodigoInternacional("EXC");
            detalle.setIsc(isc);
          }

          if (!iFrame.tfOtrosTributos.getText().equals("0.00")) {
            Impuesto otrosTributos = new Impuesto();
            otrosTributos.setTotal(Double.parseDouble(iFrame.tfOtrosTributos.getText()));
            otrosTributos.setCodigo("9999");
            otrosTributos.setDescripcion("Otros tributos");
            otrosTributos.setCodigoInternacional("OTH");
            detalle.setOtrosTributos(otrosTributos);
          }

          if (!iFrame.tfBolsasPlasticas.getText().equals("0.00")) {
            Impuesto bolsas = new Impuesto();
            bolsas.setTotal(Double.parseDouble(iFrame.tfBolsasPlasticas.getText()));
            bolsas.setCodigo("7152");
            bolsas.setDescripcion("Impuesto a la bolsa plastica");
            bolsas.setCodigoInternacional("OTH");
            detalle.setImpuestoBolsa(bolsas);
          }

          eventList.add(detalle);
          MyTableResize.resize(iFrame.table);

          iFrame.btnGuardar.setEnabled(true);
        });

    iFrame.tfDocumentoSerie.getDocument().addDocumentListener(dlEnabled);

    iFrame.tfDocumentoCorrelativo.getDocument().addDocumentListener(dlEnabled);

    iFrame.tfGravadas.getDocument().addDocumentListener(dlSum);

    iFrame.tfExoneradas.getDocument().addDocumentListener(dlSum);

    iFrame.tfInafectas.getDocument().addDocumentListener(dlSum);

    iFrame.tfExportacion.getDocument().addDocumentListener(dlSum);

    iFrame.tfOtrosCargos.getDocument().addDocumentListener(dlSum);

    iFrame.tfIsc.getDocument().addDocumentListener(dlSum);

    iFrame.tfOtrosTributos.getDocument().addDocumentListener(dlSum);

    iFrame.tfBolsasPlasticas.getDocument().addDocumentListener(dlSum);
  }

  private void initComponents() {
    summaryDao = new ISummaryDao();
    resumenDiarioDao = new IResumenDiarioDao();
    preferences = Preferences.userRoot().node(MainController.class.getPackageName());
    eventList = new BasicEventList<>();

    TableFormat<ResumenDiarioDetalle> tableFormat =
        new TableFormat<ResumenDiarioDetalle>() {
          @Override
          public int getColumnCount() {
            return 24;
          }

          @Override
          public String getColumnName(int column) {
            switch (column) {
              case 0:
                return "Serie";
              case 1:
                return "Correlativo";
              case 2:
                return "Tipo";
              case 3:
                return "Adquiriente";
              case 4:
                return "Referencia Serie";
              case 5:
                return "Referencia Correlativo";
              case 6:
                return "Referencia Tipo";
              case 7:
                return "Percepcion Descripcion";
              case 8:
                return "Percepcion Tasa";
              case 9:
                return "Percepcion Monto";
              case 10:
                return "Percepcion Monto Totsl";
              case 11:
                return "Estado";
              case 12:
                return "Importe Total";
              case 13:
                return "Moneda";
              case 14:
                return "Gravadas";
              case 15:
                return "Exoneradas";
              case 16:
                return "Inafectas";
              case 17:
                return "Gratuitas";
              case 18:
                return "Exportacion";
              case 19:
                return "Otros Cargos";
              case 20:
                return "IGV";
              case 21:
                return "ISC";
              case 22:
                return "Otros Tributos";
              case 23:
                return "Bolsas";
            }
            throw new IllegalStateException("Unexpected column: " + column);
          }

          @Override
          public Object getColumnValue(ResumenDiarioDetalle detalle, int column) {
            switch (column) {
              case 0:
                return detalle.getDocumento().getSerie();
              case 1:
                return detalle.getDocumento().getCorrelativo();
              case 2:
                return detalle.getDocumento().getTipoDocumento().getDescripcion();
              case 3:
                if (detalle.getAdquiriente() != null) return detalle.getAdquiriente().getRuc();
                else return "";
              case 4:
                if (detalle.getDocumentoReferencia() != null)
                  return detalle.getDocumentoReferencia().getSerie();
                else return "";
              case 5:
                if (detalle.getDocumentoReferencia() != null)
                  return detalle.getDocumentoReferencia().getCorrelativo();
                else return "";
              case 6:
                if (detalle.getDocumentoReferencia() != null)
                  return detalle.getDocumentoReferencia().getTipoDocumento().getDescripcion();
                else return "";
              case 7:
                if (detalle.getPercepcion() != null)
                  return detalle.getPercepcion().getRegimenPercepcion().getDescripcion();
                else return "";
              case 8:
                if (detalle.getPercepcion() != null)
                  return detalle.getPercepcion().getRegimenPercepcion().getPorcentaje();
                else return "";
              case 9:
                if (detalle.getPercepcion() != null) return detalle.getPercepcion().getMonto();
                else return "";
              case 10:
                if (detalle.getPercepcion() != null) return detalle.getPercepcion().getMontoTotal();
                else return "";
              case 11:
                return detalle.getEstado().getDescripcion();
              case 12:
                return detalle.getImporteTotal();
              case 13:
                return detalle.getMoneda().getDescripcion();
              case 14:
                if (detalle.getGravadas() != null) return detalle.getGravadas().getTotal();
                else return "";
              case 15:
                if (detalle.getExoneradas() != null) return detalle.getExoneradas().getTotal();
                else return "";
              case 16:
                if (detalle.getInafectas() != null) return detalle.getInafectas().getTotal();
                else return "";
              case 17:
                if (detalle.getGratuitas() != null) return detalle.getGratuitas().getTotal();
                else return "";
              case 18:
                if (detalle.getExportacion() != null) return detalle.getExportacion().getTotal();
                else return "";
              case 19:
                if (detalle.getOtrosCargos() != null) return detalle.getOtrosCargos().getTotal();
                else return "";
              case 20:
                return detalle.getIgv().getTotal();
              case 21:
                if (detalle.getIsc() != null) return detalle.getIsc().getTotal();
                else return "";
              case 22:
                if (detalle.getOtrosTributos() != null)
                  return detalle.getOtrosTributos().getTotal();
                else return "";
              case 23:
                if (detalle.getImpuestoBolsa() != null)
                  return detalle.getImpuestoBolsa().getTotal();
                else return "";
            }
            throw new IllegalStateException("Unexpected column: " + column);
          }
        };

    tableModel = eventTableModelWithThreadProxyList(eventList, tableFormat);
    selectionModel = new DefaultEventSelectionModel<>(eventList);
    iFrame.table.setModel(tableModel);
    iFrame.table.setSelectionModel(selectionModel);

    iFrame.show();

    iFrame.btnNuevo.requestFocus();
  }

  private void start() {
    iFrame.tabbed.setSelectedIndex(0);

    iFrame.cbxTipo.setEnabled(false);
    iFrame.cbxTipo.setSelectedIndex(-1);

    iFrame.tfSerie.setEnabled(false);
    iFrame.tfSerie.setText("");

    iFrame.tfCorrelativo.setEnabled(false);
    iFrame.tfCorrelativo.setText("");

    iFrame.dpFechaGeneracion.setEnabled(false);
    iFrame.dpFechaGeneracion.setDate(null);

    iFrame.dpFechaEmision.setEnabled(false);
    iFrame.dpFechaEmision.setDate(null);

    iFrame.tbbdDetalle.setSelectedIndex(0);
    iFrame.tbbdDetalle.setEnabledAt(2, false);

    iFrame.cbxEstado.setEnabled(false);
    iFrame.cbxEstado.setSelectedIndex(-1);

    iFrame.cbxMoneda.setEnabled(false);
    iFrame.cbxMoneda.setSelectedIndex(-1);

    iFrame.cbxDocumentoTipo.setEnabled(false);
    iFrame.cbxDocumentoTipo.setSelectedIndex(-1);

    iFrame.tfDocumentoSerie.setEnabled(false);

    iFrame.tfDocumentoCorrelativo.setEnabled(false);

    iFrame.cbxDocumentoIdentidadTipo.setEnabled(false);
    iFrame.cbxDocumentoIdentidadTipo.setSelectedIndex(-1);

    iFrame.tfDocumentoIdentidadNumero.setEnabled(false);

    iFrame.tfImporteTotal.setEnabled(false);
    iFrame.tfImporteTotal.setText("");

    iFrame.tfGravadas.setEnabled(false);
    iFrame.tfGravadas.setText("");

    iFrame.tfExoneradas.setEnabled(false);
    iFrame.tfExoneradas.setText("");

    iFrame.tfInafectas.setEnabled(false);
    iFrame.tfInafectas.setText("");

    iFrame.tfGratuitas.setEnabled(false);
    iFrame.tfGratuitas.setText("");

    iFrame.tfExportacion.setEnabled(false);
    iFrame.tfExportacion.setText("");

    iFrame.tfOtrosCargos.setEnabled(false);
    iFrame.tfOtrosCargos.setText("");

    iFrame.tfIsc.setEnabled(false);
    iFrame.tfIsc.setText("");

    iFrame.tfIgv.setEnabled(false);
    iFrame.tfIgv.setText("");

    iFrame.tfIsc.setEnabled(false);
    iFrame.tfIsc.setText("");

    iFrame.tfOtrosTributos.setEnabled(false);
    iFrame.tfOtrosTributos.setText("");

    iFrame.tfBolsasPlasticas.setEnabled(false);
    iFrame.tfBolsasPlasticas.setText("");

    iFrame.cbxPercepcionRegimen.setEnabled(false);
    iFrame.cbxPercepcionRegimen.setSelectedIndex(-1);

    iFrame.tfPercepcionTasa.setEnabled(false);
    iFrame.tfPercepcionTasa.setText("");

    iFrame.tfPercepcionMonto.setEnabled(false);
    iFrame.tfPercepcionMonto.setText("");

    iFrame.tfPercepcionMontoTotal.setEnabled(false);
    iFrame.tfPercepcionMontoTotal.setText("");

    iFrame.tfPercepcionBase.setEnabled(false);
    iFrame.tfPercepcionBase.setText("");

    eventList.clear();
    selectionModel.clearSelection();

    iFrame.btnNuevo.setEnabled(true);
    iFrame.btnNuevo.requestFocus();

    iFrame.btnGuardar.setEnabled(false);

    iFrame.btnLimpiar.setEnabled(false);
  }

  private final DocumentListener dlEnabled =
      new DocumentListener() {
        @Override
        public void insertUpdate(DocumentEvent arg0) {
          enabled();
        }

        @Override
        public void removeUpdate(DocumentEvent arg0) {
          enabled();
        }

        @Override
        public void changedUpdate(DocumentEvent arg0) {
          enabled();
        }
      };

  private void enabled() {
    if (iFrame.tfDocumentoSerie.getText().isEmpty()
        || iFrame.tfDocumentoCorrelativo.getText().isEmpty()) {
      iFrame.btnAgregar.setEnabled(false);
    } else {
      iFrame.btnAgregar.setEnabled(true);
    }
  }

  private final DocumentListener dlSum =
      new DocumentListener() {
        @Override
        public void insertUpdate(DocumentEvent arg0) {
          sum();
        }

        @Override
        public void removeUpdate(DocumentEvent arg0) {
          sum();
        }

        @Override
        public void changedUpdate(DocumentEvent arg0) {
          sum();
        }
      };

  private void sum() {
    if (!iFrame.tfGravadas.getText().isEmpty()
        && !iFrame.tfExoneradas.getText().isEmpty()
        && !iFrame.tfInafectas.getText().isEmpty()
        && !iFrame.tfExportacion.getText().isEmpty()
        && !iFrame.tfOtrosCargos.getText().isEmpty()
        && !iFrame.tfIsc.getText().isEmpty()
        && !iFrame.tfOtrosTributos.getText().isEmpty()
        && !iFrame.tfBolsasPlasticas.getText().isEmpty()) {
      double gravadas = Double.parseDouble(iFrame.tfGravadas.getText());
      double exoneradas = Double.parseDouble(iFrame.tfExoneradas.getText());
      double inafectas = Double.parseDouble(iFrame.tfInafectas.getText());
      double exportacion = Double.parseDouble(iFrame.tfExportacion.getText());
      double otrosCargos = Double.parseDouble(iFrame.tfOtrosCargos.getText());
      double igv = gravadas * 0.18;
      double isc = Double.parseDouble(iFrame.tfIsc.getText());
      double otrosTributos = Double.parseDouble(iFrame.tfOtrosTributos.getText());
      double bolsas = Double.parseDouble(iFrame.tfBolsasPlasticas.getText());
      double importeTotal =
          gravadas
              + exoneradas
              + inafectas
              + exportacion
              + otrosCargos
              + igv
              + isc
              + otrosTributos
              + bolsas;

      iFrame.tfIgv.setValue(igv);
      iFrame.tfImporteTotal.setValue(importeTotal);
    }
  }
}
