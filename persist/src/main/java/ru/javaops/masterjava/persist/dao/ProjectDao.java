package ru.javaops.masterjava.persist.dao;

import java.util.List;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlBatch;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.BatchChunkSize;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapperFactory;

import com.bertoncelj.jdbi.entitymapper.EntityMapperFactory;

import lombok.extern.slf4j.Slf4j;
import ru.javaops.masterjava.persist.model.Project;

@Slf4j
@RegisterMapperFactory(EntityMapperFactory.class)
public abstract class ProjectDao implements AbstractDao {

    @SqlUpdate("INSERT INTO projects (id, description) VALUES (:id, :description)")
    public abstract void insert(@BindBean Project project);

    @SqlQuery("SELECT * FROM projects ORDER BY id LIMIT :it")
    public abstract List<Project> getWithLimit(@Bind int limit);

    @SqlBatch("INSERT INTO projects (id, description) VALUES (:id, :description) " +
            "ON CONFLICT (id) DO NOTHING")
    public abstract void insertBatch(@BindBean List<Project> projects, @BatchChunkSize int chunkSize);

    @SqlUpdate("TRUNCATE projects CASCADE")
    @Override
    public abstract void clean();
}
