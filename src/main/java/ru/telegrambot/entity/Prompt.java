package ru.telegrambot.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Data
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "prompts")
@AllArgsConstructor
@NoArgsConstructor
public class Prompt implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * Имя получающего напоминания
     */
    @Column(name = "user_name")
    String userName;

    /**
     * Текст напоминания
     */
    @Column(name = "task_description")
    private String taskDescription;

    /**
     * Дата ближайшего напоминания
     */
    @Column(name = "reminding_date", columnDefinition = "timestamp with time zone")
    private LocalDateTime date;

    //    private Instant date;

    /**
     * Частота напоминания
     */
    @Column(name = "reminder_frequency")
    private String reminderFrequency;

    /**
     * Статус напоминания
     */
    @Column(name = "state")
    String promptStateType;

}