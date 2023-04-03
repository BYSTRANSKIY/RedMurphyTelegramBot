package ua.redmurphy.redmurphybot_v_1.controller;

import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import ua.redmurphy.redmurphybot_v_1.entity.Answer;
import ua.redmurphy.redmurphybot_v_1.entity.Exercise;
import ua.redmurphy.redmurphybot_v_1.entity.Unit;
import ua.redmurphy.redmurphybot_v_1.entity.enums.ActionStatus;
import ua.redmurphy.redmurphybot_v_1.entity.enums.TextStatus;
import ua.redmurphy.redmurphybot_v_1.service.AnswerService;
import ua.redmurphy.redmurphybot_v_1.service.ExerciseService;
import ua.redmurphy.redmurphybot_v_1.service.SessionService;
import ua.redmurphy.redmurphybot_v_1.service.UnitService;
import ua.redmurphy.redmurphybot_v_1.utils.ReplyKeyboardUtils;

import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;

@Log4j
@Component
public class MessageController {
    @Value("${bot.picture}")
    private String START_PICTURE_KEY;
    private String HELP_CAPTION =
            "Бот створений для самостійного вивчення Англійської\n" +
            "граматики. Для початку вам необхідно мати при собі блокнот,\n" +
            "ручку та бажання вивчати іноземну мову.\n" +
            "Unit - це глава теорії, яку потрібно опрацювати.\n" +
            "До кожної глави є завдання, які вам необхідно розв'язати.\n" +
            "А також ви можете глянути відповіді для самостійної перевірки.\n" +
            "Щоб розпочати нажміть /start";

    private final SessionService sessionService;
    private final UnitService unitService;
    private final ExerciseService exerciseService;
    private final AnswerService answerService;
    private final ReplyKeyboardUtils replyKeyboardUtils;
    private TelegramBot telegramBot;

