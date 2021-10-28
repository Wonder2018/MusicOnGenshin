#ifndef WD_UTILS_H
#define WD_UTILS_H

#include "WString.h"
class Utils
{

public:
    static bool isDebug;

    static void log(const __FlashStringHelper *str);

    static void log(const int num);

    static void logln(const __FlashStringHelper *str);

    static void logln(const int num);

    static int avaliableMemory();

    static void blink(int times);

private:
    static void initSerial();
    
    static bool checkSerial();
};

#endif