###  sync interface
~~~c++
    /**
     * @param userid/groupId/roomId
     * @param message   send messge
     * @param attrs     additional message
     * @param timeout   timeout(seconds)
     * @return          ModifyTimeStruct
     */
    
 send p2p chat
    public ModifyTimeStruct sendChat(long uid, String message, String attrs, int timeout)     
     
ssend chat in group
    public ModifyTimeStruct sendGroupChat(long groupId, String message, String attrs, int timeout)

ssend chat in room
    public ModifyTimeStruct sendRoomChat(long roomId, String message, String attrs, int timeout){
     
send p2p control command
    public ModifyTimeStruct sendCmd(long uid, String message, String attrs, int timeout){

ssend control command in group
    public ModifyTimeStruct sendGroupCmd(long groupId, String message, String attrs, int timeout){

ssend control command in room
    public ModifyTimeStruct sendRoomCmd(long roomId, String message, String attrs, int timeout){


    /**
     *get history chat(sync)
     * @param userid/groupId/roomId
     * @param desc      if reverse order  
     * @param count     show count 
     * @param beginMsec begin time(millisecond)
     * @param endMsec   ent time(millisecond)
     * @param lastId    last message indexId
     * @param timeout   timeout(seconds)
     * return       HistoryMessageResult
     */


    public HistoryMessageResult getP2PHistoryChat(long toUid, boolean desc, int count, long beginMsec, long endMsec, long lastId, int timeout){

    public HistoryMessageResult getGroupHistoryChat(long groupId, boolean desc, int count, long beginMsec, long endMsec, long lastId, int timeout){

    public HistoryMessageResult getRoomHistoryChat(long roomId, boolean desc, int count, long beginMsec, long endMsec, long lastId, int timeout){


    /**
     * broadcast history chat(sync)
     * @param desc      if reverse order  
     * @param count     show count 
     * @param beginMsec query begin time(millisecond)
     * @param endMsec   query end time(millisecond)
     * @param lastId    last message indexId
     * @param timeout   timeout(seconds)
     * return   HistoryMessageResult
     */
    public HistoryMessageResult getBroadcastHistoryChat(boolean desc, int count, long beginMsec, long endMsec, long lastId, int timeout){

    /*get server unread message(sync)
     * @param clear     if clear unread(default false)
     * @param timeout   timeout(seconds)
     * return           Unread struct
     */
    public Unread getUnread( boolean clear, int timeout){

    /*clear unread 
     * @param   timeout 
     * @return  RTMAnswer
     */
    public RTMAnswer clearUnread(int timeout)

    /**
     * sync(need config on console）
     * @param text          need translate message(NoNull)
     * @param destinationLanguage   
     * @param sourceLanguage        
     * @param timeout              
     * @param type                  cha or mail。default 'chat'
     * @param profanity             sensitive filter option-off, censor，default：off if choose censor sensitive word will be replace '*'
     * @return                  TranslatedInfo
     */
    public TranslatedInfo translate(String text, String destinationLanguage, String sourceLanguage, 
                         translateType type, ProfanityType profanity,int timeout){

     /**
     * sync
     * @param targetLanguage 
     * @param timeout 
     */
    public RTMAnswer setTranslatedLanguage(TranslateLang targetLanguage, int timeout){

    /**
     *text detection sync(need config on console）
     * @param text          need check text 
     * @param timeout       
     * @return              CheckResult
     */
    public CheckResult textCheck(String text, int timeout)
~~~


### async interface
~~~c++
     /**
     *(async)
     * @param callback  IRTMDoubleValueCallback<Long,Long>callback (odifytime,messageid)
     * @param uid       userid/groupId/roomId
     * @param message   
     * @param attrs     additional message
     * @param timeout   
     */

    public void sendChat(IRTMDoubleValueCallback<Long,Long> callback, long uid, String message, String attrs, int timeout)
     
    public void sendGroupChat(IRTMDoubleValueCallback<Long,Long> callback, long groupId, String message, String attrs, int timeout)

    public void sendRoomChat(IRTMDoubleValueCallback<Long,Long> callback, long roomId, String message, String attrs, int timeout){
     
    public void sendCmd(IRTMDoubleValueCallback<Long,Long> callback, long uid, String message, String attrs, int timeout)

    public void sendGroupCmd(IRTMDoubleValueCallback<Long,Long> callback, long groupId, String message, String attrs, int timeout)

    public void sendRoomCmd(IRTMDoubleValueCallback<Long,Long> callback, long roomId, String message, String attrs, int timeout){


    /*
     * @param callback  HistoryMessageCallback callback(NoNull)
     * @param userid/groupId/roomId  (NoNull)
     * @param desc      if reverse order  
     * @param count     show count 
     * @param beginMsec query begin time(millisecond)
     * @param endMsec   query end time(millisecond)
     * @param lastId    last message indexId
     * @param timeout   timeout(seconds)
     * return   HistoryMessageResult
     */

    public void getP2PHistoryChat(IRTMCallback<HistoryMessageResult> callback,  long toUid, boolean desc, int count, long beginMsec, long endMsec, long lastId, int timeout)


    public void getGroupHistoryChat(IRTMCallback<HistoryMessageResult> callback, long groupId, boolean desc, int count, long beginMsec, long endMsec, long lastId, int timeout)


    public void getRoomHistoryChat(IRTMCallback<HistoryMessageResult> callback, long roomId, boolean desc, int count, long beginMsec, long endMsec, long lastId, int timeout)

    /**
    * 
     * @param callback  HistoryMessageCallback callback(NoNull)
     * @param desc      if reverse order  
     * @param count     show count 
     * @param beginMsec query begin time(millisecond)
     * @param endMsec   query end time(millisecond)
     * @param lastId    last message indexId
     * @param timeout   timeout(seconds)
     */
    public void getBroadcastHistoryChat(IRTMCallback<HistoryMessageResult> callback, boolean desc, int count, long beginMsec, long endMsec, long lastId, int timeout)

    /**
     *get unread from server(async)
     * @param callback  IRTMCallback<Unread>
     * @param clear     if clear remind(default false)
     * @param timeout   timeout(seconds)
     */
    public void getUnread(final IRTMCallback<Unread> callback, boolean clear, int timeout)

    /**
     *clear unread(async)
     * @param callback EmptyCallback (NoNull)
     * @param timeout   timeout(seconds)
     */
    public void clearUnread(IRTMEmptyCallback callback, int timeout)
    
    /**
     * get uids and groupids when talk with me once(async)
     * @param callback UnreadCallback (NoNull)
     * @param timeout   timeout(seconds)
     */
    public void getSession(final IRTMCallback<Unread> callback, int timeout)

    /**
     * async
     * @param callback  IRTMEmptyCallback (NoNull)
     * @param targetLanguage    (NoNull)
     * @param timeout   timeout(seconds)
     */
    public void setTranslatedLanguage(IRTMEmptyCallback callback, String targetLanguage, int timeout)

    /**
     *text translate async(need config text translate service on console）
     * @param callback      TranslateCallback (NoNull)
     * @param text          need translate text(NoNull)
     * @param destinationLanguage   (NoNull)
     * @param sourceLanguage        
     * @param timeout               timeout(seconds)
     * @param type                  cha or mail。default 'chat'
     * @param profanity             sensitive filter option-off or censor，default：off if choose censor sensitive word will be replace '*'
     */
    public void translate(final IRTMCallback<TranslatedInfo> callback, String text, String destinationLanguage, String sourceLanguage, int timeout,
                             translateType type, ProfanityType profanity)

   /**
     * async(need config text check service on console）
     * @param callback      IRTMCallback<CheckResult> (NoNull)
     * @param text          need check text(NoNull)
     * @param timeout       timeout(seconds)
     */
    public void textCheck(final IRTMCallback<CheckResult> callback, String text, int timeout)


    /**audio trans text sync
     * @param content   audio message(NoNull)
     * @param lang      audio lang(NoNull)
     * @param codec     audio codec("AMR_WB")
     * @param srate     sampling rate(16000)
     */
    public void audioToText(IRTMCallback<AudioTextStruct> callback, byte[] content, TranscribeLang lang, String codec, int srate) 
~~~