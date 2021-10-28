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
const uint8_t DEBUG_PIN PROGMEM = 7;

Player *player;

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
    Utils::logln(F("in wait!"));
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
    pinMode(DEBUG_PIN, INPUT_PULLUP);
    // 检测调试模式
    Utils::isDebug = !digitalRead(DEBUG_PIN);
    // 传送开机信息
    Utils::blink(4);
    Utils::logln(F("inited Serial"));
    Utils::logln(F("initing Keyboard"));
    Keyboard.begin();
    Utils::logln(F("initing SD"));
    if (Utils::isDebug)
    {
        int ramSize = Utils::avaliableMemory();
        Utils::log(F("ram size:"));
        Utils::logln(ramSize);
    }
    if (initSD())
    {
        Utils::logln(F("Succeed!"));
        // 读取乐谱
        Utils::logln(F("loading melody - open"));
        File melody = SD.open(F(FILE_NAME), FILE_READ);
        player = new Player();
        Utils::logln(F("loading melody - read"));
        player->init(melody);
        Utils::logln(F("Succeed!"));
        melody.close();
        SD.end();
    }
    else
    {
        Utils::logln(F("Faild!"));
    }
}

void loop()
{
    waitToStart();
    Utils::log(F("size of player:"));
    Utils::logln(sizeof(player));
    Utils::log(F("size of *player:"));
    Utils::logln(sizeof(*player));
    player->start();
};