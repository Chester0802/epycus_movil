package es.epycus.app.data.local.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "write_back_queue")
public class WriteBackEntity {
    @PrimaryKey(autoGenerate = true)
    public long id;

    public String operationType;
    public String endpoint;
    public String requestBody;
    public long createdAt;
    public int retryCount;
    public String status; // pending, failed, completed
}