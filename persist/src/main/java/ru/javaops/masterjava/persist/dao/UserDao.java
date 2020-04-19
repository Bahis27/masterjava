package ru.javaops.masterjava.persist.dao;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlBatch;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.Transaction;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapperFactory;

import com.bertoncelj.jdbi.entitymapper.EntityMapperFactory;

import ru.javaops.masterjava.persist.model.User;

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

    @SqlUpdate("INSERT INTO users (full_name, email, flag) VALUES (:fullName, :email, CAST(:flag AS user_flag))")
    @GetGeneratedKeys
    abstract int insertGeneratedId(@BindBean User user);

    @SqlUpdate("INSERT INTO users (id, full_name, email, flag) VALUES (:id, :fullName, :email, CAST(:flag AS user_flag))")
    abstract void insertWitId(@BindBean User user);

    @Transaction
    public List<Integer> insertChunkWithId(List<User> users) {

        List<Integer> userIds = Arrays.stream(insertAll(users))
                .boxed()
                .collect(Collectors.toList());

        restartSeq();

        return userIds;
    }

    @SqlBatch("INSERT INTO users (id, full_name, email, flag) VALUES (:id, :fullName, :email, CAST(:flag AS user_flag)) on conflict on constraint unique_email do nothing")
    @GetGeneratedKeys
    public abstract int[] insertAll(@BindBean List<User> users);

    @SqlQuery("SELECT last_value FROM user_seq")
    public abstract int getCurrentSeq();

    @SqlUpdate("select setval('user_seq', (select max(id) + 1 from users), false)")
    abstract void restartSeq();

    @SqlQuery("SELECT * FROM users ORDER BY full_name, email LIMIT :it")
    public abstract List<User> getWithLimit(@Bind int limit);

    //   http://stackoverflow.com/questions/13223820/postgresql-delete-all-content
    @SqlUpdate("TRUNCATE users")
    @Override
    public abstract void clean();
}
