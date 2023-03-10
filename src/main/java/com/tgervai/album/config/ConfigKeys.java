package com.tgervai.album.config;

public enum ConfigKeys {
    base_dir(System.getProperty("user.home") + "/album_data"),
    pictures_path("/mnt/sdb2/Photos"),
    auto_save_config(true),
    data_file(System.getProperty("user.home") + "/album_data/data_file.data"),
    save_dupes(false);

    Object default_;

    ConfigKeys(Object def) {
        default_ = def;
    }
}
