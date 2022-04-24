import groovyjarjarpicocli.CommandLine;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;

import java.io.File;
import java.util.Collection;
import java.util.concurrent.Callable;

public class Start implements Callable<Integer> {

    @CommandLine.Option(names = "-c", description = "create a new archive")
    boolean create;

    @CommandLine.Parameters(index = "0", description = "The file whose checksum to calculate.", defaultValue = ".")
    private File file;

    @CommandLine.Option(names = {"-a", "--algorithm"}, description = "MD5, SHA-1, SHA-256, ...")
    private String algorithm = "SHA-256";

    public static void main(String[] args) {
        int exitCode = new CommandLine(new Start()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() throws Exception {

        File dir = new File("/media/crown/images/saved_photos");
        Collection files = FileUtils.listFiles(
                dir,
                //new RegexFileFilter("^(.*?)"),
                new AcceptFileFilter(),
                DirectoryFileFilter.DIRECTORY
        );
//        System.out.println(files);
        return 0;
    }
}
