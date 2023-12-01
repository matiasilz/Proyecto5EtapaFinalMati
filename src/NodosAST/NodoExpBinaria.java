package NodosAST;

import analizadorLexico.Token;
import analizadorSemantico.SemanticoExcepcion;
import analizadorSemantico.TablaDeSimbolos;
import analizadorSemantico.Tipo;
import analizadorSemantico.TipoPrimitivo;

public class NodoExpBinaria implements NodoBase {
    private NodoBase nodoExpIzq;
    private NodoBase nodoExpDer;
    private Token opBin;
    private Tipo tipoOp;
    private TablaDeSimbolos tablaDeSimbolos;
    public NodoExpBinaria(Token opBin, NodoBase nodoExpIzq, NodoBase nodoExpDer,TablaDeSimbolos tablaDeSimbolos) {
        this.nodoExpIzq = nodoExpIzq;
        this.nodoExpDer = nodoExpDer;
        this.opBin = opBin;
        this.tablaDeSimbolos = tablaDeSimbolos;
    }

    public Tipo check() throws SemanticoExcepcion {
        switch (opBin.getLexema()){
            case "+":
            case "-":
            case "*":
            case "/":
            case "%":
                return checkAritmetica();
            case "<":
            case ">":
            case "<=":
            case ">=":
                return checkRelacional();
            case "==":
            case "!=":
                return checkIgualdad();
            case "&&":
            case "||":
                return checkLogica();
            default:
                throw new SemanticoExcepcion(opBin,"Operador desconocido");
        }
    }

    private Tipo checkIgualdad() throws SemanticoExcepcion {
        Tipo tipoIzq = nodoExpIzq.check();
        Tipo tipoDer = nodoExpDer.check();
        if (!tipoIzq.esCompatible(tipoDer) && !tipoDer.esCompatible(tipoIzq)){
            throw new SemanticoExcepcion(opBin,"Los tipos de los operandos deben ser compatibles");
        } else {
            return new TipoPrimitivo(new Token("pr_boolean","boolean",0),tablaDeSimbolos);
        }
    }

    private Tipo checkLogica() throws SemanticoExcepcion {
        Tipo tipoIzq = nodoExpIzq.check();
        Tipo tipoDer = nodoExpDer.check();
        if (!tipoIzq.getToken().getLexema().equals("boolean") || !tipoDer.getToken().getLexema().equals("boolean")){
            throw new SemanticoExcepcion(opBin,"Los tipos de los operandos deben ser booleanos");
        } else {
            return new TipoPrimitivo(new Token("pr_boolean","boolean",0),tablaDeSimbolos);
        }
    }

    private Tipo checkRelacional() throws SemanticoExcepcion {
        Tipo tipoIzq = nodoExpIzq.check();
        Tipo tipoDer = nodoExpDer.check();
        if (!tipoIzq.getToken().getLexema().equals("int") || !tipoDer.getToken().getLexema().equals("int")){
            throw new SemanticoExcepcion(opBin,"Los tipos de los operandos deben ser enteros");
        } else {
            return new TipoPrimitivo(new Token("pr_boolean","boolean",0),tablaDeSimbolos);
        }
    }

    private Tipo checkAritmetica() throws SemanticoExcepcion {
        Tipo tipoIzq = nodoExpIzq.check();
        Tipo tipoDer = nodoExpDer.check();
        if (!tipoIzq.getToken().getLexema().equals("int") || !tipoDer.getToken().getLexema().equals("int")){
            throw new SemanticoExcepcion(opBin,"Los tipos de los operandos deben ser enteros");
        } else {
            return new TipoPrimitivo(new Token("pr_int","int",0),tablaDeSimbolos);
        }
    }

    public Tipo getType() {
        return null;
    }

    public Token getToken() {
        return opBin;
    }

    @Override
    public void generarCodigo() {
        nodoExpIzq.generarCodigo();
        nodoExpDer.generarCodigo();
        switch (opBin.getLexema()){
            case "+":
                tablaDeSimbolos.codigoGenerado.add("ADD");
                break;
            case "-":
                tablaDeSimbolos.codigoGenerado.add("SUB");
                break;
            case "*":
                tablaDeSimbolos.codigoGenerado.add("MUL");
                break;
            case "/":
                tablaDeSimbolos.codigoGenerado.add("DIV");
                break;
            case "%":
                tablaDeSimbolos.codigoGenerado.add("MOD");
                break;
            case "<":
                tablaDeSimbolos.codigoGenerado.add("LT");
                break;
            case ">":
                tablaDeSimbolos.codigoGenerado.add("GT");
                break;
            case "<=":
                tablaDeSimbolos.codigoGenerado.add("LE");
                break;
            case ">=":
                tablaDeSimbolos.codigoGenerado.add("GE");
                break;
            case "==":
                tablaDeSimbolos.codigoGenerado.add("EQ");
                break;
            case "!=":
                tablaDeSimbolos.codigoGenerado.add("NE");
                break;
            case "&&":
                tablaDeSimbolos.codigoGenerado.add("AND");
                break;
            case "||":
                tablaDeSimbolos.codigoGenerado.add("OR");
                break;
        }
    }
}