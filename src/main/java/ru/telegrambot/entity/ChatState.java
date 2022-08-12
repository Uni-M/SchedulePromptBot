package ru.telegrambot.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "chat_state")
@AllArgsConstructor
@NoArgsConstructor
public class ChatState {

    @Id
    @Column(name = "id")
    private Long id;

    /**
     * Имя получающего напоминания
     */
    @Column(name = "user_name")
    String userName;

    /**
     * Статус чата
     */
    @Column(name = "state")
    String chatState;
}