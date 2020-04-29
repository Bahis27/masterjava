package ru.javaops.masterjava.service.mail.dao;

import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapperFactory;

import com.bertoncelj.jdbi.entitymapper.EntityMapperFactory;

import ru.javaops.masterjava.persist.dao.AbstractDao;
import ru.javaops.masterjava.service.mail.model.MailSendResult;

@RegisterMapperFactory(EntityMapperFactory.class)
public abstract class MailSendResultDao implements AbstractDao {

    @SqlUpdate("INSERT INTO mailsentresult (addressee, subject, body, result) VALUES (:addressee, :subject, :body, :result) ")
    @GetGeneratedKeys
    public abstract int insertGeneratedId(@BindBean MailSendResult mailSendResult);

    @SqlUpdate("TRUNCATE mailsentresult")
    @Override
    public abstract void clean();
}
