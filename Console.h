#ifndef WD_CONSOLE_H
#define WD_CONSOLE_H

class Console
{
public:
    static bool isDebug;

    static void log(const char *str);

    static void log(const int num);

    static void logln(const char *str);

    static void logln(const int num);

private:
    static void initSerial();
    static void checkSerial();
};

#endif