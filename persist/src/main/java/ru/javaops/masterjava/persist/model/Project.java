package ru.javaops.masterjava.persist.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Project {
    private @NonNull String id;
    private @NonNull String description;
}
