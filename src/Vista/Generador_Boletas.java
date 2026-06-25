package Vista;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import Modelo.Boleta;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfContentByte;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import Controlador.AccesoAleatorio;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.PageSize;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.BorderFactory;
import java.awt.Font;
import java.awt.Color;
import java.awt.Dimension;

/* @author Luis
 */
public class Generador_Boletas extends javax.swing.JFrame {

        // ── Rangos de premio (local) ──
        private JTable tblRangos;
        private DefaultTableModel modeloRangos;

        // ── Redes sociales (reporte vertical tipo 3): textos junto a los logos ──
        private javax.swing.JTextField txtFacebook;
        private javax.swing.JTextField txtWhatsapp;

        /**
         * Creates new form Generador_Boletas
         */
        private final int MAXNUMBER = 10000;
        private final int MAXFIL = 4;
        private final int MAXCOL = 3;


        private ImageIcon iconEscalada(String path, int w, int h) {
                ImageIcon orig = new ImageIcon(path);
                java.awt.Image scaled = orig.getImage().getScaledInstance(w, h, java.awt.Image.SCALE_SMOOTH);
                return new ImageIcon(scaled);
        }

        private String formatearNumero(int numero, int cif) {
                String tmp_num = String.valueOf(numero);
                int ceros = cif - tmp_num.length();
                for (int i = 0; i < ceros; i++) {
                        tmp_num = "0" + tmp_num;
                }
                return tmp_num;
        }

        public int[] generarNumerosAleatorios(int nroOpor)
                        throws IOException, FileNotFoundException, BadElementException {
                int[] tmpNum = new int[MAXNUMBER + 10];
                int saltoxColumna = (int) Math.ceil(MAXNUMBER / MAXCOL);
                int saltoxBoleta = (int) Math.ceil(saltoxColumna / nroOpor);
                int saltoxFila = (int) Math.ceil((saltoxBoleta / MAXFIL));
                int num = 0;

                ArrayList<String> stmpNum = new ArrayList<>();
                int cifra = 0;

                // Generamos los numeros aleatorios y los adicionamos en un ArrayList
                for (int i = 0; i < MAXNUMBER + 1; i++) {
                        tmpNum[i] = (int) (Math.random() * MAXNUMBER);
                        if (i < MAXNUMBER) {
                                for (int j = 0; j < i; j++) {
                                        if (tmpNum[i] == tmpNum[j]) {
                                                i--;
                                        }
                                }
                        }
                }

                for (int i = 0; i < MAXNUMBER; i++) {
                        stmpNum.add(formatearNumero(tmpNum[i], 4));
                }

                if (this.C101.isSelected() || this.C102.isSelected() || this.C103.isSelected()
                                || this.C104.isSelected()) {
                        if (Integer.parseInt(this.txtNumCifra1.getText()) >= 0
                                        && Integer.parseInt(this.txtNumCifra1.getText()) <= 9) {
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
                                JOptionPane.showMessageDialog(this, "Digite Nro entre [0 y 9]", "Informacion",
                                                JOptionPane.INFORMATION_MESSAGE);
                        }
                } else {

                        if (this.C201.isSelected() || this.C202.isSelected() || this.C203.isSelected()) {
                                if (Integer.parseInt(this.txtNumCifra2.getText()) >= 0
                                                && Integer.parseInt(this.txtNumCifra2.getText()) <= 99) {
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
                                        JOptionPane.showMessageDialog(this, "Digite Nro entre [00 y 99]", "Informacion",
                                                        JOptionPane.INFORMATION_MESSAGE);
                                }

                        } else {
                                if (this.C301.isSelected() || this.C302.isSelected()) {
                                        if (Integer.parseInt(this.txtNumCifra3.getText()) >= 0
                                                        && Integer.parseInt(this.txtNumCifra3.getText()) <= 999) {
                                                if (this.C301.isSelected()) {
                                                        cifra = 31;
                                                }
                                                if (this.C302.isSelected()) {
                                                        cifra = 32;
                                                }
                                                num = Integer.parseInt(this.txtNumCifra3.getText());

                                        } else {
                                                JOptionPane.showMessageDialog(this, "Digite Nro entre [000 y 999]",
                                                                "Informacion",
                                                                JOptionPane.INFORMATION_MESSAGE);
                                        }

                                }

                        }

                }

                ArrayList<String> stmpPrint = seleccionarCifras(stmpNum, num, cifra);
                int tipoReporte = jComboBoxReporte.getSelectedIndex();
                writePDF(nroOpor, stmpPrint, saltoxFila - 1, tipoReporte);

                return tmpNum;
        }

        public ArrayList<String> seleccionarCifras(ArrayList<String> stmpNum, int num, int cifra) {
                // String stNum = "";
                ArrayList<String> stmpNum2 = new ArrayList<>();

                Iterator<String> i = stmpNum.iterator();

                while (i.hasNext()) {
                        String stNum = i.next();
                        switch (cifra) {
                                case 1:
                                        if (stNum.substring(0, 1).equals(formatearNumero(num, 1))) {
                                                i.remove();
                                                stmpNum2.add(stNum);
                                        }
                                        break;
                                case 2:
                                        if (stNum.substring(1, 2).equals(formatearNumero(num, 1))) {
                                                i.remove();
                                                stmpNum2.add(stNum);
                                        }
                                        break;
                                case 3:
                                        if (stNum.substring(2, 3).equals(formatearNumero(num, 1))) {
                                                i.remove();
                                                stmpNum2.add(stNum);
                                        }
                                        break;
                                case 4:
                                        if (stNum.substring(3, 4).equals(formatearNumero(num, 1))) {
                                                i.remove();
                                                stmpNum2.add(stNum);
                                        }
                                        break;

                                case 21:
                                        if (stNum.substring(0, 2).equals(formatearNumero(num, 2))) {
                                                i.remove();
                                                stmpNum2.add(stNum);
                                        }
                                        break;
                                case 22:
                                        if (stNum.substring(1, 3).equals(formatearNumero(num, 2))) {
                                                i.remove();
                                                stmpNum2.add(stNum);
                                        }
                                        break;
                                case 23:
                                        if (stNum.substring(2, 4).equals(formatearNumero(num, 2))) {
                                                i.remove();
                                                stmpNum2.add(stNum);
                                        }
                                        break;
                                case 31:
                                        if (stNum.substring(0, 3).equals(formatearNumero(num, 3))) {
                                                i.remove();
                                                stmpNum2.add(stNum);
                                        }
                                        break;
                                case 32:
                                        if (stNum.substring(1, 4).equals(formatearNumero(num, 3))) {
                                                i.remove();
                                                stmpNum2.add(stNum);
                                        }
                                        break;
                        }
                }

                stmpNum.addAll(stmpNum2);
                return (stmpNum);
        }

        public Generador_Boletas() {
                initComponents();
                this.setTitle("Boletas - Local");
                this.setLocationRelativeTo(null);
                Date objDate = new Date();

                System.out.println(objDate);
                String strDateFormat = "dd-MM-YYYY";
                SimpleDateFormat objSDF = new SimpleDateFormat(strDateFormat);
                System.out.println(objSDF.format(objDate));
                txtFecha.setText(objSDF.format(objDate));

                String strDateFormatFile = "ddMMYYYY_HHmmss";
                SimpleDateFormat objSDF2 = new SimpleDateFormat(strDateFormatFile);
                txtFilePDF.setText(objSDF2.format(objDate) + ".pdf");
                txtImagen.setText(AccesoAleatorio.getRutaImagenes() + "/Estrellas_3.jpeg");
                txtPremio.setText(AccesoAleatorio.getRutaImagenes() + "/Premio.jpeg");

                String path = this.txtImagen.getText();
                String pathPremio = this.txtPremio.getText();
                lblImagen.setIcon(iconEscalada(path, 220, 185));
                lblImagen1.setIcon(iconEscalada(pathPremio, 225, 185));

                // ── Panel de Redes Sociales (textos de Facebook/WhatsApp) ──
                inicializarPanelRedes();

                // ── Inicializar panel de Rangos de Premio ──
                inicializarPanelPlazaSorteo();
        }

