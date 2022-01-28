# Music On Genshin

本项目适用于 `Arduino Leonardo` ，可根据乐谱自动在原神中使用乐器小道具进行演奏。

## 一、 所需材料：

1. Arduino Leonardo 或其他支持 HID 键盘及带有内置上拉电阻的 Arduino 单片机。
2. SD 卡模块（用于存放要演奏的乐谱）
3. 杜邦线若干（用于连接 SD 卡模块和短接一些特定脚位，控制演奏流程）
4. 连接 Arduino 与电脑的数据线一条
5. Arduino IDE（或其他可以烧录的 IDE ）
6. SD 库和 KeyBoard 库
7. jdk 1.8 或以上版本（用于生成适合 Arduino 读取的乐谱）
8. 可以进行 Java 开发的 IDE（其实 NotePad 或者 Vim 也行，由于暂未提供打包好的 Jar，需要在 IDE 运行，或进行修改。）
9. 乐谱（<--废话）

## 二、使用方法：

1. 准备乐谱

    没有乐谱要怎么演奏啦！！

    1.1 录谱

    先将乐谱录入到一个 txt 文件，请使用 `utf-8` 编码。文件结构如下

    ```text

        速度,单位时值
        简谱音符，时值
        简谱音符，时值
        ...
        ...
        ...

        e.g.
        112,12      //每分钟112拍，每拍用12表示
        5,6         //第一个音符是中音 sol 半拍
        -5,3        //第一个音符是低音 sol 四分之一拍
        +5,24       //第一个音符是高音 sol 两拍

    ```

    > 注意：
    >
    > 1. 录入时用简谱录入，高音前加 `+`，低音前加 `-`。和弦写在同一行即可。例如：`-3-51,4` 表示同时按下 `低音 mi` `低音 sol` `中音 do`。
    > 2. 表示一拍的数字的指定可以比较随意，但为了方便录谱建议将乐谱中时值最短的音设置为 1，然后换算出一拍的数值，如：一份四分音符为一拍的乐谱中，出现的时值最小的音符是十六分音符，那么用 1 表示十六分音符，4 表示四分音符。如果乐谱中有三连音，可使用 3 的倍数表示一拍，这样就能准确写出三连音对应音符的时值。
    > 3. 项目中提供了一个简单的乐谱，如果你成功了，一定会听到令人愉快的声音（Doge）

    1.2 转谱

    将录好的乐谱转换成方便 Arduino 读取的格式。

    这个过程比较简单，只需将录好的乐谱命名成 `in.txt` 放到项目根目录。然后运行 `top.imwonder.musicongenshin.MusicOnGenshin` 中 `main` 方法即可。输出文件 `out` 将出现在项目根目录，控制台会显示乐谱的总拍数。你也可以在项目根目录的 `config.properties` 文件中指定乐谱文件的位置和输出文件的位置。

    > 一般情况下，IDE 会将根目录设置为运行时的 `classpath`，如果你无法完成这一步，请尝试在 `config.properties` 文件中指定绝对路径。

    1.3 存储

    将转好的乐谱文件命名为 `melody` 存在一张存储卡的根目录。储存卡的分区格式需要选择 `FAT`。

2. 准备 Arduino

    2.1 将 Arduino 用数据线连接至电脑。烧录项目中的 `Arduino/Arduino.ino` 文件。

    2.2 将 SD 卡模块连接至 Arduino。请注意，项目默认使用的 SD 卡模块`CS_PIN` 为 `4 号脚`。Leonardo 需要将 SD 卡通信脚位连接到 `ICSP` 区的针脚上。具体脚位定义为。

    ```
        □ <-- reset 按钮

        MISO--> □ □ <-- VDD(+5v)
        SCK---> □ □ <-- MOSI
        RST---> □ □ <-- GND

    ```

    2.3 连接控制线

    开机并完成初始化后，不会立即开始演奏。需要演奏时，正常使用时短接 `GND` 和 `数据 8 号脚`。需要注意的是，如果在一次演奏结束后，程序检测到 `数据 8 号脚` 仍被短接，则会从头开始再演奏一遍。

    > ~~如果需要查看调试信息，可短接 `GND` 和 `数据 7 号脚`，这样在开机后，会等待串口被打开再进行接下来的程序。并在此过程中打印一些有用的信息。~~ 为了减少程序大小，main 分支不再包含调试功能，需要调试功能请使用`low-sram-used-version`分支。

3. 演奏

    这部分最简单了。打开游戏，装备要使用的乐器，并进入使用界面。将 Arduino 连接到电脑的 USB 口。短接 `GND` 和 `数据 8 号脚` 之间的跳线。如果一切正常，此时就能自动演奏了。

> 注意：
>
> 1. 如果短接 `GND` 和 `数据 8 号脚` 之间的跳线，并未开始演奏。`TX` 灯也未闪烁，请检查各跳线是否充分连接，有无虚接。检查 SD 卡是否正确插入，金手指是否氧化，SD 卡的分区格式、乐谱文件名、乐谱位置、乐谱文件内容是否正确（如果转谱时未报错基本能够确定乐谱内容正确）。一切检查完毕确认无误后，重新将 Arduino 连接到电脑，再次尝试演奏。
> 2. 为了防止误触，需短接需短接 1s 左右演奏才会开始。
> 3. 虽然简介中写的是短接，但聪明的你一定能想到，装一个按钮开关能够更方便地控制 Arduino。并且由于延时的存在，相当于程序自带简单的防抖功能。

## 三、工具介绍

本章简单介绍 `tools` 文件夹中提供的工具。

1. ExcelTools

    此文件可对简谱录入提供一些帮助。使用前，先在 `G`、`H` 列填入拍号、速度以及乐谱用到的音符种类和表示音符时长的数值。然后在 `A`、`B` 两列依照简谱序列填写音高和音符类型，在 `J` 列会自动计算出本程序能够识别的乐谱。

    > 音调部分仍然是 `+` 代表高 8 度、`-` 代表低 8 度、`0` 代表休止符。音符类型可按照自己习惯指定，它只是一个让你能够方便录入的代称。
