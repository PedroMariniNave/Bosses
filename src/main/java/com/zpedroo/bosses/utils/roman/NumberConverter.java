package com.zpedroo.bosses.utils.roman;

public class NumberConverter {

    public static String convertToRoman(int number) {
        StringBuilder builder = new StringBuilder();
        if (number < 3999) {

            while (number >= 1) {
                int firstNumber = number / Integer.parseInt("1" + getPaddedString("0", ("" + number).length() - 1));
                if (firstNumber == 4) {
                    builder.append(getCharForDecimalPlace(("" + number).length(), false)).append(getCharForDecimalPlace(("" + number).length(), true));

                    number -= Integer.parseInt("" + firstNumber + getPaddedString("0", ("" + number).length() - 1));
                } else if (firstNumber == 9) {
                    builder.append(getCharForDecimalPlace(("" + number).length(), false)).append(getCharForDecimalPlace(("" + number).length() + 1, false));

                    number -= Integer.parseInt("" + firstNumber + getPaddedString("0", ("" + number).length() - 1));
                } else {
                    if(firstNumber >= 5) {
                        builder.append(getCharForDecimalPlace(("" + number).length(), true));
                    } else {
                        builder.append(getCharForDecimalPlace(("" + number).length(), false));
                    }

                    number -= getAsNormalFigure(builder.charAt(builder.length() - 1));
                }
            }
        }

        return builder.length() > 0 ? builder.toString() : String.valueOf(number);
    }

    private static char getCharForDecimalPlace(int place, boolean biggest) {
        switch(place) {
            case 1: {
                return biggest ? 'V' : 'I';
            }
            case 2: {
                return biggest ? 'L' : 'X';
            }
            case 3: {
                return biggest ? 'D' : 'C';
            }
            default: {
                return 'M';
            }
        }
    }

    private static String getPaddedString(String base, int amount) {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < amount; i++) {
            builder.append(base);
        }

        return builder.toString();
    }

    private static int getAsNormalFigure(char c) {
        switch (Character.toUpperCase(c)) {
            case 'I': {
                return 1;
            }
            case 'V': {
                return 5;
            }
            case 'X': {
                return 10;
            }
            case 'L': {
                return 50;
            }
            case 'C': {
                return 100;
            }
            case 'D': {
                return 500;
            }
            case 'M': {
                return 1000;
            }
        }

        return 0;
    }
}