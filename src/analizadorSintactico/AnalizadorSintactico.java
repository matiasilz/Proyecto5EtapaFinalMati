package analizadorSintactico;

import NodosAST.NodoBase;
import NodosAST.NodoBloque;
import NodosAST.*;
import analizadorLexico.AnalizadorLexico;
import analizadorLexico.Token;
import analizadorSemantico.*;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

public class AnalizadorSintactico {

    private AnalizadorLexico analizadorLex;
    private Token tokenActual;
    private TablaDeSimbolos tablaDeSimbolos;


    public AnalizadorSintactico(AnalizadorLexico analizadorLex) throws Exception {
        this.analizadorLex = analizadorLex;
        tokenActual = analizadorLex.proximoToken();
        tablaDeSimbolos = new TablaDeSimbolos();
    }

    public void match(String nombreToken) throws Exception {
        if(nombreToken.equals(tokenActual.getIdentificador())){
            tokenActual = analizadorLex.proximoToken();
        }else{
            throw new SintacticoExcepcion(tokenActual, nombreToken);
        }
    }
    public void inicial(String salida) throws Exception {
        listaClases();
        match("Eof");
        tablaDeSimbolos.realizarChequeos();
        tablaDeSimbolos.consolidar();
        tablaDeSimbolos.chequeosFinales();
        //tablaDeSimbolos.mostrar();
        tablaDeSimbolos.check();
        tablaDeSimbolos.generarCodigo();
        generarArchivoSalida(tablaDeSimbolos.codigoGenerado,salida);
    }
    private void generarArchivoSalida(ArrayList<String> codigoGenerado, String archivoSalida) {
        File archivo = new File(archivoSalida);
        FileWriter escritor;
        try {
            escritor = new FileWriter(archivo);
            for (String linea : codigoGenerado) {
                if(linea.charAt(0)=='V' || linea.charAt(0)=='l' || linea.charAt(0)=='.'|| linea.startsWith("Lcom") || linea.startsWith("Lmalloc") || linea.startsWith("Lmain") || linea.startsWith("LCon") || linea.startsWith("Lfin")  ){
                    escritor.write(linea+"\n");
                }
                else{
                    escritor.write(" "+linea+"\n");
                }
            }
            escritor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    private void listaClases() throws Exception {
        if(Arrays.asList("pr_class","pr_interface").contains(tokenActual.getIdentificador())){
            clase();
            listaClases();
        }
        else {
            //No hago nada
        }
    }
    private void clase() throws Exception {
        if(Arrays.asList("pr_class").contains(tokenActual.getIdentificador())){
            claseConcreta();
        } else if (Arrays.asList("pr_interface").contains(tokenActual.getIdentificador())) {
            mInterface();
        }
        else throw new SintacticoExcepcion(tokenActual, "clase o interface");
    }

    private void claseConcreta() throws Exception {
        match("pr_class");
        Token tkClase = tokenActual;
        match("idClase");
        Clase clase = new Clase(tkClase,tablaDeSimbolos);
        tablaDeSimbolos.setClaseActual(clase);
        herenciaOpcional();
        match("abrirLlave");
        listaMiembros();
        match("cerrarLlave");
        tablaDeSimbolos.addClase(clase.getNombre(), clase);

    }
    private void mInterface() throws Exception {
        match("pr_interface");
        Token tkInterfaz = tokenActual;
        match("idClase");
        Clase interfaz = new Clase(tkInterfaz,tablaDeSimbolos);
        interfaz.herencia = new Clase(new Token("interfazVacia","interfazVacia",0),tablaDeSimbolos);
        tablaDeSimbolos.setClaseActual(interfaz);
        extiendeOpcional();
        match("abrirLlave");
        listaEncabezados();
        match("cerrarLlave");
        tablaDeSimbolos.addInterface(interfaz.getNombre(), interfaz);
    }
    private void herenciaOpcional() throws Exception {
        if(tokenActual.getIdentificador().equals("pr_extends"))
            heredaDe();
        else if (tokenActual.getIdentificador().equals("pr_implements")) {
            implementaA();
        }
        else{
            //No hago nada
        }
    }
    private void heredaDe() throws Exception {
        match("pr_extends");
        Token hereda = tokenActual;
        match("idClase");
        tablaDeSimbolos.getClaseActual().setHerencia(hereda);
    }
    private void implementaA() throws Exception {
        match("pr_implements");
        Token implementa = tokenActual;
        match("idClase");
        tablaDeSimbolos.getClaseActual().setImplementa(implementa);
    }
    private void extiendeOpcional() throws Exception {
        if (Arrays.asList("pr_extends").contains(tokenActual.getIdentificador())){
            match("pr_extends");
            Token herencia = tokenActual;
            match("idClase");
            tablaDeSimbolos.getClaseActual().setHerencia(herencia);
        }else{
            //No hago nada
        }
    }
    private void listaMiembros() throws Exception {
        if(Arrays.asList("pr_public","pr_static","pr_void","idClase","pr_boolean","pr_int","pr_char").contains(tokenActual.getIdentificador())){
            miembro();
            listaMiembros();
        }
        else{
            //No hago nada
        }
    }
    private void listaEncabezados() throws Exception {
        if (Arrays.asList("pr_static","pr_void","idClase","pr_boolean","pr_char","pr_int").contains(tokenActual.getIdentificador())) {
            encabezadoMetodo();
            listaEncabezados();
        } else {
            //No hago nada
        }
    }
    private void miembro() throws Exception {
        if(Arrays.asList("pr_static","pr_void","idClase","pr_boolean","pr_int","pr_char").contains(tokenActual.getIdentificador())){
            boolean tipoEstatico = estaticoOpcional();
            Tipo t = tipoMiembro();
            Token tkActual = tokenActual;
            match("idMetVar");
            miembroPrima(tkActual,t,tipoEstatico);
        } else if (Arrays.asList("pr_public").contains(tokenActual.getIdentificador())) {
            mConstructor();
        }
        else{
            throw new SintacticoExcepcion(tokenActual,"tipo, static o public");
        }

    }

    private void miembroPrima(Token tkActual,Tipo t,boolean tipoEstatico) throws Exception {
        if(Arrays.asList("puntoYComa").contains(tokenActual.getIdentificador())){
            Atributo a = new Atributo(tkActual,t);
            tablaDeSimbolos.getClaseActual().addAtributo(a);
            match("puntoYComa");
        } else if (Arrays.asList("abrirParentesis").contains(tokenActual.getIdentificador())) {
            Metodo m = new Metodo(tkActual,t, tipoEstatico,tablaDeSimbolos);
            tablaDeSimbolos.getClaseActual().addMetodo(m);
            tablaDeSimbolos.setMetodoActual(m);
            argsFormales();
            bloque();
        }
        else throw new SintacticoExcepcion(tokenActual,"; o (");
    }
    private void encabezadoMetodo() throws Exception {
        Metodo m;
        if(Arrays.asList("pr_static","pr_void","idClase","pr_boolean","pr_int","pr_char").contains(tokenActual.getIdentificador())){
            boolean estatico = estaticoOpcional();
            Tipo t = tipoMiembro();
            if(!estatico) {
                m = new Metodo(tokenActual, t,false,tablaDeSimbolos);
                tablaDeSimbolos.setMetodoActual(m);
                tablaDeSimbolos.getClaseActual().addMetodo(m);
                match("idMetVar");
                argsFormales();
                match("puntoYComa");
            }

            else throw new SemanticoExcepcion(tokenActual,"Las interfaces no pueden tener metodos static");
        }
        else throw new SintacticoExcepcion(tokenActual,"static o tipo");
    }
    private void mConstructor() throws Exception {
        match("pr_public");
        Token tkMetodo = tokenActual;
        match("idClase");
        Tipo t = new Tipo(tokenActual,tablaDeSimbolos);
        Metodo c = new Metodo(tkMetodo,t,false,tablaDeSimbolos);
        tablaDeSimbolos.getClaseActual().addConstructor(c);
        tablaDeSimbolos.setMetodoActual(c);
        argsFormales();
        bloque();
    }
    private Tipo tipoMiembro() throws Exception {
        Tipo t = null;
        if(Arrays.asList("idClase","pr_boolean","pr_char","pr_int").contains(tokenActual.getIdentificador())){
            t = tipo();
        } else if (Arrays.asList("pr_void").contains(tokenActual.getIdentificador())) {
            t = new Tipo(tokenActual,tablaDeSimbolos);
            match("pr_void");
        }
        else throw new SintacticoExcepcion(tokenActual,"tipo Miembro");
        return t;
    }

    private Tipo tipo() throws Exception {
        Tipo t=null;
        if(Arrays.asList("pr_boolean","pr_char","pr_int").contains(tokenActual.getIdentificador())){
            t = tipoPrimitivo();
        } else if (Arrays.asList("idClase").contains(tokenActual.getIdentificador())) {
            t = new TipoReferencia(tokenActual,tablaDeSimbolos);
            match("idClase");
        }
        else throw new SintacticoExcepcion(tokenActual,"tipo");
        return t;
    }

    private Tipo tipoPrimitivo() throws Exception {
        Tipo t=null;
        if (Arrays.asList("pr_boolean").contains(tokenActual.getIdentificador())){
            t = new TipoPrimitivo(tokenActual,tablaDeSimbolos);
            match("pr_boolean");

        } else if (Arrays.asList("pr_char").contains(tokenActual.getIdentificador())) {
            t = new TipoPrimitivo(tokenActual, tablaDeSimbolos);
            match("pr_char");
        }else if (Arrays.asList("pr_int").contains(tokenActual.getIdentificador())) {
            t = new TipoPrimitivo(tokenActual, tablaDeSimbolos);
            match("pr_int");
        }
        else throw new SintacticoExcepcion(tokenActual,"tipo primitivo");
        return t;
    }

    private boolean estaticoOpcional() throws Exception {
        boolean estatico;
        if (Arrays.asList("pr_static").contains(tokenActual.getIdentificador())){
            estatico = true;
            match("pr_static");
        }else{
            estatico = false;
        }
        return estatico;
    }
    private void argsFormales() throws Exception {
        match("abrirParentesis");
        listaArgsFormalesOpcional();
        match("cerrarParentesis");
    }

    private void listaArgsFormalesOpcional() throws Exception {
        if (Arrays.asList("idMetVar","idClase","pr_boolean","pr_char","pr_int").contains(tokenActual.getIdentificador())){
            listaArgsFormales();
        }else{
            //No hago nada
        }
    }

    private void listaArgsFormales() throws Exception {
        if (Arrays.asList("idMetVar","idClase","pr_boolean","pr_char","pr_int").contains(tokenActual.getIdentificador())){
            argFormal();
            listaArgsFormalesPrima();
        }else throw new SintacticoExcepcion(tokenActual,"tipo de clase o tipo de met o tipo primitivo");
    }

    private void listaArgsFormalesPrima() throws Exception {
        if(Arrays.asList("coma").contains(tokenActual.getIdentificador())) {
            match("coma");
            listaArgsFormales();
        } else {
            //No hago nada
        }
    }
    private void argFormal() throws Exception {
        Tipo t;
        t = tipo();
        Parametro p = new Parametro(tokenActual,t);
        match("idMetVar");
        tablaDeSimbolos.getMetodoActual().addParametro(p);
    }

    private NodoBloque bloque() throws Exception {
        Token tkBloque = tokenActual;
        match("abrirLlave");
        NodoBloque nodoB = new NodoBloque(tkBloque,tablaDeSimbolos.getClaseActual(),tablaDeSimbolos.getMetodoActual(),tablaDeSimbolos.getBloqueActual(),tablaDeSimbolos);
        tablaDeSimbolos.setBloqueActual(nodoB);
        listaSentencias();
        if (nodoB.nodoPadre == null){
            tablaDeSimbolos.getMetodoActual().setBloque(nodoB);
        }
        match("cerrarLlave");
        tablaDeSimbolos.setBloqueActual(nodoB.nodoPadre);
        return nodoB;
    }

    private void listaSentencias() throws Exception {
        if (Arrays.asList("puntoYComa", "opSuma", "opResta" , "opNegacion", "pr_null", "pr_true", "pr_false", "intLiteral", "charLiteral", "stringLiteral", "pr_this", "idMetVar", "pr_new", "idClase", "abrirParentesis" , "abrirParentesis","pr_var","pr_return","pr_if","pr_while","abrirLlave").contains(tokenActual.getIdentificador())){
            NodoBase SE = sentencia();
            tablaDeSimbolos.getBloqueActual().addSentencia(SE);
            listaSentencias();
        }else{
            //No hago nada
        }
    }

    private NodoBase sentencia() throws Exception {
        NodoBase nodoSentencia = null;
        if(tokenActual.getIdentificador().equals("puntoYComa")) {
            //Devuelve null
            match("puntoYComa");
        }
        else if(Arrays.asList("opSuma","opResta","opNegacion","pr_null","pr_true", "pr_false","intLiteral", "charLiteral", "stringLiteral", "pr_this", "idMetVar", "pr_new", "idClase", "abrirParentesis").contains(tokenActual.getIdentificador())) {
            nodoSentencia = expresion();
            match("puntoYComa");
        } else if (tokenActual.getIdentificador().equals("pr_var")) {
            nodoSentencia = varLocal();
            match("puntoYComa");
        }else if (tokenActual.getIdentificador().equals("pr_return")) {
            nodoSentencia = mReturn();
            match("puntoYComa");
        }else if (tokenActual.getIdentificador().equals("pr_if")) {
            nodoSentencia = mIf();
        }else if (tokenActual.getIdentificador().equals("pr_while")) {
            nodoSentencia = mWhile();
        }else if (tokenActual.getIdentificador().equals("abrirLlave")) {
            nodoSentencia = bloque();
        }
        else throw new SintacticoExcepcion(tokenActual,"sentencia");
        return nodoSentencia;
    }
    private NodoVarLocal varLocal() throws Exception {
        match("pr_var");
        Token identificador = tokenActual;
        match("idMetVar");
        match("Asignacion");
        NodoBase expresionInicial = expresionCompuesta(); //Expresion sola o Compuesta?
        NodoVarLocal nodoVarLocal = new NodoVarLocal(identificador, expresionInicial,tablaDeSimbolos.getBloqueActual(),tablaDeSimbolos.getClaseActual(), tablaDeSimbolos.getMetodoActual(),tablaDeSimbolos);
        return nodoVarLocal;
    }

    private NodoBase mReturn() throws Exception {
        Token ret = tokenActual;
        NodoRetorno nodoRetorno = null;
        match("pr_return");
        NodoBase nodoExp = expresionOpcional();
        nodoRetorno = new NodoRetorno(ret,nodoExp,tablaDeSimbolos.getMetodoActual(),tablaDeSimbolos,tablaDeSimbolos.getBloqueActual());
        return nodoRetorno;
    }

    private NodoBase expresionOpcional() throws Exception {
        NodoBase nodoExp = null;
        if(Arrays.asList("opSuma","opResta","opNegacion","pr_null","pr_true", "pr_false","intLiteral", "charLiteral", "stringLiteral", "pr_this", "idMetVar", "pr_new", "idClase", "abrirParentesis").contains(tokenActual.getIdentificador()))
            nodoExp = expresion();
        else{
            //No hago nada
        }
        return nodoExp;
    }
    private NodoIf mIf() throws Exception {
        NodoIf nodoIf = null;
        Token tkif = tokenActual;
        match("pr_if");
        match("abrirParentesis");
        NodoBase EX = expresion();
        match("cerrarParentesis");
        NodoBase SE = sentencia();
        nodoIf = new NodoIf(tkif,EX,SE,tablaDeSimbolos);
        mElse(nodoIf);
        return nodoIf;
    }

    private void mElse(NodoIf nodoIf) throws Exception {
        if(Arrays.asList("pr_else").contains(tokenActual.getIdentificador())) {
            match("pr_else");
            NodoBase SE = sentencia();
            nodoIf.setSentenciaElse(SE);
        }else{
            //No hago nada
        }
    }
    private NodoBase mWhile() throws Exception {
        NodoBase nodoWhile = null;
        Token tkwhile= tokenActual;
        match("pr_while");
        match("abrirParentesis");
        NodoBase EX = expresion();
        match("cerrarParentesis");
        NodoBase SE = sentencia();
        nodoWhile = new NodoWhile(tkwhile,EX,SE,tablaDeSimbolos);
        return nodoWhile;
    }

    private NodoBase expresion() throws Exception {
        NodoBase nodoExp = null;
        NodoBase nodoExpComp= expresionCompuesta();
        nodoExp = expresionPrima(nodoExpComp);
        return nodoExp;
    }
    private NodoBase expresionPrima(NodoBase izq) throws Exception {
        if(Arrays.asList("Asignacion", "opSuma","opResta","opNegacion","pr_null","pr_true", "pr_false","intLiteral", "charLiteral", "stringLiteral", "pr_this", "idMetVar", "pr_new", "idClase", "abrirParentesis").contains(tokenActual.getIdentificador())) {
            Token token = tokenActual;
            match("Asignacion");
            NodoBase der = expresion();
            NodoAsignacion nodoAsignacion = new NodoAsignacion(token,izq,der,tablaDeSimbolos);
            return nodoAsignacion;
        }else{
            return izq;
        }
    }
    private NodoBase expresionCompuesta() throws Exception {
        NodoBase nodoExp = null;
        nodoExp = expresionBasica();
        return expresionCompuestaPrima(nodoExp);
    }
    private NodoBase expresionCompuestaPrima(NodoBase nodoExpIzq) throws Exception {
        if(Arrays.asList("op||","op&&","Igual","opDistinto","Menor", "Mayor", "MenorIgual","MayorIgual","opSuma","opResta","opMultiplicar","opDivision","op%").contains(tokenActual.getIdentificador())) {
            Token opBin = operadorBinario();
            NodoBase nodoExpDer = expresionBasica();
            NodoExpBinaria nodoExpBinaria = new NodoExpBinaria(opBin,nodoExpIzq,nodoExpDer,tablaDeSimbolos);
            return expresionCompuestaPrima(nodoExpBinaria);
        }else{
            return nodoExpIzq;
        }
    }

    private Token operadorBinario() throws Exception {
        Token tkactual = tokenActual;
        if(tokenActual.getIdentificador().equals("op||")) {
            match("op||");
        }
        else if(tokenActual.getIdentificador().equals("op&&")) {
            match("op&&");
        } else if (tokenActual.getIdentificador().equals("Igual")) {
            match("Igual");
        }else if (tokenActual.getIdentificador().equals("opDistinto")) {
            match("opDistinto");
        }else if (tokenActual.getIdentificador().equals("Menor")) {
            match("Menor");
        }else if (tokenActual.getIdentificador().equals("Mayor")) {
            match("Mayor");
        }else if (tokenActual.getIdentificador().equals("MenorIgual")) {
            match("MenorIgual");
        }else if (tokenActual.getIdentificador().equals("MayorIgual")) {
            match("MayorIgual");
        }else if (tokenActual.getIdentificador().equals("opSuma")) {
            match("opSuma");
        }else if (tokenActual.getIdentificador().equals("opResta")) {
            match("opResta");
        }else if (tokenActual.getIdentificador().equals("opMultiplicar")) {
            match("opMultiplicar");
        }else if (tokenActual.getIdentificador().equals("opDivision")) {
            match("opDivision");
        }else if (tokenActual.getIdentificador().equals("op%")) {
            match("op%");
        }
        else throw new SintacticoExcepcion(tokenActual,"operadorBinario");
        return tkactual;
    }

    private NodoBase expresionBasica() throws Exception {
        NodoBase nodo = null;
        if(Arrays.asList("opSuma","opResta","opNegacion").contains(tokenActual.getIdentificador())) {
            Token opUn = operadorUnario();
            NodoBase ex = operando();
            nodo = new NodoExpUnaria(opUn,ex,tablaDeSimbolos);
        } else if (Arrays.asList("pr_null","pr_true", "pr_false","intLiteral", "charLiteral", "stringLiteral", "pr_this", "idMetVar", "pr_new", "idClase", "abrirParentesis").contains(tokenActual.getIdentificador())){
            nodo = operando();
        }else throw new SintacticoExcepcion(tokenActual,"operadorUnario o operando");
        return nodo;
    }

    private Token operadorUnario() throws Exception {
        Token tkactual = tokenActual;
        if(tokenActual.getIdentificador().equals("opSuma"))
            match("opSuma");
        else if(tokenActual.getIdentificador().equals("opResta"))
            match("opResta");
        else if(tokenActual.getIdentificador().equals("opNegacion"))
            match("opNegacion");
        else throw new SintacticoExcepcion(tokenActual,"operadorUnario");
        return tkactual;
    }

    private NodoBase operando() throws Exception {
        NodoBase nodoExp = null;
        if(Arrays.asList("pr_null","pr_true", "pr_false","intLiteral", "charLiteral", "stringLiteral").contains(tokenActual.getIdentificador()))
            nodoExp = literal();
        else if(Arrays.asList("pr_this", "idMetVar", "pr_new", "idClase", "abrirParentesis").contains(tokenActual.getIdentificador()))
            nodoExp = acceso();
        else throw new SintacticoExcepcion(tokenActual,"literal o un acceso ");
        return nodoExp;
    }

    private NodoExpLit literal() throws Exception {
        NodoExpLit nodoLit = new NodoExpLit(tokenActual,tablaDeSimbolos);
        if(tokenActual.getIdentificador().equals("pr_null")) {
            match("pr_null");
        }
        else if(tokenActual.getIdentificador().equals("pr_true")) {
            match("pr_true");
        } else if (tokenActual.getIdentificador().equals("pr_false")) {
            match("pr_false");
        }else if (tokenActual.getIdentificador().equals("intLiteral")) {
            match("intLiteral");
        }else if (tokenActual.getIdentificador().equals("charLiteral")) {
            match("charLiteral");
        }else if (tokenActual.getIdentificador().equals("stringLiteral")) {
            match("stringLiteral");
        }
        else throw new SintacticoExcepcion(tokenActual,"literal");
        return nodoLit;
    }

    private NodoAcceso acceso() throws Exception {
        NodoAcceso nodoAcceso = primario();
        encadenadoOpcional(nodoAcceso);
        return nodoAcceso;
    }

    private NodoAcceso primario() throws Exception {
        NodoAcceso nodoExp = null;
        if(tokenActual.getIdentificador().equals("pr_this")){
            nodoExp = accesoThis();
        }
        else if(tokenActual.getIdentificador().equals("idMetVar")) {
            Token idMetVar = tokenActual;
            match("idMetVar");
            NodoAcceso nodoAcceso = new NodoAcceso(idMetVar,tablaDeSimbolos,tablaDeSimbolos.getBloqueActual());
            metVarPrima(nodoAcceso);
            nodoExp = nodoAcceso;
        } else if (tokenActual.getIdentificador().equals("pr_new")) {
            nodoExp = accesoConstructor();
        }else if (tokenActual.getIdentificador().equals("idClase")) {
            nodoExp = accesoMetodoEstatico();
        }else if (tokenActual.getIdentificador().equals("abrirParentesis")) {
            nodoExp = expresionParentizada();
        }else throw new SintacticoExcepcion(tokenActual,"primario");
        return nodoExp;
    }
    private void metVarPrima(NodoAcceso nodoAcceso) throws Exception {

        if(Arrays.asList("abrirParentesis").contains(tokenActual.getIdentificador())) {
            nodoAcceso.setAccesoAMetodo();
            LinkedList<NodoBase> argsActuales = argsActuales();
            nodoAcceso.setArgsActuales(argsActuales);
        }else{
            //No hago nada
        }
    }

    private NodoAcceso accesoThis() throws Exception {
        Token tkThis = tokenActual;
        match("pr_this");
        return new NodoExpThis(tkThis,tablaDeSimbolos.getClaseActual(),tablaDeSimbolos.getMetodoActual(),tablaDeSimbolos,tablaDeSimbolos.getBloqueActual());
    }
    private NodoAcceso accesoConstructor() throws Exception {
        Token tkNew = tokenActual;
        match("pr_new");
        Token idClase = tokenActual;
        match("idClase");
        LinkedList<NodoBase> argsActuales = argsActuales();
        return new NodoExpAccesoConstructor(tkNew,idClase, argsActuales,tablaDeSimbolos,tablaDeSimbolos.getBloqueActual());
    }
    private NodoAcceso expresionParentizada() throws Exception {
        Token tkAbrir = tokenActual;
        match("abrirParentesis");
        NodoBase nodoExp = expresion();
        match("cerrarParentesis");
        return new NodoExpParentizada(tkAbrir,nodoExp,tablaDeSimbolos);
    }
    private NodoAcceso accesoMetodoEstatico() throws Exception {
        Token idClase = tokenActual;
        match("idClase");
        match("punto");
        Token idMetVar = tokenActual;
        match("idMetVar");
        LinkedList<NodoBase> argsActuales = argsActuales();
        return new NodoExpAccesoMetodoEstatico(idClase, idMetVar, argsActuales,tablaDeSimbolos, tablaDeSimbolos.getBloqueActual());
    }

    private LinkedList<NodoBase> argsActuales() throws Exception {
        match("abrirParentesis");
        LinkedList<NodoBase> argsActuales = listaExpsOpcional();
        match("cerrarParentesis");
        return argsActuales;
    }

    private LinkedList<NodoBase> listaExpsOpcional() throws Exception {
        LinkedList<NodoBase> argsActuales = new LinkedList<NodoBase>();
        if(Arrays.asList("opSuma","opResta","opNegacion","pr_null","pr_true", "pr_false","intLiteral", "charLiteral", "stringLiteral", "pr_this", "idMetVar", "pr_new", "idClase", "abrirParentesis").contains(tokenActual.getIdentificador())) {
            argsActuales = listaExps();
        }else{
            //No hago nada
        }
        return argsActuales;
    }

    private LinkedList<NodoBase> listaExps() throws Exception {
        LinkedList<NodoBase> listaExps = new LinkedList<>();
        NodoBase expresion = expresion();
        listaExps.add(expresion);
        listaExpsPrima(listaExps);
        return listaExps;
    }

    private void listaExpsPrima(LinkedList<NodoBase> listaExps) throws Exception {
        if (Arrays.asList("coma").contains(tokenActual.getIdentificador())) {
            match("coma");
            listaExps.addAll(listaExps());
        } else {
            // No hago nada
        }
    }
    private void encadenadoOpcional(NodoAcceso nodoAcceso) throws Exception {
        if (tokenActual.getIdentificador().equals("punto")) {
            match("punto");
            Token idMetVar = tokenActual;
            match("idMetVar");
            NodoAcceso nodoEncadenado = new NodoAcceso(idMetVar,tablaDeSimbolos,tablaDeSimbolos.getBloqueActual());
            argumentosOpcionales(nodoEncadenado);
            nodoAcceso.setEncadenadoHijo(nodoEncadenado);
            encadenadoOpcional(nodoEncadenado);
        } else {
            //No hago nada
        }
    }

    private void argumentosOpcionales(NodoAcceso nodoAcceso) throws Exception {
        if(Arrays.asList("abrirParentesis").contains(tokenActual.getIdentificador())) {
            nodoAcceso.setAccesoAMetodo();
            nodoAcceso.setArgsActuales(argsActuales());
        }else{
            //No hago nada
        }
    }

}