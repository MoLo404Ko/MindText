package com.example.tp;

public class Constants {
    public static final String APP_NAME = "MindText";
    public static final String TRANSLATE_TEXT_FILE = "translatedText.txt";
    public static final String GENERATE_TEXT_FILE = "generatedText.txt";
    public static final String FIND_OBJECTS_TEXT_FILE = "names.txt";
    public static final String KEY_TEXT = "TextKey";
    public static final String KEY_LANGUAGE = "LanguageKey";
    public static final String TRANSLATE_SCRIPT_PATH = "https://practicenn.ru/translate.php";
    public static final String GENERATE_SCRIPT_PATH = "https://practicenn.ru/generate.php";
    public static final String TON_SCRIPT_PATH = "https://practicenn.ru/ton.php";
    public static final String FIND_OBJECT_SCRIPT_PATH = "https://practicenn.ru/findObjects.php";


    /**
     * Документация для получения токена
     * https://developers.sber.ru/docs/ru/gigachat/api/reference/rest/post-token
     */
    public static final String CLIENT_ID = "ef57bb0c-b34d-4e35-a6b4-d55c744f3eeb";
    public static final String SCOPE = "GIGACHAT_API_PERS"; // Scope клиента
    public static final String CLIENT_SECRET = "b54bdd89-b107-49d9-99ac-48c5ef1b54c8"; // Ключ клиента для доступа к GIGA CHAT
    public static final String AUTH_DATA =
            "ZWY1N2JiMGMtYjM0ZC00ZTM1LWE2YjQtZDU1Yzc0NGYzZWViOmI1NGJkZDg5LWIxMDctNDlkOS05OWFjLTQ4YzVlZjFiNTRjOA=="; // Авторизованные данные для получения токена
    public static final String GIGA_CHAT_GET_TOKEN_URL = "https://ngw.devices.sberbank.ru:9443/api/v2/oauth"; // URL получения токена
    public static final String GENERATED_UUID = "59f3efb0-6c46-4cbc-a53b-a762a4ef7e35"; // Случайный UUID для получения токена
    public static final String GIGA_CHAT_GET_GENERATED_TEXT_URL = "https://gigachat.devices.sberbank.ru/api/v1/chat/completions";
}
