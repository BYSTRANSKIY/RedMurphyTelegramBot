package ua.redmurphy.redmurphybot_v_1.controller;

import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;


@Log4j
@Component
public class TelegramBot extends TelegramLongPollingBot {
    @Value("${bot.username}")
    private String username;
    @Value("${bot.token}")
    private String token;

    private MessageController messageController;
    private CallbackQueryController callbackQueryController;

    public TelegramBot(MessageController messageController, CallbackQueryController callbackQueryController) {
        this.messageController = messageController;
        this.callbackQueryController = callbackQueryController;
    }

    @PostConstruct
    public void init() throws TelegramApiException {
        messageController.setTelegramBot(this);
        callbackQueryController.setTelegramBot(this);
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        try{
            telegramBotsApi.registerBot(this);
        } catch (TelegramApiException e) {
            log.error("Error occurred: " + e.getMessage());
        }
    }

    @Override
    public String getBotUsername() {
        return username;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update == null) {
            log.error("Received update is null");
            return;
        }
        if (update.hasMessage()) {
            messageController.distributeMessageByType(update.getMessage());
        } else if (update.hasCallbackQuery()) {
            callbackQueryController.distributeCallbackQueryByType(update.getCallbackQuery());
        }
    }

    public void sendMessage(SendMessage message) {
        if (message != null) {
            createMenu();
            try {
                execute(message);
            } catch (TelegramApiException e) {
                log.error(e);
            }
        }
    }

    public void sendPhoto(SendPhoto photo) {
        if (photo != null) {
            try {
                execute(photo);
            } catch (TelegramApiException e) {
                log.error(e);
            }
        }
    }

    public void editMessageMedia(EditMessageMedia media) {
        if (media != null) {
            try {
                execute(media);
            } catch (TelegramApiException e) {
                log.error(e);
            }
        }
    }

    public void deleteMessage(DeleteMessage message) {
        if (message != null) {
            try {
                execute(message);
            } catch (TelegramApiException e) {
                log.error(e);
            }
        }
    }

    private ReplyKeyboardMarkup createRKM() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();

        var row = new KeyboardRow();
        row.add("unit");
        row.add("exercises");
        keyboardRows.add(row);

        replyKeyboardMarkup.setKeyboard(keyboardRows);
        replyKeyboardMarkup.setOneTimeKeyboard(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        return replyKeyboardMarkup;
    }

    private void createMenu() {
        List<BotCommand> botCommandList = new ArrayList<>();
        botCommandList.add(new BotCommand("/start", "Розпочати"));
        botCommandList.add(new BotCommand("/unit", "Знайти сторінку глави"));
        botCommandList.add(new BotCommand("/exercises", "Знайти сторінку завдань"));
        botCommandList.add(new BotCommand("/answer", "Знайти сторінку відповідей"));
        botCommandList.add(new BotCommand("/help", "Посібник використання бота"));
        try {
            execute(new SetMyCommands(botCommandList, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error(e);
        }
    }
}
