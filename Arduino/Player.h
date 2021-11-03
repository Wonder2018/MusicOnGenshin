#ifndef PLAYER_H
#define PLAYER_H

#include <SD.h>
#include <Keyboard.h>
#include <stdlib.h>
#include "Mog.h"

class Player
{
public:
    bool needJitter;
    Player();
    Player *playWhileReading(File file);

private:
    int len;
    bool isHold;
    unsigned long lastNoteTs;
    Player *play(Mog oneNote);
    void readNextNote(Mog *mog, File file);
    void waitNextNote(unsigned long dly);
    unsigned short readForShort(File file);
    short randomShort(short smin, short smax);
    void initState();
};

#endif