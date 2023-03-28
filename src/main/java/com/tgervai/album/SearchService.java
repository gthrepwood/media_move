package com.tgervai.album;

import com.tgervai.album.config.Config;
import com.tgervai.album.db.Database;
import com.tgervai.album.db.DatabaseChecker;
import com.tgervai.album.db.Index;
import com.tgervai.album.file.CheckUtils;
import com.tgervai.album.file.DupeList;
import com.tgervai.album.file.FileData;
import com.tgervai.album.file.FileExists;
import com.tgervai.album.utils.FileUtils;
import com.tgervai.album.utils.OutputUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.tgervai.album.config.ConfigKeys.create_dupe_report;
import static com.tgervai.album.config.ConfigKeys.execute;
import static com.tgervai.album.db.Index.*;

@Slf4j
@Service
public class SearchService {
    @Autowired
    Database database;
    @Autowired
    DatabaseChecker databaseChecker;
    @Autowired
    Config config;
    @Autowired
    FileUtils fileUtils;
    @Autowired
    OutputUtils outputUtils;
    @Autowired
    CheckUtils checkUtils;

    public void execute() {

        final String source = config.getValue(execute);

        outputUtils.startOutput();

        database.reload();

/*
        if (config.isValue(check_dupes_same_dir)) {
            HashMap<String, List<FileData>> names = database.getIndexes().get(fullpath);
            for (String nameInDb : names.keySet()) {
                String parent = checkUtils.getParent(nameInDb);
                if (parent != null && names.containsKey(parent)) {
                    log.debug(nameInDb + " -> " + parent);
                }
            }
            return;
        }
*/

        if (config.isValue(create_dupe_report)) {
            DupeList dupeList = databaseChecker.searchDuplicatedByType(hash);
            final HashMap<String, DupeList> list = databaseChecker.searchDuplicate();
            log.debug(dupeList + "");
        }

        SortedSet<FileData> filesToCheck = fileUtils.walk(new TreeSet<>(), source);
        log.info("Check path: {} Found files: {}", source, filesToCheck.size());

//            fileChecker.setFiles_db(files_db);

        List<FileExists> filesExists;

        for (FileData local : filesToCheck) {
            List<Index> exists = database.exists(local);
            if (exists.size() > 0) {
                log.debug("  " + local.getName() + " [exists] " + exists);
                if (!exists.contains(sizeType) && exists.contains(nameType)) {
                    Set<FileData> set = database.get(local, nameType);
                    set.forEach(f -> log.debug("   " + local.getSize() + " [vs] " + f.getSize()));
                    set.forEach(f -> log.debug("   file://" + local.getPath() + " [vs] file://" + f.getPath()));
                }
            }
        }


//                boolean alltrue = true;
//                boolean allfalse = true;
//                for (Checker checker : requiredCheckers) {
//                    List found = checker.find(local);
//                    if (found == null) {
//                        allfalse = false;
//                    } else {
//                        alltrue = false;
//                    }
//                log.debug("alltrue:" + alltrue + "  allfalse:" + allfalse);

//                    List<FileData> foundList = files_db.byName(local);
//                if (foundList != null) {
//                    for (FileData found : foundList) {
//                        for (Checker checker : requiredCheckers) {
//                            if (checker.check(local, found)) {
//                                filesExists.add(FileExists.builder().checker(checker).local(local).databaseFile(found).build());
//                                FileExists f = filesExists.get(filesExists.size() - 1);
//                                log.debug(f.toString());
//                            }
//                        }
//                    }
//                }
//            }
//            p.println("</body></html>");
//        }

//        log.debug(temp.toUri().toString());
//        openWebpage(temp.toUri());
//        log.debug(temp.toString());

    }
}