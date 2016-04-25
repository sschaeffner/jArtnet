# jArtnet

This is an implementation of the Art-Net protocol by Artistic Licence Holdings Ltd.

Packets currently implemented:

* ArtPoll
* ArtPollReply
* ArtDmx
* ArtDiagData
* ArtIpProg
* ArtIpProgReply
* ArtSync
* ArtTimeCode
* ArtCommand
* ArtTrigger
* ArtNzs
* ArtInput
* ArtAddress

Currently tested with:

* ENTTEC PRO-Manager Art-Net Recorder

Not supported:

* Firmware upgrades over Art-Net (ArtFirmwareMaster, ArtFirmwareReply, UBEA file upload)
* RDM (ArtTodRequest, ArtTodData, ArtTodControl, ArtRdm, ArtRdmSub)

License: [LGPL version 3](http://www.gnu.org/licenses/lgpl-3.0.en.html)