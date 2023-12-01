package analizadorSemantico;

import NodosAST.NodoBloque;
import analizadorLexico.Token;

import java.util.ArrayList;
import java.util.HashMap;

public class TablaDeSimbolos {

    public HashMap<String, Clase> clases;

    private Clase claseActual;

    public HashMap<String, Clase> interfaces;
    private Metodo metodoActual;

    private ArrayList<NodoBloque> bloques = new ArrayList<>();

    public ArrayList<String> codigoGenerado = new ArrayList<>();
    private NodoBloque bloqueActual;

    public TablaDeSimbolos() throws SemanticoExcepcion {
        clases = new HashMap<String, Clase>();
        interfaces = new HashMap<String, Clase>();
        inicializarPredeterminados();
    }

    private void inicializarPredeterminados() throws SemanticoExcepcion {
        Tipo tInt = new Tipo(new Token("pr_int", "int", 0), this);
        Tipo tVoid = new Tipo(new Token("pr_void", "void", 0), this);
        Tipo tBoolean = new Tipo(new Token("pr_boolean", "boolean", 0), this);
        Tipo tChar = new Tipo(new Token("pr_char", "char", 0), this);


        Clase Object = new Clase(new Token("Object", "Object", 0), this);
        Metodo debugPrint = new Metodo(new Token("debugPrint", "debugPrint", 0), tVoid, true,this);
        Parametro parametro = new Parametro(new Token("idMetVar", "i", 0), tInt);
        debugPrint.setStatico(true);
        debugPrint.addParametro(parametro);
        Object.addMetodo(debugPrint);
        this.addClase(Object.getNombre(), Object);

        Clase String = new Clase(new Token("String", "String", 0), this);
        this.addClase(String.getNombre(), String);
        Tipo tString = new Tipo(String.getTkClase(), this);
        Clase system = new Clase(new Token("System", "System", 0), this);
        Metodo read = new Metodo(new Token("read", "read", 0), tInt, true,this);
        read.setStatico(true);
        system.addMetodo(read);
        Metodo printB = new Metodo(new Token("printB", "printB", 0), tVoid, true,this);
        printB.setStatico(true);
        Parametro parametro1 = new Parametro(new Token("idMetVar", "b", 0), tBoolean);
        printB.addParametro(parametro1);
        system.addMetodo(printB);

        Metodo printC = new Metodo(new Token("printC", "printC", 0), tVoid, true,this);
        printC.setStatico(true);
        Parametro parametro2 = new Parametro(new Token("idMetVar", "c", 0), tChar);
        printC.addParametro(parametro2);
        system.addMetodo(printC);

        Metodo printI = new Metodo(new Token("printI", "printI", 0), tVoid, true,this);
        printI.setStatico(true);
        Parametro parametro3 = new Parametro(new Token("idMetVar", "i", 0), tInt);
        printI.addParametro(parametro3);
        system.addMetodo(printI);

        Metodo printS = new Metodo(new Token("printS", "printS", 0), tVoid, true,this);
        printS.setStatico(true);
        Parametro parametro4 = new Parametro(new Token("idMetVar", "s", 0), tString);
        printS.addParametro(parametro4);
        system.addMetodo(printS);

        Metodo println = new Metodo(new Token("println", "println", 0), tVoid, true,this);
        println.setStatico(true);
        system.addMetodo(println);

        Metodo printBln = new Metodo(new Token("printBln", "printBln", 0), tVoid, true,this);
        printBln.setStatico(true);
        Parametro parametro5 = new Parametro(new Token("idMetVar", "b", 0), tBoolean);
        printBln.addParametro(parametro5);
        system.addMetodo(printBln);

        Metodo printCln = new Metodo(new Token("printCln", "printCln", 0), tVoid, true,this);
        printCln.setStatico(true);
        Parametro parametro6 = new Parametro(new Token("idMetVar", "c", 0), tChar);
        printCln.addParametro(parametro6);
        system.addMetodo(printCln);

        Metodo printIln = new Metodo(new Token("printIln", "printIln", 0), tVoid, true,this);
        printIln.setStatico(true);
        Parametro parametro7 = new Parametro(new Token("idMetVar", "i", 0), tInt);
        printIln.addParametro(parametro7);
        system.addMetodo(printIln);

        Metodo printSln = new Metodo(new Token("printSln", "printSln", 0), tVoid, true,this);
        printSln.setStatico(true);
        Parametro parametro8 = new Parametro(new Token("idMetVar", "s", 0), tString);
        printSln.addParametro(parametro8);
        system.addMetodo(printSln);
        addClase(system.getNombre(), system);

    }

