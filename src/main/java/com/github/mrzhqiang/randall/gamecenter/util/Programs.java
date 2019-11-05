package com.github.mrzhqiang.randall.gamecenter.util;

import com.github.mrzhqiang.randall.gamecenter.Share;
import com.github.mrzhqiang.randall.gamecenter.dialog.AlertDialog;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Scheduler;
import io.reactivex.rxjavafx.observables.JavaFxObservable;
import io.reactivex.rxjavafx.schedulers.JavaFxScheduler;
import io.reactivex.schedulers.Schedulers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author mrzhqiang
 */
public enum Programs {
    ;

    public static final Logger LOGGER = LoggerFactory.getLogger("gamecenter");

    public static int execute(Share.Program program) {
        return execute(program, null);
    }

    public static int execute(Share.Program program, OnProgramListener listener) {
        Preconditions.checkNotNull(program, "program == null");

        ProcessBuilder builder = new ProcessBuilder(program.programFile, program.mainFormX.toString(), program.mainFormY.toString());
        if (!Strings.isNullOrEmpty(program.directory)) {
            builder.directory(new File(program.directory));
        }
        try {
            program.process = builder.start();
        } catch (Exception e) {
            AlertDialog.showError("执行程序失败: " + program.programFile, e);
            return -1;
        }
        Observable.create((ObservableOnSubscribe<String>) observableEmitter -> {
            InputStream inputStream = program.process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                observableEmitter.onNext(line);
            }
            observableEmitter.onComplete();
        }).subscribeOn(Schedulers.io())
                .observeOn(JavaFxScheduler.platform())
                .subscribe(listener::onResult);
        return 0;
    }
}
