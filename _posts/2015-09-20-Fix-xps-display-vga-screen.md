---
layout: post
title: VGA external projector not detected with Ubuntu Dell XPS 13
modified: 2015-07-31
categories: [articles, Howto]
tags: 
  - linux
  - display
  - xrandr
  - mini display port
  - ubuntu
comments: true
contribute: https://codenvy.com/f?id=6z5761px6w9exebd
---

Just got back from an amazing [Jug Summercamp](http://www.jugsummercamp.org/edition/6) at La Rochelle. I was ready for the presentation of the [Live Pair Programing with Eclipse Cloud Development](http://www.jugsummercamp.org/edition/6/presentation/1121) and ...
AGAIN ! I got display issues with the external VGA projector not detected on my computer. The same one I had few months ago at Eclipse Con France.
Here is how I managed to get rid of my display issues.

<!-- more -->


# Searching ...
When I plugged the VGA cable to my display port, the projector was showing: "SEARCHING ...". No screen was detected on my laptop configuration screen.
I've tried on another room and it worked fine. Most of the time that problem happens when there is an extension of the VGA cable.

salut Axel

# My laptop
Operating system:

    Linux Ubuntu Trusty

Laptop:

    Dell System XPS L322X (Dell System XPS L322X)

Display device:

    00:02.0 VGA compatible controller: Intel Corporation 3rd Gen Core processor Graphics Controller (rev 09)


The laptop is plugged to the external screen projector with the VGA cable and a mini-display-port to VGA adaptor.


# Fix it
The following fix needs to be adapted to your device. These are using `eDP1` and `DP1` because it is using a mini display port.

- `eDP1` is the main laptop screen
- `DP1` is the external screen

You can run the `xrandr` command (with no parameter) to get the status of your display:

    Screen 0: minimum 320 x 200, current 1920 x 1080, maximum 32767 x 32767
    eDP1 connected primary 1920x1080+0+0 (normal left inverted right x axis y axis) 294mm x 165mm
       1920x1080      60.0*+   59.9     40.0  
       1680x1050      60.0     59.9  
       1600x1024      60.2  
       1400x1050      60.0  
       1280x1024      60.0  
       1440x900       59.9  
       1280x960       60.0  
       1360x768       59.8     60.0  
       1152x864       60.0  
       1024x768       60.0  
       800x600        60.3     56.2  
       640x480        59.9  
    VGA1 disconnected (normal left inverted right x axis y axis)
    HDMI1 disconnected (normal left inverted right x axis y axis)
    DP1 disconnected (normal left inverted right x axis y axis)
    VIRTUAL1 disconnected (normal left inverted right x axis y axis)


Choose a resolution that works with the external monitor. Make sure your VGA cable is plugged before running these commands (expecially the last one)

    # Set the main display to the resolution
    xrandr --output eDP1 --mode 1280x1024

    # add the mode resolution to external projector
    xrandr --addmode DP1 1280x1024

    # activate the output for the external projector using a clone of the main display
    xrandr --output DP1 --mode 1280x1024 --same-as eDP1


Wait 5 seconds and it should work!

# Arandr
(Edit 21/01/2015)

It's also possible to use the GUI arandr to generate xrandr commands.

![arandr]({{ site.url }}/images/arandr.png)

It generates this kind of script:

    $ cat ~/.screenlayout/clone.sh 
    #!/bin/sh
    xrandr --output HDMI1 --off --output VIRTUAL1 --off --output DP1 --mode 1920x1080 --pos 0x0 --rotate normal --output eDP1 --mode 1920x1080 --pos 0x0 --rotate normal --output VGA1 --off

So each time i need to clone my screen, I can just execute the right script ... et voilà ! 
