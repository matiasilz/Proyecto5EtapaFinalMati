package analizadorSemantico;

import analizadorLexico.Token;

import java.util.ArrayList;
import java.util.HashMap;

public class Clase {

    private String nombre;
    private Token tkClase;
    private HashMap<String, Atributo> atributos;
    private HashMap<String, Metodo> metodos;


    private HashMap<String, Clase> clasesAncestras;

    public Clase herencia;

    public Clase implementa;

    private Metodo constructor;
    private boolean esConsolidada = false;
    private TablaDeSimbolos tablaDeSimbolos;
    public int offSetVt;
    public int offSetClassInstance;

    public Clase(Token tkClase, TablaDeSimbolos tablaDeSimbolos) {
        nombre = tkClase.getLexema();
        this.tkClase = tkClase;
        atributos = new HashMap<String, Atributo>();
        metodos = new HashMap<String, Metodo>();
        herencia = null;
        implementa = null;
        clasesAncestras = new HashMap<String, Clase>();
        this.tablaDeSimbolos = tablaDeSimbolos;
        offSetVt = 0;
        offSetClassInstance = 1;
    }

    public String getNombre() {
        return nombre;
    }

    public void addConstructor(Metodo c) throws SemanticoExcepcion {
        if (constructor == null)
            constructor = c;
        else throw new SemanticoExcepcion(tkClase, "La clase no puede tener mas de un constructor");
    }

    public void addAtributo(Atributo atributo) throws SemanticoExcepcion {
        if (!atributos.containsKey(atributo.getNombre())) {
            atributos.put(atributo.getNombre(), atributo);
        } else {
            throw new SemanticoExcepcion(atributo.getTkAtributo(), "El Atributo " + atributo.getNombre() + " ya esta declarado");
        }
    }

    public Metodo getConstructor() {
        return constructor;
    }

    public HashMap<String, Clase> getClasesAncestras() {
        return clasesAncestras;
    }

    public void setClasesAncestras(HashMap<String, Clase> clasesAncestras) {
        this.clasesAncestras = clasesAncestras;
    }

    public void addMetodo(Metodo metodo) throws SemanticoExcepcion {
        if (!metodos.containsKey(metodo.getNombre())) {
            metodos.put(metodo.getNombre(), metodo);
        } else
            throw new SemanticoExcepcion(metodo.getTkMetodo(), "El Metodo " + metodo.getNombre() + " ya esta declarado");
    }


    public HashMap<String, Atributo> getAtributos() {
        return atributos;
    }

    public HashMap<String, Metodo> getMetodos() {
        return metodos;
    }

    public Token getTkClase() {
        return tkClase;
    }

    public void consolidar(ArrayList<Clase> listaHerederos, TablaDeSimbolos ts) throws SemanticoExcepcion {
        if (listaHerederos.contains(this))
            throw new SemanticoExcepcion(tkClase, "Se genera herencia circular");
        if (!nombre.equals("Object")) {
            if (herencia == null)
                heredar(ts.clases.get("Object"));
            else {
                if (!herencia.esConsolidada) {
                    if (herencia.tkClase.getIdentificador().equals("interfazVacia")) {
                        //No hago nada
                    } else {
                        listaHerederos.add(this);
                        herencia.consolidar(listaHerederos, ts);
                    }
                }
                if (!herencia.tkClase.getIdentificador().equals("interfazVacia"))
                    heredar(herencia);
            }
        }
        esConsolidada = true;
        setOffsetAtributos();
        setOffsetMetodos();
    }

    private void heredar(Clase herencia) throws SemanticoExcepcion {
        for (Metodo metodoDePadre : herencia.getMetodos().values()) {
            if (metodos.containsKey(metodoDePadre.getNombre())) {
                Metodo metodoActual = metodos.get(metodoDePadre.getNombre());
                if (metodoDePadre.isStatico() && !metodoActual.isStatico())
                    throw new SemanticoExcepcion(metodoActual.getTkMetodo(), "El metodo debe ser estatico");
                if (!metodoDePadre.getTipo().getToken().getLexema().equals(metodoActual.getTipo().getToken().getLexema()))
                    throw new SemanticoExcepcion(metodoActual.getTkMetodo(), "El tipo debe coincidir con el declarado en el padre");
                if (metodoDePadre.getParametros().size() == metodoActual.getParametros().size())
                    for (int i = 0; i < metodoDePadre.getParametros().size(); i++) {
                        Parametro pPadre = metodoDePadre.parametrosEnOrden.get(i);
                        Parametro pActual = metodoActual.parametrosEnOrden.get(i);
                        if (pPadre.getTipo().getToken().getLexema().equals(pActual.getTipo().getToken().getLexema()))
                            if (pPadre.getNombre().equals(pActual.getNombre())) {
                                //No hago nada
                            } else
                                throw new SemanticoExcepcion(pActual.getTkParametro(), "El nombre del parametro actual debe coincidir con el nombre declarado en el padre");
                        else
                            throw new SemanticoExcepcion(pActual.getTkParametro(), "El tipo del parametro actual debe coincidir con el tipo declarado en el padre");
                    }
                else
                    throw new SemanticoExcepcion(metodoActual.getTkMetodo(), "La cantidad de parametros no es igual a la del metodo padre");
            } else {
                metodos.put(metodoDePadre.getNombre(), metodoDePadre);
            }
        }
        for (Atributo atributoDePadre : herencia.getAtributos().values()) {
            if (atributos.containsKey(atributoDePadre.getNombre())) {
                throw new SemanticoExcepcion(atributos.get(atributoDePadre.getNombre()).getTkAtributo(), "El atributo no puede tener el mismo nombre que uno declarado en algun ancestro");
            } else atributos.put(atributoDePadre.getNombre(), atributoDePadre);
        }
        clasesAncestras.putAll(herencia.clasesAncestras);
        clasesAncestras.put(herencia.getNombre(), herencia);
    }

