#ifndef PLAYER_H
#define PLAYER_H

#include <SD.h>
#include <Keyboard.h>
#include <stdlib.h>
#include "Mog.h"

class Player
{
public:
    Player();
    ~Player();
    Player *init(File file);
    Player *start();
    Player *stop();

private:
    int len;
    bool isPlaying;
    bool *isStop;
    bool isHold;
    Mog *notes;
    Player *play(Mog oneNote);
    void waitNextNote(unsigned int dly, unsigned int rate, bool *tag);
};

#endif