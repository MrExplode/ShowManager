# ![icon](https://cdn.sunstorm.rocks/files/showmanager/icon32.png)  ShowManager [![Build Status](https://travis-ci.com/MrExplode/ShowManager.svg?branch=master)](https://travis-ci.com/MrExplode/ShowManager) [![Codacy Badge](https://api.codacy.com/project/badge/Grade/06b58e31d1834512bc7016d8240cb6f8)](https://www.codacy.com/manual/MrExplode/Timecode?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=MrExplode/Timecode&amp;utm_campaign=Badge_Grade)
ArtNet and LTC timecode generator.

![GUI](https://cdn.sunstorm.rocks/files/showmanager/GUI_showcase.png)

## Downloads
- Latest release (recommended, because it works): [here](https://github.com/MrExplode/Timecode/releases)

## Features
-   ArtNet Timecode
-   LTC Timecode
-   OSC command dispatcher
-   Import / export osc commands
-   Selectable outputs for everything
-   Multiple framerates
-   Play / Pause / Stop
-   Set time
-   DMX remote control over ArtNet
-   Music player
-   Always-on-top monitor window
-   Client - Server system
-   The client displays the current time, currently playing track information, and custom messages sent by the server, formatted in Markdown

### DMX Remote control

#### Parameters
-   DMX address
-   ArtNet universe
-   Subnet
#### Values
-   10% - Force Idle: locked controls
-   20% - Play
-   30% - Pause
-   40% - Stop
-   Every other value: just idle