    public void addClase(String nombre, Clase clase) throws SemanticoExcepcion {
        if (!clases.containsKey(nombre)) {
            clases.put(nombre, clase);
        } else {
            throw new SemanticoExcepcion(clase.getTkClase(), "La clase " + nombre + " ya esta declarada");
        }
    }

    public void addInterface(String nombre, Clase clase) throws SemanticoExcepcion {
        if (!interfaces.containsKey(nombre)) {
            interfaces.put(nombre, clase);
        } else {
            throw new SemanticoExcepcion(clase.getTkClase(), "La Interfaz " + nombre + " ya esta declarada");
        }
    }

    public Clase getClaseActual() {
        return claseActual;
    }

    public void setClaseActual(Clase clase) {
        this.claseActual = clase;
    }

    public NodoBloque getBloqueActual() {
        return bloqueActual;
    }

    public void setBloqueActual(NodoBloque bloque) {
        this.bloqueActual = bloque;
        bloques.add(bloque);
    }

    public void realizarChequeos() throws SemanticoExcepcion {
        boolean tengoMain = false;
        for (Clase i : interfaces.values()) {
            i.realizarChequeos(this);
        }

        for (Clase c : clases.values()) {
            c.realizarChequeos(this);
            Metodo m = c.getMetodos().get("main");
            if (m != null && m.isStatico() && m.getParametros().isEmpty())
                tengoMain = true;
        }
        if (!tengoMain)
            throw new SemanticoExcepcion(new Token(" ", " ", 0), "No existe clase con metodo main");
        realizarLosOtrosChequeos();
    }

    private void realizarLosOtrosChequeos() throws SemanticoExcepcion {
        for (Clase c : clases.values()) {
            if (c.herencia != null) {
                if (!(clases.containsKey(c.herencia.getNombre()))) {
                    throw new SemanticoExcepcion(c.herencia.getTkClase(), "No existe la clase heredada o bien se esta intentando heredar de una interfaz");
                }
            }
            if (c.implementa != null) {
                if (!(interfaces.containsKey(c.implementa.getNombre()))) {
                    throw new SemanticoExcepcion(c.implementa.getTkClase(), "No existe la interfaz implementada");
                }
            }
        }
        for (Clase c : interfaces.values()) {
            if (c.herencia != null && !c.herencia.getNombre().equals("interfazVacia")) {
                if (!(interfaces.containsKey(c.herencia.getNombre()))) {
                    throw new SemanticoExcepcion(c.herencia.getTkClase(), "No existe la interfaz heredada");
                }
            }
            if (c.implementa != null) {
                throw new SemanticoExcepcion(c.implementa.getTkClase(), "No existe la interfaz implementada");
            }
        }
    }

    public void consolidar() throws SemanticoExcepcion {
        for (Clase i : interfaces.values()) {
            i.consolidar(new ArrayList<>(), this);
        }
        for (Clase c : clases.values()) {
            c.consolidar(new ArrayList<>(), this);
        }
    }

    public void mostrar() {
        for (Clase clase : clases.values()) {
            System.out.println("------------------------------------------------------------------------------------");
            System.out.println("Clase: " + clase.getNombre());
            System.out.println("Atributos:");
            for (Atributo atributo : clase.getAtributos().values()) {
                System.out.println("\t" + atributo);
            }
            System.out.println("Constructor:");
            System.out.println("\t" + clase.getConstructor().getNombre());
            for (Parametro p : clase.getConstructor().getParametros().values()) {
                System.out.println("\t \t" + p.getNombre() + " : " + p.getTipo().getToken().getLexema());
            }
            System.out.println("Metodos:");
            for (Metodo metodo : clase.getMetodos().values()) {
                System.out.println("\t" + metodo);
            }

        }
        for (Clase clase : interfaces.values()) {
            System.out.println("------------------------------------------------------------------------------------");
            System.out.println("Interface: " + clase.getNombre());
            System.out.println("Metodos:");
            for (Metodo metodo : clase.getMetodos().values()) {
                System.out.println("\t" + metodo);
            }

        }

    }

