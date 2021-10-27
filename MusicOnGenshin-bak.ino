#include <SD.h>
#include <SPI.h>
#include <Keyboard.h>
#include <stdlib.h>
#include "Player.h"
#include "Console.h"

typedef unsigned char byte;

int switchPin = 8;
int chipSelect = 4;
int lenOfNotes = 0;
int debugPin = 7;
const char *fn = "ddz";

Player *player;

bool initSD()
{
    if (!SD.begin(chipSelect))
    {
        SD.end();
        return false;
    }
    return true;
}

void waitToStart()
{
    while (!digitalRead(switchPin))
    {
        digitalWrite(LED_BUILTIN, HIGH);
        delay(3000);
        digitalWrite(LED_BUILTIN, LOW);
        delay(2000);
    }
}

void blink(int times)
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

void setup()
{
    // 初始化针脚
    pinMode(switchPin, INPUT_PULLUP);
    pinMode(LED_BUILTIN, OUTPUT);
    pinMode(debugPin, INPUT_PULLUP);
    // 检测调试模式
    Console::isDebug = !digitalRead(debugPin);
    // 传送开机信息
    blink(4);
    Console::logln("inited Serial");
    Console::logln("initing Keyboard");
    Keyboard.begin();
    Console::logln("initing SD");
    if (initSD())
    {
        Console::logln("Succeed!");
        // 读取乐谱
        Console::logln("loading melody - open");
        File melody = SD.open(fn, FILE_READ);
        player = new Player();
        Console::logln("loading melody - read");
        player->init(melody);
        Console::logln("Succeed!");
        melody.close();
        SD.end();
    }
    else
    {
        Console::logln("Faild!");
    }
}

void loop()
{
    waitToStart();
    player->start();
};