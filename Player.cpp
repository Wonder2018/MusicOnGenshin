#include "Player.h"
#include "Console.h"

namespace ply
{

    unsigned short readForShort(File file)
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

    short randomShort(short smin, short smax)
    {
        return (short)random(smin, smax);
    }

    void delayWithBreak(unsigned int dly, unsigned int rate, bool *tag)
    {
        if (*tag)
        {
            return;
        }
        if (dly < rate)
        {
            delay(dly);
            return;
        }
        delay(rate);
        return ply::delayWithBreak(dly - rate, rate, tag);
    }
}

Player::Player()
{
    isPlaying = false;
    *(isStop) = false;
    isHold = false;
}

Player::~Player()
{
    free(notes);
    free(isStop);
    notes = nullptr;
    isStop = nullptr;
}

Player *Player::init(File file)
{
    Console::logln("initing...");
    len = file.available() / 4;
    Console::log("len: ");
    Console::logln(len);
    if (notes != nullptr)
    {
        Console::logln("free notes!");
        free(notes);
    }
    Console::logln("create new notes");
    int size = sizeof(Mog);
    Console::log("sizeof mog: ");
    Console::logln(size);
    notes = (Mog *)calloc(len, size);
    Console::logln("create Mog");
    for (int ind = 0; ind < len; ind++)
    {
        Console::log("new Mog:");
        Console::logln(ind);
        Mog mog;
        file.read();
        mog.note = file.read();
        mog.dly = ply::readForShort(file);
        notes[ind] = mog;
    }
    return this;
}

Player *Player::start()
{
    if (isPlaying)
    {
        return this;
    }
    isPlaying = true;

    for (int ind = 0; ind < len; ind++)
    {
        if (*(isStop))
        {
            Keyboard.releaseAll();
            return this;
        }
        this->play(notes[ind]);
    }
    isPlaying = false;
    isHold = false;
    isHold = false;
}

Player *Player::stop()
{
    *isStop = true;
}

Player *Player::play(Mog oneNote)
{
    short dly = oneNote.dly;
    char note = oneNote.note;
    if (note == 'P')
    {
        this->waitNextNote(dly + ply::randomShort(-10, 10), 10, isStop);
    }
    else if (dly < 10)
    {
        isHold = true;
        Keyboard.press(note);
        this->waitNextNote(ply::randomShort(6, 15), 10, isStop);
    }
    else if (isHold)
    {
        Keyboard.press(note);
        isHold = false;
        Keyboard.releaseAll();
        this->waitNextNote(dly + ply::randomShort(-10, 10), 10, isStop);
    }
    else
    {
        Keyboard.print(note);
        this->waitNextNote(dly + ply::randomShort(-10, 10), 10, isStop);
    }
    return this;
}

void Player::waitNextNote(unsigned int dly, unsigned int rate, bool *tag)
{
    ply::delayWithBreak(dly, rate, tag);
}