        /**
         * Crea el panel de Redes Sociales con los campos configurables de
         * Facebook y WhatsApp, cuyos textos se imprimen junto a los logos en el
         * reporte vertical (tipo 3).
         */
        private void inicializarPanelRedes() {
                JPanel panelRedes = new JPanel();
                panelRedes.setBorder(BorderFactory.createTitledBorder(
                                BorderFactory.createLineBorder(new Color(153, 51, 0), 2),
                                "Redes Sociales (reporte vertical)",
                                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                                new Font("Cantarell", Font.BOLD, 13),
                                new Color(153, 51, 0)));
                panelRedes.setLayout(null);

                JLabel lblFb = new JLabel("Facebook");
                lblFb.setFont(new Font("Cantarell", Font.BOLD, 12));
                lblFb.setBounds(12, 25, 90, 25);
                panelRedes.add(lblFb);

                txtFacebook = new javax.swing.JTextField("LA ESTRELLA");
                txtFacebook.setBounds(105, 25, 320, 25);
                panelRedes.add(txtFacebook);

                JLabel lblWa = new JLabel("WhatsApp");
                lblWa.setFont(new Font("Cantarell", Font.BOLD, 12));
                lblWa.setBounds(12, 58, 90, 25);
                panelRedes.add(lblWa);

                txtWhatsapp = new javax.swing.JTextField("300 000 0000");
                txtWhatsapp.setBounds(105, 58, 320, 25);
                panelRedes.add(txtWhatsapp);

                getContentPane().add(panelRedes,
                                new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 460, 455, 100));
        }

        /**
         * Crea y agrega el panel de Plaza, Sorteo y Rangos de Premio
         * al formulario, debajo de las imágenes.
         */
        private void inicializarPanelPlazaSorteo() {
                // ── Panel Rangos de Premio (local) ──
                JPanel panelRangos = new JPanel();
                panelRangos.setBorder(BorderFactory.createTitledBorder(
                                BorderFactory.createLineBorder(new Color(153, 51, 0), 2),
                                "Rangos de Premio",
                                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                                new Font("Cantarell", Font.BOLD, 13),
                                new Color(153, 51, 0)));
                panelRangos.setLayout(null);

                modeloRangos = new DefaultTableModel(
                                new String[] { "Rango Ini", "Rango Fin", "Premio (mensaje)" }, 0);
                tblRangos = new JTable(modeloRangos);
                tblRangos.setFont(new Font("Cantarell", Font.PLAIN, 11));
                // Ajustar anchos de columna: Ini (70), Fin (70), Mensaje (resto)
                tblRangos.getColumnModel().getColumn(0).setPreferredWidth(70);
                tblRangos.getColumnModel().getColumn(1).setPreferredWidth(70);
                tblRangos.getColumnModel().getColumn(2).setPreferredWidth(350);
                JScrollPane scrollRangos = new JScrollPane(tblRangos);
                scrollRangos.setBounds(10, 20, 460, 105);
                panelRangos.add(scrollRangos);

                JButton btnAgregarRango = new JButton("+");
                btnAgregarRango.setFont(new Font("Cantarell", Font.BOLD, 12));
                btnAgregarRango.setBounds(480, 20, 50, 25);
                btnAgregarRango.addActionListener(e -> {
                        modeloRangos.addRow(new Object[] { "0", "0", "" });
                });
                panelRangos.add(btnAgregarRango);

                JButton btnEliminarRango = new JButton("-");
                btnEliminarRango.setFont(new Font("Cantarell", Font.BOLD, 12));
                btnEliminarRango.setBounds(480, 50, 50, 25);
                btnEliminarRango.addActionListener(e -> {
                        int fila = tblRangos.getSelectedRow();
                        if (fila >= 0) {
                                modeloRangos.removeRow(fila);
                        }
                });
                panelRangos.add(btnEliminarRango);

                getContentPane().add(panelRangos,
                                new org.netbeans.lib.awtextra.AbsoluteConstraints(475, 460, 600, 135));

                this.setPreferredSize(new Dimension(1080, 700));
                this.pack();
        }


        /**
         * Retorna el mensaje de premio para un número de boleta según los rangos locales.
         * Si cae en varios rangos, gana el de mayor prioridad (número mayor).
         */
        private String obtenerPremioLocal(String numeroBoleta) {
                try {
                        int numBol = Integer.parseInt(numeroBoleta);
                        for (int i = 0; i < modeloRangos.getRowCount(); i++) {
                                int ini = Integer.parseInt(modeloRangos.getValueAt(i, 0).toString().trim());
                                int fin = Integer.parseInt(modeloRangos.getValueAt(i, 1).toString().trim());
                                String msg = modeloRangos.getValueAt(i, 2).toString().trim();
                                if (numBol >= ini && numBol <= fin) {
                                        return msg;
                                }
                        }
                        return "Sin Premio";
                } catch (NumberFormatException e) {
                        return "Sin Premio";
                }
        }

        /**
         * Genera el PDF de boletas.
         * 
         * @param opor        Oportunidades por boleta
         * @param stmpPrint   Lista de números
         * @param filas       Número total de páginas estimado
         * @param tipoReporte 0 = 12 boletas (carta horizontal 3x4),
         *                    1 = 21 boletas (carta vertical 3x7),
         *                    2 = 15 boletas (carta vertical 3x5)
         */
        public void writePDF(int opor, ArrayList<String> stmpPrint, int filas, int tipoReporte)
                        throws FileNotFoundException, IOException, BadElementException {
                int x, y, xi, yi, alto, ancho, paso_x, paso_y, index, col, fil;
                boolean band = true;
                int maxCol; // columnas por página
                int maxFil; // filas por página

                // --- Configuración según tipo de reporte ---
                switch (tipoReporte) {
                        case 1: // 21 boletas: 3 columnas x 7 filas, carta VERTICAL
                                maxCol = 3;
                                maxFil = 7;
                                ancho = 196;
                                alto = 110;
                                paso_x = 197;
                                paso_y = 111;
                                xi = 5;
                                // yi = margen_inferior + (maxFil-1)*paso_y + alto
                                // En iText, y=0 es la parte inferior. Carta portrait: 792 pts alto.
                                // yi = inicio de la primera fila (fila superior)
                                yi = (int) (792 - 10 - alto); // ≈ 672
                                break;
                        case 2: // 15 boletas: 3 columnas x 5 filas, carta VERTICAL
                                maxCol = 3;
                                maxFil = 5;
                                ancho = 196;
                                alto = 150;
                                paso_x = 197;
                                paso_y = 151;
                                xi = 5;
                                yi = (int) (792 - 10 - alto); // ≈ 632
                                break;
                        case 3: // 6 boletas VERTICALES (10 oportunidades): 3 col x 2 filas, carta VERTICAL
                                maxCol = 3;
                                maxFil = 2;
                                ancho = 196;
                                alto = 375;
                                paso_x = 197;
                                paso_y = 378;
                                xi = 5;
                                yi = (int) (792 - 10 - alto); // ≈ 407
                                break;
                        default: // 0 → 12 boletas: 3 columnas x 4 filas, carta HORIZONTAL
                                maxCol = MAXCOL; // 3
                                maxFil = MAXFIL; // 4
                                ancho = 260;
                                alto = 145;
                                paso_x = 261;
                                paso_y = 146;
                                xi = 5;
                                yi = 440;
                                break;
                }

                // Ajustar la Lista
                for (int i = 0; i < opor; i++) {
                        stmpPrint.add("####");
                }

                // Seleccionar el color
                int color = 0;
                if (jRBrojo.isSelected()) {
                        color = 1;
                }
                if (jRBazul.isSelected()) {
                        color = 3;
                }
                if (jRBvioleta.isSelected()) {
                        color = 4;
                }
                if (jRBverde.isSelected()) {
                        color = 2;
                }
                if (jRBnegro.isSelected()) {
                        color = 6;
                }
                if (jRBcafe.isSelected()) {
                        color = 5;
                }

                Boleta bol = new Boleta();
                Image img = Image.getInstance(txtImagen.getText());
                Image pre = Image.getInstance(txtPremio.getText());

                // Logos para el reporte vertical (tipo 3): se buscan junto a la
                // imagen de fondo (mismo directorio Imagenes). Si fallan, quedan null
                // y drawRectangleVertical dibuja un placeholder.
                Image logoFb = null, logoWa = null;
                if (tipoReporte == 3) {
                        String imgDir = new java.io.File(txtImagen.getText()).getParent();
                        try { logoFb = Image.getInstance(imgDir + "/Facebook.png"); }
                        catch (Exception ex) { System.err.println("[LOGO] Facebook.png no cargó: " + ex.getMessage()); }
                        try { logoWa = Image.getInstance(imgDir + "/Whatsapp.png"); }
                        catch (Exception ex) { System.err.println("[LOGO] Whatsapp.png no cargó: " + ex.getMessage()); }
                }

                try {
                        Document document;
                        if (tipoReporte == 0) {
                                // Carta horizontal (landscape)
                                document = new Document(PageSize.LETTER, 10, 10, 10, 10);
                                document.setPageSize(PageSize.LETTER.rotate());
                        } else {
                                // Carta vertical (portrait) para 21 y 15 boletas
                                document = new Document(PageSize.LETTER, 10, 10, 10, 10);
                                // Portrait es el default de LETTER; no se rota
                        }

                        PdfWriter writer = PdfWriter.getInstance(document,
                                        new FileOutputStream(Controlador.AccesoAleatorio.getRutaFileRep() + "/"
                                                        + txtFilePDF.getText()));
                        document.open();
                        PdfContentByte canvas = writer.getDirectContent();

                        x = xi;
                        y = yi;
                        index = 0;
                        col = 0;
                        fil = 0;

                        int totalBoletas = 0;
                        String sorteoNombre = txtTitulo.getText();

                        // Imprimir documento en PDF
                        do {
                                if (col < maxCol) {
                                        // Concatenar números de oportunidades de esta boleta
                                        StringBuilder sbNumeros = new StringBuilder();
                                        for (int m = 0; m < opor; m++) {
                                                if (index + m < stmpPrint.size()) {
                                                        if (m > 0) sbNumeros.append("-");
                                                        sbNumeros.append(stmpPrint.get(index + m));
                                                }
                                        }
                                        String numerosConcatenados = sbNumeros.toString();

                                        totalBoletas++;
                                        String boletaConsecutivoFormateada = String.format("%04d", totalBoletas);

                                        // QR Derecha: solo los números de oportunidades en cadena
                                        String qrInfoContent = numerosConcatenados;

                                        // QR Izquierda: solo el mensaje del premio
                                        String premioBoleta = obtenerPremioLocal(boletaConsecutivoFormateada);
                                        String qrPremioContent = premioBoleta;

                                        if (tipoReporte == 3) {
                                                bol.drawRectangleVertical(canvas,
                                                                x, y, ancho, alto,
                                                                5, 5,
                                                                txtTitulo.getText(), txtFecha.getText(), txtVlrBoleta.getText(),
                                                                txtMensaje1.getText(), txtMensaje2.getText(),
                                                                txtMensaje3.getText(),
                                                                txtMensaje4.getText(), txtMensaje5.getText(),
                                                                txtMensaje6.getText(),
                                                                txtMensaje7.getText(), txtMensaje8.getText(),
                                                                txtMensaje9.getText(),
                                                                opor, color,
                                                                stmpPrint, index,
                                                                img, pre, qrInfoContent, qrPremioContent,
                                                                sorteoNombre, boletaConsecutivoFormateada,
                                                                logoFb, logoWa,
                                                                txtFacebook.getText(), txtWhatsapp.getText());
                                        } else {
                                                bol.drawRectangle(canvas,
                                                        x, y, ancho, alto,
                                                        5, 5,
                                                        txtTitulo.getText(), txtFecha.getText(), txtVlrBoleta.getText(),
                                                        txtMensaje1.getText(), txtMensaje2.getText(),
                                                        txtMensaje3.getText(),
                                                        txtMensaje4.getText(), txtMensaje5.getText(),
                                                        txtMensaje6.getText(),
                                                        txtMensaje7.getText(), txtMensaje8.getText(),
                                                        txtMensaje9.getText(),
                                                        opor, color,
                                                        stmpPrint, index,
                                                        img, pre, qrInfoContent, qrPremioContent,
                                                        sorteoNombre, boletaConsecutivoFormateada);
                                        }

                                        index += opor;
                                        x = x + paso_x;
                                        col++;
                                } else {
                                        col = 0;
                                        y = y - paso_y;
                                        x = xi;
                                        fil++;
                                        if (fil == maxFil) {
                                                fil = 0;
                                                y = yi;
                                                x = xi;
                                                document.newPage();
                                        }
                                }

                                // Detener cuando se consumieron todos los números reales (10000).
                                if (index >= MAXNUMBER) {
                                        band = false;
                                }

                        } while (band);

                        document.close();

                        JOptionPane.showMessageDialog(this,
                                        "El archivo PDF [ " + this.txtFilePDF.getText() + " ] fue generado con exito.\n"
                                                        + "Total boletas generadas: " + totalBoletas
                                                        + " boletas con el servidor...",
                                        "Informacion",
                                        JOptionPane.INFORMATION_MESSAGE);
                } catch (

                DocumentException documentException) {
                        System.out.println(
                                        "The file not exists (Se ha producido un error al generar un documento): "
                                                        + documentException);
                }
        }

        /**
         * This method is called from within the constructor to initialize the form.
         * WARNING: Do NOT modify this code. The content of this method is always
         * regenerated by the Form Editor.
         */
        // <editor-fold defaultstate="collapsed" desc="Generated
        // <editor-fold defaultstate="collapsed" desc="Generated
        // <editor-fold defaultstate="collapsed" desc="Generated
        // <editor-fold defaultstate="collapsed" desc="Generated
        // Code">//GEN-BEGIN:initComponents
        private void initComponents() {

                bgColorTexto = new javax.swing.ButtonGroup();
                btnCifra = new javax.swing.ButtonGroup();
                jPanel1 = new javax.swing.JPanel();
                jLabel1 = new javax.swing.JLabel();
                jLabel2 = new javax.swing.JLabel();
                jLabel3 = new javax.swing.JLabel();
                jLabel4 = new javax.swing.JLabel();
                jLabel5 = new javax.swing.JLabel();
                jLabel6 = new javax.swing.JLabel();
                txtTitulo = new javax.swing.JTextField();
                txtVlrBoleta = new javax.swing.JTextField();
                txtFecha = new javax.swing.JTextField();
                txtMensaje1 = new javax.swing.JTextField();
                txtMensaje2 = new javax.swing.JTextField();
                txtMensaje3 = new javax.swing.JTextField();
                txtMensaje4 = new javax.swing.JTextField();
                jLabel14 = new javax.swing.JLabel();
                jLabel16 = new javax.swing.JLabel();
                txtMensaje5 = new javax.swing.JTextField();
                jLabel17 = new javax.swing.JLabel();
                txtMensaje6 = new javax.swing.JTextField();
                jLabel18 = new javax.swing.JLabel();
                txtMensaje7 = new javax.swing.JTextField();
                jLabel7 = new javax.swing.JLabel();
                txtImagen = new javax.swing.JTextField();
                btnFondo = new javax.swing.JButton();
                jLabel19 = new javax.swing.JLabel();
                txtPremio = new javax.swing.JTextField();
                btnFondo2 = new javax.swing.JButton();
                txtMensaje8 = new javax.swing.JTextField();
                jLabel20 = new javax.swing.JLabel();
                jLabel21 = new javax.swing.JLabel();
                txtMensaje9 = new javax.swing.JTextField();
                jPanel9 = new javax.swing.JPanel();
                jPanel2 = new javax.swing.JPanel();
                lblImagen1 = new javax.swing.JLabel();
                jPanel3 = new javax.swing.JPanel();
                lblImagen = new javax.swing.JLabel();
                jPanel5 = new javax.swing.JPanel();
                jLabel8 = new javax.swing.JLabel();
                txtOportunidades = new javax.swing.JTextField();
                jLabel10 = new javax.swing.JLabel();
                jRBrojo = new javax.swing.JRadioButton();
                jRBazul = new javax.swing.JRadioButton();
                jRBvioleta = new javax.swing.JRadioButton();
                jRBcafe = new javax.swing.JRadioButton();
                jRBnegro = new javax.swing.JRadioButton();
                jRBverde = new javax.swing.JRadioButton();
                jTabbedPane1 = new javax.swing.JTabbedPane();
                jPanel6 = new javax.swing.JPanel();
                txtNumCifra1 = new javax.swing.JTextField();
                jLabel9 = new javax.swing.JLabel();
                C101 = new javax.swing.JRadioButton();
                C102 = new javax.swing.JRadioButton();
                C103 = new javax.swing.JRadioButton();
                C104 = new javax.swing.JRadioButton();
                jPanel7 = new javax.swing.JPanel();
                jLabel12 = new javax.swing.JLabel();
                txtNumCifra2 = new javax.swing.JTextField();
                C201 = new javax.swing.JRadioButton();
                C202 = new javax.swing.JRadioButton();
                C203 = new javax.swing.JRadioButton();
                jPanel8 = new javax.swing.JPanel();
                jLabel13 = new javax.swing.JLabel();
                txtNumCifra3 = new javax.swing.JTextField();
                C301 = new javax.swing.JRadioButton();
                C302 = new javax.swing.JRadioButton();
                jLabel11 = new javax.swing.JLabel();
                txtFilePDF = new javax.swing.JTextField();
                btnFondo1 = new javax.swing.JButton();
                jComboBoxReporte = new javax.swing.JComboBox<>();
                jLabel15 = new javax.swing.JLabel();

                setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
                getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

                jLabel1.setText("Título");

                jLabel2.setText("Valor Boleta");

                jLabel3.setText("Fecha");

                jLabel4.setText("Mensaje No. 1");

                jLabel5.setText("Mensaje No. 2");

                jLabel6.setText("Mensaje No. 3");

                txtTitulo.setText("LA ESTRELLA");

                txtVlrBoleta.setText("  $ 5.000");

                txtFecha.setHorizontalAlignment(javax.swing.JTextField.CENTER);
                txtFecha.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                txtFechaActionPerformed(evt);
                        }
                });

                txtMensaje1.setText("El APORTE lo hace participe en el sorteo del dia ");

                txtMensaje2.setText("Responsable : ");
                txtMensaje2.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                txtMensaje2ActionPerformed(evt);
                        }
                });

                txtMensaje3.setText("XXX XXX XXXXX");

                txtMensaje4.setText("APORTE ");

                jLabel14.setText("Mensaje No. 4");

                jLabel16.setText("Mensaje No. 5");

                txtMensaje5.setText("Caducidad Cinco (5) Dias para reclamar");

                jLabel17.setText("Mensaje No. 6");

                txtMensaje6.setText("PAGADERO AL PORTADOR");

                jLabel18.setText("Mensaje No. 7");

                txtMensaje7.setText("Bono sin Cancelar no participa");

                jLabel7.setText("Imagen de Fondo");

                txtImagen.setEditable(false);
                txtImagen.setText("/Boletas/Imagenes/Estrellas_3.jpeg");

                btnFondo.setText("Fondo");
                btnFondo.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                btnFondoActionPerformed(evt);
                        }
                });

                jLabel19.setText("Imagen de Premio");

                txtPremio.setEditable(false);
                txtPremio.setText("/Boletas/Imagenes/Premio.jpeg");

                btnFondo2.setText("Premio");
                btnFondo2.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                btnFondo2ActionPerformed(evt);
                        }
                });

                txtMensaje8.setText("Adulterada o Cercenada no participa");

                jLabel20.setText("Mensaje No. 8");

                jLabel21.setText("Mensaje No. 9");

                txtMensaje9.setText("Generando empleo en paz de Ariporó");

                javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
                jPanel9.setLayout(jPanel9Layout);
                jPanel9Layout.setHorizontalGroup(
                                jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addGap(0, 0, Short.MAX_VALUE));
                jPanel9Layout.setVerticalGroup(
                                jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addGap(0, 285, Short.MAX_VALUE));

                javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
                jPanel1.setLayout(jPanel1Layout);
                jPanel1Layout.setHorizontalGroup(
                                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addGroup(jPanel1Layout.createSequentialGroup()
                                                                .addContainerGap()
                                                                .addGroup(jPanel1Layout.createParallelGroup(
                                                                                javax.swing.GroupLayout.Alignment.LEADING)
                                                                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
                                                                                                jPanel1Layout.createSequentialGroup()
                                                                                                                .addGap(0, 0, Short.MAX_VALUE)
                                                                                                                .addGroup(jPanel1Layout
                                                                                                                                .createParallelGroup(
                                                                                                                                                javax.swing.GroupLayout.Alignment.LEADING)
                                                                                                                                .addGroup(jPanel1Layout
                                                                                                                                                .createParallelGroup(
                                                                                                                                                                javax.swing.GroupLayout.Alignment.LEADING)
                                                                                                                                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
                                                                                                                                                                jPanel1Layout.createSequentialGroup()
                                                                                                                                                                                .addComponent(jLabel7)
                                                                                                                                                                                .addPreferredGap(
                                                                                                                                                                                                javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                                                                                                                                                .addComponent(txtImagen,
                                                                                                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                                                                                                                455,
                                                                                                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                                                                                                                .addPreferredGap(
                                                                                                                                                                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                                                                                                                                .addComponent(btnFondo,
                                                                                                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                                                                                                                126,
                                                                                                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                                                                                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
                                                                                                                                                                jPanel1Layout.createSequentialGroup()
                                                                                                                                                                                .addComponent(jLabel19)
                                                                                                                                                                                .addPreferredGap(
                                                                                                                                                                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                                                                                                                                .addComponent(txtPremio,
                                                                                                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                                                                                                                455,
                                                                                                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                                                                                                                .addPreferredGap(
                                                                                                                                                                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                                                                                                                                .addComponent(btnFondo2,
                                                                                                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                                                                                                                126,
                                                                                                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)))
                                                                                                                                .addGroup(jPanel1Layout
                                                                                                                                                .createSequentialGroup()
                                                                                                                                                .addComponent(jLabel20)
                                                                                                                                                .addGap(29, 29, 29)
                                                                                                                                                .addComponent(txtMensaje8,
                                                                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                                                                                595,
                                                                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                                                                                .addGroup(jPanel1Layout
                                                                                                                                                .createSequentialGroup()
                                                                                                                                                .addComponent(jLabel21)
                                                                                                                                                .addGap(29, 29, 29)
                                                                                                                                                .addComponent(txtMensaje9,
                                                                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                                                                                595,
                                                                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)))
                                                                                                                .addGap(70, 70, 70))
                                                                                .addGroup(jPanel1Layout
                                                                                                .createSequentialGroup()
                                                                                                .addGroup(jPanel1Layout
                                                                                                                .createParallelGroup(
                                                                                                                                javax.swing.GroupLayout.Alignment.LEADING)
                                                                                                                .addComponent(jLabel1)
                                                                                                                .addComponent(jLabel2)
                                                                                                                .addComponent(jLabel4)
                                                                                                                .addComponent(jLabel5)
                                                                                                                .addComponent(jLabel6)
                                                                                                                .addComponent(jLabel14)
                                                                                                                .addComponent(jLabel16)
                                                                                                                .addComponent(jLabel17)
                                                                                                                .addComponent(jLabel18))
                                                                                                .addGap(29, 29, 29)
                                                                                                .addGroup(jPanel1Layout
                                                                                                                .createParallelGroup(
                                                                                                                                javax.swing.GroupLayout.Alignment.LEADING)
                                                                                                                .addGroup(jPanel1Layout
                                                                                                                                .createParallelGroup(
                                                                                                                                                javax.swing.GroupLayout.Alignment.TRAILING,
                                                                                                                                                false)
                                                                                                                                .addGroup(jPanel1Layout
                                                                                                                                                .createSequentialGroup()
                                                                                                                                                .addComponent(txtVlrBoleta,
                                                                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                                                                                141,
                                                                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                                                                                .addGap(198, 198,
                                                                                                                                                                198)
                                                                                                                                                .addComponent(jLabel3)
                                                                                                                                                .addPreferredGap(
                                                                                                                                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED,
                                                                                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                                                                                Short.MAX_VALUE)
                                                                                                                                                .addComponent(txtFecha,
                                                                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                                                                                161,
                                                                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                                                                                .addComponent(txtTitulo,
                                                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                                                                593,
                                                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                                                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
                                                                                                                                jPanel1Layout.createSequentialGroup()
                                                                                                                                                .addGroup(jPanel1Layout
                                                                                                                                                                .createParallelGroup(
                                                                                                                                                                                javax.swing.GroupLayout.Alignment.TRAILING)
                                                                                                                                                                .addComponent(txtMensaje5,
                                                                                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                                                                                                595,
                                                                                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                                                                                                .addGroup(jPanel1Layout
                                                                                                                                                                                .createParallelGroup(
                                                                                                                                                                                                javax.swing.GroupLayout.Alignment.TRAILING,
                                                                                                                                                                                                false)
                                                                                                                                                                                .addComponent(txtMensaje1,
                                                                                                                                                                                                javax.swing.GroupLayout.Alignment.LEADING)
                                                                                                                                                                                .addComponent(txtMensaje2,
                                                                                                                                                                                                javax.swing.GroupLayout.Alignment.LEADING,
                                                                                                                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                                                                                                                595,
                                                                                                                                                                                                Short.MAX_VALUE)
                                                                                                                                                                                .addComponent(txtMensaje3,
                                                                                                                                                                                                javax.swing.GroupLayout.Alignment.LEADING)
                                                                                                                                                                                .addComponent(txtMensaje4))
                                                                                                                                                                .addComponent(txtMensaje6,
                                                                                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                                                                                                595,
                                                                                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                                                                                                .addComponent(txtMensaje7,
                                                                                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                                                                                                595,
                                                                                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                                                                                                .addGap(17, 17, 17)))
                                                                                                .addContainerGap(
                                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                                Short.MAX_VALUE))
                                                                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
                                                                                                jPanel1Layout.createSequentialGroup()
                                                                                                                .addGap(405, 405,
                                                                                                                                405)
                                                                                                                .addComponent(jPanel9,
                                                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                                                Short.MAX_VALUE)
                                                                                                                .addGap(375, 375,
                                                                                                                                375)))));
                jPanel1Layout.setVerticalGroup(
                                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addGroup(jPanel1Layout.createSequentialGroup()
                                                                .addContainerGap()
                                                                .addGroup(jPanel1Layout.createParallelGroup(
                                                                                javax.swing.GroupLayout.Alignment.BASELINE)
                                                                                .addComponent(jLabel1)
                                                                                .addComponent(txtTitulo,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                .addPreferredGap(
                                                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addGroup(jPanel1Layout.createParallelGroup(
                                                                                javax.swing.GroupLayout.Alignment.LEADING)
                                                                                .addGroup(jPanel1Layout
                                                                                                .createParallelGroup(
                                                                                                                javax.swing.GroupLayout.Alignment.BASELINE)
                                                                                                .addComponent(txtFecha,
                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                                .addComponent(jLabel3))
                                                                                .addGroup(jPanel1Layout
                                                                                                .createParallelGroup(
                                                                                                                javax.swing.GroupLayout.Alignment.BASELINE)
                                                                                                .addComponent(jLabel2)
                                                                                                .addComponent(txtVlrBoleta,
                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)))
                                                                .addPreferredGap(
                                                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addGroup(jPanel1Layout.createParallelGroup(
                                                                                javax.swing.GroupLayout.Alignment.BASELINE)
                                                                                .addComponent(txtMensaje1,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                .addComponent(jLabel4))
                                                                .addPreferredGap(
                                                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addGroup(jPanel1Layout.createParallelGroup(
                                                                                javax.swing.GroupLayout.Alignment.BASELINE)
                                                                                .addComponent(txtMensaje2,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                .addComponent(jLabel5))
                                                                .addPreferredGap(
                                                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addGroup(jPanel1Layout.createParallelGroup(
                                                                                javax.swing.GroupLayout.Alignment.BASELINE)
                                                                                .addComponent(txtMensaje3,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                .addComponent(jLabel6))
                                                                .addPreferredGap(
                                                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addGroup(jPanel1Layout.createParallelGroup(
                                                                                javax.swing.GroupLayout.Alignment.BASELINE)
                                                                                .addComponent(txtMensaje4,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                .addComponent(jLabel14))
                                                                .addPreferredGap(
                                                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addGroup(jPanel1Layout.createParallelGroup(
                                                                                javax.swing.GroupLayout.Alignment.BASELINE)
                                                                                .addComponent(txtMensaje5,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                .addComponent(jLabel16))
                                                                .addPreferredGap(
                                                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addGroup(jPanel1Layout.createParallelGroup(
                                                                                javax.swing.GroupLayout.Alignment.BASELINE)
                                                                                .addComponent(txtMensaje6,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                .addComponent(jLabel17))
                                                                .addPreferredGap(
                                                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addGroup(jPanel1Layout.createParallelGroup(
                                                                                javax.swing.GroupLayout.Alignment.BASELINE)
                                                                                .addComponent(txtMensaje7,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                .addComponent(jLabel18))
                                                                .addPreferredGap(
                                                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addGroup(jPanel1Layout.createParallelGroup(
                                                                                javax.swing.GroupLayout.Alignment.BASELINE)
                                                                                .addComponent(txtMensaje8,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                .addComponent(jLabel20))
                                                                .addPreferredGap(
                                                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addGroup(jPanel1Layout.createParallelGroup(
                                                                                javax.swing.GroupLayout.Alignment.BASELINE)
                                                                                .addComponent(txtMensaje9,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                .addComponent(jLabel21))
                                                                .addPreferredGap(
                                                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED,
                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                Short.MAX_VALUE)
                                                                .addGroup(jPanel1Layout.createParallelGroup(
                                                                                javax.swing.GroupLayout.Alignment.BASELINE)
                                                                                .addComponent(jLabel7)
                                                                                .addComponent(txtImagen,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                .addComponent(btnFondo))
                                                                .addPreferredGap(
                                                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addGroup(jPanel1Layout.createParallelGroup(
                                                                                javax.swing.GroupLayout.Alignment.BASELINE)
                                                                                .addComponent(jLabel19)
                                                                                .addComponent(txtPremio,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                .addComponent(btnFondo2))
                                                                .addPreferredGap(
                                                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(jPanel9,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                Short.MAX_VALUE)));

                getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 6, 730, 430));

                javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
                jPanel2.setLayout(jPanel2Layout);
                jPanel2Layout.setHorizontalGroup(
                                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addGroup(jPanel2Layout.createSequentialGroup()
                                                                .addContainerGap(5, 5)
                                                                .addComponent(lblImagen1,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                225,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addContainerGap(5, Short.MAX_VALUE)));
                jPanel2Layout.setVerticalGroup(
                                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addGroup(jPanel2Layout.createSequentialGroup()
                                                                .addContainerGap(5, 5)
                                                                .addComponent(lblImagen1,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                185,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addContainerGap(5, Short.MAX_VALUE)));

                getContentPane().add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(235, 440, 237, 200));

                javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
                jPanel3.setLayout(jPanel3Layout);
                jPanel3Layout.setHorizontalGroup(
                                jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addGroup(jPanel3Layout.createSequentialGroup()
                                                                .addContainerGap(5, 5)
                                                                .addComponent(lblImagen,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                220,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addContainerGap(5, Short.MAX_VALUE)));
                jPanel3Layout.setVerticalGroup(
                                jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addGroup(jPanel3Layout.createSequentialGroup()
                                                                .addContainerGap(5, 5)
                                                                .addComponent(lblImagen,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                185,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addContainerGap(5, Short.MAX_VALUE)));

                getContentPane().add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 440, 235, 200));

                jPanel5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

                jLabel8.setFont(new java.awt.Font("Cantarell", 1, 15)); // NOI18N
                jLabel8.setText("Oportunidades");

                txtOportunidades.setText("10");
                txtOportunidades.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                txtOportunidadesActionPerformed(evt);
                        }
                });

                jLabel10.setFont(new java.awt.Font("Cantarell", 1, 15)); // NOI18N
                jLabel10.setText("Color Texto");

                bgColorTexto.add(jRBrojo);
                jRBrojo.setText("Rojo");

                bgColorTexto.add(jRBazul);
                jRBazul.setText("Azul");

                bgColorTexto.add(jRBvioleta);
                jRBvioleta.setText("Violeta");

                bgColorTexto.add(jRBcafe);
                jRBcafe.setText("Café");

                bgColorTexto.add(jRBnegro);
                jRBnegro.setText("Negro");

                bgColorTexto.add(jRBverde);
                jRBverde.setText("Verde");

                txtNumCifra1.setText("-1");
                txtNumCifra1.addFocusListener(new java.awt.event.FocusAdapter() {
                        public void focusLost(java.awt.event.FocusEvent evt) {
                                txtNumCifra1FocusLost(evt);
                        }
                });
                txtNumCifra1.addInputMethodListener(new java.awt.event.InputMethodListener() {
                        public void caretPositionChanged(java.awt.event.InputMethodEvent evt) {
                        }

                        public void inputMethodTextChanged(java.awt.event.InputMethodEvent evt) {
                                txtNumCifra1InputMethodTextChanged(evt);
                        }
                });
                txtNumCifra1.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                txtNumCifra1ActionPerformed(evt);
                        }
                });

                jLabel9.setText("Digito");

                btnCifra.add(C101);
                C101.setText("1");
                C101.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                C101ActionPerformed(evt);
                        }
                });

                btnCifra.add(C102);
                C102.setText("2");

                btnCifra.add(C103);
                C103.setText("3");

                btnCifra.add(C104);
                C104.setText("4");

                javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
                jPanel6.setLayout(jPanel6Layout);
                jPanel6Layout.setHorizontalGroup(
                                jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout
                                                                .createSequentialGroup()
                                                                .addContainerGap()
                                                                .addComponent(jLabel9)
                                                                .addPreferredGap(
                                                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED,
                                                                                31, Short.MAX_VALUE)
                                                                .addGroup(jPanel6Layout.createParallelGroup(
                                                                                javax.swing.GroupLayout.Alignment.LEADING)
                                                                                .addComponent(C104)
                                                                                .addComponent(C103)
                                                                                .addComponent(C102)
                                                                                .addComponent(C101)
                                                                                .addComponent(txtNumCifra1,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                57,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                .addContainerGap()));
                jPanel6Layout.setVerticalGroup(
                                jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addGroup(jPanel6Layout.createSequentialGroup()
                                                                .addContainerGap()
                                                                .addGroup(jPanel6Layout.createParallelGroup(
                                                                                javax.swing.GroupLayout.Alignment.BASELINE)
                                                                                .addComponent(txtNumCifra1,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                .addComponent(jLabel9,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                32,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                .addGap(18, 18, 18)
                                                                .addComponent(C101)
                                                                .addPreferredGap(
                                                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(C102)
                                                                .addPreferredGap(
                                                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(C103)
                                                                .addPreferredGap(
                                                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(C104)
                                                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                Short.MAX_VALUE)));

                jTabbedPane1.addTab("1 Cifra", jPanel6);

                jLabel12.setText("Digito");

                txtNumCifra2.setText("-1");
                txtNumCifra2.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                txtNumCifra2ActionPerformed(evt);
                        }
                });

                btnCifra.add(C201);
                C201.setText("1");

                btnCifra.add(C202);
                C202.setText("2");

                btnCifra.add(C203);
                C203.setText("3");

                javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
                jPanel7.setLayout(jPanel7Layout);
                jPanel7Layout.setHorizontalGroup(
                                jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addGroup(jPanel7Layout.createSequentialGroup()
                                                                .addContainerGap()
                                                                .addComponent(jLabel12)
                                                                .addPreferredGap(
                                                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addGroup(jPanel7Layout.createParallelGroup(
                                                                                javax.swing.GroupLayout.Alignment.LEADING)
                                                                                .addComponent(C201)
                                                                                .addComponent(txtNumCifra2,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                57,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                .addComponent(C202)
                                                                                .addComponent(C203))
                                                                .addContainerGap(31, Short.MAX_VALUE)));
                jPanel7Layout.setVerticalGroup(
                                jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addGroup(jPanel7Layout.createSequentialGroup()
                                                                .addContainerGap()
                                                                .addGroup(jPanel7Layout.createParallelGroup(
                                                                                javax.swing.GroupLayout.Alignment.BASELINE)
                                                                                .addComponent(jLabel12,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                32,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                .addComponent(txtNumCifra2,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                .addPreferredGap(
                                                                                javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                                .addComponent(C201)
                                                                .addPreferredGap(
                                                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(C202)
                                                                .addPreferredGap(
                                                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(C203)
                                                                .addContainerGap(38, Short.MAX_VALUE)));

                jTabbedPane1.addTab("2 Cifras", jPanel7);

                jLabel13.setText("Digito");

                txtNumCifra3.setText("-1");
                txtNumCifra3.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                txtNumCifra3ActionPerformed(evt);
                        }
                });

                btnCifra.add(C301);
                C301.setText("1");

                btnCifra.add(C302);
                C302.setText("2");

                javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
                jPanel8.setLayout(jPanel8Layout);
                jPanel8Layout.setHorizontalGroup(
                                jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addGroup(jPanel8Layout.createSequentialGroup()
                                                                .addContainerGap()
                                                                .addComponent(jLabel13)
                                                                .addPreferredGap(
                                                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addGroup(jPanel8Layout.createParallelGroup(
                                                                                javax.swing.GroupLayout.Alignment.LEADING)
                                                                                .addComponent(C301)
                                                                                .addComponent(txtNumCifra3,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                57,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                .addComponent(C302))
                                                                .addContainerGap(31, Short.MAX_VALUE)));
                jPanel8Layout.setVerticalGroup(
                                jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addGroup(jPanel8Layout.createSequentialGroup()
                                                                .addContainerGap()
                                                                .addGroup(jPanel8Layout.createParallelGroup(
                                                                                javax.swing.GroupLayout.Alignment.BASELINE)
                                                                                .addComponent(jLabel13,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                32,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                .addComponent(txtNumCifra3,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                .addPreferredGap(
                                                                                javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                                .addComponent(C301)
                                                                .addPreferredGap(
                                                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(C302)
                                                                .addContainerGap(67, Short.MAX_VALUE)));

                jTabbedPane1.addTab("3 Cifras", jPanel8);

                jLabel11.setText("Documento PDF");

                txtFilePDF.setEditable(false);
                txtFilePDF.setText("/Boletas/Pdf/prueba.pdf");

                btnFondo1.setText("Generar");
                btnFondo1.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                btnFondo1ActionPerformed(evt);
                        }
                });

                jComboBoxReporte.setModel(new javax.swing.DefaultComboBoxModel<>(
                                new String[] { "12 Boletas - Carta H (3x4)", "21 Boletas - Carta V (3x7)", "15 Boletas - Carta V (3x5)", "6 Boletas - Vertical 10 Oport. (3x2)" }));
                jComboBoxReporte.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                jComboBoxReporteActionPerformed(evt);
                        }
                });

                jLabel15.setFont(new java.awt.Font("Cantarell", 1, 15)); // NOI18N
                jLabel15.setText("Reporte");

                javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
                jPanel5.setLayout(jPanel5Layout);
                jPanel5Layout.setHorizontalGroup(
                                jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addGroup(jPanel5Layout.createSequentialGroup()
                                                                .addContainerGap()
                                                                .addGroup(jPanel5Layout.createParallelGroup(
                                                                                javax.swing.GroupLayout.Alignment.LEADING)
                                                                                .addGroup(jPanel5Layout.createSequentialGroup()
                                                                                                .addComponent(jLabel15)
                                                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                                                .addComponent(jComboBoxReporte,
                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE, 220,
                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                                .addGroup(jPanel5Layout.createSequentialGroup()
                                                                                                .addComponent(jLabel11)
                                                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED,
                                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                                                .addComponent(btnFondo1,
                                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                                Short.MAX_VALUE))
                                                                                .addComponent(txtFilePDF,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE, 318,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                .addComponent(jLabel8)
                                                                                .addComponent(txtOportunidades,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE, 57,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                .addGroup(jPanel5Layout.createSequentialGroup()
                                                                                                .addComponent(jTabbedPane1,
                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE, 139,
                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                                                .addGroup(jPanel5Layout.createParallelGroup(
                                                                                                                javax.swing.GroupLayout.Alignment.LEADING)
                                                                                                                .addComponent(jRBcafe)
                                                                                                                .addComponent(jRBnegro)
                                                                                                                .addComponent(jRBverde)
                                                                                                                .addComponent(jRBvioleta)
                                                                                                                .addComponent(jLabel10)
                                                                                                                .addComponent(jRBrojo)
                                                                                                                .addComponent(jRBazul))))
                                                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
                jPanel5Layout.setVerticalGroup(
                                jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addGroup(jPanel5Layout.createSequentialGroup()
                                                                .addContainerGap()
                                                                .addGroup(jPanel5Layout.createParallelGroup(
                                                                                javax.swing.GroupLayout.Alignment.BASELINE)
                                                                                .addComponent(jLabel15)
                                                                                .addComponent(jComboBoxReporte,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addGroup(jPanel5Layout.createParallelGroup(
                                                                                javax.swing.GroupLayout.Alignment.BASELINE)
                                                                                .addComponent(btnFondo1)
                                                                                .addComponent(jLabel11))
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(txtFilePDF,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addGap(10, 10, 10)
                                                                .addComponent(jLabel8)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(txtOportunidades,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addGroup(jPanel5Layout.createParallelGroup(
                                                                                javax.swing.GroupLayout.Alignment.LEADING)
                                                                                .addComponent(jTabbedPane1,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE, 234,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                .addGroup(jPanel5Layout.createSequentialGroup()
                                                                                                .addComponent(jLabel10)
                                                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                                                .addComponent(jRBrojo)
                                                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                                                .addComponent(jRBazul)
                                                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                                                .addComponent(jRBvioleta)
                                                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                                                .addComponent(jRBverde)
                                                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                                                .addComponent(jRBnegro)
                                                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                                                .addComponent(jRBcafe)))
                                                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

                getContentPane().add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(736, 6, 330, 450));

                pack();
        }// </editor-fold>//GEN-END:initComponents

        private void jComboBoxReporteActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jComboBoxReporteActionPerformed
                // TODO add your handling code here:
        }// GEN-LAST:event_jComboBoxReporteActionPerformed

        private void txtFechaActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_txtFechaActionPerformed
                // TODO add your handling code here:
        }// GEN-LAST:event_txtFechaActionPerformed

        private void btnFondo1ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnFondo1ActionPerformed

                // Advertir si no hay rangos de premio definidos
                if (modeloRangos != null && modeloRangos.getRowCount() == 0) {
                        int resp = JOptionPane.showConfirmDialog(this,
                                        "No hay rangos de premio definidos.\n"
                                                        + "Los QR de premio mostrarán 'Sin Premio'.\n¿Continuar?",
                                        "Sin Rangos de Premio", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                        if (resp != JOptionPane.YES_OPTION) return;
                }

                Date objDate = new Date();

                String strDateFormatFile = "ddMMYYYY_HHmmss";
                SimpleDateFormat objSDF2 = new SimpleDateFormat(strDateFormatFile);
                txtFilePDF.setText(objSDF2.format(objDate) + ".pdf");

                try {
                        int nroOportunidades = Integer.parseInt(this.txtOportunidades.getText());
                        if (nroOportunidades >= 0 && nroOportunidades <= 10) {
                                this.generarNumerosAleatorios(nroOportunidades);
                        } else {
                                JOptionPane.showMessageDialog(this, "Digite Nro Oportunidades entre [3 y 10]",
                                                "Informacion",
                                                JOptionPane.INFORMATION_MESSAGE);
                        }
                } catch (FileNotFoundException | BadElementException ex) {
                        Logger.getLogger(Generador_Boletas.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                        Logger.getLogger(Generador_Boletas.class.getName()).log(Level.SEVERE, null, ex);
                }

        }// GEN-LAST:event_btnFondo1ActionPerformed

        private void btnFondoActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnFondoActionPerformed
                // TODO add your handling code here:

                JFileChooser selectorArchivos = new JFileChooser();
                selectorArchivos.setCurrentDirectory(new File(AccesoAleatorio.getRutaImagenes()));
                selectorArchivos.setFileSelectionMode(JFileChooser.FILES_ONLY);

                int resultado = selectorArchivos.showOpenDialog(this);
                if (resultado == JFileChooser.APPROVE_OPTION) {
                        File fichero = selectorArchivos.getSelectedFile();
                        txtImagen.setText(fichero.getPath());
                        txtPremio.setText(fichero.getPath());
                }

                String path = this.txtImagen.getText();
                lblImagen.setIcon(iconEscalada(path, 220, 185));
                String pathPremio = this.txtPremio.getText();
                lblImagen1.setIcon(iconEscalada(pathPremio, 225, 185));

        }// GEN-LAST:event_btnFondoActionPerformed

        private void txtNumCifra1ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_txtNumCifra1ActionPerformed
                // TODO add your handling code here:
        }// GEN-LAST:event_txtNumCifra1ActionPerformed

        private void txtNumCifra2ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_txtNumCifra2ActionPerformed
                // TODO add your handling code here:
        }// GEN-LAST:event_txtNumCifra2ActionPerformed

        private void txtNumCifra3ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_txtNumCifra3ActionPerformed
                // TODO add your handling code here:
        }// GEN-LAST:event_txtNumCifra3ActionPerformed

        private void txtNumCifra1InputMethodTextChanged(java.awt.event.InputMethodEvent evt) {// GEN-FIRST:event_txtNumCifra1InputMethodTextChanged
                // TODO add your handling code here:

        }// GEN-LAST:event_txtNumCifra1InputMethodTextChanged

        private void txtNumCifra1FocusLost(java.awt.event.FocusEvent evt) {// GEN-FIRST:event_txtNumCifra1FocusLost
                // TODO add your handling code here:
        }// GEN-LAST:event_txtNumCifra1FocusLost

        private void C101ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_C101ActionPerformed
                // TODO add your handling code here:
        }// GEN-LAST:event_C101ActionPerformed

        private void txtMensaje2ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_txtMensaje2ActionPerformed
                // TODO add your handling code here:

                // No se paga el bono si presenta Alteraciones o Perforaciones.
        }// GEN-LAST:event_txtMensaje2ActionPerformed

        private void btnFondo2ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnFondo2ActionPerformed
                // TODO add your handling code here:

                JFileChooser selectorArchivos = new JFileChooser();
                selectorArchivos.setCurrentDirectory(new File(AccesoAleatorio.getRutaImagenes()));
                selectorArchivos.setFileSelectionMode(JFileChooser.FILES_ONLY);

                int resultado = selectorArchivos.showOpenDialog(this);
                if (resultado == JFileChooser.APPROVE_OPTION) {
                        File fichero = selectorArchivos.getSelectedFile();
                        txtPremio.setText(fichero.getPath());
                }

                String pathPremio = this.txtPremio.getText();
                lblImagen1.setIcon(iconEscalada(pathPremio, 225, 185));
        }// GEN-LAST:event_btnFondo2ActionPerformed

        private void txtOportunidadesActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_txtOportunidadesActionPerformed
                // TODO add your handling code here:
        }// GEN-LAST:event_txtOportunidadesActionPerformed

        /**
         * @param args the command line arguments
         * @throws java.io.IOException
         */
        public static void main(String args[]) throws IOException {

                try {
                        for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager
                                        .getInstalledLookAndFeels()) {
                                if ("Nimbus".equals(info.getName())) {
                                        javax.swing.UIManager.setLookAndFeel(info.getClassName());
                                        break;
                                }
                        }
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                                | javax.swing.UnsupportedLookAndFeelException ex) {
                        java.util.logging.Logger.getLogger(Generador_Boletas.class.getName()).log(
                                        java.util.logging.Level.SEVERE,
                                        null, ex);
                }

                if (!AccesoAleatorio.buscarFile(new File(AccesoAleatorio.getRutaFiledb()))) {
                        System.out.print("Error de Conexion a Base de Datos");
                        System.exit(0);
                }

                /* Create and display the form */
                java.awt.EventQueue.invokeLater(() -> {
                        new Generador_Boletas().setVisible(true);
                });
        }

        // Variables declaration - do not modify//GEN-BEGIN:variables
        private javax.swing.JRadioButton C101;
        private javax.swing.JRadioButton C102;
        private javax.swing.JRadioButton C103;
        private javax.swing.JRadioButton C104;
        private javax.swing.JRadioButton C201;
        private javax.swing.JRadioButton C202;
        private javax.swing.JRadioButton C203;
        private javax.swing.JRadioButton C301;
        private javax.swing.JRadioButton C302;
        private javax.swing.ButtonGroup bgColorTexto;
        private javax.swing.ButtonGroup btnCifra;
        private javax.swing.JButton btnFondo;
        private javax.swing.JButton btnFondo1;
        private javax.swing.JButton btnFondo2;
        private javax.swing.JComboBox<String> jComboBoxReporte;
        private javax.swing.JLabel jLabel1;
        private javax.swing.JLabel jLabel10;
        private javax.swing.JLabel jLabel11;
        private javax.swing.JLabel jLabel12;
        private javax.swing.JLabel jLabel13;
        private javax.swing.JLabel jLabel14;
        private javax.swing.JLabel jLabel15;
        private javax.swing.JLabel jLabel16;
        private javax.swing.JLabel jLabel17;
        private javax.swing.JLabel jLabel18;
        private javax.swing.JLabel jLabel19;
        private javax.swing.JLabel jLabel2;
        private javax.swing.JLabel jLabel20;
        private javax.swing.JLabel jLabel21;
        private javax.swing.JLabel jLabel3;
        private javax.swing.JLabel jLabel4;
        private javax.swing.JLabel jLabel5;
        private javax.swing.JLabel jLabel6;
        private javax.swing.JLabel jLabel7;
        private javax.swing.JLabel jLabel8;
        private javax.swing.JLabel jLabel9;
        private javax.swing.JPanel jPanel1;
        private javax.swing.JPanel jPanel2;
        private javax.swing.JPanel jPanel3;
        private javax.swing.JPanel jPanel5;
        private javax.swing.JPanel jPanel6;
        private javax.swing.JPanel jPanel7;
        private javax.swing.JPanel jPanel8;
        private javax.swing.JPanel jPanel9;
        private javax.swing.JRadioButton jRBazul;
        private javax.swing.JRadioButton jRBcafe;
        private javax.swing.JRadioButton jRBnegro;
        private javax.swing.JRadioButton jRBrojo;
        private javax.swing.JRadioButton jRBverde;
        private javax.swing.JRadioButton jRBvioleta;
        private javax.swing.JTabbedPane jTabbedPane1;
        private javax.swing.JLabel lblImagen;
        private javax.swing.JLabel lblImagen1;
        private javax.swing.JTextField txtFecha;
        private javax.swing.JTextField txtFilePDF;
        private javax.swing.JTextField txtImagen;
        private javax.swing.JTextField txtMensaje1;
        private javax.swing.JTextField txtMensaje2;
        private javax.swing.JTextField txtMensaje3;
        private javax.swing.JTextField txtMensaje4;
        private javax.swing.JTextField txtMensaje5;
        private javax.swing.JTextField txtMensaje6;
        private javax.swing.JTextField txtMensaje7;
        private javax.swing.JTextField txtMensaje8;
        private javax.swing.JTextField txtMensaje9;
        private javax.swing.JTextField txtNumCifra1;
        private javax.swing.JTextField txtNumCifra2;
        private javax.swing.JTextField txtNumCifra3;
        private javax.swing.JTextField txtOportunidades;
        private javax.swing.JTextField txtPremio;
        private javax.swing.JTextField txtTitulo;
        private javax.swing.JTextField txtVlrBoleta;
        // End of variables declaration//GEN-END:variables
}