    public boolean esSubTipo(Clase clase) {
        if (clase.getNombre().equals("Object"))
            return true;
        if (clasesAncestras.containsKey(clase.getNombre()) || clasesAncestrasSonSubtipo(clase))
            return true;
        if (clase.getNombre().equals(getNombre()))
            return true;
        if (implementa != null && (clase.getNombre().equals(implementa.getNombre()) || tablaDeSimbolos.getClaseOInterface(implementa.getNombre()).esSubTipo(clase)))
            return true;
        return false;
    }

    private boolean clasesAncestrasSonSubtipo(Clase clase) {
        for (Clase claseAncestra : clasesAncestras.values()) {
            if (claseAncestra.esSubTipo(clase)) {
                return true;
            }
        }
        return false;
    }

    public void realizarChequeos(TablaDeSimbolos ts) throws SemanticoExcepcion {

        for (Atributo atributo : atributos.values()) {
            if (atributo.getTipo().getToken().getIdentificador().equals("idClase"))
                if (!ts.clases.containsKey(atributo.getTipo().getToken().getLexema()) && !ts.interfaces.containsKey(atributo.getTipo().getToken().getLexema())) {
                    throw new SemanticoExcepcion(atributo.getTkAtributo(), "El tipo no esta declarado");
                }
            if (atributo.getTipo().getToken().getIdentificador().equals("pr_void"))
                throw new SemanticoExcepcion(atributo.getTipo().getToken(), "El tipo no puede ser void");
        }
        for (Metodo metodo : metodos.values()) {
            metodo.realizarChequeos(ts);
        }
        if (constructor == null) {
            Metodo c = new Metodo(tkClase, new TipoReferencia(tkClase, ts), false, tablaDeSimbolos);
            constructor = c;
        }
        if (herencia != null && !herencia.getNombre().equals("interfazVacia")) {
            Clase claseAHeredar = ts.clases.get(herencia.getNombre());
            if (claseAHeredar == null)
                claseAHeredar = ts.interfaces.get(herencia.getNombre());
            if (claseAHeredar == null)
                throw new SemanticoExcepcion(herencia.getTkClase(), "La clase o la interfaz no esta declarada");
            else herencia = claseAHeredar;
        }
        if (implementa != null && !ts.interfaces.containsKey(implementa.getNombre())) {
            throw new SemanticoExcepcion(implementa.getTkClase(), "La clase o la interfaz no esta definida");
        }

        //chequear que el constructor sea del tipo de la clase
        if (!constructor.getTipo().getToken().getLexema().equals(tkClase.getLexema()))
            throw new SemanticoExcepcion(constructor.getTkMetodo(), "El tipo de retorno del constructor debe ser el mismo que el de la clase");
    }

    public void setHerencia(Token hereda) {
        herencia = new Clase(hereda, tablaDeSimbolos);
    }

    public void setImplementa(Token implement) {
        implementa = new Clase(implement, tablaDeSimbolos);
    }

    public void check() throws SemanticoExcepcion {
        for (Metodo metodo : metodos.values()) {
            metodo.check();
        }
    }

    public void generarVT() {
        tablaDeSimbolos.codigoGenerado.add("VT_" + nombre + ":");
        String etiquetametodo = "DW ";
        for (Metodo metodo : metodos.values()) {
            etiquetametodo += metodo.getNombre() + ", ";
        }
        tablaDeSimbolos.codigoGenerado.add(etiquetametodo.substring(0, etiquetametodo.length() - 2));
    }

    public void setOffsetMetodos() {
        if (clasesAncestras.size() > 0) {
            for (Clase claseAncestra : clasesAncestras.values()) {
                offSetVt += claseAncestra.getMetodos().size();
            }
            for (Metodo metodo : metodos.values()) {
                if (!metodo.isStatico()) {
                    if (!metodo.isTieneOffset())
                        metodo.setOffset(offSetVt);
                    offSetVt++;
                }
            }
        }
    }
    public void setOffsetAtributos() {
        if (clasesAncestras.size() > 0) {
            for (Clase claseAncestra : clasesAncestras.values()) {
                offSetClassInstance += claseAncestra.getAtributos().size();
            }
            for (Atributo atributo : atributos.values()) {
                if (!atributo.isTieneOffset())
                    atributo.setOffset(offSetClassInstance);
                offSetClassInstance++;
            }
        }
    }

    public void generarCodigo() {
        for (Metodo metodo : metodos.values()) {
            boolean esDeClase = true;
            for (Clase clase : clasesAncestras.values() ) {
                if(clase.getMetodos().containsKey(metodo.getNombre())) {
                    esDeClase = false;
                }
            }
            if (esDeClase) {
                tablaDeSimbolos.codigoGenerado.add(metodo.getNombre() + ":");
                metodo.generarCodigo();
            }

        }
    }
}