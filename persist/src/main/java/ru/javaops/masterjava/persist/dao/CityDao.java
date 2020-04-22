package ru.javaops.masterjava.persist.dao;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlBatch;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.BatchChunkSize;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapperFactory;

import com.bertoncelj.jdbi.entitymapper.EntityMapperFactory;

import lombok.extern.slf4j.Slf4j;
import ru.javaops.masterjava.persist.model.City;

@Slf4j
@RegisterMapperFactory(EntityMapperFactory.class)
public abstract class CityDao implements AbstractDao {

    @SqlUpdate("INSERT INTO cities (id, name) VALUES (:id, :name)")
    public abstract void insert(@BindBean City city);

    @SqlQuery("SELECT * FROM cities ORDER BY name LIMIT :it")
    public abstract List<City> getWithLimit(@Bind int limit);

    @SqlBatch("INSERT INTO cities (id, name) VALUES (:id, :name) " +
            "ON CONFLICT (id) DO NOTHING")
    public abstract int[] insertBatch(@BindBean List<City> cities, @BatchChunkSize int chunkSize);

    @SqlUpdate("TRUNCATE cities CASCADE")
    @Override
    public abstract void clean();

    public List<City> insertAndGetAddedCities(List<City> cities) {
        log.info(Thread.currentThread().getName());
        int[] result = insertBatch(cities, cities.size());
        return IntStream.range(0, cities.size())
                .filter(i -> result[i] != 0)
                .mapToObj(cities::get)
                .collect(Collectors.toList());
    }
}
