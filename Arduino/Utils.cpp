#include <USBAPI.h>
#include <stdlib.h>
#include "Utils.h"

bool Utils::isDebug = false;

void Utils::initSerial()
{
    if (Serial)
    {
        return;
    }
    Serial.begin(9600);
    while (!Serial)
        blink(3);
}

bool Utils::checkSerial()
{
    if (!isDebug)
    {
        return false;
    }
    if (!Serial)
    {
        initSerial();
    }
    return true;
}

void Utils::log(const __FlashStringHelper *str)
{
    if (!checkSerial())
    {
        return;
    }
    Serial.print(str);
}

void Utils::log(const int num)
{
    if (!checkSerial())
    {
        return;
    }
    Serial.print(num);
}
void Utils::logln(const __FlashStringHelper *str)
{
    if (!checkSerial())
    {
        return;
    }
    Serial.println(str);
}

void Utils::logln(const int num)
{
    if (!checkSerial())
    {
        return;
    }
    Serial.println(num);
}

int Utils::avaliableMemory()
{
    int size = 1;
    byte *buf;
    while ((buf = (byte *)malloc(size++)) != NULL)
    {
        free(buf);
    }
    return size - 1;
}

void Utils::blink(int times)
{
    for (int i = 0; i < times; i++)
    {
        digitalWrite(LED_BUILTIN, LOW);
        delay(500);
        digitalWrite(LED_BUILTIN, HIGH);
        delay(500);
    }
    digitalWrite(LED_BUILTIN, LOW);
}