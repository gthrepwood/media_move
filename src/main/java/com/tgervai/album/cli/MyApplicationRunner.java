package com.tgervai.album.cli;

import com.tgervai.album.SearchService;
import com.tgervai.album.config.Config;
import com.tgervai.album.config.ConfigKeys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.stereotype.Component;
import picocli.CommandLine;
import picocli.CommandLine.IFactory;

@Component
@Slf4j
public class MyApplicationRunner implements CommandLineRunner, ExitCodeGenerator {

    /* auto-configured to inject PicocliSpringFactory */
    private final IFactory factory;

    public MyApplicationRunner(IFactory factory) {
        this.factory = factory;
    }

    @Autowired
    Config config;
    @Autowired
    SearchService searchService;

    @Override
    public void run(String... args) {

        CommandLine.Model.CommandSpec spec = CommandLine.Model.CommandSpec.create();
        spec.mixinStandardHelpOptions(true); // usageHelp and versionHelp options

        spec.addPositional(
                CommandLine.Model.PositionalParamSpec.builder()
                        .paramLabel("command")
                        .type(String.class)
                        .description("Command to run")
                        .required(true)
                        .build()
        );

        for (ConfigKeys key : ConfigKeys.values()) {
            spec.addOption(CommandLine.Model.OptionSpec
                    .builder("--" + key.name())
                    .paramLabel(key.getDefaultValue() + "")
                    .type(String.class)
                    .defaultValue(key.getDefaultValue() + "")
                    .showDefaultValue(CommandLine.Help.Visibility.ON_DEMAND)
                    .description(key.getDescription()).build());
        }

        CommandLine commandLine = new CommandLine(spec);

        commandLine.setExecutionStrategy(MyApplicationRunner::run);

        commandLine.execute(args);

        if (commandLine.isUsageHelpRequested()) {
            commandLine.usage(System.out);
        } else {
            CommandLine.ParseResult pr = commandLine.getParseResult();

            for (ConfigKeys key : ConfigKeys.values()) {
                if (pr.hasMatchedOption(key.toString())) {
                    config.set(key.toString(), pr.matchedOption(key.toString()).getValue());
                }
            }

            if (pr.unmatched().size() == 0) {
                switch (pr.matchedPositionalValue(0, "")) {
                    case "run" -> searchService.execute();
                    default -> throw new IllegalStateException("Unexpected value: " + pr.matchedPositionalValue(0, ""));
                }
            }
        }
    }

    private static int run(final CommandLine.ParseResult parseResult) {
        return 0;
    }

    @Override
    public int getExitCode() {
        return 0;
    }
}