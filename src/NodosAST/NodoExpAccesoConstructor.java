package NodosAST;

import analizadorLexico.Token;
import analizadorSemantico.*;

import java.util.Collections;
import java.util.LinkedList;

public class NodoExpAccesoConstructor extends NodoAcceso {
    private Token token;
    private Token idClase;
    private LinkedList<NodoBase> args;
    private TablaDeSimbolos tablaDeSimbolos;

    public NodoExpAccesoConstructor(Token token, Token tk, LinkedList<NodoBase> args, TablaDeSimbolos tablaDeSimbolos, NodoBloque bloque) {
        super(tk, tablaDeSimbolos, bloque);
        this.token = token;
        this.idClase = tk;
        this.args = args;
        this.tablaDeSimbolos = tablaDeSimbolos;
    }

    @Override
    public Tipo check() throws SemanticoExcepcion {
        Clase claseAConstruir = tablaDeSimbolos.clases.get(idClase.getLexema());
        if (claseAConstruir != null) {
            if (args.size() != claseAConstruir.getConstructor().parametrosEnOrden.size())
                throw new SemanticoExcepcion(idClase, "La cantidad de argumentos no coincide con la cantidad de parametros del constructor de la clase " + idClase.getLexema());
            for (int i = 0; i < args.size(); i++) {
                Tipo tipoParametro = claseAConstruir.getConstructor().parametrosEnOrden.get(i).getTipo();
                Tipo tipoArgumento = args.get(i).check();
                if (!tipoParametro.esCompatible(tipoArgumento))
                    throw new SemanticoExcepcion(idClase, "El tipo del argumento " + (i + 1) + " no es compatible con el tipo del parametro " + (i + 1) + " del constructor de la clase " + idClase.getLexema());
            }
        } else {
            throw new SemanticoExcepcion(idClase, "No existe la clase " + idClase.getLexema());
        }
        setTipo(new TipoReferencia(idClase, tablaDeSimbolos));
        if (encadenadoHijo != null) {
            return encadenadoHijo.check();
        } else {
            return new TipoReferencia(idClase, tablaDeSimbolos);
        }

    }

    public void generarCodigo() {
        tablaDeSimbolos.codigoGenerado.add("RMEM 1");
        tablaDeSimbolos.codigoGenerado.add("PUSH " + (tablaDeSimbolos.getClaseOInterface(idClase.getLexema()).getAtributos().size() + 1));
        tablaDeSimbolos.codigoGenerado.add("PUSH Lmalloc");
        tablaDeSimbolos.codigoGenerado.add("CALL");
        tablaDeSimbolos.codigoGenerado.add("DUP");
        tablaDeSimbolos.codigoGenerado.add("PUSH VT_" + idClase.getLexema());
        tablaDeSimbolos.codigoGenerado.add("STOREREF 0");
        tablaDeSimbolos.codigoGenerado.add("DUP");
        Collections.reverse(args);
        for (NodoBase exp : args) {
            exp.generarCodigo();
            tablaDeSimbolos.codigoGenerado.add("SWAP");
        }
        Collections.reverse(args);
        tablaDeSimbolos.codigoGenerado.add("PUSH " + tablaDeSimbolos.getClaseOInterface(idClase.getLexema()).getConstructor().generarEtiqueta());
        tablaDeSimbolos.codigoGenerado.add("CALL");

        if (encadenadoHijo != null)
            encadenadoHijo.generarCodigo();

    }


    public Token getToken() {
        return token;
    }
}
