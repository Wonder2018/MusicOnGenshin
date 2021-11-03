#include "Player.h"
#include "Utils.h"

unsigned short Player::readForShort(File file)
{
    if (file.available() < 2)
    {
        return (unsigned char)file.read();
    }
    byte tmp[2] = {0};
    file.read(tmp, 2);
    unsigned short s = tmp[0];
    return (s << 8) + tmp[1];
}

short Player::randomShort(short smin, short smax)
{
    return (short)random(smin, smax);
}

Player::Player()
{
    isHold = false;
}

Player *Player::playWhileReading(File file)
{
    Utils::logln(F("initing..."));
    len = file.available() / 4;
    Utils::log(F("len: "));
    Utils::logln(len);
    Utils::logln(F("Will start playing when reading!"));
    Mog mog;
    readNextNote(&mog, file);
    unsigned long dly = mog.dly;
    for (int ind = 0; ind < len; ind++)
    {
        play(mog);
        dly = mog.dly;
        readNextNote(&mog, file);
        waitNextNote(dly);
    }
    initState();
}

Player *Player::play(Mog oneNote)
{
    short dly = oneNote.dly;
    char note = oneNote.note;
    if (note == 'P')
    {
        lastNoteTs = millis();
        return;
    }
    else if (dly < 1)
    {
        isHold = true;
        // Utils::log(F("play at:"));
        Utils::log((unsigned int)millis());
        Utils::log(F("--"));
        Keyboard.press(note);
        Utils::logln((unsigned int)millis());
    }
    else if (isHold)
    {
        // Utils::log(F("play at:"));
        Utils::log((unsigned int)millis());
        Utils::log(F("--"));
        Keyboard.press(note);
        lastNoteTs = millis();
        Utils::logln((unsigned int)lastNoteTs);
        isHold = false;
        Keyboard.releaseAll();
    }
    else
    {
        // Utils::log(F("play at:"));
        Utils::log((unsigned int)millis());
        Utils::log(F("--"));
        Keyboard.print(note);
        lastNoteTs = millis();
        Utils::logln((unsigned int)lastNoteTs);
    }
    return this;
}

void Player::readNextNote(Mog *mog, File file)
{
    file.read();
    (*mog).note = file.read();
    (*mog).dly = readForShort(file);
}

void Player::waitNextNote(unsigned long dly)
{
    Utils::log(F("dly: "));
    Utils::logln(dly);
    byte tag = needJitter;
    tag <<= 1;
    if (dly)
    {
        tag += 1;
    }

    Utils::log(F("tag: "));
    Utils::logln(tag);
    switch (tag)
    {
    case 0b10:
        Utils::log(F("1"));
        delay(randomShort(6, 15));
        return;
    case 0b00:
        Utils::log(F("2"));
        return;
    case 0b11:
        Utils::log(F("3"));
        dly = dly + randomShort(-10, 10);
        break;
    default:
        Utils::log(F("4"));
        break;
    }
    dly = dly - millis() + lastNoteTs;
    Utils::logln(F("new dly!"));
    if (dly > 0)
    {
        Utils::log(F("dling:"));
        Utils::logln(dly);
        delay(dly);
    }
}

void Player::initState()
{
    isHold = false;
}