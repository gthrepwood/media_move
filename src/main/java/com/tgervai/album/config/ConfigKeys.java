package com.tgervai.album.config;

import lombok.Getter;

public enum ConfigKeys {
    execute("/home/crown/3"),
    base_dir("{user.home}/album_data", "Base directory"),
    pictures_path("/mnt/sdb2/Photos", "Path to pictures"),
    data_file("{user.home}/album_data/data_file", "where to save the data file with filename"),
    save_data_file(true),
    read_from_filesdb(true),
    save_config(false),
    create_dupe_report(true),
    check_dupes_same_dir(false),
    save_object_gz(true),
    save_json(true),
    job_create_shell_result("{user.home}/");
    @Getter
    private String description;
    @Getter
    private Object defaultValue;

    ConfigKeys(Object defaultValue) {
        this.defaultValue = defaultValue;
        this.description = "";
    }

    ConfigKeys(Object defaultValue, String description) {
        this.defaultValue = defaultValue;
        this.description = description;
    }
}
