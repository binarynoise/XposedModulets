# Better Verbose WiFi Logging

This makes the verbose Wi-Fi information (enabled in the developer settings) more readable, adds some and hides some useless information.

*Before:*

```
Name of the Network
Connected / f = 2422 aa:bb:cc:dd:ee:ff standard = Wi-Fi 4 rssi = -44 score = 60 tx=0.0, 0.0, 0.0 rx=0.0 hasInternet:true, isDefaultNetwork:true, isLowQuality:false [(1) {aa:bb:cc:dd:ee:ff*=,-44,Wi-Fi 4,2s};;;]
``` 

Notice how it is all one long line that gets wrapped at your screen to make it fit.

*After:*

```
Name of the Network
Connected / 2422 MHz (3), bssid:aa:bb:cc:dd:ee:ff, standard=4, rssi=-35dBm, hasInternet:true, isDefaultNetwork:true, isLowQuality:false
[{aa:bb:cc:dd:ee:ff*, 2422 MHz (3), -26 dBm}]
```

This one is split into multiple lines: one for the general status info, one for _each_ network.  
In parentheses, you find the channel number because that is usually more interesting than the frequency itself.  
Everything that has units now displays those units.  
What you don't see is that elements that belong together have no break spaces between them
so that they are always displayed in one line next to each other.
