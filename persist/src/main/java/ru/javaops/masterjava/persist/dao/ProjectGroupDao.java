package ru.javaops.masterjava.persist.dao;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlBatch;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.BatchChunkSize;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapperFactory;

import com.bertoncelj.jdbi.entitymapper.EntityMapperFactory;

import lombok.extern.slf4j.Slf4j;
import ru.javaops.masterjava.persist.model.ProjectGroup;

@Slf4j
@RegisterMapperFactory(EntityMapperFactory.class)
public abstract class ProjectGroupDao implements AbstractDao {

    @SqlUpdate("INSERT INTO projectgroups (id, type, project_id) VALUES (:id, CAST(:type AS GROUP_TYPE), :project_id)")
    public abstract void insert(@BindBean ProjectGroup projectGroup);

    @SqlQuery("SELECT * FROM projectgroups ORDER BY project_id, id LIMIT :it")
    public abstract void getWithLimit(@Bind int limit);

    @SqlBatch("INSERT INTO projectgroups (id, type, project_id) VALUES (:id, CAST(:type AS GROUP_TYPE), :project_id) " +
            "ON CONFLICT (id) DO NOTHING")
    public abstract void insertBatch(@BindBean ProjectGroup projectGroup, @BatchChunkSize int chunkSize);

    @SqlUpdate("TRUNCATE projectgroups")
    @Override
    public abstract void clean();
}
