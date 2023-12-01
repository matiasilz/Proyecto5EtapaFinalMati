package NodosAST;

import analizadorLexico.Token;
import analizadorSemantico.SemanticoExcepcion;
import analizadorSemantico.Tipo;

public interface NodoBase {
    public Tipo check() throws SemanticoExcepcion;
    public Tipo getType();
    public Token getToken();
    public void generarCodigo();
}