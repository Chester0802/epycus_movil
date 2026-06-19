package es.epycus.app.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "cache")
public class CacheEntity {

    @PrimaryKey
    @NonNull
    private String key;

    private String value;

    public CacheEntity(@NonNull String key, String value) {
        this.key = key;
        this.value = value;
    }

    @NonNull
    public String getKey() { return key; }
    public String getValue() { return value; }
}
