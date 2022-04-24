import groovy.util.logging.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.AbstractFileFilter;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;

@Slf4j
public class AcceptFileFilter extends AbstractFileFilter {
    Collection ext = Arrays.asList("jpg", "jpeg");

    @Override
    public boolean accept(File file) {
        if (!file.isFile()) {
            System.out.println(file.getName());
            return true;
        } else {
            boolean ok = FilenameUtils.isExtension(file.getName().toLowerCase(Locale.ROOT), ext);
            if (!ok) {


            }
            return ok;
        }
    }
}
