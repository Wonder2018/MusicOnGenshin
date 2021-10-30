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

void Player::delayWithBreak(unsigned int dly, unsigned int rate)
{
    if (isStop)
    {
        return;
    }
    if (dly < rate)
    {
        delay(dly);
        return;
    }
    delay(rate);
    return delayWithBreak(dly - rate, rate);
}

Player::Player()
{
    isPlaying = false;
    isStop = false;
    isHold = false;
}

Player::~Player()
{
    free(notes);
    notes = nullptr;
}

Player *Player::init(File file)
{
    Utils::logln(F("initing..."));
    len = file.available() / 4;
    Utils::log(F("len: "));
    Utils::logln(len);
    // if (notes != nullptr)
    // {
    //     // Utils::logln(F("free notes!"));
    //     free(notes);
    // }
    int ramSize = Utils::avaliableMemory();
    Utils::log(F("ram size:"));
    Utils::logln(ramSize);
    Utils::logln(F("create new notes"));
    int size = sizeof(Mog);
    Utils::log(F("sizeof mog: "));
    Utils::logln(size);
    if (ramSize < len * size)
    {
        len = 0;
        return;
    }
    notes = (Mog *)calloc(len, size);
    Utils::logln(F("create Mog"));
    for (int ind = 0; ind < len; ind++)
    {
        Mog mog;
        file.read();
        mog.note = file.read();
        mog.dly = readForShort(file);
        notes[ind] = mog;
    }
    return this;
}

Player *Player::playWhileReading(File file)
{
    Utils::logln(F("initing..."));
    len = file.available() / 4;
    Utils::log(F("len: "));
    Utils::logln(len);
    Utils::logln(F("Will start playing when reading!"));
    Mog mog;
    for (int ind = 0; ind < len; ind++)
    {
        if (isStop)
        {
            Keyboard.releaseAll();
            return this;
        }
        file.read();
        mog.note = file.read();
        mog.dly = readForShort(file);
        play(mog);
    }
    initState();
}

Player *Player::start()
{
    Utils::logln(F("in start!"));
    if (isPlaying)
    {
        return this;
    }
    isPlaying = true;
    Utils::log(F("start with "));
    Utils::log(len);
    Utils::logln(F("notes."));
    for (int ind = 0; ind < len; ind++)
    {
        Utils::log(F("play:"));
        Utils::logln(ind);
        if (isStop)
        {
            Keyboard.releaseAll();
            return this;
        }
        this->play(notes[ind]);
    }
    initState();
}

Player *Player::stop()
{
    isStop = true;
}

Player *Player::play(Mog oneNote)
{
    short dly = oneNote.dly;
    char note = oneNote.note;
    if (note == 'P')
    {
        this->waitNextNote(dly + randomShort(-10, 10), 10);
    }
    else if (dly < 10)
    {
        isHold = true;
        Keyboard.press(note);
        this->waitNextNote(randomShort(6, 15), 10);
    }
    else if (isHold)
    {
        Keyboard.press(note);
        isHold = false;
        Keyboard.releaseAll();
        this->waitNextNote(dly + randomShort(-10, 10), 10);
    }
    else
    {
        Keyboard.print(note);
        this->waitNextNote(dly + randomShort(-10, 10), 10);
    }
    return this;
}

void Player::waitNextNote(unsigned int dly, unsigned int rate)
{
    delayWithBreak(dly, rate);
}

void Player::initState(){
    isPlaying = false;
    isStop = false;
    isHold = false;
}