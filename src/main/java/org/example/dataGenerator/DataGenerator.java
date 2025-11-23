package org.example.dataGenerator;

import java.util.Random;

/**
 * Класс для генерации тестовых данных, соответствующих валидации бэкенда
 */
public class DataGenerator {
    // Создаем генератор случайных чисел для всех методов класса
    private static final Random random = new Random();

    // ↓ МАССИВЫ ДАННЫХ ДЛЯ ГЕНЕРАЦИИ ↓

    // Русские имена для случайного выбора (все <= 20 символов)
    private static final String[] RUSSIAN_NAMES = {
            "Антон", "Мария", "Сергей", "Ольга", "Дмитрий", "Елена",
            "Александр", "Наталья", "Иван", "Светлана", "Михаил", "Татьяна",
            "Андрей", "Юлия", "Алексей", "Анна", "Владимир", "Екатерина",
            "Павел", "Ирина", "Николай", "Марина", "Виктор", "Людмила"
    };

    // Кириллические буквы в нижнем регистре для генерации логина
    private static final String CYRILLIC_LETTERS = "абвгдеёжзийклмнопрстуфхцчшщъыьэюя";
    // Кириллические буквы в верхнем регистре
    private static final String CYRILLIC_UPPER = "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ";

    // Латинские буквы и цифры для генерации пароля и email
    private static final String LATIN_LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String LATIN_UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String DIGITS = "0123456789";

    /**
     * Генерирует имя на кириллице (до 20 символов)
     * Правило бэкенда: имя должно быть на кириллице, максимум 20 символов
     */
    public static String generateName() {
        // Просто возвращаем случайное русское имя из массива
        // Все имена в массиве уже соответствуют правилам (кириллица, <=20 символов)
        return RUSSIAN_NAMES[random.nextInt(RUSSIAN_NAMES.length)];
    }

    /**
     * Генерирует логин: кириллица + цифры (до 20 символов)
     * Правило бэкенда: логин должен содержать только кириллицу и цифры
     */
    public static String generateLogin() {
        // Генерируем длину логина от 8 до 20 символов
        int length = 8 + random.nextInt(13);
        StringBuilder login = new StringBuilder();

        // Заполняем логин символами
        for (int i = 0; i < length; i++) {
            if (random.nextBoolean()) {
                // 50% chance - добавляем кириллическую букву
                if (random.nextBoolean()) {
                    // Случайная заглавная буква
                    login.append(CYRILLIC_UPPER.charAt(random.nextInt(CYRILLIC_UPPER.length())));
                } else {
                    // Случайная строчная буква
                    login.append(CYRILLIC_LETTERS.charAt(random.nextInt(CYRILLIC_LETTERS.length())));
                }
            } else {
                // 50% chance - добавляем цифру
                login.append(DIGITS.charAt(random.nextInt(DIGITS.length())));
            }
        }
        return login.toString();
    }

    /**
     * Генерирует email (для email используем только латиницу)
     * Email не может содержать кириллицу, поэтому генерируем на латинице
     */
    public static String generateEmail() {
        // Используем текущее время чтобы сделать email уникальным
        String timestamp = String.valueOf(System.currentTimeMillis());
        String randomNum = String.valueOf(random.nextInt(1000));

        // Логин для email (только латиница)
        String emailLogin = "user" + timestamp.substring(7) + randomNum;

        // Случайный домен из списка
        String[] domains = {"mail.ru", "gmail.com", "yandex.ru", "test.org"};
        String domain = domains[random.nextInt(domains.length)];

        return emailLogin + "@" + domain;
    }

    /**
     * Генерирует пароль по правилам бэкенда:
     * - Минимум 6 символов
     * - Хотя бы одна заглавная буква
     * - Оба алфавита (кириллица и латиница)
     */
    public static String generatePassword() {
        // Генерируем длину пароля от 8 до 12 символов
        int length = 8 + random.nextInt(5);
        StringBuilder password = new StringBuilder();

        // ↓ ГАРАНТИРУЕМ ВЫПОЛНЕНИЕ ПРАВИЛ БЭКЕНДА ↓

        // 1. Обязательно добавляем заглавную латинскую букву
        password.append(LATIN_UPPER.charAt(random.nextInt(LATIN_UPPER.length())));

        // 2. Обязательно добавляем кириллическую букву (строчную)
        password.append(CYRILLIC_LETTERS.charAt(random.nextInt(CYRILLIC_LETTERS.length())));

        // 3. Заполняем оставшиеся символы случайными символами из всех доступных
        String allChars = LATIN_LOWER + LATIN_UPPER + CYRILLIC_LETTERS + CYRILLIC_UPPER + DIGITS;

        for (int i = password.length(); i < length; i++) {
            password.append(allChars.charAt(random.nextInt(allChars.length())));
        }

        // Перемешиваем символы чтобы правила выполнялись не только в начале
        return shuffleString(password.toString());
    }

    /**
     * Вспомогательный метод для перемешивания символов в строке
     * Нужен чтобы заглавная буква не всегда была первой
     */
    private static String shuffleString(String input) {
        char[] characters = input.toCharArray();
        // Проходим по всем символам и случайно меняем их местами
        for (int i = 0; i < characters.length; i++) {
            int randomIndex = random.nextInt(characters.length);
            char temp = characters[i];
            characters[i] = characters[randomIndex];
            characters[randomIndex] = temp;
        }
        return new String(characters);
    }

    /**
     * Генерирует confirm_password (должен совпадать с password)
     */
    public static String generateConfirmPassword() {
        // Для confirm_password просто возвращаем тот же пароль
        // чтобы они совпадали (это требование бэкенда)
        return generatePassword();
    }

    /**
     * Генерирует ВСЕ данные для регистрации сразу и возвращает объект
     * Удобно использовать в тестах - одной строкой получаем все данные
     */
    public static RegistrationData generateRegistrationData() {
        String password = generatePassword(); // Генерируем пароль один раз

        return new RegistrationData(
                generateName(),      // Кириллическое имя
                generateEmail(),     // Email на латинице
                generateLogin(),     // Логин: кириллица + цифры
                password,           // Пароль
                password            // confirm_password совпадает с password
        );
    }

    /**
     * Внутренний класс для удобного хранения всех данных регистрации
     * Используем public final поля для простоты доступа
     */
    public static class RegistrationData {
        public final String name;
        public final String email;
        public final String login;
        public final String password;
        public final String confirmPassword;

        public RegistrationData(String name, String email, String login,
                                String password, String confirmPassword) {
            this.name = name;
            this.email = email;
            this.login = login;
            this.password = password;
            this.confirmPassword = confirmPassword;
        }
    }
}