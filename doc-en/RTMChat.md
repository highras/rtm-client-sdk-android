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

    /*get server unread message(sync)
     * @param clear     if clear unread(default false)
     
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
     * @param targetLanguage 
     */
    public RTMAnswer setTranslatedLanguage(TranslateLang targetLanguage){

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
     *get unread from server(async)
     * @param callback  IRTMCallback<Unread>
     * @param clear     if clear remind(default false)
     
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
     * @param targetLanguage    (NoNull)
     
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


    /**audio trans text sync
     * @param content   audio message(NoNull)
     * @param lang      audio lang(NoNull)
     * @param codec     audio codec("AMR_WB")
     * @param srate     sampling rate(16000)
     */
    public void audioToText(IRTMCallback<AudioTextStruct> callback, byte[] content, TranscribeLang lang, String codec, int srate) 
~~~