package minijavaCompiler.file_manager;

import java.io.*;

public class SourceFileReader {

    private final char EOF = (char) -1;
    private final char EOL = '\n';

    private BufferedReader bufferedReader;
    private String newLine;

    private String currentLine;
    private char currentChar;
    private int lineNumber;
    private int colNumber;

    public SourceFileReader(String filePath) throws SourceFileReaderException {
        try  {
            bufferedReader = new BufferedReader(new FileReader(filePath));
            lineNumber = 0;
            colNumber = 0;
            newLine = null;
            currentLine = null;
        } catch (FileNotFoundException e){
            throw new SourceFileReaderException("Error: wrong file name");
        }
    }

    public char readCharacter() throws SourceFileReaderException {
        if (currentChar == EOF) return EOF;
        try {
            if (currentLine == null || currentChar == EOL) {
                if ((newLine = bufferedReader.readLine()) != null) {
                    currentLine = newLine + EOL;
                    lineNumber++;
                    colNumber = 0;
                } else {
                    bufferedReader.close();
                    currentChar = EOF;
                    return EOF;
                }
            }
            currentChar = currentLine.charAt(colNumber);
            colNumber++;
        } catch (IOException e){
            throw new SourceFileReaderException("Error reading file");
        }
        return currentChar;
    }

    public boolean isEOF(char character){
        return character == EOF;
    }

    public boolean isEOL(char character){
        return character == EOL;
    }

    public String getCurrentLine(){return currentLine;}

    public int getLineNumber(){
        return lineNumber;
    }

    public int getColNumber(){
        return colNumber;
    }

}
