package uz.pdp;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import uz.pdp.model.BotUser;
import uz.pdp.util.CbuApiUtil;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class Main extends TelegramLongPollingBot {
    /**
     * YOU CAN USE MAP AS DATABASE OR USE POSTGRESQL
     */
    Map<Long, BotUser> userMap=new HashMap<>();

    /**
     * THE MAP BELOW WAS USED AS DATABASE OR YOU CAN HAVE A CHANCE TO USE POSTGRESQL
     */
    Map<String,String> moneyTypeMap=new HashMap<>();


    /**
     * THIS IS CONTAINER TO CARRY ON REQUEST
     */
    int step=0;
    SendMessage sendMessage=new SendMessage();
  public  String receivedMessage;
  public  String sendingMessage;



    public static void main(String[] args) {

        try {

            TelegramBotsApi botsApi=new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new Main());


        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }

    @Override
    public String getBotUsername() {
        return "mybotcha_bot";
    }

    public void setButtons(SendMessage sendMessage,int step) {


        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();

        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);

        List<KeyboardRow> keyboardRows = new ArrayList<>();


        if (step == 2) {

            if (receivedMessage.startsWith("UZS")) {

                KeyboardRow row1 = new KeyboardRow();
                row1.add("500000 So'm");
                row1.add("1000000 So'm");
                keyboardRows.add(row1);

                KeyboardRow row2 = new KeyboardRow();
                row2.add("5000000 So'm");
                row2.add("10000000 So'm");
                keyboardRows.add(row2);

            } else if (receivedMessage.startsWith("USD")) {

                KeyboardRow row3 = new KeyboardRow();
                row3.add("$100");
                row3.add("$500");
                keyboardRows.add(row3);

                KeyboardRow row4 = new KeyboardRow();
                row4.add("$1000");
                row4.add("$10000");
                keyboardRows.add(row4);

            } else if (receivedMessage.startsWith("EUR")) {

                KeyboardRow row3 = new KeyboardRow();
                row3.add("€100");
                row3.add("$500");
                keyboardRows.add(row3);

                KeyboardRow row4 = new KeyboardRow();
                row4.add("$1000");
                row4.add("$10000");
                keyboardRows.add(row4);

            } else if (receivedMessage.startsWith("RUB")) {

                KeyboardRow row3 = new KeyboardRow();
                row3.add("₽200");
                row3.add("₽700");
                keyboardRows.add(row3);

                KeyboardRow row4 = new KeyboardRow();
                row4.add("₽5000");
                row4.add("₽20000");
                keyboardRows.add(row4);
            }
                KeyboardRow row1 = new KeyboardRow();
                row1.add("Orqaga");
                keyboardRows.add(row1);
//
//
        } else if (step == 1) {

            KeyboardRow row1 = new KeyboardRow();
            row1.add("USD-UZS");
            row1.add("UZS-USD");
            keyboardRows.add(row1);

            KeyboardRow row2 = new KeyboardRow();
            row2.add("EUR-UZS");
            row2.add("UZS-EUR");
            keyboardRows.add(row2);

            KeyboardRow row3 = new KeyboardRow();
            row3.add("RUB-UZS");
            row3.add("UZS-RUB");
            keyboardRows.add(row3);

        }

        replyKeyboardMarkup.setKeyboard(keyboardRows);
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
    }


    @Override
    public String getBotToken() {
        return "insertToken";
    }


              /********YOU CAN WRITE LOGIC CODES BELOW *******/

    @Override
    public synchronized void onUpdateReceived(Update update) {
         receivedMessage=update.getMessage().getText();

        BotUser botUser;

        if (receivedMessage.equalsIgnoreCase("/start")){
            botUser=new BotUser();
            botUser.setStep(1);
            userMap.put(update.getMessage().getChatId(),botUser);
        }else{
            botUser=userMap.get(update.getMessage().getChatId());
        }



        if (receivedMessage.contains("UZS")){
            botUser.setStep(2);

        }
        if ((receivedMessage.equals("Orqaga"))&&(botUser.getStep()==3)){
            botUser.setStep(botUser.getStep()-2);

        }
        if ((botUser.getStep()==4)&&(receivedMessage.equals("Orqaga"))) {

                botUser.setStep(botUser.getStep() - 3);
        }
        switch (botUser.getStep()){

            case 1:
                sendingMessage="\uD83E\uDD16"+ "Botga xush kelibsiz.\n"+ "\uD83D\uDC68"+ "Janob:" +update.getMessage().getFrom().getUserName()+"\n\n" + "\uD83D\uDCEF"+ "Konvertatsiya turini tanlang.";
                setButtons(sendMessage,botUser.getStep());

                break;

            case 2:
                sendingMessage="Summani kiriting (Qo'lda kiriting yoki tanlang)";
                moneyTypeMap.put(update.getMessage().getText(),update.getMessage().getText());

                CbuApiUtil.getCurrency(receivedMessage,botUser);
                setButtons(sendMessage,botUser.getStep());
                break;

            case 3:

                if ( moneyTypeMap.containsKey("USD-UZS")){
                    if (receivedMessage.startsWith("$")){
                        receivedMessage=receivedMessage.substring(1);
                        sendingMessage=" USA \uD83C\uDDFA\uD83C\uDDF8" +" \uD83D\uDD03 "+"Uzbekistan" +"  \uD83C\uDDFA\uD83C\uDDFF\n " + "$" +receivedMessage + " => " + CbuApiUtil.convertion(receivedMessage ,botUser) +" So'm";
                    }
                    sendingMessage=" USA \uD83C\uDDFA\uD83C\uDDF8" +" \uD83D\uDD03 "+"Uzbekistan" +"  \uD83C\uDDFA\uD83C\uDDFF\n " + "$" +receivedMessage + " => " + CbuApiUtil.convertion(receivedMessage ,botUser) +" So'm";
//
                }else if (moneyTypeMap.containsKey("EUR-UZS")){
                    if ((receivedMessage.startsWith("€"))){
                        receivedMessage=receivedMessage.substring(1);
                        sendingMessage="Germany \uD83C\uDDE9\uD83C\uDDEA " +" \uD83D\uDD03 "+" Uzbekistan" +"  \uD83C\uDDFA\uD83C\uDDFF\n "+"€" +receivedMessage + " => " + CbuApiUtil.convertion(receivedMessage ,botUser) + " So'm";

                    }
                    sendingMessage="Germany \uD83C\uDDE9\uD83C\uDDEA " +" \uD83D\uDD03 "+" Uzbekistan" +"  \uD83C\uDDFA\uD83C\uDDFF\n "+"€" +receivedMessage + " => " + CbuApiUtil.convertion(receivedMessage ,botUser) + " So'm";

                }else if (moneyTypeMap.containsKey("RUB-UZS")){
                    if (receivedMessage.startsWith("₽")){
                        receivedMessage=receivedMessage.substring(1);
                        sendingMessage="Russian \uD83C\uDDF7\uD83C\uDDFA " + " \uD83D\uDD03 " + " Uzbekistan \uD83C\uDDFA\uD83C\uDDFF\n " +  "₽" + receivedMessage + " => " + CbuApiUtil.convertion(receivedMessage,botUser) + " So'm" ;

                    }

                    sendingMessage="Russian \uD83C\uDDF7\uD83C\uDDFA " + " \uD83D\uDD03 " + " Uzbekistan \uD83C\uDDFA\uD83C\uDDFF\n " +  "₽" + receivedMessage + " => " + CbuApiUtil.convertion(receivedMessage,botUser) + " So'm" ;
                }
                else if (moneyTypeMap.containsKey("UZS-USD")){
                    if (receivedMessage.endsWith("So'm")) {
                        receivedMessage = receivedMessage.substring(0, receivedMessage.length() - 5);
                        sendingMessage = "Uzbekistan" + "  \uD83C\uDDFA\uD83C\uDDFF " + " \uD83D\uDD03 " + " USA \uD83C\uDDFA\uD83C\uDDF8 \n " + receivedMessage + " So'm => " + " $ " + CbuApiUtil.convertion(receivedMessage, botUser);
                    }
                    sendingMessage = "Uzbekistan" + "  \uD83C\uDDFA\uD83C\uDDFF " + " \uD83D\uDD03 " + " USA \uD83C\uDDFA\uD83C\uDDF8 \n " + receivedMessage + " So'm => " + " $ " + CbuApiUtil.convertion(receivedMessage, botUser);

                } else if (moneyTypeMap.containsKey("UZS-EUR")){
                    if (receivedMessage.endsWith("So'm")) {

                        receivedMessage = receivedMessage.substring(0, receivedMessage.length() - 5);
                        sendingMessage = "Uzbekistan" + "  \uD83C\uDDFA\uD83C\uDDFF " + " \uD83D\uDD03 " + " Germany \uD83C\uDDE9\uD83C\uDDEA\n " + receivedMessage + " So'm => " + " € " + CbuApiUtil.convertion(receivedMessage, botUser) + " So'm";
                    }
                    sendingMessage = "Uzbekistan" + "  \uD83C\uDDFA\uD83C\uDDFF " + " \uD83D\uDD03 " + " Germany \uD83C\uDDE9\uD83C\uDDEA\n " + receivedMessage + " So'm => " + " € " + CbuApiUtil.convertion(receivedMessage, botUser) + " So'm";

                } else if (moneyTypeMap.containsKey("UZS-RUB")){
                    if (receivedMessage.endsWith("So'm")) {
                        receivedMessage = receivedMessage.substring(0, receivedMessage.length() - 5);
                        sendingMessage = "Uzbekistan" + "  \uD83C\uDDFA\uD83C\uDDFF " + " \uD83D\uDD03 " + " Russian \uD83C\uDDF7\uD83C\uDDFA\n " + receivedMessage + " So'm => " + " ₽ " + CbuApiUtil.convertion(receivedMessage, botUser);
                    }
                    sendingMessage = "Uzbekistan" + "  \uD83C\uDDFA\uD83C\uDDFF " + " \uD83D\uDD03 " + " Russian \uD83C\uDDF7\uD83C\uDDFA\n " + receivedMessage + " So'm => " + " ₽ " + CbuApiUtil.convertion(receivedMessage, botUser);

                }
                break;

            default:
                sendingMessage="Xatolik bor";
                break;
        }
        botUser.setStep(botUser.getStep()+1);


//        SendMessage sendMessage=new SendMessage();//upakovga uchun kerak bo'ladi!
        sendMessage.setText(sendingMessage);
        sendMessage.setChatId(update.getMessage().getChatId().toString()
        );
        try {

            execute(sendMessage); //karobkani  yuborish uchun shu metodni chaqirish kerak

        } catch (TelegramApiException e) {
            e.printStackTrace();
        }


    }
}
