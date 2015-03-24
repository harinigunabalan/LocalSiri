# LocalSiri - ReadMe #
### Project: Context Aware Opportunistic Sensing 
KOM-Lab, TU Darmstadt (WiSe-2014/15) 

####Tutor:
The An Binh Nguyen

#### Members: 
Hariharan Gandhi (2546148), Harini Gunabalan (2246600)

Opportunistic Sensing Android App that Localizes users based on GPS data and uses the Context to help newbies in an area with their queries by providing a Location based chat Forum.

### Key features ###

* Decentralized and without Internet - using Wi-Fi P2p API of Android
* Exchanges only with Trusted Friends - using NFC and local PINs to securely Add Friends to Trust Zone
* Collected profiles are stored Locally on user's devices - using SQLite DataHandlers and JSON objects
*  User can later browse friends Profile - using Profile View UIs
*  Automatic Content Exchange whenever friends are in Proximity via any D2D Communication Technique(WifiP2P) Network Service Discovery 
*  The app should be Secure - Only friends can exchange Contents  - using Encrypted exchange of Profile data AES-256 
*  UI - As Less Interactions as possible - Implementing as foreground Service
*  UI – Appealing to view profiles