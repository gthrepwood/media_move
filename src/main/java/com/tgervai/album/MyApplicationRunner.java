package com.tgervai.album;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.stereotype.Component;
import picocli.CommandLine;
import picocli.CommandLine.IFactory;

@Component
@Slf4j
public class MyApplicationRunner implements CommandLineRunner, ExitCodeGenerator {

    private final MyCommand myCommand;

    private final IFactory factory; // auto-configured to inject PicocliSpringFactory

    private int exitCode;

    public MyApplicationRunner(MyCommand myCommand, IFactory factory) {
        this.myCommand = myCommand;
        this.factory = factory;
        log.debug("run code");
    }

    @Override
    public void run(String... args) throws Exception {
        exitCode = new CommandLine(myCommand, factory).execute(args);
    }

    @Override
    public int getExitCode() {
        return exitCode;
    }
}