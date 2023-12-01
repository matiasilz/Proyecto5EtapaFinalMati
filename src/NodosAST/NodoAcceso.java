package NodosAST;

import analizadorLexico.Token;
import analizadorSemantico.*;

import java.util.Collections;
import java.util.LinkedList;

public class NodoAcceso implements NodoBase {
    private NodoBase acceso;
    private Token token;
    private TablaDeSimbolos tablaDeSimbolos;
    private boolean esMetodo = false;
    private LinkedList<NodoBase> argsActuales;
    protected NodoAcceso encadenadoHijo, encadenadoPadre;

    public Tipo tipo = null;
    public NodoBloque bloqueContenedor;
    public Metodo metodoALlamar;
    public Atributo atributo;
    public boolean estaALaIzquierdaDeAsignacion = false;

    public NodoAcceso(Token tk, TablaDeSimbolos tablaDeSimbolos, NodoBloque bloque) {
        this.token = tk;
        this.tablaDeSimbolos = tablaDeSimbolos;
        this.bloqueContenedor = bloque;
    }

    @Override
    public Tipo check() throws SemanticoExcepcion {
        Tipo retorno = null;
        //Es acceso a metodo
        if (esMetodo) {
            if (encadenadoPadre == null) {
                Clase claseQueMeContiene = bloqueContenedor.getClaseActual();
                Metodo metodoALlamar = claseQueMeContiene.getMetodos().get(token.getLexema());
                setMetodoALlamar(metodoALlamar);
                if (metodoALlamar == null)
                    throw new SemanticoExcepcion(token, "No existe el metodo " + token.getLexema() + " en la clase " + claseQueMeContiene.getNombre());
                chequearMetodo(metodoALlamar,claseQueMeContiene);
                setTipo(metodoALlamar.getTipo());
                if(!(token.getLexema().equals("main")) && getType().getToken().getLexema().equals("void") && encadenadoHijo != null)
                    throw new SemanticoExcepcion(token, "El metodo no puede tener encadenado si es de tipo void");
                if (encadenadoHijo != null) {
                    Tipo hijo = encadenadoHijo.check();
                    retorno = hijo;
                } else {
                    retorno = getType();
                }
                for (NodoBase arg : argsActuales) {
                    arg.check();
                }
            } else {
                Clase claseQueMeContiene = tablaDeSimbolos.getClaseOInterface(encadenadoPadre.getType().getToken().getLexema());
                Metodo metodoALlamar = claseQueMeContiene.getMetodos().get(token.getLexema());
                setMetodoALlamar(metodoALlamar);
                if (metodoALlamar == null)
                    throw new SemanticoExcepcion(token, "No existe el metodo " + token.getLexema() + " en la clase " + claseQueMeContiene.getNombre());
                chequearMetodo(metodoALlamar,claseQueMeContiene);
                setTipo(metodoALlamar.getTipo());
                if(!encadenadoPadre.getToken().getLexema().equals("this") && !(token.getLexema().equals("main")) && encadenadoPadre.getType().getToken().getLexema().equals("void")) {
                    throw new SemanticoExcepcion(token, "El metodo no puede tener encadenado si es de tipo void");
                }
                if (encadenadoHijo != null) {
                    Tipo hijo = encadenadoHijo.check();
                    retorno = hijo;
                } else {
                    retorno = getType();
                }
                for (NodoBase arg : argsActuales) {
                    arg.check();
                }
            }
        } else {
            //Es un acceso a un atributo or this
            if (encadenadoPadre == null) {
                if(!token.getLexema().equals("this")){
                    System.out.println("Buscando atributo "+token.getLexema()+" en la clase "+bloqueContenedor.getClaseActual().getNombre());
                    atributo = bloqueContenedor.getVisibles(token.getLexema());
                    if (atributo == null)
                        throw new SemanticoExcepcion(token, "No existe el atributo " + token.getLexema() + " en la clase " + bloqueContenedor.getClaseActual().getNombre());
                    setTipo(atributo.getTipo());
                }
                else {
                    if(this instanceof NodoExpThis ) {
                        NodoExpThis expThis = (NodoExpThis) this;
                        Clase claseQueMeContiene = expThis.getClaseActual();
                        setTipo(new TipoReferencia(claseQueMeContiene.getTkClase(),tablaDeSimbolos));
                    }

                }
                if (encadenadoHijo != null) {
                    Tipo hijo = encadenadoHijo.check();
                    retorno = hijo;
                } else {
                    retorno = getType();
                }
            } else {
                Clase claseQueMeContiene = tablaDeSimbolos.getClaseOInterface(encadenadoPadre.getType().getToken().getLexema());
                 atributo = claseQueMeContiene.getAtributos().get(token.getLexema());
                if(atributo==null)
                    throw new SemanticoExcepcion(token, "No existe el atributo " + token.getLexema() + " en la clase " + claseQueMeContiene.getNombre());
                setTipo(atributo.getTipo());
                if (encadenadoHijo != null) {
                    Tipo hijo = encadenadoHijo.check();
                    retorno = hijo;
                } else {
                    retorno = getType();
                }
            }

        }
        tipo = retorno;
        return retorno;
    }

