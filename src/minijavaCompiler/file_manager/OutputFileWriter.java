package minijavaCompiler.file_manager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static minijavaCompiler.Main.symbolTable;

public class OutputFileWriter {

    private File file;
    private FileWriter fileWriter;

    public OutputFileWriter(String filePath) throws FileManagerException {
        file = new File(filePath);
        try {
            file.createNewFile();
            fileWriter = new FileWriter(file);
        } catch (IOException e) {
            throw new FileManagerException("Error: no se ha podido crear el archivo");
        }
    }

    public void writeCodeToFile() throws FileManagerException {
        try {
            //writeIntro();
            for (String line : symbolTable.ceiASM_instructionList)
                fileWriter.write(line+"\n");
            fileWriter.close();
        } catch (IOException e) {
            throw new FileManagerException("Error: no se ha podido escribir el archivo");
        }
    }

    private void writeIntro() throws IOException {
        fileWriter.write("# Codigo CeIASM para programa MiniJava "+file.getName()+"\n");
        fileWriter.write("# Correr usando maquina CeIVM | UNS 2011 - 2022"+"\n");
        fileWriter.write("\n");
    }
}
