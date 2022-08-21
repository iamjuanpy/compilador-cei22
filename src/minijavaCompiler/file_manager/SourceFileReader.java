package minijavaCompiler.file_manager;

import java.io.*;

public class SourceFileReader {

    private final char EOF = 0;
    private final char EOL = '\n';

    private BufferedReader bufferedReader;
    private String currentLine;
    private char currentChar;
    private int lineNumber;
    private int colNumber;

    public SourceFileReader(String filePath) throws SourceFileReaderException {
        try  {
            bufferedReader = new BufferedReader(new FileReader(filePath));
            lineNumber = 0;
            colNumber = 0;
            currentLine = null;
        } catch (FileNotFoundException e){
            throw new SourceFileReaderException("Error: wrong file name");
        }
    }

    public char readCharacter(){
        try {
            if (currentLine == null || colNumber == currentLine.length()) {
                if ((currentLine = bufferedReader.readLine()) != null) {
                    currentLine = currentLine + EOL; // ver doc
                    lineNumber++;
                    colNumber = 0;
                } else {
                    return EOF;
                }
            }
            currentChar = currentLine.charAt(colNumber);
            colNumber++;
        } catch (IOException e){ } // ver que hacer con esta excep

        return currentChar;
    }

    public boolean isEOF(char character){
        return character == EOF;
    }

    public boolean isEOL(char character){
        return character == EOL;
    }

    public int getLineNumber(){
        return lineNumber;
    }

    public int getColNumber(){
        return colNumber;
    }

}
