package ua.redmurphy.redmurphybot_v_1.controller;

import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ua.redmurphy.redmurphybot_v_1.entity.Answer;
import ua.redmurphy.redmurphybot_v_1.entity.Exercise;
import ua.redmurphy.redmurphybot_v_1.entity.Session;
import ua.redmurphy.redmurphybot_v_1.entity.Unit;
import ua.redmurphy.redmurphybot_v_1.entity.enums.ActionStatus;
import ua.redmurphy.redmurphybot_v_1.entity.enums.TextStatus;
import ua.redmurphy.redmurphybot_v_1.service.AnswerService;
import ua.redmurphy.redmurphybot_v_1.service.ExerciseService;
import ua.redmurphy.redmurphybot_v_1.service.SessionService;
import ua.redmurphy.redmurphybot_v_1.service.UnitService;
import ua.redmurphy.redmurphybot_v_1.utils.ReplyKeyboardUtils;

import java.util.Optional;

import static ua.redmurphy.redmurphybot_v_1.controller.enums.CallbackData.*;

@Log4j
@Component
public class CallbackQueryController {

    private final SessionService sessionService;
    private final UnitService unitService;
    private final ExerciseService exerciseService;
    private final AnswerService answerService;
    private final ReplyKeyboardUtils replyKeyboardUtils;
    private TelegramBot telegramBot;

    public void setTelegramBot(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    public CallbackQueryController(SessionService sessionService, UnitService unitService, ExerciseService exerciseService, AnswerService answerService, ReplyKeyboardUtils replyKeyboardUtils) {
        this.sessionService = sessionService;
        this.unitService = unitService;
        this.exerciseService = exerciseService;
        this.answerService = answerService;
        this.replyKeyboardUtils = replyKeyboardUtils;
    }

    public void distributeCallbackQueryByType(CallbackQuery callbackQuery) {
        var chatId = callbackQuery.getMessage().getChatId();
        var messageId = callbackQuery.getMessage().getMessageId();
        var data = callbackQuery.getData();
        var session = receiveSession(chatId);

        if (messageId != session.getMessageId() && session.getMessageId() != 0) {
            deleteMessage(chatId, session.getMessageId());
        }
        if (data.equals(START.name())) {
            buttonStart(callbackQuery);
        } else if (data.equals(NEXT.name())) {
            buttonNext(callbackQuery, session);
        } else if (data.equals(BACK.name())) {
            buttonBack(callbackQuery, session);
        } else if (data.equals(GO.name())) {
            buttonGo(callbackQuery, session);
        }
    }

    private void buttonStart(CallbackQuery callbackQuery) {
        update(callbackQuery, ActionStatus.UNIT, 1);
    }

    private void buttonNext(CallbackQuery callbackQuery, Session session) {
        var actionStatus = session.getActionStatus();
        var unitId = session.getUnitId();
        var exerciseId = session.getExerciseId();
        var answerId = session.getAnswerId();
        switch (actionStatus) {
            case UNIT -> {
                update(callbackQuery, actionStatus, ++unitId);
            }
            case EXERCISE -> {
                update(callbackQuery, actionStatus, ++exerciseId);
            }
            case ANSWER -> {
                update(callbackQuery, actionStatus, ++answerId);
            }
        }
    }

    private void buttonBack(CallbackQuery callbackQuery, Session session) {
        var actionStatus = session.getActionStatus();
        var unitId = session.getUnitId();
        var exerciseId = session.getExerciseId();
        var answerId = session.getAnswerId();
        switch (actionStatus) {
            case UNIT -> {
                update(callbackQuery, actionStatus, --unitId);
            }
            case EXERCISE -> {
                update(callbackQuery, actionStatus, --exerciseId);
            }
            case ANSWER -> {
                update(callbackQuery, actionStatus, --answerId);
            }
        }
    }

    private void buttonGo(CallbackQuery callbackQuery, Session session) {
        var actionStatus = session.getActionStatus();
        switch (actionStatus) {
            case UNIT -> {
                update(callbackQuery, ActionStatus.EXERCISE, session.getUnitId());
            }
            case EXERCISE -> {
                update(callbackQuery, ActionStatus.ANSWER, 1);
            }
            case ANSWER -> {
                update(callbackQuery, ActionStatus.UNIT, session.getUnitId());
            }
        }
    }

    private void update(CallbackQuery callbackQuery, ActionStatus actionStatus, int actionId) {
        var chatId = callbackQuery.getMessage().getChatId();
        var messageId = callbackQuery.getMessage().getMessageId();
        String fileId = "";
        var sequenceNumber = 0;
        switch (actionStatus) {
            case UNIT -> {
                Optional<Unit> unitOptional = unitService.findUnit(actionId);
                if (unitOptional.isPresent()) {
                    fileId = unitOptional.get().getFileId();
                    sequenceNumber = unitService.checkPosition(actionId);
                }
            }
            case EXERCISE -> {
                Optional<Exercise> exerciseOptional = exerciseService.findExercise(actionId);
                if (exerciseOptional.isPresent()) {
                    fileId = exerciseOptional.get().getFileId();
                    sequenceNumber = exerciseService.checkPosition(actionId);
                }
            }
            case ANSWER -> {
                Optional<Answer> answerOptional = answerService.findAnswer(actionId);
                if (answerOptional.isPresent()) {
                    fileId = answerOptional.get().getFileId();
                    sequenceNumber = answerService.checkPosition(actionId);
                }
            }
        }
        var media = new InputMediaPhoto(fileId);
        var replyMarkup = replyKeyboardUtils.createReplyMarkup(actionStatus, sequenceNumber);
        editMessageMedia(chatId, messageId, replyMarkup, media);
        updateSession(chatId, messageId, actionStatus, actionId);
    }

    private Session receiveSession(long chatId) {
        var sessionOptional = sessionService.findSession(chatId);
        if (sessionOptional.isPresent()) {
            return sessionOptional.get();
        } else {
            sessionService.saveSession(chatId, TextStatus.SIMPLE, ActionStatus.START);
            return Session.builder()
                    .chatId(chatId)
                    .textStatus(TextStatus.SIMPLE)
                    .actionStatus(ActionStatus.START).build();
        }
    }

    private void updateSession(Long chatId, Integer messageId, ActionStatus actionStatus, int actionId) {
        sessionService.updateSessionMessageId(chatId, messageId);
        sessionService.updateSessionActionStatus(chatId, actionStatus);
        sessionService.updateSessionActionId(chatId, actionStatus, actionId);
    }

    private EditMessageMedia generatedEditMessageMedia(long chatId, int messageId,
                                                       InlineKeyboardMarkup replyMarkup, InputMedia media) {
        return EditMessageMedia.builder()
                .chatId(chatId)
                .messageId(messageId)
                .replyMarkup(replyMarkup)
                .media(media).build();
    }

    private DeleteMessage generatedDeleteMessage(Long chatId, int messageId) {
        return DeleteMessage.builder()
                .chatId(chatId)
                .messageId(messageId).build();
    }

    private void editMessageMedia(long chatId, int messageId, InlineKeyboardMarkup replyMarkup, InputMedia media) {
        var editMessageMedia = generatedEditMessageMedia(chatId, messageId, replyMarkup, media);
        telegramBot.editMessageMedia(editMessageMedia);
    }

    private void deleteMessage(Long chatId, int messageId) {
        var deleteMessage = generatedDeleteMessage(chatId, messageId);
        telegramBot.deleteMessage(deleteMessage);
    }
}
