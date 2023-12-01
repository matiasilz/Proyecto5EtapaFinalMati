package analizadorSemantico;

import NodosAST.NodoBloque;
import analizadorLexico.Token;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class Metodo {

    private HashMap<String, Parametro> parametros;
    private String nombre;
    private Tipo tipoRetorno;
    private Token tkMetodo;
    private int offset;
    private boolean tieneOffset;
    public ArrayList<Parametro> parametrosEnOrden;
    private boolean esConstructor = false;

    private TablaDeSimbolos tablaDeSimbolos;
    private NodoBloque bloqueMetodo;
    private boolean statico;


    public Metodo(Token tkMetodo, Tipo tipo, boolean statico, TablaDeSimbolos tablaDeSimbolos) {
        this.nombre = tkMetodo.getLexema();
        this.tipoRetorno = tipo;
        parametros = new HashMap<String, Parametro>();
        this.statico = statico;
        parametrosEnOrden = new ArrayList<>();
        this.tkMetodo = tkMetodo;
        this.tieneOffset = false;
        this.tablaDeSimbolos = tablaDeSimbolos;
    }

    public void addParametro(Parametro p) throws SemanticoExcepcion {
        if (!parametros.containsKey(p.getNombre())) {
            parametros.put(p.getNombre(), p);
            parametrosEnOrden.add(p);
        } else
            throw new SemanticoExcepcion(p.getTkParametro(), "No puede haber mas de un parametro con el mismo nombre en el Metodo");
    }

    public int getOffset() {
        return offset;
    }

    public boolean isStatico() {
        return statico;
    }

    public HashMap<String, Parametro> getParametrosMap() {
        return parametros;
    }

    public void setStatico(boolean statico) {
        this.statico = statico;
    }

    public String getNombre() {
        return nombre;
    }

    public Tipo getTipo() {
        return tipoRetorno;
    }

    public ArrayList<Parametro> getParametrosEnOrden() {
        return parametrosEnOrden;
    }

    public String toString() {
        return "Metodo: " + nombre + " Tipo: " + tipoRetorno.getToken().getLexema();
    }

    public HashMap<String, Parametro> getParametros() {
        return parametros;
    }

    public Token getTkMetodo() {
        return tkMetodo;
    }

    public NodoBloque getBloqueMetodo() {
        return bloqueMetodo;
    }

    public void realizarChequeos(TablaDeSimbolos ts) throws SemanticoExcepcion {
        if (tipoRetorno.getToken().getIdentificador().equals("idClase"))
            if (!ts.clases.containsKey(tipoRetorno.getToken().getLexema()) && !ts.interfaces.containsKey(tipoRetorno.getToken().getLexema())) {
                throw new SemanticoExcepcion(tipoRetorno.getToken(), "El tipo no esta declarado");
            }
        for (Parametro par : parametros.values()) {
            if (par.getTipo().getToken().getIdentificador().equals("idClase"))
                if (!ts.clases.containsKey(par.getTipo().getToken().getLexema()) && !ts.interfaces.containsKey(par.getTipo().getToken().getLexema())) {
                    throw new SemanticoExcepcion(par.getTipo().getToken(), "El tipo no esta declarado");
                }
            if (par.getTipo().getToken().getIdentificador().equals("pr_void"))
                throw new SemanticoExcepcion(par.getTipo().getToken(), "El tipo no puede ser void");
        }
    }

    public void check() throws SemanticoExcepcion {
        if (bloqueMetodo != null)
            bloqueMetodo.check();
    }

    public void setBloque(NodoBloque nodoB) {
        this.bloqueMetodo = nodoB;
    }

    public void generarCodigo() {

        tablaDeSimbolos.codigoGenerado.add("LOADFP");
        tablaDeSimbolos.codigoGenerado.add("LOADSP");
        tablaDeSimbolos.codigoGenerado.add("STOREFP");
        setOffsetsParametros();
        switch (nombre) {
            case "debugPrint": {
                tablaDeSimbolos.codigoGenerado.add("LOAD 3");
                tablaDeSimbolos.codigoGenerado.add("IPRINT");
                tablaDeSimbolos.codigoGenerado.add("PRNLN");
                tablaDeSimbolos.codigoGenerado.add("STOREFP");
                tablaDeSimbolos.codigoGenerado.add("RET 1");
                break;
            }
            case "println": {
                tablaDeSimbolos.codigoGenerado.add("IPRINT");
                tablaDeSimbolos.codigoGenerado.add("PRNLN");
                tablaDeSimbolos.codigoGenerado.add("STOREFP");
                tablaDeSimbolos.codigoGenerado.add("RET 0");
                break;
            }
            case "printB": {
                tablaDeSimbolos.codigoGenerado.add("LOAD 3");
                tablaDeSimbolos.codigoGenerado.add("BPRINT");
                tablaDeSimbolos.codigoGenerado.add("STOREFP");
                tablaDeSimbolos.codigoGenerado.add("RET 1");
                break;
            }
            case "printC": {
                tablaDeSimbolos.codigoGenerado.add("LOAD 3");
                tablaDeSimbolos.codigoGenerado.add("CPRINT");
                tablaDeSimbolos.codigoGenerado.add("STOREFP");
                tablaDeSimbolos.codigoGenerado.add("RET 1");
                break;
            }
            case "printI": {
                tablaDeSimbolos.codigoGenerado.add("LOAD 3");
                tablaDeSimbolos.codigoGenerado.add("IPRINT");
                tablaDeSimbolos.codigoGenerado.add("STOREFP");
                tablaDeSimbolos.codigoGenerado.add("RET 1");
                break;
            }
            case "printS": {
                tablaDeSimbolos.codigoGenerado.add("LOAD 3");
                tablaDeSimbolos.codigoGenerado.add("SPRINT");
                tablaDeSimbolos.codigoGenerado.add("STOREFP");
                tablaDeSimbolos.codigoGenerado.add("RET 1");
                break;
            }
            case "read": {
                tablaDeSimbolos.codigoGenerado.add("READ");
                tablaDeSimbolos.codigoGenerado.add("STOREFP");
                tablaDeSimbolos.codigoGenerado.add("RET 1");
                break;
            }
            case "printBln": {
                tablaDeSimbolos.codigoGenerado.add("LOAD 3");
                tablaDeSimbolos.codigoGenerado.add("BPRINT");
                tablaDeSimbolos.codigoGenerado.add("PRNLN");
                tablaDeSimbolos.codigoGenerado.add("STOREFP");
                tablaDeSimbolos.codigoGenerado.add("RET 1");
                break;
            }
            case "printCln": {
                tablaDeSimbolos.codigoGenerado.add("LOAD 3");
                tablaDeSimbolos.codigoGenerado.add("CPRINT");
                tablaDeSimbolos.codigoGenerado.add("PRNLN");
                tablaDeSimbolos.codigoGenerado.add("STOREFP");
                tablaDeSimbolos.codigoGenerado.add("RET 1");
                break;
            }
            case "printIln": {
                tablaDeSimbolos.codigoGenerado.add("LOAD 3");
                tablaDeSimbolos.codigoGenerado.add("IPRINT");
                tablaDeSimbolos.codigoGenerado.add("PRNLN");
                tablaDeSimbolos.codigoGenerado.add("STOREFP");
                tablaDeSimbolos.codigoGenerado.add("RET 1");
                break;
            }
            case "printSln": {
                tablaDeSimbolos.codigoGenerado.add("LOAD 3");
                tablaDeSimbolos.codigoGenerado.add("SPRINT");
                tablaDeSimbolos.codigoGenerado.add("PRNLN");
                tablaDeSimbolos.codigoGenerado.add("STOREFP");
                tablaDeSimbolos.codigoGenerado.add("RET 1");
                break;
            }
            default: {
                if (bloqueMetodo != null)
                    bloqueMetodo.generarCodigo();
                tablaDeSimbolos.codigoGenerado.add("STOREFP");
                if (isStatico())
                    tablaDeSimbolos.codigoGenerado.add("RET " + (parametrosEnOrden.size()));
                else
                    tablaDeSimbolos.codigoGenerado.add("RET " + (parametrosEnOrden.size() + 1));
                break;
            }

        }

    }
    private void setOffsetsParametros() {
        int i;
        if (isStatico())
            i = parametrosEnOrden.size() + 2;
        else
            i = parametrosEnOrden.size() + 3;
        for (Parametro p : parametrosEnOrden) {
            p.setOffset(i);
            i--;
        }
        System.out.println("\n");
        System.out.println("Printing offsets of the params for method " + nombre);
        for (Parametro p : parametrosEnOrden) {
            System.out.println(p.getNombre() + " " + p.getOffset());
        }
    }

    public String generarEtiqueta() {
        return nombre;
    }

    public boolean isTieneOffset() {
        return tieneOffset;
    }

    public void setOffset(int offSetVt) {
        this.offset = offSetVt;
        this.tieneOffset = true;
    }

}