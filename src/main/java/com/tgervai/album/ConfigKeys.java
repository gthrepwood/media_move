package com.tgervai.album;

public enum ConfigKeys {
    base_dir(System.getProperty("user.home") + "/album_data"),
    pictures_path("/mnt/sdb2/Photos"),
    auto_save_config(true),
    data_file(System.getProperty("user.home") + "/album_data/data_file.data");

    Object default_;

    ConfigKeys(Object def) {
        default_ = def;
    }
}
