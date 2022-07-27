package com.example;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for simple App.
 */
public class AppTest {
    /**
     * Rigorous Test.
     */
    @Test
    public void testApp() {
        assertEquals(1, 1);
    }

    @Test
    public void testListDirectoriesAndFilesApp() {
        String result = App.listDirectoriesAndFiles();

        final String[] name = new String[1];
        File[] files = new File(".").listFiles();
        assertNotNull(files);
        Optional<File> maybeFile = Arrays.stream(files).findFirst();
        maybeFile.ifPresent(file -> name[0] = file.getName());
        assertTrue(result.contains(name[0]));
    }
}
