###  sync interface
~~~c++
    /**
     * @param userid/groupId/roomId
     * @param message   send messge
     * @param attrs     additional message
     
     * @return          ModifyTimeStruct
     */
    
 send p2p chat
    public ModifyTimeStruct sendChat(long uid, String message, String attrs)     
     
ssend chat in group
    public ModifyTimeStruct sendGroupChat(long groupId, String message, String attrs)

ssend chat in room
    public ModifyTimeStruct sendRoomChat(long roomId, String message, String attrs){
     
send p2p control command
    public ModifyTimeStruct sendCmd(long uid, String message, String attrs){

ssend control command in group
    public ModifyTimeStruct sendGroupCmd(long groupId, String message, String attrs){

ssend control command in room
    public ModifyTimeStruct sendRoomCmd(long roomId, String message, String attrs){


    /**
     *get history chat(sync)
     * @param userid/groupId/roomId
     * @param desc      if reverse order  
     * @param count     show count 
     * @param beginMsec begin time(millisecond)
     * @param endMsec   ent time(millisecond)
     * @param lastId    last message indexId
     
     * return       HistoryMessageResult
     */


    public HistoryMessageResult getP2PHistoryChat(long toUid, boolean desc, int count, long beginMsec, long endMsec, long lastId){

    public HistoryMessageResult getGroupHistoryChat(long groupId, boolean desc, int count, long beginMsec, long endMsec, long lastId){

    public HistoryMessageResult getRoomHistoryChat(long roomId, boolean desc, int count, long beginMsec, long endMsec, long lastId){


    /**
     * broadcast history chat(sync)
     * @param desc      if reverse order  
     * @param count     show count 
     * @param beginMsec query begin time(millisecond)
     * @param endMsec   query end time(millisecond)
     * @param lastId    last message indexId
     
     * return   HistoryMessageResult
     */
    public HistoryMessageResult getBroadcastHistoryChat(boolean desc, int count, long beginMsec, long endMsec, long lastId){

    /**get p2p urnead count(sync)
     * @param uids      user ids(get user ids by getsession)
     * @param lastMessageTime (ms)(default user last logout time)
     * @param messageTypes (default chat message types, not included customize message type)
     *return        UnreadNum
     */
    public UnreadNum getP2PUnread(HashSet<Long> uids, long lastMessageTime, List<Byte> messageTypes)
    
    
    /**get group urnead count （sync）
     * @param gids  group ids(get group ids by getsession)
     * @param lastMessageTime (ms)(default user last logout time)
     * @param messageTypes (default chat message types, not included customize message type)
     *return        Map<String, Integer>> ey-grouprid，value-unread count
     */
    public UnreadNum getGroupUnread(HashSet<Long> gids, long lastMessageTime, List<Byte> messageTypes)



    /*get server unread message(sync)
     * @param clear     if clear unread(default true)
     * return           Unread struct
     */
    public Unread getUnread( boolean clear){

    /*clear unread 
     * @return  RTMAnswer
     */
    public RTMAnswer clearUnread()

    /**
     * sync(need config on console）
     * @param text          need translate message(NoNull)
     * @param destinationLanguage   
     * @param sourceLanguage        
     * @param type                  cha or mail。default 'chat'
     * @param profanity             sensitive filter option-off, censor，default：off if choose censor sensitive word will be replace '*'
     * @return                  TranslatedInfo
     */
    public TranslatedInfo translate(String text, String destinationLanguage, String sourceLanguage, 
                         translateType type, ProfanityType profanity,){

     /**
     * sync
     * @param targetLanguage (the language refer to TranslateLang.java)
     */
    public RTMAnswer setTranslatedLanguage(String targetLanguage){

    /**
     *text detection sync(need config on console）
     * @param text          need check text 
     * @return              CheckResult
     */
    public CheckResult textCheck(String text)
~~~


### async interface
~~~c++
     /**
     *(async)
     * @param callback  IRTMDoubleValueCallback<Long,Long>callback (odifytime,messageid)
     * @param uid       userid/groupId/roomId
     * @param message   
     * @param attrs     additional message
     */

    public void sendChat(IRTMDoubleValueCallback<Long,Long> callback, long uid, String message, String attrs)
     
    public void sendGroupChat(IRTMDoubleValueCallback<Long,Long> callback, long groupId, String message, String attrs)

    public void sendRoomChat(IRTMDoubleValueCallback<Long,Long> callback, long roomId, String message, String attrs){
     
    public void sendCmd(IRTMDoubleValueCallback<Long,Long> callback, long uid, String message, String attrs)

    public void sendGroupCmd(IRTMDoubleValueCallback<Long,Long> callback, long groupId, String message, String attrs)

    public void sendRoomCmd(IRTMDoubleValueCallback<Long,Long> callback, long roomId, String message, String attrs){


    /*
     * @param callback  HistoryMessageCallback callback(NoNull)
     * @param userid/groupId/roomId  (NoNull)
     * @param desc      if reverse order  
     * @param count     show count 
     * @param beginMsec query begin time(millisecond)
     * @param endMsec   query end time(millisecond)
     * @param lastId    last message indexId
     
     * return   HistoryMessageResult
     */

    public void getP2PHistoryChat(IRTMCallback<HistoryMessageResult> callback,  long toUid, boolean desc, int count, long beginMsec, long endMsec, long lastId)


    public void getGroupHistoryChat(IRTMCallback<HistoryMessageResult> callback, long groupId, boolean desc, int count, long beginMsec, long endMsec, long lastId)


    public void getRoomHistoryChat(IRTMCallback<HistoryMessageResult> callback, long roomId, boolean desc, int count, long beginMsec, long endMsec, long lastId)

    /**
    * 
     * @param callback  HistoryMessageCallback callback(NoNull)
     * @param desc      if reverse order  
     * @param count     show count 
     * @param beginMsec query begin time(millisecond)
     * @param endMsec   query end time(millisecond)
     * @param lastId    last message indexId
     
     */
    public void getBroadcastHistoryChat(IRTMCallback<HistoryMessageResult> callback, boolean desc, int count, long beginMsec, long endMsec, long lastId)


    /**
     *get p2p urnead count(async)
     * @param callback   IRTMCallback<Map<String, Integer>> key-userid，value-unread count
     * @param     uids   user ids(get user ids by getsession)
     * @param lastMessageTime (ms)(default user last logout time)
     * @param messageTypes (default chat message types, not included customize message type)
     */
    public void getP2PUnread(@NonNull final IRTMCallback<Map<String, Integer>> callback, HashSet<Long> uids,long lastMessageTime, List<Byte> messageTypes) 
    
    
    
     /**
     *get group unread count(async)
     * @param callback   IIRTMCallback<Map<String, Integer>> key-groupid，value-unread count
     * @param     gids   group ids((get group ids by getsession))   
     * @param lastMessageTime(ms)(default user last logout time)
     * @param messageTypes (default chat message types, not included customize message type)
     */
    public void getGroupUnread(@NonNull final IRTMCallback<Map<String, Integer>> callback, HashSet<Long> gids,long lastMessageTime, List<Byte> messageTypes) 
    

    /**
     *get unread from server(async)
     * @param callback  IRTMCallback<Unread>
     * @param clear     if clear remind(default true)
     */
    public void getUnread(final IRTMCallback<Unread> callback, boolean clear)

    /**
     *clear unread(async)
     * @param callback EmptyCallback (NoNull)
     */
    public void clearUnread(IRTMEmptyCallback callback)
    
    /**
     * get uids and groupids when talk with me once(async)
     * @param callback UnreadCallback (NoNull)
     
     */
    public void getSession(final IRTMCallback<Unread> callback)

    /**
     * async
     * @param callback  IRTMEmptyCallback (NoNull)
     * @param targetLanguage    (the language refer to TranslateLang.java)(NoNull)
     
     */
    public void setTranslatedLanguage(IRTMEmptyCallback callback, String targetLanguage)

    /**
     *text translate async(need config text translate service on console）
     * @param callback      TranslateCallback (NoNull)
     * @param text          need translate text(NoNull)
     * @param destinationLanguage   (NoNull)
     * @param sourceLanguage        
     * @param type                  cha or mail。default 'chat'
     * @param profanity             sensitive filter option-off or censor，default：off if choose censor sensitive word will be replace '*'
     */
    public void translate(final IRTMCallback<TranslatedInfo> callback, String text, String destinationLanguage, String sourceLanguage, ,
                             translateType type, ProfanityType profanity)

   /**
     * async(need config text check service on console）
     * @param callback      IRTMCallback<CheckResult> (NoNull)
     * @param text          need check text(NoNull)
     */
    public void textCheck(final IRTMCallback<CheckResult> callback, String text)


    /**audio trans text async
     * @param content   audio message(NoNull)
     * @param lang      audio lang(NoNull)
     * @param codec     audio codec("AMR_WB")
     * @param srate     sampling rate(16000)
     */
    public void audioToText(IRTMCallback<AudioTextStruct> callback, byte[] content, String lang, String codec, int srate) 
~~~