    private void chequearMetodo(Metodo metodoALlamar, Clase claseQueMeContiene) throws SemanticoExcepcion{
        if (metodoALlamar.getNombre().equals("debugPrint")) {
           return;
        }
        if(claseQueMeContiene.getMetodos().containsKey(metodoALlamar.getNombre())){
            if(metodoALlamar.getParametrosEnOrden().size() != argsActuales.size())
                throw new SemanticoExcepcion(token,"La cantidad de argumentos no coincide con la cantidad de parametros del metodo "+metodoALlamar.getNombre());
            for (int i = 0; i < argsActuales.size(); i++) {
                Tipo tipoParametro = metodoALlamar.getParametrosEnOrden().get(i).getTipo();
                Tipo tipoArgumento = argsActuales.get(i).check();
                if (!tipoParametro.esCompatible(tipoArgumento))
                    throw new SemanticoExcepcion(token,"El tipo del argumento "+(i+1)+" no es compatible con el tipo del parametro "+(i+1)+" del metodo "+metodoALlamar.getNombre());
            }
        } else {
            throw new SemanticoExcepcion(token,"No existe el metodo "+metodoALlamar.getNombre()+" en la clase "+claseQueMeContiene.getNombre());
        }
    }

    @Override
    public Tipo getType() {
        return tipo;
    }

    public void setTipo(Tipo t) {
        tipo = t;
    }

    @Override
    public Token getToken() {
        return token;
    }

    @Override
    public void generarCodigo() {
        System.out.println("Generando codigo para " + token.getLexema());
        if(esMetodo){
            generarCodigoParaMetodo();
        } else {
            generarCodigoParaAtributos();
        }
        if(encadenadoHijo != null){
            encadenadoHijo.generarCodigo();
        }
    }

    private void generarCodigoParaAtributos() {
        if(atributo.esAtributo()){
            tablaDeSimbolos.codigoGenerado.add("LOAD 3 ;Apilo this");
            if(!estaALaIzquierdaDeAsignacion || encadenadoHijo!=null){
                tablaDeSimbolos.codigoGenerado.add("LOADREF "+ atributo.getOffset());
            }
            else{
                tablaDeSimbolos.codigoGenerado.add("SWAP");
                tablaDeSimbolos.codigoGenerado.add("STOREREF "+ atributo.getOffset());
            }
        }
        else if (atributo.isParametro()){
            if(!estaALaIzquierdaDeAsignacion || encadenadoHijo!=null){
                tablaDeSimbolos.codigoGenerado.add("LOAD "+ atributo.getOffset());
            }
            else{
                tablaDeSimbolos.codigoGenerado.add("STORE "+ atributo.getOffset());
            }
        }
        else if (atributo.isVarLocal()){
            if(!estaALaIzquierdaDeAsignacion || encadenadoHijo!=null){
                tablaDeSimbolos.codigoGenerado.add("LOAD "+  atributo.getOffset());
            }
            else{
                tablaDeSimbolos.codigoGenerado.add("STORE "+ atributo.getOffset());
            }
        }
    }