    public void setMetodoActual(Metodo m) {
        metodoActual = m;
    }

    public Metodo getMetodoActual() {
        return metodoActual;
    }

    public void chequeosFinales() throws SemanticoExcepcion {
        for (Clase c : clases.values()) {
            if (c.implementa != null) {
                if (interfaces.containsKey(c.implementa.getNombre())) {
                    HashMap<String, Metodo> coleccionMetodosClase = c.getMetodos();
                    for (Metodo metodoInterfaz : interfaces.get(c.implementa.getNombre()).getMetodos().values()) {
                        if (coleccionMetodosClase.containsKey(metodoInterfaz.getNombre())) {
                            Metodo mActual = coleccionMetodosClase.get(metodoInterfaz.getNombre());
                            if (!metodoInterfaz.getTipo().getToken().getLexema().equals(mActual.getTipo().getToken().getLexema()))
                                throw new SemanticoExcepcion(mActual.getTkMetodo(), "El tipo debe coincidir con el declarado en la interfaz");
                            if (metodoInterfaz.getParametros().size() == mActual.getParametros().size())
                                for (int i = 0; i < metodoInterfaz.getParametros().size(); i++) {
                                    Parametro pPadre = metodoInterfaz.parametrosEnOrden.get(i);
                                    Parametro pActual = mActual.parametrosEnOrden.get(i);
                                    if (pPadre.getTipo().getToken().getLexema().equals(pActual.getTipo().getToken().getLexema()))
                                        if (pPadre.getNombre().equals(pActual.getNombre())) {
                                            //No hago nada
                                        } else
                                            throw new SemanticoExcepcion(pActual.getTkParametro(), "El nombre del parametro actual debe coincidir con el nombre declarado en la interfaz");
                                    else
                                        throw new SemanticoExcepcion(pActual.getTkParametro(), "El tipo del parametro actual debe coincidir con el tipo declarado en la interfaz");
                                }
                            else
                                throw new SemanticoExcepcion(mActual.getTkMetodo(), "La cantidad de parametros no es igual a la de la interfaz");
                        } else {
                            throw new SemanticoExcepcion(metodoInterfaz.getTkMetodo(), "El metodo esta declarado en la interfaz pero no esta implementado");
                        }
                    }
                } else {
                    throw new SemanticoExcepcion(c.implementa.getTkClase(), "No existe la interfaz implementada");
                }
            }
        }
    }

    public Clase getClaseOInterface(String nombreClaseActual) {
        if (clases.containsKey(nombreClaseActual))
            return clases.get(nombreClaseActual);
        else
            return interfaces.get(nombreClaseActual);
    }

    public void check() throws SemanticoExcepcion {
        for (Clase clase : clases.values()) {
            clase.check();
        }
        for (Clase clase : interfaces.values()) {
            clase.check();
        }
    }
    public void generarCodigo() throws SemanticoExcepcion {
        //Carga
        codigoGenerado.add(".CODE");
        codigoGenerado.add("PUSH main");
        codigoGenerado.add("CALL");
        codigoGenerado.add("HALT");
        codigoGenerado.add("Lmalloc:");
        codigoGenerado.add("LOADFP ");
        codigoGenerado.add("LOADSP");
        codigoGenerado.add("STOREFP");
        codigoGenerado.add("LOADHL");
        codigoGenerado.add("DUP");
        codigoGenerado.add("PUSH 1");
        codigoGenerado.add("ADD");
        codigoGenerado.add("STORE 4");
        codigoGenerado.add("LOAD 3");
        codigoGenerado.add("ADD");
        codigoGenerado.add("STOREHL");
        codigoGenerado.add("STOREFP");
        codigoGenerado.add("RET 1");

        codigoGenerado.add(".DATA");
        for (Clase clase : clases.values()) {
            clase.generarVT();
        }
        codigoGenerado.add(".CODE");
        for (Clase clase : clases.values()) {
            clase.generarCodigo();
        }
    }

}