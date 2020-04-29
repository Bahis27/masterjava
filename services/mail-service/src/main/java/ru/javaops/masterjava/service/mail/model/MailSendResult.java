package ru.javaops.masterjava.service.mail.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;
import ru.javaops.masterjava.persist.model.BaseEntity;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MailSendResult extends BaseEntity {
    private @NonNull String addressee;
    private @NonNull String subject;
    private @NonNull String body;
    private @NonNull String result;

    public MailSendResult(Integer id, String addressee, String subject, String body, String result) {
        this(addressee, subject, body, result);
        this.id = id;
    }
}
