package ru.javaops.masterjava.persist.dao;

import java.util.List;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlBatch;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.Transaction;
import org.skife.jdbi.v2.sqlobject.customizers.BatchChunkSize;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapperFactory;

import com.bertoncelj.jdbi.entitymapper.EntityMapperFactory;

import lombok.extern.slf4j.Slf4j;
import one.util.streamex.IntStreamEx;
import ru.javaops.masterjava.persist.DBIProvider;
import ru.javaops.masterjava.persist.model.User;

@Slf4j
@RegisterMapperFactory(EntityMapperFactory.class)
public abstract class UserDao implements AbstractDao {

    public User insert(User user) {
        if (user.isNew()) {
            int id = insertGeneratedId(user);
            user.setId(id);
        } else {
            insertWitId(user);
        }
        return user;
    }

    @SqlQuery("SELECT nextval('user_seq')")
    abstract int getNextVal();

    @Transaction
    public int getSeqAndSkip(int step) {
        int id = getNextVal();
        DBIProvider.getDBI().useHandle(h -> h.execute("SELECT setval('user_seq', " + (id + step - 1) + ")"));
        return id;
    }

    @SqlUpdate("INSERT INTO users (full_name, email, flag, city_id) VALUES (:fullName, :email, CAST(:flag AS USER_FLAG), :cityId) ")
    @GetGeneratedKeys
    abstract int insertGeneratedId(@BindBean User user);

    @SqlUpdate("INSERT INTO users (id, full_name, email, flag, city_id) VALUES (:id, :fullName, :email, CAST(:flag AS USER_FLAG), :cityId) ")
    abstract void insertWitId(@BindBean User user);

    @SqlQuery("SELECT * FROM users ORDER BY full_name, email LIMIT :it")
    public abstract List<User> getWithLimit(@Bind int limit);

    //   http://stackoverflow.com/questions/13223820/postgresql-delete-all-content
    @SqlUpdate("TRUNCATE users")
    @Override
    public abstract void clean();

    //    https://habrahabr.ru/post/264281/
    @SqlBatch("INSERT INTO users (id, full_name, email, flag, city_id) VALUES (:id, :fullName, :email, CAST(:flag AS USER_FLAG), :cityId)" +
            "ON CONFLICT DO NOTHING")
//            "ON CONFLICT (email) DO UPDATE SET full_name=:fullName, flag=CAST(:flag AS USER_FLAG)")
    public abstract int[] insertBatch(@BindBean List<User> users, @BatchChunkSize int chunkSize);


    public List<String> insertAndGetConflictEmails(List<User> users) {
        log.info(Thread.currentThread().getName());
        int[] result = insertBatch(users, users.size());
        return IntStreamEx.range(0, users.size())
                .filter(i -> result[i] == 0)
                .mapToObj(index -> users.get(index).getEmail())
                .toList();
    }
}
