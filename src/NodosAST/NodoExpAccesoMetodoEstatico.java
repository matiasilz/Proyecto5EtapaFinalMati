package NodosAST;

import analizadorLexico.Token;
import analizadorSemantico.Clase;
import analizadorSemantico.SemanticoExcepcion;
import analizadorSemantico.TablaDeSimbolos;
import analizadorSemantico.Tipo;

import java.util.Collections;
import java.util.LinkedList;

public class NodoExpAccesoMetodoEstatico extends NodoAcceso {
    private Token idClase;
    private Token idMetVar;
    private LinkedList<NodoBase> argsActuales;
    private TablaDeSimbolos tablaDeSimbolos;

    public NodoExpAccesoMetodoEstatico(Token idClase, Token idMetVar, LinkedList<NodoBase> argsActuales, TablaDeSimbolos tablaDeSimbolos, NodoBloque bloque) {
        super(idMetVar, tablaDeSimbolos, bloque);
        this.idClase = idClase;
        this.idMetVar = idMetVar;
        this.argsActuales = argsActuales;
        this.tablaDeSimbolos = tablaDeSimbolos;
    }

    @Override
    public Tipo check() throws SemanticoExcepcion {
        Clase clase = tablaDeSimbolos.clases.get(idClase.getLexema());
        if (clase == null) {
            throw new SemanticoExcepcion(idMetVar, "No existe un metodo estatico en la clase " + idClase.getLexema() + " con el nombre " + idMetVar.getLexema());
        }
        if (clase.getMetodos().containsKey(idMetVar.getLexema())) {
            if (clase.getMetodos().get(idMetVar.getLexema()).isStatico()) {
                if (clase.getMetodos().get(idMetVar.getLexema()).parametrosEnOrden.size() == argsActuales.size()) {
                    for (int i = 0; i < argsActuales.size(); i++) {
                        Tipo tipoArgActual = argsActuales.get(i).check();
                        Tipo tipoArgFormal = clase.getMetodos().get(idMetVar.getLexema()).parametrosEnOrden.get(i).getTipo();
                        if (!tipoArgActual.conformaCon(tipoArgFormal)) {
                            throw new SemanticoExcepcion(idMetVar, "El tipo del argumento actual " + tipoArgActual.getToken().getLexema() + " no conforma con el tipo del argumento formal " + tipoArgFormal.getToken().getLexema());
                        }
                    }
                } else {
                    throw new SemanticoExcepcion(idMetVar, "La cantidad de argumentos actual no coincide con la cantidad de argumentos formales");
                }
            } else {
                throw new SemanticoExcepcion(idMetVar, "El metodo " + idMetVar.getLexema() + " no es estatico");
            }
        } else {
            throw new SemanticoExcepcion(idMetVar, "El metodo " + idMetVar.getLexema() + " no existe");
        }
        metodoALlamar = clase.getMetodos().get(idMetVar.getLexema());
        System.out.println("Metodo a llamar: " + metodoALlamar.getNombre() + " con tipo de retorno: " + metodoALlamar.getTipo().getToken().getLexema());
        if (encadenadoHijo != null)
            return encadenadoHijo.check();
        else{
            return clase.getMetodos().get(idMetVar.getLexema()).getTipo();
        }
    }

    public void generarCodigo() {
        System.out.println("Generando codigo de llamada a metodo estatico");
        System.out.println("Metodo a llamar: " + idMetVar.getLexema() + " en la clase " + idClase.getLexema() + " llamada en la linea " + idMetVar.getNroLinea());

        if (!metodoALlamar.getTipo().getToken().getLexema().equals("void")){
            tablaDeSimbolos.codigoGenerado.add("RMEM 1");
        }
        Collections.reverse(argsActuales);
        for (NodoBase expresion:argsActuales) {
            expresion.generarCodigo();
        }
        Collections.reverse(argsActuales);
        tablaDeSimbolos.codigoGenerado.add("PUSH "+metodoALlamar.generarEtiqueta());
        tablaDeSimbolos.codigoGenerado.add("CALL");

        if(encadenadoHijo != null){
            encadenadoHijo.generarCodigo();
        }
    }
    public Token getToken() {
        return idMetVar;
    }
}
