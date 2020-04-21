package ru.javaops.masterjava.persist.model;

import com.bertoncelj.jdbi.entitymapper.Column;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectGroup {
    private @NonNull String id;
    private @NonNull GroupType type;
    @Column("project_id")
    private @NonNull String projectId;
}
