[![Codacy Badge](https://api.codacy.com/project/badge/Grade/c2d0c2b663174c37a0fc88d20ebb2611)](https://app.codacy.com/manual/pjanos/Timecode?utm_source=github.com&utm_medium=referral&utm_content=MrExplode/Timecode&utm_campaign=Badge_Grade_Dashboard)
# ![icon](https://mrexplode.github.io/resources/icon32.png)  Timecode [![Build Status](https://travis-ci.org/MrExplode/Timecode.svg?branch=master)](https://travis-ci.org/MrExplode/Timecode)
ArtNet and LTC timecode generator.

![GUI showcase](https://mrexplode.github.io/resources/Timecode.png)

## Features
 - ArtNet Timecode
 - LTC Timecode
 - OSC command dispatcher
 - Selectable outputs for everything
 - Multiple framerates
 - Play / Pause / Stop
 - Set time
 - DMX remote control over ArtNet
 - Music player (Very alpha)

### DMX Remote control

#### Parameters
 - DMX address
 - ArtNet universe
 - Subnet
#### Values
 - 10% - Force Idle: locked controls
 - 20% - Play
 - 30% - Pause
 - 40% - Stop
 - Every other value: just idle
