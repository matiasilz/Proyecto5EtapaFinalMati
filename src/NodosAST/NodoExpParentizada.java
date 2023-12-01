package NodosAST;

import analizadorLexico.Token;
import analizadorSemantico.SemanticoExcepcion;
import analizadorSemantico.TablaDeSimbolos;
import analizadorSemantico.Tipo;

public class NodoExpParentizada extends NodoAcceso {

    private Token token;
    private NodoBase nodoExp;
    private TablaDeSimbolos tablaDeSimbolos;
    public NodoExpParentizada(Token token, NodoBase nodoExp, TablaDeSimbolos tablaDeSimbolos) {
        super(token, tablaDeSimbolos, null);
        this.token = token;
        this.nodoExp = nodoExp;
        this.tablaDeSimbolos = tablaDeSimbolos;
    }
    @Override
    public Tipo check() throws SemanticoExcepcion {
            Tipo tipoExp = nodoExp.check();
            if(encadenadoHijo != null) {
                encadenadoHijo.setEncadenadoPadre((NodoAcceso) nodoExp);
                tipoExp = encadenadoHijo.check();
            }
            return tipoExp;
    }
    public void generarCodigo() {
        nodoExp.generarCodigo();
        if(encadenadoHijo != null)
            encadenadoHijo.generarCodigo();
    }


    public Token getToken() {
        return token;
    }
}
