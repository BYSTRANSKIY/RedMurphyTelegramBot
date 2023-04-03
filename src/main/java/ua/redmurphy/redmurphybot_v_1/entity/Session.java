package ua.redmurphy.redmurphybot_v_1.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.redmurphy.redmurphybot_v_1.entity.enums.ActionStatus;
import ua.redmurphy.redmurphybot_v_1.entity.enums.TextStatus;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Session {
    @Id
    long chatId;
    @Enumerated(EnumType.STRING)
    TextStatus textStatus;
    @Enumerated(EnumType.STRING)
    ActionStatus actionStatus;
    int messageId;
    int unitId;
    int exerciseId;
    int answerId;
}
