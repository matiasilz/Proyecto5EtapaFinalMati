package NodosAST;

import analizadorLexico.Token;
import analizadorSemantico.*;

public class NodoAsignacion implements NodoExpInterface {
    private NodoBase izquierda;
    private NodoBase derecha;
    private Token token;
    private TablaDeSimbolos tablaDeSimbolos;

    public NodoAsignacion(Token token, NodoBase izquierda, NodoBase derecha, TablaDeSimbolos tablaDeSimbolos) {
        this.izquierda = izquierda;
        this.derecha = derecha;
        this.token = token;
        this.tablaDeSimbolos = tablaDeSimbolos;
    }

    public Tipo check() throws SemanticoExcepcion {
        Tipo tipoIzq = izquierda.check();
        Tipo tipoDer = derecha.check();
        if (!(izquierda instanceof NodoAcceso))
            throw new SemanticoExcepcion(token, "Error en la sentencia de asignación: La variable de la izquierda no es una variable");

        if (tipoIzq instanceof TipoPrimitivo) {
            if (tipoDer instanceof TipoPrimitivo) {
                if (tipoDer.getToken().getIdentificador().equals(tipoIzq.getToken().getIdentificador())) {
                } else {
                    throw new SemanticoExcepcion(token, "Error en la sentencia de asignación: El tipo de la expresión de la derecha no es compatible con el tipo de la variable de la izquierda.");
                }
            } else {
                throw new SemanticoExcepcion(token, "Error en la sentencia de asignación: El tipo de la expresión de la derecha no es compatible con el tipo de la variable de la izquierda.");
            }
        } else {
            if (tipoDer instanceof TipoReferencia) {
                Clase der = tablaDeSimbolos.getClaseOInterface(tipoDer.getToken().getLexema());
                Clase izq = tablaDeSimbolos.getClaseOInterface(tipoIzq.getToken().getLexema());
                if (tipoDer.conformaCon(tipoIzq) || der.esSubTipo(izq)) {
                } else {
                    throw new SemanticoExcepcion(token, "Error en la sentencia de asignación: El tipo de la expresión de la derecha no es compatible con el tipo de la variable de la izquierda.");
                }
            } else {
                throw new SemanticoExcepcion(token, "Error en la sentencia de asignación: El tipo de la expresión de la derecha no es compatible con el tipo de la variable de la izquierda.");
            }
        }
        if (!((NodoAcceso) izquierda).alFinalEsAsignable()) {
            throw new SemanticoExcepcion(token, "Error en la sentencia de asignación: La variable de la izquierda no es asignable");
        }
        NodoAcceso nodoAcceso = (NodoAcceso) izquierda;
        nodoAcceso.setEstaALaIzquierdaDeAsignacion();
        return tipoIzq;

    }

    @Override
    public Tipo getType() {
        return null;
    }

    @Override
    public Token getToken() {
        return token;
    }

    @Override
    public void generarCodigo() {
        if (derecha != null)
            derecha.generarCodigo();
        if (izquierda != null)
            izquierda.generarCodigo();
    }
}