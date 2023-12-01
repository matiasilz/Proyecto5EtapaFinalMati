package NodosAST;

import analizadorLexico.Token;
import analizadorSemantico.*;

public class NodoRetorno implements NodoSentencia {
    private NodoBase expresion;
    private Metodo metodoActual;
    private Token ret;
    private TablaDeSimbolos tablaDeSimbolos;
    private NodoBloque bloquePadre;
    public NodoRetorno(Token ret, NodoBase expresion, Metodo metodoActual, TablaDeSimbolos tablaDeSimbolos, NodoBloque bloquePadre) {
        this.expresion = expresion;
        this.metodoActual = metodoActual;
        this.ret = ret;
        this.tablaDeSimbolos = tablaDeSimbolos;
        this.bloquePadre = bloquePadre;
    }

    public Tipo check() throws SemanticoExcepcion {
        Tipo tipoRet = null;
        if (metodoActual.getTipo() instanceof TipoPrimitivo && ((TipoPrimitivo) metodoActual.getTipo()).getToken().getIdentificador().equals("void")) {
            if (expresion == null) {
                Token tokenVoid = new Token("pr_void", "void", 0);
                tipoRet = new TipoPrimitivo(tokenVoid, tablaDeSimbolos);
            } else {
                throw new SemanticoExcepcion(ret, "Error en la sentencia de retorno en el método " + metodoActual.getNombre() +
                        ": Se esperaba una sentencia de retorno sin expresión.");
            }
        }
        if (expresion != null) {
            if(metodoActual.getTipo().getToken().getLexema().equals("void"))
                throw new SemanticoExcepcion(ret, "No se puede retornar en un metodo con tipo void");
            Tipo tipoExpresion = expresion.check();
            if (tipoExpresion instanceof TipoPrimitivo) {
                if (!tipoExpresion.conformaCon(metodoActual.getTipo())){
                    throw new SemanticoExcepcion(ret, "Error en la sentencia de retorno en el método " + metodoActual.getNombre() +
                            ": El tipo de la expresión de retorno no es compatible con el tipo de retorno del método.");
                }
                tipoRet = tipoExpresion;
            } else {
                //tipo expresion entonces es tipo ref, si la clase que se llama de esa manera, es subtipo con el tipo de retorno del metodo actual entonces esta bien
                if (tipoExpresion.conformaCon(metodoActual.getTipo())) {
                    tipoRet = tipoExpresion;
                } else {
                    throw new SemanticoExcepcion(ret, "Error en la sentencia de retorno en el método " + metodoActual.getNombre() +
                            ": El tipo de la expresión de retorno no es compatible con el tipo de retorno del método.");
                }
            }
        }
        return tipoRet;
    }

    public Tipo getType() {
        return null;
    }

    public Token getToken() {
        return ret;
    }

    @Override
    public void generarCodigo() {
        tablaDeSimbolos.codigoGenerado.add("FMEM "+ bloquePadre.varLocals.size() + " ; Reservo espacio para las variables locales");
        if(metodoActual.getTipo().getToken().getLexema().equals("void")){
            tablaDeSimbolos.codigoGenerado.add("STOREFP ; actualizo el FP para que apunte al RA llamador");
            if (metodoActual.isStatico()){
                tablaDeSimbolos.codigoGenerado.add("RET "+metodoActual.getParametrosEnOrden().size());
            }
            else {
                tablaDeSimbolos.codigoGenerado.add("RET "+(metodoActual.getParametrosEnOrden().size()+1));
            }
        } else{
            expresion.generarCodigo();
            if (metodoActual.isStatico()){
                tablaDeSimbolos.codigoGenerado.add("STORE "+ (metodoActual.getParametrosEnOrden().size()+3));
                tablaDeSimbolos.codigoGenerado.add("STOREFP ; actualizo el FP para que apunte al RA llamador");
                tablaDeSimbolos.codigoGenerado.add("RET "+ metodoActual.getParametrosEnOrden().size()+ " ; Se liberan " + metodoActual.getParametrosEnOrden().size() + " lugares de la pila");
            }
            else {
                tablaDeSimbolos.codigoGenerado.add("STORE "+ (metodoActual.getParametrosEnOrden().size()+4));//this
                tablaDeSimbolos.codigoGenerado.add("STOREFP");
                tablaDeSimbolos.codigoGenerado.add("RET "+ (metodoActual.getParametrosEnOrden().size()+1)+ " ; Se liberan " + (metodoActual.getParametrosEnOrden().size()+1) + " lugares de la pila");
            }

        }


    }
}