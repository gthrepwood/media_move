package com.tgervai.album;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.util.List;
import java.util.concurrent.Callable;

@Slf4j
@Component
@Command(name = "mycommand", mixinStandardHelpOptions = true)
public class MyCommand implements Callable<Integer> {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private SerachService searcher;

    @Option(names = "-p", description = "path to check", defaultValue = ".")
    private String p;

    @Option(names = "-c", description = "optional option")
    private String command;

    @Parameters(description = "positional params")
    private List<String> positionals;

    @Override
    public Integer call() {
        if (positionals == null) {
            log.debug("commandas: run, help");
        } else {
            switch (positionals.get(0).toLowerCase()) {
                case "help": {
                    log.debug("help!");
                }
                case "run": {
                    try {
                        searcher.run(p);
                    } catch (Exception e) {
                        log.error("", e);
                    }
                }
            }
        }

        return 23;
    }

//    @Component
//    @Command(name = "sub", mixinStandardHelpOptions = true, subcommands = MyCommand.SubSub.class,
//            exitCodeOnExecutionException = 34)
//    static class Sub implements Callable<Integer> {
//        @Option(names = "-y", description = "optional option")
//        private String y;
//
//        @Parameters(description = "positional params")
//        private List<String> positionals;
//
//        @Override
//        public Integer call() {
//            System.out.printf("mycommand sub was called with -y=%s and positionals: %s%n", y, positionals);
//            return 33;
//        }
//    }

//    @Component
//    @Command(name = "subsub", mixinStandardHelpOptions = true, exitCodeOnExecutionException = 44)
//    static class SubSub implements Callable<Integer> {
//        @Option(names = "-z", description = "optional option")
//        private String z;
//
//        @Autowired
//        private SomeService service;
//
//        @Override
//        public Integer call() {
//            System.out.printf("mycommand sub subsub was called with -z=%s. Service says: '%s'%n", z, service.service());
//            return 43;
//        }
//    }
}