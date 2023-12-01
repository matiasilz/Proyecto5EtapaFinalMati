package NodosAST;

import analizadorLexico.Token;
import analizadorSemantico.SemanticoExcepcion;
import analizadorSemantico.TablaDeSimbolos;
import analizadorSemantico.Tipo;
import analizadorSemantico.TipoPrimitivo;

import java.util.Arrays;

public class NodoExpUnaria implements NodoBase {

    private NodoBase expresion;
    private TablaDeSimbolos tablaDeSimbolos;
    private Token token;
    public NodoExpUnaria(Token token, NodoBase expresion, TablaDeSimbolos tablaDeSimbolos) {
        this.expresion = expresion;
        this.token = token;
        this.tablaDeSimbolos = tablaDeSimbolos;
    }
    public Tipo check() throws SemanticoExcepcion {
        TipoPrimitivo tipoPrimitivo = null;
        Tipo tipoExp = expresion.check();
        if(token!=null){
            if(Arrays.asList("+","-").contains(token.getLexema())) {
                Token tkInt = new Token("int", "int", 0);
                tipoPrimitivo = new TipoPrimitivo(tkInt, tablaDeSimbolos);
            }
            else if(Arrays.asList("!").contains(token.getLexema())) {
                Token tkBoolean = new Token("boolean", "boolean", 0);
                tipoPrimitivo = new TipoPrimitivo(tkBoolean, tablaDeSimbolos);
            }
            if(!tipoPrimitivo.conformaCon(tipoExp)){
                throw new SemanticoExcepcion(token, "El tipo de la expresion no coincide con el operador unario");
            }

        }
        else{
            tipoPrimitivo = (TipoPrimitivo) expresion.check();
        }
        return tipoPrimitivo;
    }
    @Override
    public Tipo getType() {
        return null;
    }

    public Token getToken() {
        return token;
    }

    @Override
    public void generarCodigo() {
        expresion.generarCodigo();
        if(token!=null){
            switch (token.getLexema()){
                case "-":
                    tablaDeSimbolos.codigoGenerado.add("NEG");
                    break;
                case "!":
                    tablaDeSimbolos.codigoGenerado.add("NOT");
                    break;
            }
        }
    }
}