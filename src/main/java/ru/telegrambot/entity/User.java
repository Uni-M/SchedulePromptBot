package ru.telegrambot.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.List;

@Data
@Builder
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;

    /**
     * Имя получающего напоминания
     */
    @Column(name = "user_name")
    String userName;

    /**
     * Имя получающего напоминания
     */
    @Column(name = "first_name")
    String firstName;

    /**
     * Имя получающего напоминания
     */
    @Column(name = "second_name")
    String secondName;

    /**
     * Id чата получающего напоминания
     */
    @Column(name = "chat_id")
    Long chatId;

    /**
     * Часовой пояс
     */
    @Column(name = "time_zone")
    String timeZone;

    /**
     * Список напоминаний
     */
    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.EAGER) //mappedBy="User"
    @JoinColumn(name = "user_name", referencedColumnName = "user_name")
    List<Prompt> prompts;

}