    private void generarCodigoParaMetodo() {
        if (metodoALlamar.isStatico()){
            if (!metodoALlamar.getTipo().getToken().getLexema().equals("void")){
                tablaDeSimbolos.codigoGenerado.add("RMEM 1");
            }
            Collections.reverse(argsActuales);
            for (NodoBase expresion:argsActuales){
                expresion.generarCodigo();
            }
            Collections.reverse(argsActuales);
            tablaDeSimbolos.codigoGenerado.add("PUSH "+metodoALlamar.generarEtiqueta());
            tablaDeSimbolos.codigoGenerado.add("CALL");
        }
        else{
            if (encadenadoPadre == null)
                tablaDeSimbolos.codigoGenerado.add("LOAD 3 ;Se apila this");

            if (!metodoALlamar.getTipo().getToken().getLexema().equals("void")){
                tablaDeSimbolos.codigoGenerado.add("RMEM 1");
                tablaDeSimbolos.codigoGenerado.add("SWAP");
            }
            Collections.reverse(argsActuales);
            for (NodoBase expresion: argsActuales) {
                expresion.generarCodigo();
                tablaDeSimbolos.codigoGenerado.add("SWAP");
            }
            Collections.reverse(argsActuales);
            tablaDeSimbolos.codigoGenerado.add("DUP ");
            tablaDeSimbolos.codigoGenerado.add("LOADREF 0");
            tablaDeSimbolos.codigoGenerado.add("LOADREF "+metodoALlamar.getOffset());
            tablaDeSimbolos.codigoGenerado.add("CALL");
        }
    }

    public NodoBloque getBloqueContenedor() {
        return bloqueContenedor;
    }

    public NodoAcceso getEncadenadoHijo() {
        return encadenadoHijo;
    }

    public NodoAcceso getEncadenadoPadre() {
        return encadenadoPadre;
    }

    public void setEncadenadoHijo(NodoAcceso encadenado) {
        this.encadenadoHijo = encadenado;
        encadenado.setEncadenadoPadre(this);
    }

    protected void setEncadenadoPadre(NodoAcceso nodoAcceso) {
        this.encadenadoPadre = nodoAcceso;
    }

    public void setAccesoAMetodo() {
        this.esMetodo = true;
    }

    public boolean esAccesoAMetodo() {
        return this.esMetodo;
    }

    public boolean alFinalDelEncadenadoEsMetodo() {
        if (encadenadoHijo == null) {
            return esMetodo;
        } else {
            return encadenadoHijo.alFinalDelEncadenadoEsMetodo();
        }
    }

    public void setArgsActuales(LinkedList<NodoBase> argsActuales) {
        this.argsActuales = argsActuales;
    }

    public LinkedList<NodoBase> getArgsActuales() {
        return argsActuales;
    }

    public boolean alFinalEsAsignable() {
        if (esMetodo && encadenadoHijo == null) {
            return false;
        } else {
            if (!esMetodo && encadenadoHijo == null) {
                return true;
            } else {
                return encadenadoHijo.alFinalEsAsignable();
            }
        }
    }

    public Metodo getMetodoALlamar() {
        return metodoALlamar;
    }

    public void setMetodoALlamar(Metodo metodoALlamar) {
        this.metodoALlamar = metodoALlamar;
    }

    public void setEstaALaIzquierdaDeAsignacion() {
        this.estaALaIzquierdaDeAsignacion = true;
    }

    public Tipo getTipo() {
        return tipo;
    }
}