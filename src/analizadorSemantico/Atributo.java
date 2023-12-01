package analizadorSemantico;

import NodosAST.NodoBloque;
import analizadorLexico.Token;

public class Atributo {
    protected Token tkAtributo;
    protected String nombre;
    protected Tipo tipo;
    protected NodoBloque bloqueAtributo;
    protected int offset;
    protected boolean tieneOffset;
    protected boolean statico;
    protected boolean varLocal;
    protected boolean parametro;
    protected boolean esAtributo;

    public Atributo(Token tkAtributo, Tipo tipo)
    {
        this.tkAtributo = tkAtributo;
        nombre = tkAtributo.getLexema();
        this.tipo = tipo;
        statico= true;
        this.bloqueAtributo = bloqueAtributo;
        this.tieneOffset = false;
        this.varLocal = false;
        this.parametro = false;
        this.esAtributo = false;
    }
    public Token getTkAtributo() {
        return tkAtributo;
    }
    public void setOffset(int i) {
        this.offset = i;
        this.tieneOffset = true;
    }
    public int getOffset() {
        return offset;
    }
    public boolean isTieneOffset() {
        return tieneOffset;
    }
    public boolean isStatico() {
        return statico;
    }

    public void setStatico(boolean statico) {
        this.statico = statico;
    }
    public String getNombre() {
        return nombre;
    }

    public Tipo getTipo() {
        return tipo;
    }
    public NodoBloque getBloqueAtributo() {
        return bloqueAtributo;
    }

    public String toString() {
        return "Atributo: " + nombre + " Tipo: " + tipo.getToken().getLexema();
    }

    public boolean esAtributo() {
        return esAtributo;
    }
    public void setVarLocal(boolean varLocal) {
        this.varLocal = varLocal;
    }
    public boolean isVarLocal() {
        return varLocal;
    }
    public boolean isParametro() {
        return parametro;
    }
    public void setParametro(boolean parametro) {
        this.parametro = parametro;
    }
    public void setEsAtributo(boolean esAtributo) {
        this.esAtributo = esAtributo;
    }

}
