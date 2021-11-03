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
    len = file.available() / 4;
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
        Keyboard.press(note);
    }
    else if (isHold)
    {
        Keyboard.press(note);
        lastNoteTs = millis();
        isHold = false;
        Keyboard.releaseAll();
    }
    else
    {
        Keyboard.print(note);
        lastNoteTs = millis();
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
    byte tag = needJitter;
    tag <<= 1;
    if (dly)
    {
        tag += 1;
    }

    switch (tag)
    {
    case 0b10:
        delay(randomShort(6, 15));
        return;
    case 0b00:
        return;
    case 0b11:
        dly = dly + randomShort(-10, 10);
        break;
    default:
        break;
    }
    dly = dly - millis() + lastNoteTs;
    if (dly > 0)
    {
        delay(dly);
    }
}

void Player::initState()
{
    isHold = false;
}