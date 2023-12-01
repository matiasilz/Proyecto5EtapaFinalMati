package analizadorLexico;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class GestorDeArchivo {
    private FileReader fileReader;
    private int lineNumber;
    private int characterPosition;
    private char character = ' ';

    private boolean eof;

    public GestorDeArchivo(File file) throws IOException {
        fileReader = new FileReader(file);
        lineNumber = 1;
        characterPosition = 0;
    }

    public char readNextCharacter() throws IOException {
        int charCode = fileReader.read();
        eof = false;
        if (charCode == -1) {
            eof = true;
            close();
            return '\0'; // Return null character to indicate end of file
        }
        if (character == '\n') {
            lineNumber++;
            characterPosition = 0;
        } else {
            characterPosition++;
        }
        character = (char) charCode;

        return character;
    }

    public int getCurrentLineNumber() {
        return lineNumber;
    }

    public int getCurrentCharacterPosition() {
        return characterPosition;
    }

    public void close() throws IOException {
        if (fileReader != null) {
            fileReader.close();
            fileReader = null;
        }
    }

    public boolean isEof() {
        return eof;
    }
}

