/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Modelo;
import java.io.Serializable;
import Controlador.AccesoAleatorio;
import java.io.File;
import java.io.IOException;

/**
 *
 * @author Luis
 */
public class Tabla implements Serializable {
    

      // 2 bytes
    private int numTabla;
    private boolean activo;
    // Total bytes 102 + ( 2 ) correccion errores  
    private int n1;
    private int n2;
    private int n3;
    private int n4;
    private int n5;
    private int n6;
    private int n7;
    private int n8;
    private int n9;
    private int n10;
    private int n11;
    private int n12;
    private int n13;
    private int n14;
    private int n15;
    private int n16;
    private int n17;
    private int n18;
    private int n19;
    private int n20;
    private int n21;
    private int n22;
    private int n23;
    private int n24;
    private int n25;
    
    
    public Tabla() throws IOException {
        numTabla = -1;
        activo = true;
        n1=0;
        n2=0;
        n3=0;
        n4=0;
        n5=0;
        n6=0;
        n7=0;
        n8=0;
        n9=0;
        n10=0;
        n11=0;
        n12=0;
        n13=0;
        n14=0;
        n15=0;
        n16=0;
        n17=0;
        n18=0;
        n19=0;
        n20=0;
        n21=0;
        n22=0;
        n23=0;
        n24=0;
        n25=0;
        
        if (!AccesoAleatorio.buscarFile(new File (AccesoAleatorio.getRutaFileDB()+"tablas.db")) || !AccesoAleatorio.buscarFile(new File (AccesoAleatorio.getRutaFiledb()))) {
            System.out.print("Error de Conexion a Base de Datos");
            System.exit(0);
        }

    }

    public Tabla(int numTabla, boolean activo, int n1, int n2, int n3, int n4, int n5, 
                                               int n6, int n7, int n8, int n9, int n10, 
                                               int n11, int n12, int n13, int n14, int n15, 
                                               int n16, int n17, int n18, int n19, int n20, 
                                               int n21, int n22, int n23, int n24, int n25) {
        this.numTabla = numTabla;
        this.activo = activo;
        this.n1 = n1;
        this.n2 = n2;
        this.n3 = n3;
        this.n4 = n4;
        this.n5 = n5;
        this.n6 = n6;
        this.n7 = n7;
        this.n8 = n8;
        this.n9 = n9;
        this.n10 = n10;
        this.n11 = n11;
        this.n12 = n12;
        this.n13 = n13;
        this.n14 = n14;
        this.n15 = n15;
        this.n16 = n16;
        this.n17 = n17;
        this.n18 = n18;
        this.n19 = n19;
        this.n20 = n20;
        this.n21 = n21;
        this.n22 = n22;
        this.n23 = n23;
        this.n24 = n24;
        this.n25 = n25;
    }


    


    public void setNumTabla(int numTabla) {
        this.numTabla = numTabla;
    }

    public int getNumTabla() {
        return numTabla;
    }
   
    public int getTamaño() {
        return 104;
    }

    public int getN1() {
        return n1;
    }

    public void setN1(int n1) {
        this.n1 = n1;
    }

    public int getN2() {
        return n2;
    }

    public void setN2(int n2) {
        this.n2 = n2;
    }

    public int getN3() {
        return n3;
    }

    public void setN3(int n3) {
        this.n3 = n3;
    }

    public int getN4() {
        return n4;
    }

    public void setN4(int n4) {
        this.n4 = n4;
    }

    public int getN5() {
        return n5;
    }

    public void setN5(int n5) {
        this.n5 = n5;
    }

    public int getN6() {
        return n6;
    }

    public void setN6(int n6) {
        this.n6 = n6;
    }

    public int getN7() {
        return n7;
    }

    public void setN7(int n7) {
        this.n7 = n7;
    }

    public int getN8() {
        return n8;
    }

    public void setN8(int n8) {
        this.n8 = n8;
    }

    public int getN9() {
        return n9;
    }

    public void setN9(int n9) {
        this.n9 = n9;
    }

    public int getN10() {
        return n10;
    }

    public void setN10(int n10) {
        this.n10 = n10;
    }

    public int getN11() {
        return n11;
    }

    public void setN11(int n11) {
        this.n11 = n11;
    }

    public int getN12() {
        return n12;
    }

    public void setN12(int n12) {
        this.n12 = n12;
    }

    public int getN13() {
        return n13;
    }

    public void setN13(int n13) {
        this.n13 = n13;
    }

    public int getN14() {
        return n14;
    }

    public void setN14(int n14) {
        this.n14 = n14;
    }

    public int getN15() {
        return n15;
    }

    public void setN15(int n15) {
        this.n15 = n15;
    }

    public int getN16() {
        return n16;
    }

    public void setN16(int n16) {
        this.n16 = n16;
    }

    public int getN17() {
        return n17;
    }

    public void setN17(int n17) {
        this.n17 = n17;
    }

    public int getN18() {
        return n18;
    }

    public void setN18(int n18) {
        this.n18 = n18;
    }

    public int getN19() {
        return n19;
    }

    public void setN19(int n19) {
        this.n19 = n19;
    }

    public int getN20() {
        return n20;
    }

    public void setN20(int n20) {
        this.n20 = n20;
    }

    public int getN21() {
        return n21;
    }

    public void setN21(int n21) {
        this.n21 = n21;
    }

    public int getN22() {
        return n22;
    }

    public void setN22(int n22) {
        this.n22 = n22;
    }

    public int getN23() {
        return n23;
    }

    public void setN23(int n23) {
        this.n23 = n23;
    }

    public int getN24() {
        return n24;
    }

    public void setN24(int n24) {
        this.n24 = n24;
    }

    public int getN25() {
        return n25;
    }

    public void setN25(int n25) {
        this.n25 = n25;
    }
    
    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

}
