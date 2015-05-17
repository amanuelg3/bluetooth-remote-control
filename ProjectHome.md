This is an open source android application that can let you control bluetooth based radio controlled devices using your android phone. I built it to try out controlling a quadcopter using bluetooth. The bluetooth module I use is BTM-182, but it should work with any bluetooth module that supports the serial protocol.

Here is a picture. It's a bit ugly at the moment. You can see the serial chatter on the left hand side behind the controls. Two yellow dots indicate the left and right stick on the control. When you let go of both 'controls', the throttle (left hand side) goes to 0 automatically and the others center.

![http://lars.roland.bz/images/BluetoothRemote.png](http://lars.roland.bz/images/BluetoothRemote.png)

The code is based on the Android Bluetooth chat example and the BlueTerm project http://pymasde.es/blueterm/

It is still work in progress, but for some types of devices it should be possible to use already. Feel free to use and/or contribute. The picture is already out of date as far as the protocol is concerned.

The quad I'm working on is [here](http://www.rcgroups.com/forums/showthread.php?t=1335765). It doesn't support Bluetooth out of the box, but I've added the module and modified the code a bit to get it working. That code isn't available here, but I'll make it available via the developer of the quadcopter firmware if anyone wants it.

[My blog](http://lars.roland.bz/) has some of the other projects I've done.

If you like it, plus it:  :-)