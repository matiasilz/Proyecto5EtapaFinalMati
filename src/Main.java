import analizadorLexico.AnalizadorLexico;
import analizadorLexico.GestorDeArchivo;
import analizadorLexico.LexicoExcepcion;
import analizadorLexico.Token;
import analizadorSintactico.AnalizadorSintactico;
import analizadorSintactico.SintacticoExcepcion;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        List<Exception> listaExcepciones = new ArrayList<>();
        AnalizadorSintactico analizadorSintactico;
        AnalizadorLexico analizadorLexico;
        try {
            if (args.length <= 0){
                System.out.println("No se encontro parametro");
            }else{
                GestorDeArchivo gestorDeArchivo;
                String salida = args[1];
                File archivo = new File(args[0]);
                gestorDeArchivo = new GestorDeArchivo(archivo);
                analizadorLexico = new AnalizadorLexico(gestorDeArchivo);
                analizadorSintactico = new AnalizadorSintactico(analizadorLexico);
                inicializarSintactico(analizadorSintactico,listaExcepciones,salida);
            }
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("Se ha producido un error ajeno al compilador");
        }
        if(listaExcepciones.isEmpty()){
            System.out.println("[SinErrores]");
        }
        else{
            for(Exception se : listaExcepciones){
                System.out.println(se.toString());
                se.printStackTrace();
            }

        }
    }
    private static void inicializarSintactico(AnalizadorSintactico analizadorSintactico,List<Exception> listaExcepciones,String salida){
        try {
            analizadorSintactico.inicial(salida);
        }catch(Exception e){
            listaExcepciones.add(e);
        }
    }

}
