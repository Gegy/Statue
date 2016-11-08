package net.gegy1000.statue.server.api;

import java.io.File;

public class ImportableFile implements ImportFile {
    private File file;

    public ImportableFile(File file) {
        this.file = file;
    }

    public File get() {
        return this.file;
    }

    @Override
    public String getName() {
        return this.file.getName();
    }
}
