package com.github.mrzhqiang.randall.gamecenter;

import com.github.mrzhqiang.randall.gamecenter.dialog.AlertDialog;
import com.github.mrzhqiang.randall.gamecenter.util.Programs;
import helper.DateTimeHelper;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public final class Controller {
    private static final Logger LOGGER = LoggerFactory.getLogger("gamecenter");

    public static final int STOPPED_STATE = 0;
    public static final int STARTING_STATE = 1;
    public static final int RUNNING_STATE = 2;
    public static final int STOPPING_STATE = 3;
    public static final int ERROR_STATE = 9;

    public TabPane mainTabPane;
    public CheckBox dbServerCheckBox;
    public CheckBox loginSrvCheckBox;
    public CheckBox m2ServerCheckBox;
    public CheckBox logServerCheckBox;
    public CheckBox gameGateCheckBox1;
    public CheckBox gameGateCheckBox2;
    public CheckBox gameGateCheckBox3;
    public CheckBox gameGateCheckBox4;
    public CheckBox gameGateCheckBox5;
    public CheckBox gameGateCheckBox6;
    public CheckBox gameGateCheckBox7;
    public CheckBox gameGateCheckBox8;
    public CheckBox selGateCheckBox1;
    public CheckBox selGateCheckBox2;
    public CheckBox loginGateCheckBox;
    public ComboBox<StartMode> startModeComboBox;
    public Spinner<Integer> hoursSpinner;
    public Spinner<Integer> minutesSpinner;
    public TextArea gameInfoTextArea;
    public Button btnStartGame;

    public TabPane configTabPane;

    private boolean opened = false;
    // 0 -- default; 1 -- starting; 2 -- running; 3 -- stopping; 9 -- error
    private int startState = 0;
    // 0 -- disabled; 1 -- enabled;
    private int backupState = 0;

    private long refTick;
    private long showTick;

    private final static Share share = new Share();
    private long runTick;

    private final Timer startGameTimer = new Timer();
    private final Timer stopGameTimer = new Timer();
    private final Timer checkRunTimer = new Timer();

    @FXML
    public void initialize() {
        opened = false;
        mainTabPane.getSelectionModel().select(0);
        configTabPane.getSelectionModel().select(0);
        startState = 0;
        backupState = 0;
        gameInfoTextArea.clear();
        refTick = System.currentTimeMillis();
        share.loadConfig();
        loadBackupList();
        refBackupListToView();
        if (!startService()) {
            Platform.exit();
        }
        refGameConsole();
        // todo backup check from autoRunBakEnabled

        opened = true;
        if (share.autoRunBakEnabled) {
            // todo run backup service
        }
        startModeComboBox.setItems(FXCollections.observableArrayList(StartMode.values()));
        startModeComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            hoursSpinner.setDisable(newValue.equals(StartMode.NORMAL));
            minutesSpinner.setDisable(newValue.equals(StartMode.NORMAL));
        });
        startModeComboBox.getSelectionModel().select(StartMode.NORMAL);
    }

    private void refGameConsole() {
        opened = false;
        m2ServerCheckBox.setSelected(share.config.m2Server.getStart);
        dbServerCheckBox.setSelected(share.config.dbServer.getStart);
        loginSrvCheckBox.setSelected(share.config.loginSrv.getStart);
        logServerCheckBox.setSelected(share.config.logServer.getStart);
        loginGateCheckBox.setSelected(share.config.loginGate.getStart);
        selGateCheckBox1.setSelected(share.config.selGate.getStart1);
        selGateCheckBox2.setSelected(share.config.selGate.getStart2);
        gameGateCheckBox1.setSelected(share.config.runGate.getStart[0]);
        gameGateCheckBox2.setSelected(share.config.runGate.getStart[1]);
        gameGateCheckBox3.setSelected(share.config.runGate.getStart[2]);
        gameGateCheckBox4.setSelected(share.config.runGate.getStart[3]);
        gameGateCheckBox5.setSelected(share.config.runGate.getStart[4]);
        gameGateCheckBox6.setSelected(share.config.runGate.getStart[5]);
        gameGateCheckBox7.setSelected(share.config.runGate.getStart[6]);
        gameGateCheckBox8.setSelected(share.config.runGate.getStart[7]);

        // todo config view refresh

        opened = true;
    }

    private boolean startService() {
        mainOutMessage("正在启动游戏客户端控制器...");
        showTick = System.currentTimeMillis();
        mainOutMessage("游戏控制台启动完成...");
        return true;
    }

    private void mainOutMessage(String message) {
        gameInfoTextArea.appendText("[" + DateTimeHelper.format(new Date()) + "] --" + message + "\r\n");
    }

    public void onDBServerClicked(ActionEvent actionEvent) {

    }

    public void onLoginSrvClicked(ActionEvent actionEvent) {

    }

    public void onM2ServerClicked(ActionEvent actionEvent) {

    }

    public void onLogServerClicked(ActionEvent actionEvent) {

    }

    public void onRunGate1Clicked(ActionEvent actionEvent) {

    }

    public void onRunGate2Clicked(ActionEvent actionEvent) {

    }

    public void onRunGate3Clicked(ActionEvent actionEvent) {

    }

    public void onRunGate4Clicked(ActionEvent actionEvent) {

    }

    public void onRunGate5Clicked(ActionEvent actionEvent) {

    }

    public void onRunGate6Clicked(ActionEvent actionEvent) {

    }

    public void onRunGate7Clicked(ActionEvent actionEvent) {

    }

    public void onRunGate8Clicked(ActionEvent actionEvent) {

    }

    public void onSelGate1Clicked(ActionEvent actionEvent) {

    }

    public void onSelGate2Clicked(ActionEvent actionEvent) {

    }

    public void onLoginGateClicked(ActionEvent actionEvent) {

    }

    public void onStartGameClicked() {
        switch (startState) {
            case STOPPED_STATE:
                AlertDialog.waitConfirm("是否确认启动游戏服务器？")
                        .ifPresent(buttonType -> startGame());
                break;
            case STARTING_STATE:
                break;
            case RUNNING_STATE:
                break;
            case STOPPING_STATE:
                break;
            case ERROR_STATE:
                break;
        }
    }

    private void startGame() {
        runTick = System.currentTimeMillis();
        share.dbServer.getStart = share.config.dbServer.getStart;
        share.dbServer.reStart = true;
        share.dbServer.directory = share.gameDirectory + "DBServer\\";
        share.dbServer.programFile = share.config.dbServer.programFile;
        share.dbServer.mainFormX = share.config.dbServer.mainFormX;
        share.dbServer.mainFormY = share.config.dbServer.mainFormY;

        share.loginServer.getStart = share.config.loginSrv.getStart;
        share.loginServer.reStart = true;
        share.loginServer.directory = share.gameDirectory + "LoginSrv\\";
        share.loginServer.programFile = share.config.loginSrv.programFile;
        share.loginServer.mainFormX = share.config.loginSrv.mainFormX;
        share.loginServer.mainFormY = share.config.loginSrv.mainFormY;

        share.logServer.getStart = share.config.logServer.getStart;
        share.logServer.reStart = true;
        share.logServer.directory = share.gameDirectory + "LogServer\\";
        share.logServer.programFile = share.config.logServer.programFile;
        share.logServer.mainFormX = share.config.logServer.mainFormX;
        share.logServer.mainFormY = share.config.logServer.mainFormY;

        share.m2Server.getStart = share.config.m2Server.getStart;
        share.m2Server.reStart = true;
        share.m2Server.directory = share.gameDirectory + "Mir200\\";
        share.m2Server.programFile = share.config.m2Server.programFile;
        share.m2Server.mainFormX = share.config.m2Server.mainFormX;
        share.m2Server.mainFormY = share.config.m2Server.mainFormY;

        for (int i = 0; i < share.runGate.size(); i++) {
            share.runGate.get(i).startStatus = 0;
            share.runGate.get(i).getStart = share.config.runGate.getStart[i];
            share.runGate.get(i).reStart = true;
            share.runGate.get(i).directory = share.gameDirectory + "RunGate\\";
            share.runGate.get(i).programFile = share.config.runGate.programFile;
            if ((i + 1) % 2 == 0) {
                share.runGate.get(i).mainFormX = share.config.runGate.mainFormX + 276;
            } else {
                share.runGate.get(i).mainFormX = share.config.runGate.mainFormX;
            }
            if (i == 2 || i == 3 || i == 6 || i == 7) {
                share.runGate.get(i).mainFormY = share.config.runGate.mainFormY + 187;
            } else {
                share.runGate.get(i).mainFormY = share.config.runGate.mainFormY;
            }
        }

        share.selGate.getStart = share.config.selGate.getStart1;
        share.selGate.reStart = true;
        share.selGate.directory = share.gameDirectory + "SelGate\\";
        share.selGate.programFile = share.config.selGate.programFile;
        share.selGate.mainFormX = share.config.selGate.mainFormX;
        share.selGate.mainFormY = share.config.selGate.mainFormY;

        share.selGate1.getStart = share.config.selGate.getStart2;
        share.selGate1.reStart = true;
        share.selGate1.directory = share.gameDirectory + "SelGate\\";
        share.selGate1.programFile = share.config.selGate.programFile;
        share.selGate1.mainFormX = share.config.selGate.mainFormX;
        share.selGate1.mainFormY = share.config.selGate.mainFormY;

        share.loginGate.getStart = share.config.loginGate.getStart;
        share.loginGate.reStart = true;
        share.loginGate.directory = share.gameDirectory + "LoginGate\\";
        share.loginGate.programFile = share.config.loginGate.programFile;
        share.loginGate.mainFormX = share.config.loginGate.mainFormX;
        share.loginGate.mainFormY = share.config.loginGate.mainFormY;

        share.loginGate2.getStart = share.config.loginGate.getStart && share.ip2Enabled;
        share.loginGate2.reStart = true;
        share.loginGate2.directory = share.gameDirectory + "M2Server\\";
        share.loginGate2.programFile = share.config.loginGate.programFile;
        share.loginGate2.mainFormX = share.config.loginGate.mainFormX;
        share.loginGate2.mainFormY = share.config.loginGate.mainFormY;

        share.plugTop.getStart = share.config.plugTop.getStart;
        share.plugTop.reStart = true;
        share.plugTop.directory = share.gameDirectory + "Mir200\\";
        share.plugTop.programFile = share.config.plugTop.programFile;
        share.plugTop.mainFormX = share.config.plugTop.mainFormX;
        share.plugTop.mainFormY = share.config.plugTop.mainFormY;

        dbServerCheckBox.setSelected(share.config.dbServer.getStart);
        loginSrvCheckBox.setSelected(share.config.loginSrv.getStart);
        m2ServerCheckBox.setSelected(share.config.m2Server.getStart);
        logServerCheckBox.setSelected(share.config.logServer.getStart);
        loginGateCheckBox.setSelected(share.config.loginGate.getStart);
        selGateCheckBox1.setSelected(share.config.selGate.getStart1);
        selGateCheckBox2.setSelected(share.config.selGate.getStart2);
        gameGateCheckBox1.setSelected(share.config.runGate.getStart[0]);
        gameGateCheckBox2.setSelected(share.config.runGate.getStart[1]);
        gameGateCheckBox3.setSelected(share.config.runGate.getStart[2]);
        gameGateCheckBox4.setSelected(share.config.runGate.getStart[3]);
        gameGateCheckBox5.setSelected(share.config.runGate.getStart[4]);
        gameGateCheckBox6.setSelected(share.config.runGate.getStart[5]);
        gameGateCheckBox7.setSelected(share.config.runGate.getStart[6]);
        gameGateCheckBox8.setSelected(share.config.runGate.getStart[7]);

        btnStartGame.setText(share.textCancelStartGame);
        startState = STARTING_STATE;

        startGameTimer.schedule(new StartGameTask(), 1000, 1000);
    }

    private void loadBackupList() {
        // todo 加载备份列表
    }

    private void refBackupListToView() {

    }

    public void onDestroy() {
        startGameTimer.cancel();
        stopGameTimer.cancel();
        checkRunTimer.cancel();
    }

    public enum StartMode {
        NORMAL("正常启动"),
        DELAY("延时启动"),
        TIMING("定时启动"),
        ;

        public final String name;

        StartMode(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public static class StartGameTask extends TimerTask {
        @Override
        public void run() {
            if (share.dbServer.getStart) {
                switch (share.dbServer.startStatus) {
                    case 0:
                        LOGGER.info("prepare start database server program...");
                        int code = Programs.execute(share.dbServer);
                        if (code == 0) {
                            share.dbServer.startStatus = 1;
                            LOGGER.info("database server program start successful!");
                        } else {
                            cancel();
                        }
                        return;
                    case 1:
                        LOGGER.info("waiting database server program.");
                        return;
                }
            }
            if (share.loginServer.getStart) {
                // todo login server
            }
        }

        private boolean getStartRunGate() {
            for (int i = 0; i < share.runGate.size(); i++) {
                Share.Program program = share.runGate.get(i);
                if (program.getStart && program.startStatus == STARTING_STATE) {
                    return true;
                }
            }
            return false;
        }
    }

}
