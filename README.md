MJPG Server

A basic implementation of a MJPG streamer as a Java servlet. MJPG is commonly used to deliver image information from networked cameras via an HTTP stream. The set up of this class allws for dynamic image information that is created in real time to be sent to any tool capable of reading MJPG including several major internet browsers (Safari, Chrome, and Firefox).

We exclude four necessary images, which can be places in the user's home directory. We name our revolving images 'winter', 'spring', 'summer', and 'fall'. Images can be of any type, but this class is set up to read JPG images. Altering the image source type involves changing the ImageIO.write() function call that returns the image as a byte array.

The byte array could also be loaded live and not generated from a static file.

Feedback may be sent to josh dot dickson at wpi dot edu.
