package com.github.mrzhqiang.randall.gamecenter.util;

import com.github.mrzhqiang.randall.gamecenter.Share;
import org.junit.Test;

import java.io.*;
import java.nio.charset.Charset;

import static org.junit.Assert.*;

/**
 * @author mrzhqiang
 */
public class ProgramsTest {

    @Test
    public void execute() {
        Share.Program program = new Share.Program();
        program.directory = "C:\\Windows";
        program.programFile = "notepad.exe";
        Programs.execute(program, System.out::println);
        assertTrue(program.process.isAlive());
    }

    @Test
    public void executePing() throws IOException {
        Process process = Runtime.getRuntime().exec("ping 127.0.0.1");
        assertTrue(process.isAlive());
        InputStream inputStream = process.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("GBK")));
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }
//        int code = process.waitFor();
//        System.out.println("code: " + code);
    }
}