    public void setTelegramBot(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    public MessageController(SessionService sessionService, UnitService unitService, ExerciseService exerciseService, AnswerService answerService, ReplyKeyboardUtils replyKeyboardUtils) {
        this.sessionService = sessionService;
        this.unitService = unitService;
        this.exerciseService = exerciseService;
        this.answerService = answerService;
        this.replyKeyboardUtils = replyKeyboardUtils;
    }

    public void distributeMessageByType(Message message) {
        if (message.hasText()) {
            distributeMessageTextByTextStatus(message);
        }
        if (message.hasPhoto()) {
            addPhoto(message);
        }
    }

    private void distributeMessageTextByTextStatus(Message message) {
        var chatId = message.getChatId();
        var textStatus = receiveTextStatus(chatId);
        switch (textStatus) {
            case SIMPLE -> {
                distributeMessageTextByType(message);
            }
            case UNIT_SEARCH -> {
                sendAction(message, ActionStatus.UNIT);
                sessionService.updateSessionTextStatus(chatId, TextStatus.SIMPLE);
            }
            case EXERCISE_SEARCH -> {
                sendAction(message, ActionStatus.EXERCISE);
                sessionService.updateSessionTextStatus(chatId, TextStatus.SIMPLE);
            }
            case ANSWER_SEARCH -> {
                sendAction(message, ActionStatus.ANSWER);
                sessionService.updateSessionTextStatus(chatId, TextStatus.SIMPLE);
            }
        }
    }
    private void addPhoto(Message message) {
        var photos = message.getPhoto();
        var chatId = message.getChatId();
        String fileId = Objects.requireNonNull(photos.stream().max(Comparator.comparing(PhotoSize::getFileSize))
                .orElse(null)).getFileId();
        String caption = "fileId: " + fileId;
        sendMessage(chatId, caption);
//        unitService.saveUnit(fileId);
//        exerciseService.saveExercise(fileId);
//        answerService.saveAnswer(fileId);
    }

    private TextStatus receiveTextStatus(long chatId) {
        var sessionOptional = sessionService.findSession(chatId);
        if (sessionOptional.isPresent()) {
            return sessionOptional.get().getTextStatus();
        } else {
            sessionService.saveSession(chatId, TextStatus.SIMPLE, ActionStatus.START);
            return TextStatus.SIMPLE;
        }
    }

    private void distributeMessageTextByType(Message message) {
        long chatId = message.getChatId();
        String messageText = message.getText();
        switch (messageText) {
            case "/start" -> {
                sendAction(message, ActionStatus.START);
            }
            case "/help" -> {
                sendMessage(chatId, HELP_CAPTION);
            }
            case "/unit" -> {
                sessionService.updateSessionTextStatus(chatId, TextStatus.UNIT_SEARCH);
                sendMessage(chatId, "Напишіть номер частини в проміжку від 1 до " + unitService.count());
            }
            case "/exercises" -> {
                sessionService.updateSessionTextStatus(chatId, TextStatus.EXERCISE_SEARCH);
                sendMessage(chatId, "Напишіть номер завдання в проміжку від 1 до " + exerciseService.count());
            }
            case "/answer" -> {
                sessionService.updateSessionTextStatus(chatId, TextStatus.ANSWER_SEARCH);
                sendMessage(chatId, "Напишіть номер сторінки відповідей в проміжку від 1 до " + answerService.count());
            }
            default -> {
                sendMessage(chatId, "Я не відповідаю на провокаційні питання");
            }
        }
    }

    private void sendAction(Message message, ActionStatus actionStatus) {
        var chatId = message.getChatId();
        var text = message.getText();
        if (actionStatus == ActionStatus.START) text = "0";
        int actionId;
        try {
            actionId = Integer.parseInt(text);
        } catch (NumberFormatException e) {
            sendMessage(chatId, "Невірний формат, щоб повторити натисніть /unit /exercise /answer");
            return;
        }
        var fileId = "";
        var caption = "";
        var sequenceNumber = 0;
        switch (actionStatus) {
            case START -> {
                caption = "Рекомендую для початку ознайомитись із посібником використання тут /help";
                fileId = START_PICTURE_KEY;
            }
            case UNIT -> {
                Optional<Unit> unitOptional = unitService.findUnit(actionId);
                if (unitOptional.isPresent()) {
                    fileId = unitOptional.get().getFileId();
                    sequenceNumber = unitService.checkPosition(actionId);
                    sessionService.updateSessionActionId(chatId, actionStatus, actionId);
                } else {
                    sendMessage(chatId, "Сторінку глави не знайдено");
                    return;
                }
            }
            case EXERCISE -> {
                Optional<Exercise> exerciseOptional = exerciseService.findExercise(actionId);
                if (exerciseOptional.isPresent()) {
                    fileId = exerciseOptional.get().getFileId();
                    sequenceNumber = exerciseService.checkPosition(actionId);
                    sessionService.updateSessionActionId(chatId, actionStatus, actionId);
                } else {
                    sendMessage(chatId, "Сторінку завдання не знайдено");
                    return;
                }
            }
            case ANSWER -> {
                Optional<Answer> answerOptional = answerService.findAnswer(actionId);
                if (answerOptional.isPresent()) {
                    fileId = answerOptional.get().getFileId();
                    sequenceNumber = answerService.checkPosition(actionId);
                    sessionService.updateSessionActionId(chatId, actionStatus, actionId);
                } else {
                    sendMessage(chatId, "Сторінку відповідей не знайдено");
                    return;
                }
            }
        }
        var inputFile = new InputFile(fileId);
        var replyMarkup = replyKeyboardUtils.createReplyMarkup(actionStatus, sequenceNumber);
        sendPhoto(chatId, caption, inputFile, replyMarkup);
        sessionService.updateSessionActionStatus(chatId, actionStatus);
    }

    private SendMessage generateSendMessage(long chatId, String text) {
        return SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .build();
    }

    private SendPhoto generateSendPhoto(long chatId, String caption, InputFile photo, ReplyKeyboard replyKeyboard) {
        return SendPhoto.builder()
                .chatId(chatId)
                .photo(photo)
                .caption(caption)
                .replyMarkup(replyKeyboard)
                .build();
    }

    private void sendPhoto(Long chatId, String caption, InputFile photo, ReplyKeyboard replyKeyboard) {
        var sendPhoto = generateSendPhoto(chatId, caption, photo, replyKeyboard);
        telegramBot.sendPhoto(sendPhoto);
    }

    private void sendMessage(Long chatId, String text) {
        telegramBot.sendMessage(generateSendMessage(chatId, text));
    }
}
