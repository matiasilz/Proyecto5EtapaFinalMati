package NodosAST;

import analizadorLexico.Token;
import analizadorSemantico.*;

public class NodoExpThis extends NodoAcceso {
    private Token tkthis;
    private Clase claseActual;
    private TablaDeSimbolos tablaDeSimbolos;
    private Metodo metodoActual;

    public Clase getClaseActual() {
        return claseActual;
    }

    public NodoExpThis(Token tk, Clase claseActual, Metodo metodoActual, TablaDeSimbolos tablaDeSimbolos, NodoBloque padre) {
        super(tk, tablaDeSimbolos, padre);
        this.tkthis = tk;
        this.claseActual = claseActual;
        this.tablaDeSimbolos = tablaDeSimbolos;
        this.metodoActual = metodoActual;
    }

    public Tipo check() throws SemanticoExcepcion {
        if (claseActual == null) {
            throw new SemanticoExcepcion(tkthis, "Uso inválido de this fuera de una clase.");
        }
        if (metodoActual.isStatico())
            throw new SemanticoExcepcion(tkthis, "Uso inválido de this en un método estático.");
        return super.check();
    }

    public void generarCodigo() {
        tablaDeSimbolos.codigoGenerado.add("LOAD 3");
        if (encadenadoHijo != null)
            encadenadoHijo.generarCodigo();
    }

    public Token getToken() {
        return tkthis;
    }
}