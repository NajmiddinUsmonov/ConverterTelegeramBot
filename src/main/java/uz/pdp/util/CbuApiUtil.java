package uz.pdp.util;

import com.google.gson.Gson;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import uz.pdp.model.BotUser;
import uz.pdp.model.Currency;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class CbuApiUtil  {
   static boolean convertionType;
   static Currency currentCurrency;


    public static Currency[] connectToCbuApi(){
        Currency[] currencies=null;

        try {
            URL url=new URL("https://cbu.uz/oz/arkhiv-kursov-valyut/json/");
            URLConnection urlConnection=url.openConnection();
            Gson gson=new Gson();
            InputStreamReader reader=new InputStreamReader(urlConnection.getInputStream());

            currencies= gson.fromJson(reader,Currency[].class);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return currencies;
    }

    public static void getCurrency(String convertMessage, BotUser botUser){

        botUser.setConvertionType(convertMessage.startsWith("UZS"));

        Currency[] currencies=connectToCbuApi();


        for (Currency currency : currencies) {

            if (convertMessage.contains(currency.getCcy())){
                botUser.setCurrency(currency);
                break;

            }

        }

    }

    public static Double convertion(String summa,BotUser botUser) {
        Double sum = Double.parseDouble(summa);
        Double result;

        result = botUser.isConvertionType() ? sum / Double.parseDouble(botUser.getCurrency().getRate())
                : sum * Double.parseDouble(botUser.getCurrency().getRate());

        return result;
    }

    }





