#include <SD.h>
#include <SPI.h>
#include <avr/pgmspace.h>
#include <Keyboard.h>
#include <stdlib.h>
#include "Player.h"
#include "Utils.h"

#define FILE_NAME "melody"

typedef unsigned char byte;

const uint8_t SWITCH_PIN PROGMEM = 8;
const uint8_t CHIP_SELECT PROGMEM = 4;

bool initSD()
{
    if (!SD.begin(CHIP_SELECT))
    {
        SD.end();
        return false;
    }
    return true;
}

void waitToStart()
{
    while (!digitalRead(SWITCH_PIN))
    {
        digitalWrite(LED_BUILTIN, HIGH);
        delay(300);
        digitalWrite(LED_BUILTIN, LOW);
        delay(200);
    }
}

void setup()
{
    // 初始化针脚
    pinMode(SWITCH_PIN, INPUT_PULLUP);
    pinMode(LED_BUILTIN, OUTPUT);
    // 关闭调试模式
    Utils::isDebug = false;
    // 传送开机信息
    Utils::blink(4);
    Keyboard.begin();
}

void loop()
{
    waitToStart();
    if (initSD())
    {
        // 读取乐谱
        File melody = SD.open(F(FILE_NAME), FILE_READ);
        Player *player = new Player();
        player->needJitter = true;
        player->playWhileReading(melody);
        melody.close();
        SD.end();
    }
};