package com.github.mrzhqiang.randall.gamecenter.util;

import com.github.mrzhqiang.randall.gamecenter.Share;
import com.github.mrzhqiang.randall.gamecenter.dialog.AlertDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * @author mrzhqiang
 */
public enum Programs {
    ;

    public static final Logger LOGGER = LoggerFactory.getLogger("gamecenter");

    public static int execute(Share.Program program) {
        ProcessBuilder builder = new ProcessBuilder(program.programFile, String.valueOf(program.mainFormX), String.valueOf(program.mainFormY));
        builder.directory(new File(program.directory));
        try {
            program.process = builder.start();
        } catch (IOException e) {
            LOGGER.error("execute failed: " + program.programFile, e);
            AlertDialog.showError(e);
            return -1;
        }
        return 0;
    }
}
