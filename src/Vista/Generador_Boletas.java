package Vista;

import Controlador.AccesoAleatorio;
import Modelo.Boleta;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.InputMethodEvent;
import java.awt.event.InputMethodListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class Generador_Boletas
extends JFrame {
    private final int MAXNUMBER = 100000;
    private final int MAXFIL = 2;
    private final int MAXCOL = 3;
    private JRadioButton C101;
    private JRadioButton C102;
    private JRadioButton C103;
    private JRadioButton C104;
    private JRadioButton C201;
    private JRadioButton C202;
    private JRadioButton C203;
    private JRadioButton C301;
    private JRadioButton C302;
    private ButtonGroup bgCifra1;
    private ButtonGroup bgCifra2;
    private ButtonGroup bgCifra3;
    private ButtonGroup bgColorTexto;
    private ButtonGroup bgMarco;
    private ButtonGroup btnCifra;
    private JButton btnFondo;
    private JButton btnFondo1;
    private JLabel jLabel1;
    private JLabel jLabel10;
    private JLabel jLabel11;
    private JLabel jLabel12;
    private JLabel jLabel13;
    private JLabel jLabel14;
    private JLabel jLabel15;
    private JLabel jLabel16;
    private JLabel jLabel2;
    private JLabel jLabel3;
    private JLabel jLabel4;
    private JLabel jLabel5;
    private JLabel jLabel6;
    private JLabel jLabel7;
    private JLabel jLabel8;
    private JLabel jLabel9;
    private JRadioButton jMarcoNo;
    private JRadioButton jMarcoSi;
    private JPanel jPanel1;
    private JPanel jPanel2;
    private JPanel jPanel3;
    private JPanel jPanel4;
    private JPanel jPanel5;
    private JPanel jPanel6;
    private JPanel jPanel7;
    private JPanel jPanel8;
    private JRadioButton jRBazul;
    private JRadioButton jRBcafe;
    private JRadioButton jRBnegro;
    private JRadioButton jRBrojo;
    private JRadioButton jRBverde;
    private JRadioButton jRBvioleta;
    private JTabbedPane jTabbedPane1;
    private JLabel lblImagen;
    private JTextField txtFecha;
    private JTextField txtFilePDF;
    private JTextField txtImagen;
    private JTextField txtMensaje1;
    private JTextField txtMensaje2;
    private JTextField txtMensaje3;
    private JTextField txtMensaje4;
    private JTextField txtMensaje5;
    private JTextField txtNumCifra1;
    private JTextField txtNumCifra2;
    private JTextField txtNumCifra3;
    private JTextField txtOportunidades;
    private JTextField txtTitulo;
    private JTextField txtTitulo1;
    private JTextField txtVlrBoleta;

    private String formatearNumero(int numero, int cif) {
        String tmp_num = String.valueOf(numero);
        int ceros = cif - tmp_num.length();
        for (int i = 0; i < ceros; ++i) {
            tmp_num = "0" + tmp_num;
        }
        return tmp_num;
    }

    public int[] generarNumerosAleatorios(int nroOpor) throws IOException, FileNotFoundException, BadElementException {
        int[] tmpNum = new int[this.MAXNUMBER];
        int saltoxColumna = (int)Math.ceil(33333.0);
        int saltoxBoleta = (int)Math.ceil(saltoxColumna / nroOpor);
        int saltoxFila = (int)Math.ceil(saltoxBoleta / 2);
        int nrosGen = nroOpor * saltoxFila * 3 * 2;
        int num = 0;
        ArrayList<String> stmpNum = new ArrayList<String>();
        int cifra = 0;
        Random random = new Random();
        // Baraja los MAXNUMBER numeros posibles (00000..99999), cada uno una sola vez.
        // El arreglo debe medir exactamente MAXNUMBER: si sobran posiciones quedan en
        // cero, entran a la baraja y el 00000 termina repetido en varias boletas.
        ArrayList<Integer> lista = new ArrayList<Integer>(this.MAXNUMBER);
        for (int numero = 0; numero < this.MAXNUMBER; ++numero) {
            lista.add(numero);
        }
        Collections.shuffle(lista, random);
        int index = 0;
        for (int numero : lista) {
            tmpNum[index++] = numero;
        }
        for (int i = 0; i < this.MAXNUMBER; ++i) {
            stmpNum.add(this.formatearNumero(tmpNum[i], 5));
        }
        if (this.C101.isSelected() || this.C102.isSelected() || this.C103.isSelected() || this.C104.isSelected()) {
            if (Integer.parseInt(this.txtNumCifra1.getText()) >= 0 && Integer.parseInt(this.txtNumCifra1.getText()) <= 9) {
                if (this.C101.isSelected()) {
                    cifra = 1;
                }
                if (this.C102.isSelected()) {
                    cifra = 2;
                }
                if (this.C103.isSelected()) {
                    cifra = 3;
                }
                if (this.C104.isSelected()) {
                    cifra = 4;
                }
                num = Integer.parseInt(this.txtNumCifra1.getText());
            } else {
                JOptionPane.showMessageDialog(this, "Digite Nro entre [0 y 9]", "Informacion", 1);
            }
        } else if (this.C201.isSelected() || this.C202.isSelected() || this.C203.isSelected()) {
            if (Integer.parseInt(this.txtNumCifra2.getText()) >= 0 && Integer.parseInt(this.txtNumCifra2.getText()) <= 99) {
                if (this.C201.isSelected()) {
                    cifra = 21;
                }
                if (this.C202.isSelected()) {
                    cifra = 22;
                }
                if (this.C203.isSelected()) {
                    cifra = 23;
                }
                num = Integer.parseInt(this.txtNumCifra2.getText());
            } else {
                JOptionPane.showMessageDialog(this, "Digite Nro entre [00 y 99]", "Informacion", 1);
            }
        } else if (this.C301.isSelected() || this.C302.isSelected()) {
            if (Integer.parseInt(this.txtNumCifra3.getText()) >= 0 && Integer.parseInt(this.txtNumCifra3.getText()) <= 999) {
                if (this.C301.isSelected()) {
                    cifra = 31;
                }
                if (this.C302.isSelected()) {
                    cifra = 32;
                }
                num = Integer.parseInt(this.txtNumCifra3.getText());
            } else {
                JOptionPane.showMessageDialog(this, "Digite Nro entre [000 y 999]", "Informacion", 1);
            }
        }
        ArrayList<String> stmpPrint = this.seleccionarCifras(stmpNum, num, cifra);
        this.writePDF(nroOpor, stmpPrint, saltoxFila - 1);
        return tmpNum;
    }

    public ArrayList<String> seleccionarCifras(ArrayList<String> stmpNum, int num, int cifra) {
        ArrayList<String> stmpNum2 = new ArrayList<String>();
        Iterator<String> i = stmpNum.iterator();
        while (i.hasNext()) {
            String stNum = i.next();
            switch (cifra) {
                case 1: {
                    if (!stNum.substring(0, 1).equals(this.formatearNumero(num, 1))) break;
                    i.remove();
                    stmpNum2.add(stNum);
                    break;
                }
                case 2: {
                    if (!stNum.substring(1, 2).equals(this.formatearNumero(num, 1))) break;
                    i.remove();
                    stmpNum2.add(stNum);
                    break;
                }
                case 3: {
                    if (!stNum.substring(2, 3).equals(this.formatearNumero(num, 1))) break;
                    i.remove();
                    stmpNum2.add(stNum);
                    break;
                }
                case 4: {
                    if (!stNum.substring(3, 4).equals(this.formatearNumero(num, 1))) break;
                    i.remove();
                    stmpNum2.add(stNum);
                    break;
                }
                case 21: {
                    if (!stNum.substring(0, 2).equals(this.formatearNumero(num, 2))) break;
                    i.remove();
                    stmpNum2.add(stNum);
                    break;
                }
                case 22: {
                    if (!stNum.substring(1, 3).equals(this.formatearNumero(num, 2))) break;
                    i.remove();
                    stmpNum2.add(stNum);
                    break;
                }
                case 23: {
                    if (!stNum.substring(2, 4).equals(this.formatearNumero(num, 2))) break;
                    i.remove();
                    stmpNum2.add(stNum);
                    break;
                }
                case 31: {
                    if (!stNum.substring(0, 3).equals(this.formatearNumero(num, 3))) break;
                    i.remove();
                    stmpNum2.add(stNum);
                    break;
                }
                case 32: {
                    if (!stNum.substring(1, 4).equals(this.formatearNumero(num, 3))) break;
                    i.remove();
                    stmpNum2.add(stNum);
                }
            }
        }
        stmpNum.addAll(stmpNum2);
        return stmpNum;
    }

    public Generador_Boletas() {
        this.initComponents();
        this.setLocationRelativeTo(null);
        Date objDate = new Date();
        System.out.println(objDate);
        String strDateFormat = "dd-MM-YYYY";
        SimpleDateFormat objSDF = new SimpleDateFormat(strDateFormat);
        System.out.println(objSDF.format(objDate));
        this.txtFecha.setText(objSDF.format(objDate));
        String strDateFormatFile = "ddMMYYYY_HHmmss";
        SimpleDateFormat objSDF2 = new SimpleDateFormat(strDateFormatFile);
        this.txtFilePDF.setText(objSDF2.format(objDate) + ".pdf");
        this.txtImagen.setText(AccesoAleatorio.getRutaImagenes() + "/Fondo.jpg");
        String path = this.txtImagen.getText();
        ImageIcon icono = new ImageIcon(path);
        this.lblImagen.setIcon(icono);
    }

    public void writePDF(int opor, ArrayList<String> stmpPrint, int filas) throws FileNotFoundException, IOException, BadElementException {
        boolean band = true;
        int pag = 0;
        int totpag = filas;
        int xi = 5;
        int yi = 395;
        int ancho = 198;
        int alto = 380;
        int paso_x = 202;
        int paso_y = 385;
        for (int i = 0; i < opor; ++i) {
            stmpPrint.add("#####");
        }
        int marco = 0;
        if (this.jMarcoSi.isSelected()) {
            marco = 1;
        }
        int color = 0;
        if (this.jRBrojo.isSelected()) {
            color = 1;
        }
        if (this.jRBazul.isSelected()) {
            color = 3;
        }
        if (this.jRBvioleta.isSelected()) {
            color = 4;
        }
        if (this.jRBverde.isSelected()) {
            color = 2;
        }
        if (this.jRBnegro.isSelected()) {
            color = 6;
        }
        if (this.jRBcafe.isSelected()) {
            color = 5;
        }
        Boleta bol = new Boleta();
        Image img = Image.getInstance((String)this.txtImagen.getText());
        try {
            Document document = new Document(PageSize.LETTER, 10.0f, 10.0f, 10.0f, 10.0f);
            PdfWriter writer = PdfWriter.getInstance((Document)document, (OutputStream)new FileOutputStream(AccesoAleatorio.getRutaFileRep() + "/" + this.txtFilePDF.getText()));
            document.open();
            PdfContentByte canvas = writer.getDirectContent();
            int x = xi;
            int y = yi;
            int index = 0;
            int col = 0;
            int fil = 0;
            String elemento = "";
            boolean idxx = false;
            do {
                if (col < 3) {
                    bol.drawRectangle(canvas, x, y, ancho, alto, 5.0f, 5.0f, this.txtTitulo.getText(), this.txtTitulo1.getText(), this.txtFecha.getText(), this.txtVlrBoleta.getText(), this.txtMensaje1.getText(), this.txtMensaje2.getText(), this.txtMensaje3.getText(), this.txtMensaje4.getText(), this.txtMensaje5.getText(), opor, color, marco, stmpPrint, index, img);
                    index += opor;
                    x += paso_x;
                    ++col;
                } else {
                    col = 0;
                    y -= paso_y;
                    x = xi;
                    if (++fil == 2) {
                        fil = 0;
                        y = yi;
                        x = xi;
                        ++pag;
                        document.newPage();
                    }
                }
                if (pag <= totpag || index + opor <= 100000 + opor) continue;
                band = false;
            } while (band);
            document.close();
            JOptionPane.showMessageDialog(this, "El archivo PDF [ " + this.txtFilePDF.getText() + " ] fue generado con exito", "Informacion", 1);
        }
        catch (DocumentException documentException) {
            System.out.println("The file not exists (Se ha producido un error al generar un documento): " + (Object)((Object)documentException));
        }
    }

    private void initComponents() {
        this.bgColorTexto = new ButtonGroup();
        this.bgCifra1 = new ButtonGroup();
        this.bgCifra2 = new ButtonGroup();
        this.bgCifra3 = new ButtonGroup();
        this.btnCifra = new ButtonGroup();
        this.bgMarco = new ButtonGroup();
        this.jPanel1 = new JPanel();
        this.jLabel1 = new JLabel();
        this.jLabel2 = new JLabel();
        this.jLabel3 = new JLabel();
        this.jLabel4 = new JLabel();
        this.jLabel5 = new JLabel();
        this.jLabel6 = new JLabel();
        this.txtTitulo = new JTextField();
        this.txtVlrBoleta = new JTextField();
        this.txtFecha = new JTextField();
        this.txtMensaje1 = new JTextField();
        this.txtMensaje2 = new JTextField();
        this.txtMensaje3 = new JTextField();
        this.txtMensaje4 = new JTextField();
        this.jLabel14 = new JLabel();
        this.txtTitulo1 = new JTextField();
        this.jLabel16 = new JLabel();
        this.txtMensaje5 = new JTextField();
        this.jPanel2 = new JPanel();
        this.jLabel7 = new JLabel();
        this.txtImagen = new JTextField();
        this.btnFondo = new JButton();
        this.jPanel3 = new JPanel();
        this.lblImagen = new JLabel();
        this.jPanel5 = new JPanel();
        this.jLabel8 = new JLabel();
        this.txtOportunidades = new JTextField();
        this.jLabel10 = new JLabel();
        this.jRBrojo = new JRadioButton();
        this.jRBazul = new JRadioButton();
        this.jRBvioleta = new JRadioButton();
        this.jRBcafe = new JRadioButton();
        this.jRBnegro = new JRadioButton();
        this.jRBverde = new JRadioButton();
        this.jTabbedPane1 = new JTabbedPane();
        this.jPanel6 = new JPanel();
        this.txtNumCifra1 = new JTextField();
        this.jLabel9 = new JLabel();
        this.C101 = new JRadioButton();
        this.C102 = new JRadioButton();
        this.C103 = new JRadioButton();
        this.C104 = new JRadioButton();
        this.jPanel7 = new JPanel();
        this.jLabel12 = new JLabel();
        this.txtNumCifra2 = new JTextField();
        this.C201 = new JRadioButton();
        this.C202 = new JRadioButton();
        this.C203 = new JRadioButton();
        this.jPanel8 = new JPanel();
        this.jLabel13 = new JLabel();
        this.txtNumCifra3 = new JTextField();
        this.C301 = new JRadioButton();
        this.C302 = new JRadioButton();
        this.jLabel15 = new JLabel();
        this.jMarcoSi = new JRadioButton();
        this.jMarcoNo = new JRadioButton();
        this.jLabel11 = new JLabel();
        this.txtFilePDF = new JTextField();
        this.btnFondo1 = new JButton();
        this.jPanel4 = new JPanel();
        this.setDefaultCloseOperation(3);
        this.jLabel1.setText("T\u00edtulo");
        this.jLabel2.setText("Valor Boleta");
        this.jLabel3.setText("Fecha");
        this.jLabel4.setText("Mensaje No. 1");
        this.jLabel5.setText("Mensaje No. 2");
        this.jLabel6.setText("Mensaje No. 3");
        this.txtTitulo.setText("   RIFA ENTRE OS");
        this.txtTitulo.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                Generador_Boletas.this.txtTituloActionPerformed(evt);
            }
        });
        this.txtVlrBoleta.setText("$ 5.000");
        this.txtFecha.setHorizontalAlignment(0);
        this.txtFecha.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                Generador_Boletas.this.txtFechaActionPerformed(evt);
            }
        });
        this.txtMensaje1.setText("n\u00e3o s\u00e3o aceitos sorteios raspados");
        this.txtMensaje2.setText("rasgados, emendados ou de qualquer");
        this.txtMensaje2.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                Generador_Boletas.this.txtMensaje2ActionPerformed(evt);
            }
        });
        this.txtMensaje3.setText("forma que comprometa a veracidade ");
        this.txtMensaje4.setText("do bilhete");
        this.jLabel14.setText("Mensaje No. 4");
        this.txtTitulo1.setText("          AMIGOS");
        this.txtTitulo1.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                Generador_Boletas.this.txtTitulo1ActionPerformed(evt);
            }
        });
        this.jLabel16.setText("Mensaje No. 5");
        this.txtMensaje5.setText("bilhete v\u00e1lido at\u00e9 \u00e0s 7h30 do dia seguinte");
        GroupLayout jPanel1Layout = new GroupLayout(this.jPanel1);
        this.jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(jPanel1Layout.createSequentialGroup().addContainerGap().addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.jLabel1).addComponent(this.jLabel2).addComponent(this.jLabel4).addComponent(this.jLabel5).addComponent(this.jLabel6).addComponent(this.jLabel14).addComponent(this.jLabel16)).addGap(24, 24, 24).addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.txtMensaje2).addComponent(this.txtMensaje3).addComponent(this.txtMensaje4).addComponent(this.txtMensaje5).addGroup(jPanel1Layout.createSequentialGroup().addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.txtMensaje1, -2, 593, -2).addGroup(jPanel1Layout.createSequentialGroup().addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.txtTitulo, -2, 196, -2).addComponent(this.txtVlrBoleta, -2, 141, -2)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING, false).addGroup(jPanel1Layout.createSequentialGroup().addComponent(this.jLabel3).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.txtFecha)).addComponent(this.txtTitulo1, -2, 196, -2)))).addGap(0, 0, Short.MAX_VALUE))).addGap(16, 16, 16)));
        jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(jPanel1Layout.createSequentialGroup().addContainerGap().addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(jPanel1Layout.createSequentialGroup().addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(this.jLabel1).addComponent(this.txtTitulo, -2, -1, -2).addComponent(this.txtTitulo1, -2, -1, -2)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.txtVlrBoleta, -2, -1, -2).addComponent(this.jLabel2))).addGroup(GroupLayout.Alignment.TRAILING, jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(this.txtFecha, -2, -1, -2).addComponent(this.jLabel3))).addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(jPanel1Layout.createSequentialGroup().addGap(29, 29, 29).addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(this.txtMensaje2, -2, -1, -2).addComponent(this.jLabel5)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(this.txtMensaje3, -2, -1, -2).addComponent(this.jLabel6)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(this.txtMensaje4, -2, -1, -2).addComponent(this.jLabel14))).addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(this.jLabel4).addComponent(this.txtMensaje1, -2, -1, -2))).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(this.txtMensaje5, -2, -1, -2).addComponent(this.jLabel16)).addContainerGap(-1, Short.MAX_VALUE)));
        this.jLabel7.setText("Imagen");
        this.txtImagen.setEditable(false);
        this.txtImagen.setText("/Boletas/Imagenes//Sac_Money.png");
        this.btnFondo.setText("Fondo");
        this.btnFondo.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                Generador_Boletas.this.btnFondoActionPerformed(evt);
            }
        });
        GroupLayout jPanel3Layout = new GroupLayout(this.jPanel3);
        this.jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(jPanel3Layout.createSequentialGroup().addContainerGap().addComponent(this.lblImagen, -2, 400, -2).addContainerGap()));
        jPanel3Layout.setVerticalGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup().addContainerGap().addComponent(this.lblImagen, -2, 200, -2).addContainerGap()));
        this.jPanel5.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0)));
        this.jLabel8.setFont(new Font("Cantarell", 1, 15));
        this.jLabel8.setText("Oportunidades");
        this.txtOportunidades.setText("20");
        this.jLabel10.setFont(new Font("Cantarell", 1, 15));
        this.jLabel10.setText("Color Texto");
        this.bgColorTexto.add(this.jRBrojo);
        this.jRBrojo.setText("Rojo");
        this.bgColorTexto.add(this.jRBazul);
        this.jRBazul.setText("Azul");
        this.bgColorTexto.add(this.jRBvioleta);
        this.jRBvioleta.setText("Violeta");
        this.bgColorTexto.add(this.jRBcafe);
        this.jRBcafe.setText("Caf\u00e9");
        this.bgColorTexto.add(this.jRBnegro);
        this.jRBnegro.setText("Negro");
        this.bgColorTexto.add(this.jRBverde);
        this.jRBverde.setText("Verde");
        this.txtNumCifra1.setText("-1");
        this.txtNumCifra1.addFocusListener(new FocusAdapter(){

            @Override
            public void focusLost(FocusEvent evt) {
                Generador_Boletas.this.txtNumCifra1FocusLost(evt);
            }
        });
        this.txtNumCifra1.addInputMethodListener(new InputMethodListener(){

            @Override
            public void caretPositionChanged(InputMethodEvent evt) {
            }

            @Override
            public void inputMethodTextChanged(InputMethodEvent evt) {
                Generador_Boletas.this.txtNumCifra1InputMethodTextChanged(evt);
            }
        });
        this.txtNumCifra1.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                Generador_Boletas.this.txtNumCifra1ActionPerformed(evt);
            }
        });
        this.jLabel9.setText("Digito");
        this.btnCifra.add(this.C101);
        this.C101.setText("1");
        this.C101.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                Generador_Boletas.this.C101ActionPerformed(evt);
            }
        });
        this.btnCifra.add(this.C102);
        this.C102.setText("2");
        this.btnCifra.add(this.C103);
        this.C103.setText("3");
        this.btnCifra.add(this.C104);
        this.C104.setText("4");
        GroupLayout jPanel6Layout = new GroupLayout(this.jPanel6);
        this.jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(jPanel6Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup().addContainerGap().addComponent(this.jLabel9).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, -1, Short.MAX_VALUE).addGroup(jPanel6Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.C104).addComponent(this.C103).addComponent(this.C102).addComponent(this.C101).addComponent(this.txtNumCifra1, -2, 57, -2)).addContainerGap()));
        jPanel6Layout.setVerticalGroup(jPanel6Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(jPanel6Layout.createSequentialGroup().addContainerGap().addGroup(jPanel6Layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(this.txtNumCifra1, -2, -1, -2).addComponent(this.jLabel9, -2, 32, -2)).addGap(18, 18, 18).addComponent(this.C101).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.C102).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.C103).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.C104).addContainerGap(-1, Short.MAX_VALUE)));
        this.jTabbedPane1.addTab("1 Cifra", this.jPanel6);
        this.jLabel12.setText("Digito");
        this.txtNumCifra2.setText("-1");
        this.txtNumCifra2.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                Generador_Boletas.this.txtNumCifra2ActionPerformed(evt);
            }
        });
        this.btnCifra.add(this.C201);
        this.C201.setText("1");
        this.btnCifra.add(this.C202);
        this.C202.setText("2");
        this.btnCifra.add(this.C203);
        this.C203.setText("3");
        GroupLayout jPanel7Layout = new GroupLayout(this.jPanel7);
        this.jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(jPanel7Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(jPanel7Layout.createSequentialGroup().addContainerGap().addComponent(this.jLabel12).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(jPanel7Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.C201).addComponent(this.txtNumCifra2, -2, 57, -2).addComponent(this.C202).addComponent(this.C203)).addContainerGap(-1, Short.MAX_VALUE)));
        jPanel7Layout.setVerticalGroup(jPanel7Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(jPanel7Layout.createSequentialGroup().addContainerGap().addGroup(jPanel7Layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(this.jLabel12, -2, 32, -2).addComponent(this.txtNumCifra2, -2, -1, -2)).addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED).addComponent(this.C201).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.C202).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.C203).addContainerGap(56, Short.MAX_VALUE)));
        this.jTabbedPane1.addTab("2 Cifras", this.jPanel7);
        this.jLabel13.setText("Digito");
        this.txtNumCifra3.setText("-1");
        this.txtNumCifra3.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                Generador_Boletas.this.txtNumCifra3ActionPerformed(evt);
            }
        });
        this.btnCifra.add(this.C301);
        this.C301.setText("1");
        this.btnCifra.add(this.C302);
        this.C302.setText("2");
        GroupLayout jPanel8Layout = new GroupLayout(this.jPanel8);
        this.jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(jPanel8Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(jPanel8Layout.createSequentialGroup().addContainerGap().addComponent(this.jLabel13).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(jPanel8Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.C301).addComponent(this.txtNumCifra3, -2, 57, -2).addComponent(this.C302)).addContainerGap(-1, Short.MAX_VALUE)));
        jPanel8Layout.setVerticalGroup(jPanel8Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(jPanel8Layout.createSequentialGroup().addContainerGap().addGroup(jPanel8Layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(this.jLabel13, -2, 32, -2).addComponent(this.txtNumCifra3, -2, -1, -2)).addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED).addComponent(this.C301).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.C302).addContainerGap(-1, Short.MAX_VALUE)));
        this.jTabbedPane1.addTab("3 Cifras", this.jPanel8);
        this.jLabel15.setFont(new Font("Cantarell", 1, 15));
        this.jLabel15.setText("Marco");
        this.bgMarco.add(this.jMarcoSi);
        this.jMarcoSi.setText("Si");
        this.bgMarco.add(this.jMarcoNo);
        this.jMarcoNo.setSelected(true);
        this.jMarcoNo.setText("No");
        GroupLayout jPanel5Layout = new GroupLayout(this.jPanel5);
        this.jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(jPanel5Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(jPanel5Layout.createSequentialGroup().addContainerGap().addGroup(jPanel5Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.jLabel8).addComponent(this.txtOportunidades, -2, 57, -2).addComponent(this.jTabbedPane1, -2, 139, -2)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(jPanel5Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.jRBcafe).addComponent(this.jRBnegro).addComponent(this.jRBverde).addComponent(this.jRBvioleta).addComponent(this.jRBazul).addComponent(this.jLabel10).addComponent(this.jRBrojo).addComponent(this.jMarcoSi).addComponent(this.jLabel15).addComponent(this.jMarcoNo)).addContainerGap(-1, Short.MAX_VALUE)));
        jPanel5Layout.setVerticalGroup(jPanel5Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(jPanel5Layout.createSequentialGroup().addContainerGap().addGroup(jPanel5Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(jPanel5Layout.createSequentialGroup().addComponent(this.jLabel10).addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED).addComponent(this.jRBrojo).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.jRBazul).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.jRBvioleta).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.jRBverde).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.jRBnegro).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.jRBcafe).addGap(18, 18, 18).addComponent(this.jLabel15).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.jMarcoSi).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.jMarcoNo).addGap(0, 0, Short.MAX_VALUE)).addGroup(jPanel5Layout.createSequentialGroup().addComponent(this.jLabel8).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.txtOportunidades, -2, -1, -2).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.jTabbedPane1)))));
        this.jLabel11.setText("Documento PDF");
        this.txtFilePDF.setEditable(false);
        this.txtFilePDF.setText("/Boletas/Pdf/prueba.pdf");
        this.btnFondo1.setText("Generar");
        this.btnFondo1.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                Generador_Boletas.this.btnFondo1ActionPerformed(evt);
            }
        });
        GroupLayout jPanel2Layout = new GroupLayout(this.jPanel2);
        this.jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(jPanel2Layout.createSequentialGroup().addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(jPanel2Layout.createSequentialGroup().addContainerGap().addComponent(this.jLabel7).addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED).addComponent(this.txtImagen, -2, 455, -2).addGap(18, 18, 18).addComponent(this.btnFondo, -1, -1, Short.MAX_VALUE)).addGroup(jPanel2Layout.createSequentialGroup().addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.jPanel3, -1, -1, Short.MAX_VALUE).addGroup(jPanel2Layout.createSequentialGroup().addComponent(this.jLabel11).addGap(0, 0, Short.MAX_VALUE)).addGroup(jPanel2Layout.createSequentialGroup().addComponent(this.txtFilePDF, -2, 318, -2).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.btnFondo1, -1, -1, Short.MAX_VALUE))).addGap(18, 18, 18).addComponent(this.jPanel5, -2, -1, -2))).addContainerGap()));
        jPanel2Layout.setVerticalGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(jPanel2Layout.createSequentialGroup().addContainerGap(-1, Short.MAX_VALUE).addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(this.jLabel7).addComponent(this.txtImagen, -2, -1, -2).addComponent(this.btnFondo)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(jPanel2Layout.createSequentialGroup().addComponent(this.jPanel3, -2, -1, -2).addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED).addComponent(this.jLabel11).addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED).addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(this.txtFilePDF, -2, -1, -2).addComponent(this.btnFondo1))).addComponent(this.jPanel5, -2, -1, -2)).addGap(308, 308, 308)));
        GroupLayout jPanel4Layout = new GroupLayout(this.jPanel4);
        this.jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(jPanel4Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGap(0, 0, Short.MAX_VALUE));
        jPanel4Layout.setVerticalGroup(jPanel4Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGap(0, 0, Short.MAX_VALUE));
        GroupLayout layout = new GroupLayout(this.getContentPane());
        this.getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addContainerGap().addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.jPanel2, -1, -1, Short.MAX_VALUE).addComponent(this.jPanel4, -1, -1, Short.MAX_VALUE).addComponent(this.jPanel1, -2, -1, -2))));
        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addContainerGap(-1, Short.MAX_VALUE).addComponent(this.jPanel1, -2, -1, -2).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.jPanel2, -2, 362, -2).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.jPanel4, -2, -1, -2).addGap(9, 9, 9)));
        this.pack();
    }

    private void txtFechaActionPerformed(ActionEvent evt) {
    }

    private void btnFondo1ActionPerformed(ActionEvent evt) {
        Date objDate = new Date();
        String strDateFormatFile = "ddMMYYYY_HHmmss";
        SimpleDateFormat objSDF2 = new SimpleDateFormat(strDateFormatFile);
        this.txtFilePDF.setText(objSDF2.format(objDate) + ".pdf");
        try {
            if (Integer.parseInt(this.txtOportunidades.getText()) >= 1 && Integer.parseInt(this.txtOportunidades.getText()) <= 20) {
                this.generarNumerosAleatorios(Integer.parseInt(this.txtOportunidades.getText()));
            } else {
                JOptionPane.showMessageDialog(this, "Digite Nro Oportunidades entre [Del 1 al 20]", "Informacion", 1);
            }
        }
        catch (BadElementException | FileNotFoundException ex) {
            Logger.getLogger(Generador_Boletas.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (IOException ex) {
            Logger.getLogger(Generador_Boletas.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void btnFondoActionPerformed(ActionEvent evt) {
        JFileChooser selectorArchivos = new JFileChooser();
        selectorArchivos.setCurrentDirectory(new File(AccesoAleatorio.getRutaImagenes()));
        selectorArchivos.setFileSelectionMode(0);
        int resultado = selectorArchivos.showOpenDialog(this);
        if (resultado == 0) {
            File fichero = selectorArchivos.getSelectedFile();
            this.txtImagen.setText(fichero.getPath());
        }
        String path = this.txtImagen.getText();
        ImageIcon icono = new ImageIcon(path);
        this.lblImagen.setIcon(icono);
    }

    private void txtNumCifra1ActionPerformed(ActionEvent evt) {
    }

    private void txtNumCifra2ActionPerformed(ActionEvent evt) {
    }

    private void txtNumCifra3ActionPerformed(ActionEvent evt) {
    }

    private void txtNumCifra1InputMethodTextChanged(InputMethodEvent evt) {
    }

    private void txtNumCifra1FocusLost(FocusEvent evt) {
    }

    private void C101ActionPerformed(ActionEvent evt) {
    }

    private void txtMensaje2ActionPerformed(ActionEvent evt) {
    }

    private void txtTituloActionPerformed(ActionEvent evt) {
    }

    private void txtTitulo1ActionPerformed(ActionEvent evt) {
    }

    public static void main(String[] args) throws IOException {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if (!"Nimbus".equals(info.getName())) continue;
                UIManager.setLookAndFeel(info.getClassName());
                break;
            }
        }
        catch (ClassNotFoundException | IllegalAccessException | InstantiationException | UnsupportedLookAndFeelException ex) {
            Logger.getLogger(Generador_Boletas.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (!AccesoAleatorio.buscarFile(new File(AccesoAleatorio.getRutaFiledb()))) {
            System.out.print("Error de Conexion a Base de Datos");
            System.exit(0);
        }
        EventQueue.invokeLater(() -> new Generador_Boletas().setVisible(true));
    }
}

