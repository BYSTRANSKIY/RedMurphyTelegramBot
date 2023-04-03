package ua.redmurphy.redmurphybot_v_1.utils;

import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ua.redmurphy.redmurphybot_v_1.controller.enums.CallbackData;
import ua.redmurphy.redmurphybot_v_1.entity.enums.ActionStatus;
import ua.redmurphy.redmurphybot_v_1.service.AnswerService;
import ua.redmurphy.redmurphybot_v_1.service.ExerciseService;
import ua.redmurphy.redmurphybot_v_1.service.UnitService;

import java.util.ArrayList;
import java.util.List;

@Log4j
@Component
public class ReplyKeyboardUtils {

    public InlineKeyboardMarkup createReplyMarkup(ActionStatus actionStatus, int sequenceNumber) {
        switch (actionStatus) {
            case START -> {
                return createInlKeyMarForStart();
            }
            case UNIT -> {
                var str = "Перейти до завдань";
                if (sequenceNumber == 0) {
                    return createInlKeyMarForActions(str);
                } else if (sequenceNumber == 1){
                    return createInlKeyMarForActionsWithEnd(str);
                } else if (sequenceNumber == -1){
                    return createInlKeyMarForActionsWithFirst(str);
                }
            }
            case EXERCISE -> {
                var str = "Перейти до відповідей";
                if (sequenceNumber == 0) {
                    return createInlKeyMarForActions(str);
                } else if (sequenceNumber == 1){
                    return createInlKeyMarForActionsWithEnd(str);
                } else if (sequenceNumber == -1){
                    return createInlKeyMarForActionsWithFirst(str);
                }
            }
            case ANSWER -> {
                var str = "Перейти до глави";
                if (sequenceNumber == 0) {
                    return createInlKeyMarForActions(str);
                } else if (sequenceNumber == 1){
                    return createInlKeyMarForActionsWithEnd(str);
                } else if (sequenceNumber == -1){
                    return createInlKeyMarForActionsWithFirst(str);
                }
            }
        }
        return null;
    }

    private InlineKeyboardMarkup createInlKeyMarForStart() {
        var inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        List<InlineKeyboardButton> rowInLine = new ArrayList<>();

        var button = new InlineKeyboardButton();
        button.setText("Розпочати негайно");
        button.setCallbackData(CallbackData.START.name());

        rowInLine.add(button);
        rowsInLine.add(rowInLine);
        inlineKeyboardMarkup.setKeyboard(rowsInLine);
        return inlineKeyboardMarkup;
    }

    private InlineKeyboardMarkup createInlKeyMarForActions(String textToGo) {
        var inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        List<InlineKeyboardButton> rowInLine1 = new ArrayList<>();
        List<InlineKeyboardButton> rowInLine2 = new ArrayList<>();

        var button = new InlineKeyboardButton();
        button.setText("Назад");
        button.setCallbackData(CallbackData.BACK.name());
        rowInLine1.add(button);

        button = new InlineKeyboardButton();
        button.setText("Далі");
        button.setCallbackData(CallbackData.NEXT.name());
        rowInLine1.add(button);

        button = new InlineKeyboardButton();
        button.setText(textToGo);
        button.setCallbackData(CallbackData.GO.name());
        rowInLine2.add(button);

        rowsInLine.add(rowInLine1);
        rowsInLine.add(rowInLine2);
        inlineKeyboardMarkup.setKeyboard(rowsInLine);
        return inlineKeyboardMarkup;
    }

    private InlineKeyboardMarkup createInlKeyMarForActionsWithFirst(String textToGo) {
        var inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        List<InlineKeyboardButton> rowInLine1 = new ArrayList<>();
        List<InlineKeyboardButton> rowInLine2 = new ArrayList<>();

        var button = new InlineKeyboardButton();
        button.setText("Далі");
        button.setCallbackData(CallbackData.NEXT.name());
        rowInLine1.add(button);

        button = new InlineKeyboardButton();
        button.setText(textToGo);
        button.setCallbackData(CallbackData.GO.name());
        rowInLine2.add(button);

        rowsInLine.add(rowInLine1);
        rowsInLine.add(rowInLine2);
        inlineKeyboardMarkup.setKeyboard(rowsInLine);
        return inlineKeyboardMarkup;
    }

    private InlineKeyboardMarkup createInlKeyMarForActionsWithEnd(String textToGo) {
        var inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        List<InlineKeyboardButton> rowInLine1 = new ArrayList<>();
        List<InlineKeyboardButton> rowInLine2 = new ArrayList<>();

        var button = new InlineKeyboardButton();
        button.setText("Назад");
        button.setCallbackData(CallbackData.BACK.name());
        rowInLine1.add(button);

        button = new InlineKeyboardButton();
        button.setText(textToGo);
        button.setCallbackData(CallbackData.GO.name());
        rowInLine2.add(button);

        rowsInLine.add(rowInLine1);
        rowsInLine.add(rowInLine2);
        inlineKeyboardMarkup.setKeyboard(rowsInLine);
        return inlineKeyboardMarkup;
    }
}
