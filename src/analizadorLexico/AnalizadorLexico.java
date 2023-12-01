package analizadorLexico;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AnalizadorLexico {
    String lexema;
    char caracterActual;
    GestorDeArchivo gestorDeFuente;

    Map<String,Token> tablaPalabrasClaves=null;

    public Token proximoToken() throws Exception {
            if (!gestorDeFuente.isEof()) {
                lexema = "";
                return e0();
            } else {
                return e5();
            }
    }
    private void llenarTablaPalabrasClaves(){
        tablaPalabrasClaves.put("class",new Token("pr_class","class",0));
        tablaPalabrasClaves.put("public",new Token("pr_public","public",0));
        tablaPalabrasClaves.put("void",new Token("pr_void","void",0));
        tablaPalabrasClaves.put("if",new Token("pr_if","if",0));
        tablaPalabrasClaves.put("this",new Token("pr_this","this",0));
        tablaPalabrasClaves.put("interface",new Token("pr_interface","interface",0));
        tablaPalabrasClaves.put("static",new Token("pr_static","static",0));
        tablaPalabrasClaves.put("boolean",new Token("pr_boolean","boolean",0));
        tablaPalabrasClaves.put("else",new Token("pr_else","else",0));
        tablaPalabrasClaves.put("new",new Token("pr_new","new",0));
        tablaPalabrasClaves.put("extends",new Token("pr_extends","extends",0));
        tablaPalabrasClaves.put("char",new Token("pr_char","char",0));
        tablaPalabrasClaves.put("while",new Token("pr_while","while",0));
        tablaPalabrasClaves.put("null",new Token("pr_null","null",0));
        tablaPalabrasClaves.put("implements",new Token("pr_implements","implements",0));
        tablaPalabrasClaves.put("int",new Token("pr_int","int",0));
        tablaPalabrasClaves.put("return",new Token("pr_return","return",0));
        tablaPalabrasClaves.put("true",new Token("pr_true","true",0));
        tablaPalabrasClaves.put("var",new Token("pr_var","var",0));
        tablaPalabrasClaves.put("false",new Token("pr_false","false",0));
    }
    private void actualizarLexema(){
        lexema = lexema + caracterActual;
    }
    private void actualizarCaracterActual() throws IOException {
        caracterActual = gestorDeFuente.readNextCharacter();
    }
    private Token e0() throws LexicoExcepcion,IOException {
        Token retorno = null;
        if(Character.isDigit(caracterActual)){
            actualizarLexema();
            actualizarCaracterActual();
            retorno = e1(1);
        }else if(Character.isUpperCase(caracterActual)){
            actualizarLexema();
            actualizarCaracterActual();
            retorno = e2();
        }else if(Character.isLowerCase(caracterActual)){
            actualizarLexema();
            actualizarCaracterActual();
            retorno = e27();
        } else if(caracterActual == '>'){
            actualizarLexema();
            actualizarCaracterActual();
            retorno = e3();
        }else if(gestorDeFuente.isEof()){
            retorno = e5();
        }else if(Character.isWhitespace(caracterActual) || caracterActual == '\r' || caracterActual == '\n'){
            actualizarCaracterActual();
            retorno = e0();
        }else if(caracterActual == '<'){
            actualizarLexema();
            actualizarCaracterActual();
            retorno = e6();
        }else if(caracterActual == '=') {
            actualizarLexema();
            actualizarCaracterActual();
            retorno = e8();
        }else if(caracterActual == '!') {
            actualizarLexema();
            actualizarCaracterActual();
            retorno = e10();
        }else if(caracterActual == '+') {
            actualizarLexema();
            actualizarCaracterActual();
            retorno = e11();
        }else if(caracterActual == '-') {
            actualizarLexema();
            actualizarCaracterActual();
            retorno = e36();
        }else if(caracterActual == '&') {
            actualizarLexema();
            actualizarCaracterActual();
            retorno = e23();
        }else if(caracterActual == '%') {
            actualizarLexema();
            actualizarCaracterActual();
            retorno = e24();
        }else if(caracterActual == '|') {
            actualizarLexema();
            actualizarCaracterActual();
            retorno = e25();
        }else if (caracterActual==('\'')) {
            actualizarLexema();
            actualizarCaracterActual();
            return e31();
        }else if (caracterActual==('/')) {
            actualizarLexema();
            actualizarCaracterActual();
            return e35();
        }
        else if(caracterActual == '*') {
            actualizarLexema();
            actualizarCaracterActual();
            retorno = e13();
        }else if(caracterActual == '"') {
            actualizarLexema();
            actualizarCaracterActual();
            retorno = e28();
        } else if(caracterActual == '('){
        actualizarLexema();
        actualizarCaracterActual();
        retorno = e16();
        }else if(caracterActual == ')') {
        actualizarLexema();
        actualizarCaracterActual();
        retorno = e17();
        }else if(caracterActual == '{') {
        actualizarLexema();
        actualizarCaracterActual();
        retorno = e18();
        }else if(caracterActual == '}') {
        actualizarLexema();
        actualizarCaracterActual();
        retorno = e19();
        }else if(caracterActual == ';') {
        actualizarLexema();
        actualizarCaracterActual();
        retorno = e20();
        }else if(caracterActual == ',') {
        actualizarLexema();
        actualizarCaracterActual();
        retorno = e21();
        }else if(caracterActual == '.') {
        actualizarLexema();
        actualizarCaracterActual();
        retorno = e22();
    }
        else{
            actualizarLexema();
            actualizarCaracterActual();
            throw new LexicoExcepcion(lexema, gestorDeFuente.getCurrentLineNumber(),"El Caracter no es valido");
        }
        return retorno;
    }

    private Token e35() throws IOException, LexicoExcepcion {
        if(caracterActual=='='){
            actualizarLexema();
            actualizarCaracterActual();
            return e38();
        }
        else if(caracterActual=='/'){
            lexema="";
            actualizarCaracterActual();
            return e39();
        }
        else if(caracterActual=='*'){
            actualizarCaracterActual();
            return e40(false);
        }
        else{
            return new Token("opDivision",lexema, gestorDeFuente.getCurrentLineNumber());
        }
    }

    private Token e40(boolean encontreAsterisco) throws IOException, LexicoExcepcion {
        if(caracterActual=='*') {
            lexema="";
            actualizarCaracterActual();
            encontreAsterisco=true;
            return e40(encontreAsterisco);
        }else if (caracterActual=='/' && encontreAsterisco) {
            lexema="";
            actualizarCaracterActual();
            return e0();
        }else if(caracterActual!='/'){
            actualizarCaracterActual();
            encontreAsterisco=false;
            return e40(encontreAsterisco);
        }
        else {
            throw new LexicoExcepcion(lexema, gestorDeFuente.getCurrentLineNumber(), "Comentario de varias líneas sin cerrar");
        }
    }

    private Token e39() throws LexicoExcepcion, IOException {
        if(caracterActual=='\n'){
            actualizarCaracterActual();
            return e0();
        }
        else{
            actualizarCaracterActual();
           return e39();
        }
    }

    private Token e31() throws IOException, LexicoExcepcion {
        if (caracterActual!='\''&& caracterActual!='\\'&& caracterActual!='\r' && caracterActual!='\n'){
            actualizarLexema();
            actualizarCaracterActual();
            return e32();
        }  else if (caracterActual=='\r' || caracterActual=='\n') {
            throw new LexicoExcepcion(lexema, gestorDeFuente.getCurrentLineNumber(),"Es invalido saltar de linea en un Char");
        }else {
            actualizarLexema();
            throw new LexicoExcepcion(lexema, gestorDeFuente.getCurrentLineNumber(), "No valido al ser vacio el charLiteral");
        }
    }

    private Token e32() throws IOException, LexicoExcepcion {
        if(caracterActual=='\'') {
            actualizarLexema();
            actualizarCaracterActual();
            return e34();
        }
        else {
            if (!gestorDeFuente.isEof() && caracterActual!='\r' && caracterActual!='\n'){
                actualizarLexema();
            }
            throw new LexicoExcepcion(lexema, gestorDeFuente.getCurrentLineNumber(), "El caracter no es valido");

        }
    }

    private Token e34() {
        return new Token("charLiteral",lexema, gestorDeFuente.getCurrentLineNumber());
    }

    private Token e28() throws IOException, LexicoExcepcion {
        if(!gestorDeFuente.isEof()) {
            if (caracterActual=='"'){
                actualizarLexema();
                actualizarCaracterActual();
                return e29();
            } else if (caracterActual=='\\') {
                actualizarLexema();
                actualizarCaracterActual();
                return e30();
            } else if (caracterActual!='\r'&& caracterActual!='\n') {
                actualizarLexema();
                actualizarCaracterActual();
                return e28();
            }
            else {
                throw new LexicoExcepcion(lexema, gestorDeFuente.getCurrentLineNumber(), "Es invalido saltar de linea en String ");
            }
        }
        else {
            throw new LexicoExcepcion(lexema, gestorDeFuente.getCurrentLineNumber(),"Se llego al EOF String sin cerrar");
        }
    }

    private Token e30() throws IOException, LexicoExcepcion {
        if (caracterActual!='\r'&& caracterActual!='\n') {
            actualizarLexema();
            actualizarCaracterActual();
            return e28();
        }
        else {
            throw new LexicoExcepcion(lexema, gestorDeFuente.getCurrentLineNumber(),"Es invalido saltar de linea en String ");
        }
    }

    private Token e29() {
        return new Token("stringLiteral",lexema,gestorDeFuente.getCurrentLineNumber());
    }

    private Token e27() throws IOException {
        if (Character.isLetterOrDigit(caracterActual) || caracterActual == '_'){
            actualizarLexema();
            actualizarCaracterActual();
            return e27();
        } else if (tablaPalabrasClaves.containsKey(lexema)) {
            Token tokenReserved=tablaPalabrasClaves.get(lexema);
            tokenReserved.setNroLinea(gestorDeFuente.getCurrentLineNumber());
            return new Token(tokenReserved.getIdentificador(),tokenReserved.getLexema(),tokenReserved.getNroLinea());
        } else {
            return new Token("idMetVar",lexema,gestorDeFuente.getCurrentLineNumber());
        }
    }

    private Token e25() throws IOException, LexicoExcepcion {
        if(caracterActual=='|'){
            actualizarLexema();
            actualizarCaracterActual();
            return new Token("op||",lexema,gestorDeFuente.getCurrentLineNumber());
        }
        throw new LexicoExcepcion(lexema, gestorDeFuente.getCurrentLineNumber(),"Falto otro |");
    }

    private Token e24() {
        return new Token("op%",lexema,gestorDeFuente.getCurrentLineNumber());
    }

    private Token e23() throws IOException, LexicoExcepcion {
        if (caracterActual=='&') {
            actualizarLexema();
            actualizarCaracterActual();
            return new Token("op&&",lexema, gestorDeFuente.getCurrentLineNumber());
        }
        else {
            throw new LexicoExcepcion(lexema, gestorDeFuente.getCurrentLineNumber(),"Falto ingresar otro &");
        }
    }

    private Token e16() {
        return new Token("abrirParentesis",lexema,gestorDeFuente.getCurrentLineNumber());
    }
    private Token e17() {
        return new Token("cerrarParentesis",lexema,gestorDeFuente.getCurrentLineNumber());
    }
    private Token e18() {
        return new Token("abrirLlave",lexema,gestorDeFuente.getCurrentLineNumber());
    }
    private Token e19() {
        return new Token("cerrarLlave",lexema,gestorDeFuente.getCurrentLineNumber());
    }
    private Token e20() {
        return new Token("puntoYComa",lexema,gestorDeFuente.getCurrentLineNumber());
    }
    private Token e21() {
        return new Token("coma",lexema,gestorDeFuente.getCurrentLineNumber());
    }
    private Token e22() {
        return new Token("punto",lexema,gestorDeFuente.getCurrentLineNumber());
    }

    private Token e13() throws IOException {
        if(caracterActual=='='){
            actualizarLexema();
            actualizarCaracterActual();
            return e37();
        }
        else{
            return new Token("opMultiplicar",lexema, gestorDeFuente.getCurrentLineNumber());
        }
    }

    private Token e37() {
        return new Token("opMultiplicarYAsignar",lexema, gestorDeFuente.getCurrentLineNumber());
    }
    private Token e38() {
        return new Token("opDividirYAsignar",lexema, gestorDeFuente.getCurrentLineNumber());
    }
    private Token e12() {
        return new Token("opRestaYAsignar",lexema, gestorDeFuente.getCurrentLineNumber());
    }

    private Token e36() throws IOException {
        if(caracterActual=='='){
            actualizarLexema();
            actualizarCaracterActual();
            return e12();
        }
        else{
            return new Token("opResta",lexema, gestorDeFuente.getCurrentLineNumber());
        }
    }
    private Token e11() throws IOException {
        if(caracterActual=='='){
            actualizarLexema();
            actualizarCaracterActual();
            return e15();
        }
        else{
            return new Token("opSuma",lexema, gestorDeFuente.getCurrentLineNumber());
        }
    }

    private Token e15() {
        return new Token("SumaYAsignar",lexema, gestorDeFuente.getCurrentLineNumber());
    }

    private Token e10() throws IOException {
        if(caracterActual=='='){
            actualizarLexema();
            actualizarCaracterActual();
            return e14();
        }
        else{
            return new Token("opNegacion",lexema, gestorDeFuente.getCurrentLineNumber());
        }
    }

    private Token e14() {
        return new Token("opDistinto",lexema, gestorDeFuente.getCurrentLineNumber());
    }

    private Token e8() throws IOException {
        if(caracterActual=='='){
            actualizarLexema();
            actualizarCaracterActual();
            return e9();
        }
        else{
            return new Token("Asignacion",lexema, gestorDeFuente.getCurrentLineNumber());
        }
    }

    private Token e9() {
        return new Token("Igual",lexema, gestorDeFuente.getCurrentLineNumber());
    }

    private Token e1(int nDigitos) throws IOException, LexicoExcepcion {
        if(Character.isDigit(caracterActual) && nDigitos<=9){
            actualizarLexema();
            actualizarCaracterActual();
            nDigitos++;
            return e1(nDigitos);
        }
        else if(Character.isDigit(caracterActual)&& nDigitos>9){
            actualizarLexema();
            actualizarCaracterActual();
            throw new LexicoExcepcion(lexema, gestorDeFuente.getCurrentLineNumber(), "Entero con mas de 9 digitos");
        }
        else{
            return new Token("intLiteral", lexema, gestorDeFuente.getCurrentLineNumber());
        }
    }
    private Token e2() throws IOException {
        if(Character.isLetterOrDigit(caracterActual) || caracterActual == '_'){
            actualizarLexema();
            actualizarCaracterActual();
            return e2();
        }else{
            return new Token("idClase",lexema,gestorDeFuente.getCurrentLineNumber());
        }
    }
    private Token e3() throws IOException {
        if(caracterActual == '='){
            actualizarLexema();
            actualizarCaracterActual();
            return e4();
        }
        else{
            return new Token("Mayor",lexema, gestorDeFuente.getCurrentLineNumber());
        }
    }
    private Token e4(){
        return new Token("MayorIgual",lexema, gestorDeFuente.getCurrentLineNumber());
    }
    private Token e5(){
        return new Token("Eof","", gestorDeFuente.getCurrentLineNumber());
    }

    private Token e6() throws IOException {
        if(caracterActual == '='){
            actualizarLexema();
            actualizarCaracterActual();
            return e7();
        }
        else{
            return new Token("Menor",lexema, gestorDeFuente.getCurrentLineNumber());
        }
    }
    private Token e7(){
        return new Token("MenorIgual",lexema, gestorDeFuente.getCurrentLineNumber());
    }

    public AnalizadorLexico(GestorDeArchivo gestor) throws IOException {
        gestorDeFuente = gestor;
        actualizarCaracterActual();
        tablaPalabrasClaves = new HashMap<>();
        llenarTablaPalabrasClaves();
    }


}
