#include "Console.h"
#include "USBAPI.h"

bool Console::isDebug = false;

void Console::initSerial()
{
    Serial.begin(9600);
    while (!Serial)
        ;
}

void Console::checkSerial()
{
    if (!isDebug)
    {
        return;
    }
    if (!Serial)
    {
        initSerial();
    }
}

void Console::log(const char *str)
{
    checkSerial();
    Serial.print(str);
}

void Console::log(const int num)
{
    checkSerial();
    Serial.print(num);
}
void Console::logln(const char *str)
{
    checkSerial();
    Serial.println(str);
}

void Console::logln(const int num)
{
    checkSerial();
    Serial.println(num);
}
